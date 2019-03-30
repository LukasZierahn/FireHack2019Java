// ===============================================================================
// Authors: Jacob Allex-Buckner
// Organization: University of Dayton Research Institute Applied Sensing Division
//
// Copyright (c) 2018 Government of the United State of America, as represented by
// the Secretary of the Air Force.  No copyright is claimed in the United States under
// Title 17, U.S. Code.  All Other Rights Reserved.
// ===============================================================================

// This file was auto-created by LmcpGen. Modifications will be overwritten.

import afrl.cmasi.AltitudeType;
import afrl.cmasi.CommandStatusType;
import afrl.cmasi.GimbalStareAction;
import afrl.cmasi.Location3D;
import afrl.cmasi.LoiterAction;
import afrl.cmasi.LoiterDirection;
import afrl.cmasi.LoiterType;
import afrl.cmasi.MissionCommand;
import afrl.cmasi.SessionStatus;
import afrl.cmasi.SpeedType;
import afrl.cmasi.TurnType;
import afrl.cmasi.VehicleAction;
import afrl.cmasi.VehicleActionCommand;
import afrl.cmasi.Waypoint;
import avtas.lmcp.LMCPFactory;
import avtas.lmcp.LMCPObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Connects to the simulation and sends a fake mission command to every UAV that is requested in the plan request.
 */
public class AMASEJavaClient extends Thread {

    /** simulation TCP port to connect to */
    private static int port = 5555;
    /** address of the server */
    private static String host = "localhost";
    /** scenario clock time */
    private static long scenarioTime = 0;

    public AMASEJavaClient() {
    }

