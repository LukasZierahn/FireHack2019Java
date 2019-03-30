import afrl.cmasi.*;
import java.util.ArrayList;

import java.util.List;


public class UAV {

    public AirVehicleState airVehicleState;
    public AirVehicleConfiguration airVehicleConfiguration;

    public FireZoneController fireZoneController = null;
    public UAVTASKS currentTask = UAVTASKS.NO_TASK;
    protected boolean sawFire = false;
    protected long lastSwitch = 0;
    public Location3D standbyPoint;

    protected Main main;

    public boolean fixedWing;
    protected float targetSpeed;
    protected float targetHeight = 700;
    private boolean once = true;
    
    protected FuelState fuelState = new FuelState();
    protected ArrayList<Location3D> refuelPoints = new ArrayList<Location3D>();

    public UAV(Main main, AirVehicleConfiguration airVehicleConfiguration) {
        this.main = main;
        this.airVehicleConfiguration = airVehicleConfiguration;

        switch (airVehicleConfiguration.getEntityType()) {
            case "":
                fixedWing = false;
                break;
            case "Multi":
                fixedWing = false;
                targetSpeed = 20;
                break;
            case "FixedWing":
                fixedWing = true;
                targetSpeed = 30;
                break;
            default:
                System.out.println("Unexpected entity type: " + airVehicleConfiguration.getEntityType());
                break;
        }

        System.out.printf("Added UAV:\n\tID: %d\n", airVehicleConfiguration.getID());
    }

    public void Update() {


        if(UpdateFuel()) {
            if(refuelPoints.isEmpty()) {
                System.out.println("WARNING - Drone needs fuel but has no refuel position");
            } else {
                if(currentTask != UAVTASKS.REFUEL) return;
                currentTask = UAVTASKS.REFUEL;
                InitRefuelMission();
            }
        } else {
            if(currentTask == UAVTASKS.REFUEL) {
                currentTask = null;
            }
        }
        
        //Add initial position as refuel point
        if(refuelPoints.isEmpty()) refuelPoints.add(airVehicleState.getLocation());
        
        if (currentTask == UAVTASKS.FOLLOW_EDGE_CLOCKWISE || currentTask == UAVTASKS.FOLLOW_EDGE_COUNTER_CLOCKWISE) {
            UpdateFollow();
        } else if (currentTask == UAVTASKS.FLYTHROUGH) {
            if (!sawFire) {
                currentTask = UAVTASKS.NO_TASK;
                //TODO:Ask the fire zone controller to give us a new job
            }
        } else if (currentTask == UAVTASKS.STANDBY) {
            if (!airVehicleState.getLocation().equals(standbyPoint)) {
                MoveToPoint(main.getFireMap().waypointCenter);
            }
        } else if (currentTask == UAVTASKS.NO_TASK) {
            if (fireZoneController == null) {
                main.getFireMap().getTask(this);
            }
        }
    }

