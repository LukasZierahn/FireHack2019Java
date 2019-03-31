// ===============================================================================
// Authors: Jacob Allex-Buckner
// Organization: University of Dayton Research Institute Applied Sensing Division
//
// Copyright (c) 2018 Government of the United State of America, as represented by
// the Secretary of the Air Force.  No copyright is claimed in the United States under
// Title 17, U.S. Code.  All Other Rights Reserved.
// ===============================================================================

// This file was auto-created by LmcpGen. Modifications will be overwritten.

import afrl.cmasi.*;
import afrl.cmasi.perceive.EntityPerception;
import afrl.cmasi.searchai.HazardZoneDetection;
import afrl.cmasi.searchai.RecoveryPoint;
import avtas.lmcp.LMCPFactory;
import avtas.lmcp.LMCPObject;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Connects to the simulation and sends a fake mission command to every UAV that is requested in the plan request.
 */
public class Main extends Thread {

    /** simulation TCP port to connect to */
    private static int port = 5555;
    /** address of the server */
    private static String host = "localhost";

    public int fixedWings = 0;
    public int multi = 0;

    private FireMap fireMap = null;

    private long commandID = 0;
    private long waypointID = 0;

    private Map<Long, UAV> UAVMap = new HashMap<>();
    private Map<Long, UAVTASKS> UAVTasks = new HashMap<>();
    private DroneStore droneStore = new DroneStore();

    private List<FireZoneController> hazardZones = new ArrayList<>();

    private List<Waypoint> waypoints = new ArrayList<>();

    private InputStream in;
    private OutputStream out;

    private long time;
    


    public Main() {
    }

    @Override
    public void run() {
        try {
            // connect to the server
            Socket socket = connect(host, port);

            in = socket.getInputStream();
            out = socket.getOutputStream();

            while(true) {
                //Continually read the LMCP messages that AMASE is sending out
                try {
                    readMessages();
                } catch (Exception e) {
                    System.out.println("Error while reading messages");
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(HazardDetectionJavaClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public void readMessages() throws Exception {
        
        // Every time we receive a message we clean the drone store
        // This helps remove drones are refuelling
        droneStore.Clean();

        LMCPObject o = LMCPFactory.getObject(in);
        if (o instanceof KeepInZone) {
            if (fireMap == null) {
                fireMap = new FireMap(this, (KeepInZone) o);
            }

        } else if (o instanceof AirVehicleState) {
            AirVehicleState msg = (AirVehicleState) o;
            UAVMap.get(msg.getID()).airVehicleState = msg;

            UAV uav = UAVMap.get(msg.getID());

            Point2D pos = fireMap.Location3DToCoord(uav.airVehicleState.getLocation());

            int boundaryLimit = 500;
            if (pos.getX() < boundaryLimit || pos.getX() > fireMap.width - boundaryLimit || pos.getY() < boundaryLimit || pos.getY() > fireMap.height - boundaryLimit) {
                uav.InitRefuelMission();
            }

            fireMap.HandleAirVehicleState(msg);

        } else if (o instanceof SessionStatus) {
            SessionStatus msg = (SessionStatus) o;
            time = msg.getScenarioTime();

            for (FireZoneController FZC : hazardZones) {
                FZC.RequisitionUpToMaxDrones();
            }

            for (UAV uav : UAVMap.values()) {
                uav.Update();
            }

            UAVTaskOutput();

        } else if (o instanceof AirVehicleConfiguration) {
            UAV newUav = new UAV( this, (AirVehicleConfiguration) o);
            UAVMap.put(((AirVehicleConfiguration) o).getID(), newUav);
            droneStore.AddToStore(newUav);
            UAVTasks.put(((AirVehicleConfiguration) o).getID(), UAVTASKS.NO_TASK);

        } else if (o instanceof HazardZoneDetection) {
            HazardZoneDetection msg = ((HazardZoneDetection) o);
            Location3D detectedLocation = msg.getDetectedLocation();

            UAV uav = UAVMap.get(msg.getDetectingEnitiyID());

            if ((!uav.fixedWing || (!uav.HasSeenFire() && uav.currentTask != UAVTASKS.FLYTHROUGH)) && uav.currentTask != UAVTASKS.REFUEL) {
                if (uav.fireZoneController == null) {
                     // We've bumped into a firezone in the wild, remove this drone from the store if present, and manually requisition for mapping
                    droneStore.RemoveFromStore(uav);

                    List<Long> UAVS = new ArrayList<>();
                    UAVS.add(msg.getDetectingEnitiyID());

                    hazardZones.add(new FireZoneController(this, detectedLocation, UAVS));
                    if (uav.fixedWing) {
                        uav.currentTask = UAVTASKS.FLYTHROUGH;
                        uav.fireZoneController = hazardZones.get(hazardZones.size() - 1);
                    }
                } else {
                    uav.fireZoneController.HandleHazardZoneDetection(msg);
                }
            }

            uav.SawFire();
        }
        else if (o instanceof RecoveryPoint) {
            RecoveryPoint recoveryPoint = (RecoveryPoint)o;
            Location3D center = ((Circle)recoveryPoint.getBoundary()).getCenterPoint();
            for (UAV uav : UAVMap.values()) {
                uav.refuelPoints.add(center);
            }
        }
        else if (o instanceof EntityPerception) {
            //Stop the spam
        }
        else {
            System.out.println("Unhandled Message: " + o.getLMCPTypeName());
        }
    }

    public long getNextCommandID() {
        return commandID++;
    }

    public long getNextWaypointID() {
        return waypointID++;
    }

    public OutputStream getOut() {
        return out;
    }

    public UAV getUAV(Long ID) {
        if (!UAVMap.containsKey(ID)) {
            System.out.printf("Tried to receive UAV with ID: %s but that ID doesn't exist!\n", ID);
        }

        return UAVMap.get(ID);
    }

    public Map<Long, UAV> getUAVMap() {
        return UAVMap;
    }
    
    public DroneStore getDroneStore() {
        return droneStore;
    }

    public long getTime() {
        return time;
    }

    public FireMap getFireMap() {
        return fireMap;
    }

    public List<FireZoneController> getHazardZones() {
        return hazardZones;
    }

    public void UAVTaskOutput() {
        for (UAV uav : UAVMap.values()) {
            if (uav.currentTask != UAVTasks.get(uav.airVehicleState.getID())) {
                System.out.printf("UAV ID %d, %s, Task %s\n", uav.airVehicleState.getID(), uav.fixedWing ? "Fixed" : "Multi", uav.currentTask.toString());
                UAVTasks.put(uav.airVehicleState.getID(), uav.currentTask);
            }
        }
    }

    /** tries to connect to the server.  If there is a problem (such as the server not running yet) it
     *  pauses, then tries again.  If the server quits and restarts, this method is called by the thread
     *  in order to re-establish communication.
     * @param host
     * @param port
     * @return
     */
    public Socket connect(String host, int port) {
        Socket socket = null;
        try {
            socket = new Socket(host, port);
        } catch (UnknownHostException ex) {
            System.err.println("Host Unknown. Quitting");
            System.exit(0);
        } catch (IOException ex) {
            System.err.println("Could not Connect to " + host + ":" + port + ".  Trying again...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex1) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return connect(host, port);
        }
        System.out.println("Connected to " + host + ":" + port);
        return socket;
    }

    public static void main(String[] args) {
        new Main().start();
    }
}