    @Override
    public void run() {
        try {
            // connect to the server
            Socket socket = connect(host, port);
            Boolean missionCommandSent = false;
            Boolean sensorCommandSent = false;
            Boolean loiterCommandSent = false;
            while(true) {
                //Continually read the LMCP messages that AMASE is sending out
                readMessages(socket.getInputStream());
                //Example of how to send a message after 15 seconds of the scenario starting
                if(scenarioTime >= 15000 && missionCommandSent == false){
                    //Send a message to change the 1st entity's waypoints
                    sendMissionCommand(socket.getOutputStream());
                    missionCommandSent = true;
                }
                if(scenarioTime >= 40000 && sensorCommandSent == false) {
                    sendSensorCommand(socket.getOutputStream());
                    sensorCommandSent = true;
                }
                if(scenarioTime >= 100000 && loiterCommandSent == false) {
                    sendLoiterCommand(socket.getOutputStream());
                    loiterCommandSent = true;
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(AMASEJavaClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Sends mission command to the AMASE Server
     * @param out
     * @throws Exception 
     */
    public void sendMissionCommand(OutputStream out) throws Exception {
        //Setting up the mission to send to the UAV
         MissionCommand o = new MissionCommand();
         o.setFirstWaypoint(1);
         //Setting the UAV to recieve the mission
         o.setVehicleID(1);
         o.setStatus(CommandStatusType.Pending);
         //Setting a unique mission command ID
         o.setCommandID(1);
         //Creating the list of waypoints to be sent with the mission command
         ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
         //Creating the first waypoint
         //Note: all the following attributes must be set to avoid issues
         Waypoint waypoint1 = new Waypoint();
         //Setting 3D coordinates
         waypoint1.setLatitude(1.505);
         waypoint1.setLongitude(-132.539);
         waypoint1.setAltitude(100);
         waypoint1.setAltitudeType(AltitudeType.MSL);
         //Setting unique ID for the waypoint
         waypoint1.setNumber(1);
         waypoint1.setNextWaypoint(2);
         //Setting speed to reach the waypoint
         waypoint1.setSpeed(30);
         waypoint1.setSpeedType(SpeedType.Airspeed);
         //Setting the climb rate to reach new altitude (if applicable)
         waypoint1.setClimbRate(0);
         waypoint1.setTurnType(TurnType.TurnShort);
         //Setting backup waypoints if new waypoint can't be reached
         waypoint1.setContingencyWaypointA(0);
         waypoint1.setContingencyWaypointB(0);
         
         //Setting up the second waypoint to be sent in the mission command
         Waypoint waypoint2 = new Waypoint();
         waypoint2.setLatitude(1.52);
         waypoint2.setLongitude(-132.51);
         waypoint2.setAltitude(100);
         waypoint2.setAltitudeType(AltitudeType.MSL);
         waypoint2.setNumber(2);
         waypoint2.setNextWaypoint(1);
         waypoint2.setSpeed(30);
         waypoint2.setSpeedType(SpeedType.Airspeed);
         waypoint2.setClimbRate(0);
         waypoint2.setTurnType(TurnType.TurnShort);
         waypoint2.setContingencyWaypointA(0);
         waypoint2.setContingencyWaypointB(0);
         
         //Adding the waypoints to the waypoint list
         waypoints.add(waypoint1);
         waypoints.add(waypoint2);
         
         //Setting the waypoint list in the mission command
         o.getWaypointList().addAll(waypoints);
         
         //Sending the Mission Command message to AMASE to be interpreted
         out.write(avtas.lmcp.LMCPFactory.packMessage(o, true));
    }
    
    /**
     * Sends gimbal stare command to the AMASE Server
     * @param out
     * @throws Exception 
     */
    public void sendSensorCommand (OutputStream out) throws Exception {
        //Setting up the mission to send to the UAV
         VehicleActionCommand o = new VehicleActionCommand();
         o.setVehicleID(1);
         o.setStatus(CommandStatusType.Pending);
         o.setCommandID(1);
         
         //Setting up the vehical action command list
         ArrayList<VehicleAction> vehicleActionList = new ArrayList<VehicleAction>();
         
         //Setting up the gimbal stare vehicle action
         GimbalStareAction gimbalStareAction = new GimbalStareAction();
         gimbalStareAction.setPayloadID(1);
         gimbalStareAction.setDuration(1000000);
         
         //Creating a 3D location object for the stare point
         Location3D location = new Location3D(1.52, -132.51, 0, afrl.cmasi.AltitudeType.MSL);
         gimbalStareAction.setStarepoint(location);
         
         //Adding the gimbal stare action to the vehicle action list
         vehicleActionList.add(gimbalStareAction);
         o.getVehicleActionList().addAll(vehicleActionList);
         
         //Sending the Vehicle Action Command message to AMASE to be interpreted
         out.write(avtas.lmcp.LMCPFactory.packMessage(o, true));
    }
    
    /**
     * Sends loiter command to the AMASE Server
     * @param out
     * @throws Exception 
     */
    public void sendLoiterCommand (OutputStream out) throws Exception {
        //Setting up the mission to send to the UAV
         VehicleActionCommand o = new VehicleActionCommand();
         o.setVehicleID(1);
         o.setStatus(CommandStatusType.Pending);
         o.setCommandID(1);
         
         //Setting up the loiter action
         LoiterAction loiterAction = new LoiterAction();
         loiterAction.setLoiterType(LoiterType.Circular);
         loiterAction.setRadius(500);
         loiterAction.setAxis(0);
         loiterAction.setLength(0);
         loiterAction.setDirection(LoiterDirection.Clockwise);
         loiterAction.setDuration(100000);
         loiterAction.setAirspeed(30);
         
         //Creating a 3D location object for the stare point
         Location3D location = new Location3D(1.52, -132.51, 0, afrl.cmasi.AltitudeType.MSL);
         loiterAction.setLocation(location);
         
         //Adding the loiter action to the vehicle action list
         o.getVehicleActionList().add(loiterAction);
         
         //Sending the Vehicle Action Command message to AMASE to be interpreted
         out.write(avtas.lmcp.LMCPFactory.packMessage(o, true));
    }

    /**
    * Reads in messages being sent out by the AMASE Server
    */
    public void readMessages(InputStream in) throws Exception {
        //Use each of the if statements to use the incoming message
        LMCPObject o = LMCPFactory.getObject(in);
        System.out.println(o.getLMCPTypeName());
        if (o instanceof afrl.cmasi.AbstractGeometry) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.KeyValuePair) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.Location3D) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.PayloadAction) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.PayloadConfiguration) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.PayloadState) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.VehicleAction) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.Task) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.SearchTask) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.AbstractZone) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.EntityConfiguration) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.FlightProfile) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.AirVehicleConfiguration) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.EntityState) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.AirVehicleState) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.Wedge) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.AreaSearchTask) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.CameraAction) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.CameraConfiguration) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.GimballedPayloadState) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.CameraState) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.Circle) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.GimbalAngleAction) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.GimbalConfiguration) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.GimbalScanAction) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.GimbalStareAction) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.GimbalState) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.GoToWaypointAction) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.KeepInZone) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.KeepOutZone) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.LineSearchTask) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.NavigationAction) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.LoiterAction) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.LoiterTask) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.Waypoint) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.MissionCommand) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.MustFlyTask) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.OperatorSignal) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.OperatingRegion) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.AutomationRequest) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.PointSearchTask) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.Polygon) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.Rectangle) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.RemoveTasks) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.ServiceStatus) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.SessionStatus) {
            //Example of using an incoming LMCP message
            scenarioTime = ((SessionStatus) o).getScenarioTime();
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.VehicleActionCommand) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.VideoStreamAction) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.VideoStreamConfiguration) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.VideoStreamState) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.AutomationResponse) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.RemoveZones) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.RemoveEntities) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.FlightDirectorAction) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.WeatherReport) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.FollowPathCommand) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.PathWaypoint) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.StopMovementAction) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.WaypointTransfer) {
            System.out.println(o.toString());
        } else if (o instanceof afrl.cmasi.PayloadStowAction) {
            System.out.println(o.toString());
        } else {
            //Don't do anything if the message isn't for AMASE
            System.out.println("Could not read byte");
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
                Logger.getLogger(AMASEJavaClient.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return connect(host, port);
        }
        System.out.println("Connected to " + host + ":" + port);
        return socket;
    }

    public static void main(String[] args) {
        new AMASEJavaClient().start();
    }
}
