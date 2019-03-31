import afrl.cmasi.AltitudeType;
import afrl.cmasi.Location3D;
import afrl.cmasi.TurnType;
import afrl.cmasi.Waypoint;
import java.awt.geom.Point2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class RouteManager {

    public Location3D StartPoint;

    public List<Route> routes = new ArrayList<>();
    private static int lastWaypointID = 0;
    private FireMap firemap;

    public RouteManager(FireMap firemap) {
        this.firemap = firemap;
        
        routes.add(CreateSquareRoute(0.1f));
        routes.add(CreateSquareRoute(0.15f));
        routes.add(CreateSquareRoute(0.2f));
        routes.add(CreateSquareRoute(0.25f));
        routes.add(CreateSquareRoute(0.3f));
        routes.add(CreateSquareRoute(0.35f));
        routes.add(CreateSquareRoute(0.4f));
    }
    
    private Route CreateSquareRoute(float scale) {
        float inverseScale = 1f - scale;
        List<Location3D> locations = new ArrayList<>();
        locations.add(firemap.CoordToLocation(new Point2D.Float(firemap.width * scale, firemap.height * scale)));
        locations.add(firemap.CoordToLocation(new Point2D.Float(firemap.width * scale, firemap.height * inverseScale)));
        locations.add(firemap.CoordToLocation(new Point2D.Float(firemap.width * inverseScale, firemap.height * inverseScale)));
        locations.add(firemap.CoordToLocation(new Point2D.Float(firemap.width * inverseScale, firemap.height * scale)));
        
        for (Location3D location : locations) {
            setLocationData(location);
        }
        
        return new Route(locations, 30, TurnType.TurnShort);
    }
    
    private void setLocationData(Location3D location) {
        location.setAltitude(700);
        location.setAltitudeType(AltitudeType.AGL);
    }
    
    public void AddRoute(Route route) {
        routes.add(route);
    }
    
    public Route GetNextRouteFor(UAV uav, Main main) {
        Route maxRoute = null;
        long topScore = -999999999;
        for(Route route : routes) {
            long urgency = route.UrgencyScore(main);
            System.out.println("RouteManager.GetNextRouteFor() " + urgency);
            if(urgency > topScore) {
                topScore = urgency;
                maxRoute = route;
            }
        }
        if(maxRoute != null) {
            maxRoute.AssignToDrone(uav, main);
        }
        return maxRoute;
    }
    
    public static int getNextWaypointId(){
        lastWaypointID++;
        return lastWaypointID;
    }
    
    public Route GetRouteWithDrone(UAV uav) {
        for(Route route : routes) {
            if(route.DroneOnRoute(uav)) return route;
        }
        return null;
    }
}
