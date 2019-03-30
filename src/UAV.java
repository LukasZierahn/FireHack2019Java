import afrl.cmasi.*;


public class UAV {

    public AirVehicleState airVehicleState;
    public AirVehicleConfiguration airVehicleConfiguration;

    public FireZoneController fireZoneController = null;
    public UAVTASKS currentTask = UAVTASKS.NO_TASK;
    protected boolean sawFire = false;
    protected long lastSwitch = 0;

    protected Main main;

    public boolean fixedWing;
    protected float targetSpeed;
    protected float targetHeight = 700;

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
        if (currentTask == UAVTASKS.FOLLOW_EDGE_CLOCKWISE || currentTask == UAVTASKS.FOLLOW_EDGE_COUNTER_CLOCKWISE) {
            UpdateFollow();
        } else if (currentTask == UAVTASKS.FLYTHROUGH) {
            if (!sawFire) {
                currentTask = UAVTASKS.NO_TASK;
                //TODO:Ask the fire zone controller to give us a new job
            }
        }
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

        ResetCamera();

        Waypoint waypoint1 = new Waypoint();
        waypoint1.setLatitude(target.getLatitude());
        waypoint1.setLongitude(target.getLongitude());
        waypoint1.setAltitude(targetHeight);
        waypoint1.setAltitudeType(AltitudeType.MSL);
        //Setting unique ID for the waypoint
        waypoint1.setNumber(1);
        //Setting speed to reach the waypoint
        waypoint1.setSpeed(targetSpeed);
        waypoint1.setTurnType(TurnType.TurnShort);

        MissionCommand o = new MissionCommand();
        o.setVehicleID(airVehicleState.getID());
        o.setCommandID(main.getNextCommandID());
        o.getWaypointList().add(waypoint1);

        try {
            main.getOut().write(avtas.lmcp.LMCPFactory.packMessage(o, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
