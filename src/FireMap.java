import afrl.cmasi.AirVehicleState;
import afrl.cmasi.KeepInZone;
import afrl.cmasi.Location3D;
import afrl.cmasi.Rectangle;

import afrl.cmasi.searchai.HazardZoneDetection;
import mil.afrl.amase.util.CmasiNavUtils;

import java.awt.geom.Point2D;

import static java.lang.Math.*;

public class FireMap {

    private Main main = null;

    private int width, height;

    private int data[];

    private Location3D center;


    public FireMap(Main main, KeepInZone msg) {
        this.main = main;
        center = ((Rectangle) msg.getBoundary()).getCenterPoint();
        width = Math.round(((Rectangle) msg.getBoundary()).getWidth());
        height = Math.round(((Rectangle) msg.getBoundary()).getHeight());

        data = new int[width * height];

        System.out.printf("Created map with %d/%d\n", width, height);
    }

    public void HandleAirVehicleState(AirVehicleState msg) {
    }

    public void HandleHazardZoneDetection(HazardZoneDetection msg) {
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
