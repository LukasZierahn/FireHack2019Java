import afrl.cmasi.*;

import afrl.cmasi.searchai.HazardSensorState;
import afrl.cmasi.searchai.HazardZoneDetection;
import mil.afrl.amase.util.CmasiNavUtils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class FireMap {

    private Main main;

    private int width, height;

    private long data[];
    private boolean dataFire[];

    private Location3D center;

    public Waypoint waypointCenter;
    public List<Waypoint> route = new ArrayList<>();
    public long firstWaypointID;

    private boolean firstFixedWing = true;


    public FireMap(Main main, KeepInZone msg) {
        this.main = main;
        center = ((Rectangle) msg.getBoundary()).getCenterPoint();
        width = Math.round(((Rectangle) msg.getBoundary()).getWidth());
        height = Math.round(((Rectangle) msg.getBoundary()).getHeight());

        //data = new long[width * height];
        //dataFire = new boolean[width * height];

        waypointCenter = new Waypoint();
        waypointCenter.setLatitude(center.getLatitude());
        waypointCenter.setLongitude(center.getLongitude());
        waypointCenter.setAltitude(700);
        waypointCenter.setAltitudeType(AltitudeType.MSL);
        //Setting unique ID for the waypoint
        waypointCenter.setNumber(main.getNextWaypointID());
        //Setting speed to reach the waypoint
        waypointCenter.setSpeed(30);
        waypointCenter.setTurnType(TurnType.TurnShort);
        waypointCenter.setNextWaypoint(0);


        for (int i = 0; i < 4; i++) {
            Waypoint nextWaypoint = new Waypoint();
            Location3D location = CoordToLocation(new Point2D.Float(  1 + (i % 2) * 5000, ((i / 2) + 1) * 5000));
            nextWaypoint.setLatitude(location.getLatitude());
            nextWaypoint.setLongitude(location.getLongitude());
            nextWaypoint.setAltitude(700);
            nextWaypoint.setAltitudeType(AltitudeType.MSL);
            //Setting unique ID for the waypoint
            nextWaypoint.setNumber(main.getNextWaypointID());
            //Setting speed to reach the waypoint
            nextWaypoint.setSpeed(30);
            nextWaypoint.setTurnType(TurnType.TurnShort);

            if (route.size() != 0) {
                nextWaypoint.setNextWaypoint(route.get(route.size() - 1).getNumber());
            }
            route.add(nextWaypoint);
        }

        route.get(route.size() - 1).setNextWaypoint(route.get(0).getNumber());

        System.out.printf("Created map with %d/%d\n", width, height);
    }

    public void HandleAirVehicleState(AirVehicleState msg) {
        if (((HazardSensorState)msg.getPayloadStateList().get(2)).getCenterpoint() != null) {
            Point2D coords = Location3DToCoord(((HazardSensorState) msg.getPayloadStateList().get(2)).getCenterpoint());
            setPoint(coords, false);
        }
    }

    public void HandleHazardZoneDetection(HazardZoneDetection msg) {
        Point2D coords = Location3DToCoord(msg.getDetectedLocation());
        setPoint(coords, true);
    }

    public void getTask(UAV uav) {
        if (uav.fixedWing) {
            if (firstFixedWing) {
                uav.MoveToPoint(route.get(0));
                firstFixedWing = false;
            } else {
                uav.MoveToPoint(route.get(0));
            }
        } else {
            uav.currentTask = UAVTASKS.STANDBY;
            uav.standbyPoint = center;
        }

    }

    public void setPoint(Point2D point, boolean onFire) {
        return;
        /*if (!onFire && 20000 < main.getTime() - data[toIntExact(round(point.getY()* width + point.getX()))]) {
            return;
        }*/

        //data[toIntExact(round(point.getY()* width + point.getX()))] = main.getTime();
        //dataFire[toIntExact(round(point.getY() * width + point.getX()))] = onFire;
    }

    public Location3D CoordToLocation(Point2D pos) {
        double angle = (atan2((height / 2.0f) - pos.getY(), (width / 2.0f) - pos.getX()) + 0.5 * PI)* 360 / (2*PI);
        double distance = sqrt(pow((width / 2.0f) - pos.getX(), 2) + pow((height / 2.0f) - pos.getY(), 2));

        if (angle < 0) {
            angle += 360;
        }

        return CmasiNavUtils.getPoint(center, distance , angle);
    }

    public Point2D Location3DToCoord(Location3D location) {
        double angle = (CmasiNavUtils.headingBetween(center, location) * 2 * PI / 360.0f) + 0.5 * PI;
        double distance = CmasiNavUtils.distance(center, location);

        return new Point2D.Double(cos(angle) * distance + (width / 2.0f), sin(angle) * distance + (height / 2.0f));
    }
}
