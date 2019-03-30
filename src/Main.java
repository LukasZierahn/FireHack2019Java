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
import afrl.cmasi.searchai.HazardZoneDetection;
import afrl.cmasi.searchai.HazardZoneEstimateReport;
import avtas.lmcp.LMCPFactory;
import avtas.lmcp.LMCPObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
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

    private FireMap fireMap = null;

    private Map<Long, UAV> UAVMap = new HashMap<>();

    private InputStream in;
    private OutputStream out;


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
                    e.printStackTrace();
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(HazardDetectionJavaClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public void readMessages() throws Exception {

        LMCPObject o = LMCPFactory.getObject(in);
        if (o instanceof KeepInZone) {
            fireMap = new FireMap(this, (KeepInZone) o);
        } else if (o instanceof AirVehicleState) {
            AirVehicleState msg = (AirVehicleState) o;
            UAVMap.get(msg.getID()).airVehicleState = msg;
            fireMap.HandleAirVehicleState(msg);
        } else if (o instanceof AirVehicleConfiguration) {
            UAVMap.put(((AirVehicleConfiguration) o).getID(), new UAV((AirVehicleConfiguration)o));
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
