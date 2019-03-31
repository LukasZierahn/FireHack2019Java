import afrl.cmasi.Location3D;
import afrl.cmasi.Waypoint;

import java.util.ArrayList;
import java.util.List;

public class RouteManager {

    public Location3D StartPoint;

    public List<Route> routes = new ArrayList<>();
    private static int lastWaypointID = 0;

    public RouteManager() {

    }
    
    public void AddRoute(Route route) {
        
    }
    
    public static int getNextWaypointId(){
        return ++lastWaypointID;
    }
}