    public void Patrol(List<Waypoint> route, long StartID) {
        System.out.println(airVehicleState.getID());
        currentTask = UAVTASKS.PATROL;
        MissionCommand o = new MissionCommand();
        o.setVehicleID(airVehicleState.getID());
        o.setCommandID(main.getNextCommandID());
        //o.getVehicleActionList().add(new GoToWaypointAction());
        o.getWaypointList().add(route.get(0));

        try {
            main.getOut().write(avtas.lmcp.LMCPFactory.packMessage(o, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void InitRefuelMission() {
        Location3D closest = null;
        double minDist = Double.MAX_VALUE;
        for(Location3D pos : refuelPoints) {
            double dist = FireZoneController.distance(airVehicleState.getLocation(), pos);
            if(dist < minDist) {
                minDist = dist;
                closest = pos;
            }
        }

        Waypoint refuelWaypoint = CreateWaypoint(closest.getLatitude(), closest.getLongitude(), targetHeight, AltitudeType.MSL, main.getNextWaypointID(), targetSpeed, TurnType.TurnShort);
        LoiterType loiterType = fixedWing ? LoiterType.Circular : LoiterType.Hover;
        LoiterAction loiterAction = CreateLoiter(loiterType, 200, 0, 0, LoiterDirection.Clockwise, 1000, targetSpeed, closest);
        MissionCommand o = new MissionCommand();
        o.setVehicleID(airVehicleState.getID());
        o.setCommandID(main.getNextCommandID());
        o.getWaypointList().add(refuelWaypoint);
        o.getVehicleActionList().add(loiterAction);
        
        try {
            main.getOut().write(avtas.lmcp.LMCPFactory.packMessage(o, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Updates the aircraft fuel state with the current AirVehicleState
     * @return True if the aircraft needs refuel, false otherwise
     */
    public boolean UpdateFuel() {
        fuelState.Update(airVehicleState);
        return fuelState.requiresRefuel;
    }

    public void UpdateFollow() {
        boolean clockwise = true;
        if (currentTask == UAVTASKS.FOLLOW_EDGE_COUNTER_CLOCKWISE) {
            clockwise = false;
        }

        FlightDirectorAction msg = new FlightDirectorAction();
        msg.setSpeed(targetSpeed);
        msg.setAltitudeType(AltitudeType.MSL);
        msg.setAltitude(700);
        msg.setClimbRate(0);


        if (fixedWing) {
            final float inFire = 12;
            final float notInFire = 5;

            if (sawFire) {
                if (clockwise) {
                    msg.setHeading(airVehicleState.getHeading() - inFire);
                } else {
                    msg.setHeading(airVehicleState.getHeading() + inFire);
                }
            } else {
                if (clockwise) {
                    msg.setHeading(airVehicleState.getHeading() + notInFire);
                } else {
                    msg.setHeading(airVehicleState.getHeading() - notInFire);
                }
            }

        } else {

            final float flatMove = 5;
            final float scale = (((GimbalState) airVehicleState.getPayloadStateList().get(0)).getAzimuth() - 45) / 45.0f;

            if (sawFire) {
                if (clockwise) {
                    msg.setHeading(airVehicleState.getHeading() + flatMove * scale);
                } else {
                    msg.setHeading(airVehicleState.getHeading() - flatMove * scale);
                }
            } else {
                if (clockwise) {
                    msg.setHeading(airVehicleState.getHeading() - flatMove * scale);
                } else {
                    msg.setHeading(airVehicleState.getHeading() + flatMove * scale);
                }
            }
        }

        MissionCommand o = new MissionCommand();
        o.setVehicleID(airVehicleState.getID());
        o.setCommandID(main.getNextCommandID());
        o.getVehicleActionList().add(msg);

        try {
            main.getOut().write(avtas.lmcp.LMCPFactory.packMessage(o, true));
        } catch (Exception e) {
            e.printStackTrace();
        }



        sawFire = false;

    }

    public void MoveToPoint(Waypoint point) {
        /*MissionCommand o = new MissionCommand();
        o.setVehicleID(airVehicleState.getID());
        o.setCommandID(main.getNextCommandID());
        o.getVehicleActionList().add(new GoToWaypointAction(point.getNumber()));

        try {
            main.getOut().write(avtas.lmcp.LMCPFactory.packMessage(o, true));
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        Location3D location = new Location3D();
        location.setLongitude(point.getLongitude());
        location.setLatitude(point.getLatitude());

        MustFlyTask flyTask = new MustFlyTask();
        flyTask.setPosition(location);
        flyTask.setTaskID(main.getNextCommandID());
        flyTask.getEligibleEntities().add(airVehicleState.getID());

        try {
            main.getOut().write(avtas.lmcp.LMCPFactory.packMessage(flyTask, true));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void ResetCamera() {
        GimbalAngleAction cameraMsg = new GimbalAngleAction();
        cameraMsg.setPayloadID(1);
        cameraMsg.setElevation(-45);

        MissionCommand o = new MissionCommand();
        o.setVehicleID(airVehicleState.getID());
        o.setCommandID(main.getNextCommandID());
        o.getVehicleActionList().add(cameraMsg);

        try {
            main.getOut().write(avtas.lmcp.LMCPFactory.packMessage(o, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void FlyThrough(Location3D target) {
        currentTask = UAVTASKS.FLYTHROUGH;

        System.out.println("calling it");

        ResetCamera();

        Waypoint waypoint1 = new Waypoint();
        waypoint1.setLatitude(target.getLatitude());
        waypoint1.setLongitude(target.getLongitude());
        waypoint1.setAltitude(targetHeight);
        waypoint1.setAltitudeType(AltitudeType.MSL);
        //Setting unique ID for the waypoint
        waypoint1.setNumber(main.getNextWaypointID());
        //Setting speed to reach the waypoint
        waypoint1.setSpeed(targetSpeed);
        waypoint1.setTurnType(TurnType.TurnShort);

        MissionCommand o = new MissionCommand();
        o.setVehicleID(airVehicleState.getID());
        o.setCommandID(main.getNextCommandID());
        o.getWaypointList().add(waypoint1);

        o.getVehicleActionList().add(new GoToWaypointAction());

        try {
            main.getOut().write(avtas.lmcp.LMCPFactory.packMessage(o, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Waypoint CreateWaypoint(double lat, double lon, float altitude, AltitudeType altType, long number, float speed, TurnType turnType) {
        Waypoint waypoint1 = new Waypoint();
        waypoint1.setLatitude(lat);
        waypoint1.setLongitude(lon);
        waypoint1.setAltitude(altitude);
        waypoint1.setAltitudeType(altType);
        //Setting unique ID for the waypoint
        waypoint1.setNumber(number);
        //Setting speed to reach the waypoint
        waypoint1.setSpeed(speed);
        waypoint1.setTurnType(turnType);
        return waypoint1;
    }
    
    private LoiterAction CreateLoiter(LoiterType loiterType, float radius, float axis, int length, LoiterDirection direction, long duration, float speed, Location3D location){
        //Setting up the loiter action
         LoiterAction loiterAction = new LoiterAction();
         loiterAction.setLoiterType(loiterType);
         loiterAction.setRadius(radius);
         loiterAction.setAxis(axis);
         loiterAction.setLength(length);
         loiterAction.setDirection(direction);
         loiterAction.setDuration(duration);
         loiterAction.setAirspeed(speed);
         //Creating a 3D location object for the stare point
         loiterAction.setLocation(location);
         return loiterAction;
    }

    public void FollowEdge(Boolean clockwise) {
        MissionCommand o = new MissionCommand();
        o.setVehicleID(airVehicleState.getID());
        o.setCommandID(main.getNextCommandID());


        if (fixedWing) {
            GimbalAngleAction cameraMsg = new GimbalAngleAction();

            cameraMsg.setPayloadID(1);
            cameraMsg.setElevation(-45);
            if (clockwise) {
                currentTask = UAVTASKS.FOLLOW_EDGE_CLOCKWISE;
                cameraMsg.setAzimuth(90);
            } else {
                currentTask = UAVTASKS.FOLLOW_EDGE_COUNTER_CLOCKWISE;
                cameraMsg.setAzimuth(-90);
            }
            o.getVehicleActionList().add(cameraMsg);
        } else {
            GimbalScanAction cameraMsg = new GimbalScanAction();

            cameraMsg.setStartElevation(-20);
            cameraMsg.setEndElevation(-20);

            if (clockwise) {
                currentTask = UAVTASKS.FOLLOW_EDGE_CLOCKWISE;
                cameraMsg.setStartAzimuth(30);
                cameraMsg.setEndAzimuth(80);
            } else {
                currentTask = UAVTASKS.FOLLOW_EDGE_COUNTER_CLOCKWISE;
                cameraMsg.setStartAzimuth(-30);
                cameraMsg.setEndAzimuth(-80);
            }

            cameraMsg.setAzimuthSlewRate(5);

            o.getVehicleActionList().add(cameraMsg);
        }

        FlightDirectorAction initialMsg = new FlightDirectorAction();

        if (clockwise) {
            initialMsg.setHeading(airVehicleState.getHeading() - 80);
        } else {
            initialMsg.setHeading(airVehicleState.getHeading() + 80);
        }

        initialMsg.setSpeed(targetSpeed);

        initialMsg.setAltitudeType(AltitudeType.MSL);
        initialMsg.setAltitude(700);
        initialMsg.setClimbRate(0);
        o.getVehicleActionList().add(initialMsg);


        try {
            main.getOut().write(avtas.lmcp.LMCPFactory.packMessage(o, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SawFire() {
        if (!sawFire) {
            lastSwitch = main.getTime();
        }
        sawFire = true;
    }
}
