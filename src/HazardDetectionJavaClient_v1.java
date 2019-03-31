// ===============================================================================
// Authors: Jacob Allex-Buckner
// Organization: University of Dayton Research Institute Applied Sensing Division
//
// Copyright (c) 2018 Government of the United State of America, as represented by
// the Secretary of the Air Force.  No copyright is claimed in the United States under
// Title 17, U.S. Code.  All Other Rights Reserved.
// ===============================================================================

// This file was auto-created by LmcpGen. Modifications will be overwritten.

import afrl.cmasi.searchai.HazardZoneDetection;
import afrl.cmasi.searchai.HazardZoneEstimateReport;
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
import afrl.cmasi.Polygon;
import afrl.cmasi.Circle;
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
import afrl.cmasi.*;

import java.util.Random;
import static java.lang.Math.*;

/**
 * Connects to the simulation and sends a fake mission command to every UAV that is requested in the plan request.
 */
public class HazardDetectionJavaClient_v1 extends Thread {

    /** simulation TCP port to connect to */
    private static int port = 5555;
    /** address of the server */
    private static String host = "localhost";
    /** scenario clock time */
    private static long scenarioTime = 0;

    private int width, height;
    public double search_area_width;

    /**Array of booleans indicating if loiter command has been sent to each UAV */
    boolean[] uavsLoiter = new boolean[4];
    Polygon estimatedHazardZone = new Polygon();
    boolean missionCommandSent = false;



    //Polygon estimatedHazardZone = new Polygon();

    public HazardDetectionJavaClient_v1() {
    }

    @Override
    public void run() {
        try {
            // connect to the server
            Socket socket = connect(host, port);
            //Boolean missionCommandSent = false;

            while(true) {
                //Continually read the LMCP messages that AMASE is sending out
                //readMessages(socket.getInputStream(), socket.getOutputStream());
                readMessages(socket.getInputStream(),socket.getOutputStream());
                if(scenarioTime >= 15000 && missionCommandSent == false){
                  //Send a message to change the 1st entity's waypoints
                  sendMissionCommand(socket.getOutputStream());
                  sendMissionCommand_2(socket.getOutputStream());
                  sendMissionCommand_3(socket.getOutputStream());
                  sendMissionCommand_4(socket.getOutputStream());
                  missionCommandSent = true;
                }


            }

        } catch (Exception ex) {
            Logger.getLogger(HazardDetectionJavaClient_v1.class.getName()).log(Level.SEVERE, null, ex);
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
         o.setVehicleID(5);
         o.setStatus(CommandStatusType.Pending);
         //Setting a unique mission command ID
         o.setCommandID(1);


         //Creating the list of waypoints to be sent with the mission command
         ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
         //Creating the first waypoint
         //Note: all the following attributes must be set to avoid issues
         /*Location3D uav_lat = new Location3D();
         //uav_lat.getLatitude();
         double new_lat = uav_lat.getLatitude();
         System.out.print(new_lat);

         EntityState uav_lat_s = new EntityState();
         Location3D new_lat_s = uav_lat_s.getLocation();
         System.out.print(new_lat_s);*/

         Waypoint waypoint1 = new Waypoint();

         //Setting 3D coordinates
         waypoint1.setLatitude(53.3317);
         //waypoint1.setLatitude(new_lat);
         waypoint1.setLongitude(-1.52);
         waypoint1.setAltitude(700);
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
         //Generate random value

         Waypoint waypoint2 = new Waypoint();
         waypoint2.setLatitude(53.56);
         waypoint2.setLongitude(-1.60);
         waypoint2.setAltitude(700);
         waypoint2.setAltitudeType(AltitudeType.MSL);
         waypoint2.setNumber(2);
         waypoint2.setNextWaypoint(3);
         waypoint2.setSpeed(30);
         waypoint2.setSpeedType(SpeedType.Airspeed);
         waypoint2.setClimbRate(0);
         waypoint2.setTurnType(TurnType.TurnShort);
         waypoint2.setContingencyWaypointA(0);
         waypoint2.setContingencyWaypointB(0);

         Waypoint waypoint3 = new Waypoint();
         waypoint3.setLatitude(53.45);
         waypoint3.setLongitude(-1.65);
         waypoint3.setAltitude(700);
         waypoint3.setAltitudeType(AltitudeType.MSL);
         waypoint3.setNumber(3);
         waypoint3.setNextWaypoint(4);
         waypoint3.setSpeed(30);
         waypoint3.setSpeedType(SpeedType.Airspeed);
         waypoint3.setClimbRate(0);
         waypoint3.setTurnType(TurnType.TurnShort);
         waypoint3.setContingencyWaypointA(0);
         waypoint3.setContingencyWaypointB(0);

         Waypoint waypoint4 = new Waypoint();
         waypoint4.setLatitude(53.3917);
         waypoint4.setLongitude(-1.60);
         waypoint4.setAltitude(700);
         waypoint4.setAltitudeType(AltitudeType.MSL);
         waypoint4.setNumber(4);
         waypoint4.setNextWaypoint(5);
         waypoint4.setSpeed(30);
         waypoint4.setSpeedType(SpeedType.Airspeed);
         waypoint4.setClimbRate(0);
         waypoint4.setTurnType(TurnType.TurnShort);
         waypoint4.setContingencyWaypointA(0);
         waypoint4.setContingencyWaypointB(0);

         Waypoint waypoint5 = new Waypoint();
         waypoint5.setLatitude(53.3317);
         waypoint5.setLongitude(-1.60);
         waypoint5.setAltitude(700);
         waypoint5.setAltitudeType(AltitudeType.MSL);
         waypoint5.setNumber(5);
         waypoint5.setNextWaypoint(6);
         waypoint5.setSpeed(30);
         waypoint5.setSpeedType(SpeedType.Airspeed);
         waypoint5.setClimbRate(0);
         waypoint5.setTurnType(TurnType.TurnShort);
         waypoint5.setContingencyWaypointA(0);
         waypoint5.setContingencyWaypointB(0);

         Waypoint waypoint6 = new Waypoint();
         waypoint6.setLatitude(53.3317);
         waypoint6.setLongitude(-1.60);
         waypoint6.setAltitude(700);
         waypoint6.setAltitudeType(AltitudeType.MSL);
         waypoint6.setNumber(6);
         waypoint6.setNextWaypoint(1);
         waypoint6.setSpeed(30);
         waypoint6.setSpeedType(SpeedType.Airspeed);
         waypoint6.setClimbRate(0);
         waypoint6.setTurnType(TurnType.TurnShort);
         waypoint6.setContingencyWaypointA(0);
         waypoint6.setContingencyWaypointB(0);



         //Adding the waypoints to the waypoint list
         waypoints.add(waypoint1);
         waypoints.add(waypoint2);
         waypoints.add(waypoint3);
         waypoints.add(waypoint4);
         waypoints.add(waypoint5);
         waypoints.add(waypoint6);

         //Setting the waypoint list in the mission command
         o.getWaypointList().addAll(waypoints);

         //Sending the Mission Command message to AMASE to be interpreted
         out.write(avtas.lmcp.LMCPFactory.packMessage(o, true));
    }

    public void sendMissionCommand_2(OutputStream out) throws Exception {
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
         waypoint1.setLatitude(53.3317);
         waypoint1.setLongitude(-1.75);
         waypoint1.setAltitude(700);
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
         //Generate random value

         Waypoint waypoint2 = new Waypoint();
         waypoint2.setLatitude(53.50);
         waypoint2.setLongitude(-1.65);
         waypoint2.setAltitude(700);
         waypoint2.setAltitudeType(AltitudeType.MSL);
         waypoint2.setNumber(2);
         waypoint2.setNextWaypoint(3);
         waypoint2.setSpeed(30);
         waypoint2.setSpeedType(SpeedType.Airspeed);
         waypoint2.setClimbRate(0);
         waypoint2.setTurnType(TurnType.TurnShort);
         waypoint2.setContingencyWaypointA(0);
         waypoint2.setContingencyWaypointB(0);

         Waypoint waypoint3 = new Waypoint();
         waypoint3.setLatitude(53.43);
         waypoint3.setLongitude(-1.75);
         waypoint3.setAltitude(700);
         waypoint3.setAltitudeType(AltitudeType.MSL);
         waypoint3.setNumber(3);
         waypoint3.setNextWaypoint(4);
         waypoint3.setSpeed(30);
         waypoint3.setSpeedType(SpeedType.Airspeed);
         waypoint3.setClimbRate(0);
         waypoint3.setTurnType(TurnType.TurnShort);
         waypoint3.setContingencyWaypointA(0);
         waypoint3.setContingencyWaypointB(0);

         Waypoint waypoint4 = new Waypoint();
         waypoint4.setLatitude(53.57);
         waypoint4.setLongitude(-1.65);
         waypoint4.setAltitude(700);
         waypoint4.setAltitudeType(AltitudeType.MSL);
         waypoint4.setNumber(4);
         waypoint4.setNextWaypoint(5);
         waypoint4.setSpeed(30);
         waypoint4.setSpeedType(SpeedType.Airspeed);
         waypoint4.setClimbRate(0);
         waypoint4.setTurnType(TurnType.TurnShort);
         waypoint4.setContingencyWaypointA(0);
         waypoint4.setContingencyWaypointB(0);

         Waypoint waypoint5 = new Waypoint();
         waypoint5.setLatitude(53.3317);
         waypoint5.setLongitude(-1.65);
         waypoint5.setAltitude(700);
         waypoint5.setAltitudeType(AltitudeType.MSL);
         waypoint5.setNumber(5);
         waypoint5.setNextWaypoint(6);
         waypoint5.setSpeed(30);
         waypoint5.setSpeedType(SpeedType.Airspeed);
         waypoint5.setClimbRate(0);
         waypoint5.setTurnType(TurnType.TurnShort);
         waypoint5.setContingencyWaypointA(0);
         waypoint5.setContingencyWaypointB(0);

         Waypoint waypoint6 = new Waypoint();
         waypoint6.setLatitude(53.3317);
         waypoint6.setLongitude(-1.75);
         waypoint6.setAltitude(700);
         waypoint6.setAltitudeType(AltitudeType.MSL);
         waypoint6.setNumber(6);
         waypoint6.setNextWaypoint(1);
         waypoint6.setSpeed(30);
         waypoint6.setSpeedType(SpeedType.Airspeed);
         waypoint6.setClimbRate(0);
         waypoint6.setTurnType(TurnType.TurnShort);
         waypoint6.setContingencyWaypointA(0);
         waypoint6.setContingencyWaypointB(0);



         //Adding the waypoints to the waypoint list
         waypoints.add(waypoint1);
         waypoints.add(waypoint2);
         waypoints.add(waypoint3);
         waypoints.add(waypoint4);
         waypoints.add(waypoint5);
         waypoints.add(waypoint6);
         //Setting the waypoint list in the mission command
         o.getWaypointList().addAll(waypoints);

         //Sending the Mission Command message to AMASE to be interpreted
         out.write(avtas.lmcp.LMCPFactory.packMessage(o, true));
    }

    public void sendMissionCommand_3(OutputStream out) throws Exception {
        //Setting up the mission to send to the UAV
         MissionCommand o = new MissionCommand();
         o.setFirstWaypoint(1);
         //Setting the UAV to recieve the mission
         o.setVehicleID(4);
         o.setStatus(CommandStatusType.Pending);
         //Setting a unique mission command ID
         o.setCommandID(1);
         //Creating the list of waypoints to be sent with the mission command
         ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
         //Creating the first waypoint
         //Note: all the following attributes must be set to avoid issues
         Waypoint waypoint1 = new Waypoint();
         //Setting 3D coordinates
         waypoint1.setLatitude(53.3317);
         waypoint1.setLongitude(-1.85);
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
         //Generate random value

         Waypoint waypoint2 = new Waypoint();
         waypoint2.setLatitude(53.3617);
         waypoint2.setLongitude(-1.70);
         waypoint2.setAltitude(700);
         waypoint2.setAltitudeType(AltitudeType.MSL);
         waypoint2.setNumber(2);
         waypoint2.setNextWaypoint(3);
         waypoint2.setSpeed(30);
         waypoint2.setSpeedType(SpeedType.Airspeed);
         waypoint2.setClimbRate(0);
         waypoint2.setTurnType(TurnType.TurnShort);
         waypoint2.setContingencyWaypointA(0);
         waypoint2.setContingencyWaypointB(0);

         Waypoint waypoint3 = new Waypoint();
         waypoint3.setLatitude(53.45);
         waypoint3.setLongitude(-1.85);
         waypoint3.setAltitude(700);
         waypoint3.setAltitudeType(AltitudeType.MSL);
         waypoint3.setNumber(3);
         waypoint3.setNextWaypoint(4);
         waypoint3.setSpeed(30);
         waypoint3.setSpeedType(SpeedType.Airspeed);
         waypoint3.setClimbRate(0);
         waypoint3.setTurnType(TurnType.TurnShort);
         waypoint3.setContingencyWaypointA(0);
         waypoint3.setContingencyWaypointB(0);

         Waypoint waypoint4 = new Waypoint();
         waypoint4.setLatitude(53.55);
         waypoint4.setLongitude(-1.71);
         waypoint4.setAltitude(700);
         waypoint4.setAltitudeType(AltitudeType.MSL);
         waypoint4.setNumber(4);
         waypoint4.setNextWaypoint(5);
         waypoint4.setSpeed(30);
         waypoint4.setSpeedType(SpeedType.Airspeed);
         waypoint4.setClimbRate(0);
         waypoint4.setTurnType(TurnType.TurnShort);
         waypoint4.setContingencyWaypointA(0);
         waypoint4.setContingencyWaypointB(0);

         Waypoint waypoint5 = new Waypoint();
         waypoint5.setLatitude(53.3317);
         waypoint5.setLongitude(-1.85);
         waypoint5.setAltitude(700);
         waypoint5.setAltitudeType(AltitudeType.MSL);
         waypoint5.setNumber(5);
         waypoint5.setNextWaypoint(6);
         waypoint5.setSpeed(30);
         waypoint5.setSpeedType(SpeedType.Airspeed);
         waypoint5.setClimbRate(0);
         waypoint5.setTurnType(TurnType.TurnShort);
         waypoint5.setContingencyWaypointA(0);
         waypoint5.setContingencyWaypointB(0);

         Waypoint waypoint6 = new Waypoint();
         waypoint6.setLatitude(53.3317);
         waypoint6.setLongitude(-1.85);
         waypoint6.setAltitude(700);
         waypoint6.setAltitudeType(AltitudeType.MSL);
         waypoint6.setNumber(6);
         waypoint6.setNextWaypoint(1);
         waypoint6.setSpeed(30);
         waypoint6.setSpeedType(SpeedType.Airspeed);
         waypoint6.setClimbRate(0);
         waypoint6.setTurnType(TurnType.TurnShort);
         waypoint6.setContingencyWaypointA(0);
         waypoint6.setContingencyWaypointB(0);



         //Adding the waypoints to the waypoint list
         waypoints.add(waypoint1);
         waypoints.add(waypoint2);
         waypoints.add(waypoint3);
         waypoints.add(waypoint4);
         waypoints.add(waypoint5);
         waypoints.add(waypoint6);
         //Setting the waypoint list in the mission command
         o.getWaypointList().addAll(waypoints);

         //Sending the Mission Command message to AMASE to be interpreted
         out.write(avtas.lmcp.LMCPFactory.packMessage(o, true));
    }

    public void sendMissionCommand_4(OutputStream out) throws Exception {
        //Setting up the mission to send to the UAV
         MissionCommand o = new MissionCommand();
         o.setFirstWaypoint(1);
         //Setting the UAV to recieve the mission
         o.setVehicleID(6);
         o.setStatus(CommandStatusType.Pending);
         //Setting a unique mission command ID
         o.setCommandID(1);
         //Creating the list of waypoints to be sent with the mission command
         ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
         //Creating the first waypoint
         //Note: all the following attributes must be set to avoid issues
         Waypoint waypoint1 = new Waypoint();
         //Setting 3D coordinates
         waypoint1.setLatitude(53.3317);
         waypoint1.setLongitude(-1.95);
         waypoint1.setAltitude(700);
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
         //Generate random value

         Waypoint waypoint2 = new Waypoint();
         waypoint2.setLatitude(53.3517);
         waypoint2.setLongitude(-1.85);
         waypoint2.setAltitude(700);
         waypoint2.setAltitudeType(AltitudeType.MSL);
         waypoint2.setNumber(2);
         waypoint2.setNextWaypoint(3);
         waypoint2.setSpeed(30);
         waypoint2.setSpeedType(SpeedType.Airspeed);
         waypoint2.setClimbRate(0);
         waypoint2.setTurnType(TurnType.TurnShort);
         waypoint2.setContingencyWaypointA(0);
         waypoint2.setContingencyWaypointB(0);

         Waypoint waypoint3 = new Waypoint();
         waypoint3.setLatitude(53.50);
         waypoint3.setLongitude(-1.95);
         waypoint3.setAltitude(700);
         waypoint3.setAltitudeType(AltitudeType.MSL);
         waypoint3.setNumber(3);
         waypoint3.setNextWaypoint(4);
         waypoint3.setSpeed(30);
         waypoint3.setSpeedType(SpeedType.Airspeed);
         waypoint3.setClimbRate(0);
         waypoint3.setTurnType(TurnType.TurnShort);
         waypoint3.setContingencyWaypointA(0);
         waypoint3.setContingencyWaypointB(0);

         Waypoint waypoint4 = new Waypoint();
         waypoint4.setLatitude(53.54);
         waypoint4.setLongitude(-1.78);
         waypoint4.setAltitude(700);
         waypoint4.setAltitudeType(AltitudeType.MSL);
         waypoint4.setNumber(4);
         waypoint4.setNextWaypoint(5);
         waypoint4.setSpeed(30);
         waypoint4.setSpeedType(SpeedType.Airspeed);
         waypoint4.setClimbRate(0);
         waypoint4.setTurnType(TurnType.TurnShort);
         waypoint4.setContingencyWaypointA(0);
         waypoint4.setContingencyWaypointB(0);

         Waypoint waypoint5 = new Waypoint();
         waypoint5.setLatitude(53.3317);
         waypoint5.setLongitude(-1.95);
         waypoint5.setAltitude(700);
         waypoint5.setAltitudeType(AltitudeType.MSL);
         waypoint5.setNumber(5);
         waypoint5.setNextWaypoint(6);
         waypoint5.setSpeed(30);
         waypoint5.setSpeedType(SpeedType.Airspeed);
         waypoint5.setClimbRate(0);
         waypoint5.setTurnType(TurnType.TurnShort);
         waypoint5.setContingencyWaypointA(0);
         waypoint5.setContingencyWaypointB(0);

         Waypoint waypoint6 = new Waypoint();
         waypoint6.setLatitude(53.3317);
         waypoint6.setLongitude(-1.95);
         waypoint6.setAltitude(700);
         waypoint6.setAltitudeType(AltitudeType.MSL);
         waypoint6.setNumber(6);
         waypoint6.setNextWaypoint(1);
         waypoint6.setSpeed(30);
         waypoint6.setSpeedType(SpeedType.Airspeed);
         waypoint6.setClimbRate(0);
         waypoint6.setTurnType(TurnType.TurnShort);
         waypoint6.setContingencyWaypointA(0);
         waypoint6.setContingencyWaypointB(0);

          //Adding the waypoints to the waypoint list
          waypoints.add(waypoint1);
          waypoints.add(waypoint2);
          waypoints.add(waypoint3);
          waypoints.add(waypoint4);
          waypoints.add(waypoint5);
          waypoints.add(waypoint6);

         //Setting the waypoint list in the mission command
         o.getWaypointList().addAll(waypoints);

         //Sending the Mission Command message to AMASE to be interpreted
         out.write(avtas.lmcp.LMCPFactory.packMessage(o, true));
    }

    /**
     * Sends loiter command to the AMASE Server
     * @param out
     * @throws Exception
     */
    public void sendLoiterCommand (OutputStream out, long vehicleId , Location3D location) throws Exception {
        //Setting up the mission to send to the UAV
         VehicleActionCommand o = new VehicleActionCommand();
         o.setVehicleID(vehicleId);
         o.setStatus(CommandStatusType.Pending);
         o.setCommandID(1);

         //Setting up the loiter action
         LoiterAction loiterAction = new LoiterAction();
         loiterAction.setLoiterType(LoiterType.Circular);
         loiterAction.setRadius(250);
         loiterAction.setAxis(0);
         loiterAction.setLength(0);
         loiterAction.setDirection(LoiterDirection.Clockwise);
         loiterAction.setDuration(100000);
         loiterAction.setAirspeed(15);

         //Creating a 3D location object for the stare point
         loiterAction.setLocation(location);

         //Adding the loiter action to the vehicle action list
         o.getVehicleActionList().add(loiterAction);

         //Sending the Vehicle Action Command message to AMASE to be interpreted
         out.write(avtas.lmcp.LMCPFactory.packMessage(o, true));
    }

    /**
     * Sends loiter command to the AMASE Server
     * @param out
     * @throws Exception
     */
    public void sendEstimateReport(OutputStream out, Polygon estimatedShape) throws Exception {
        //Setting up the mission to send to the UAV
        HazardZoneEstimateReport o = new HazardZoneEstimateReport();
        o.setEstimatedZoneShape(estimatedShape);
        o.setUniqueTrackingID(1);
        o.setEstimatedGrowthRate(0);
        o.setPerceivedZoneType(afrl.cmasi.searchai.HazardType.Fire);
        o.setEstimatedZoneDirection(0);
        o.setEstimatedZoneSpeed(0);


        //Sending the Vehicle Action Command message to AMASE to be interpreted
        out.write(avtas.lmcp.LMCPFactory.packMessage(o, true));
    }

    /**
    * Reads in messages being sent out by the AMASE Server
    */
    public void readMessages(InputStream in, OutputStream out) throws Exception {
        //Use each of the if statements to use the incoming message
        LMCPObject o = LMCPFactory.getObject(in);
        //System.out.println(o.getLMCPTypeName());
      /*  if (o instanceof afrl.cmasi.AbstractGeometry) {
            System.out.println(o.toString());
        } else*/
        if (o instanceof afrl.cmasi.searchai.HazardZoneDetection){
          HazardZoneDetection hazardDetected = ((HazardZoneDetection) o);
          //Get location where zone first detected
          Location3D detectedLocation = hazardDetected.getDetectedLocation();
          //Get entity that detected the zone
          int detectingEntity = (int) hazardDetected.getDetectingEnitiyID();

          //Check if hint
          /*if (detectingEntity == 0) {
              //Do nothing for now, hints will be added later
              return;
            }
            //Check if the UAV has already been sent the loiter command and proceed if it hasn't
            if (uavsLoiter[detectingEntity - 1] == false) {

              //Send the loiter command
              sendLoiterCommand(out, detectingEntity, detectedLocation);

              //Note: Polygon points must be in clockwise or counter-clockwise order to get a shape without intersections
              estimatedHazardZone.getBoundaryPoints().add(detectedLocation);

              //Send out the estimation report to draw the polygon
              sendEstimateReport(out, estimatedHazardZone);

              uavsLoiter[detectingEntity - 1] = true;
              System.out.println("UAV" + detectingEntity + " detected hazard at " + detectedLocation.getLatitude() +
                      "," + detectedLocation.getLongitude() + ". Sending loiter command.");
            }*/



        } else if(o instanceof afrl.cmasi.SessionStatus) {
            //Example of using an incoming LMCP message
            scenarioTime = ((SessionStatus) o).getScenarioTime();
          //  System.out.println(o.toString());

       //} else if (o instanceof afrl.cmasi.EntityState) {
        //    System.out.println(o.toString());
            //System.out.println(o.getLatitude());

        //} else {
            //Don't do anything if the message isn't for AMASE
        //    System.out.println("Could not read byte");
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
                Logger.getLogger(HazardDetectionJavaClient_v1.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return connect(host, port);
        }
        System.out.println("Connected to " + host + ":" + port);
        return socket;
    }

    public static void main(String[] args) {
        new HazardDetectionJavaClient_v1().start();
    }
}
