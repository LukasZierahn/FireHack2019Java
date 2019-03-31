
import afrl.cmasi.AltitudeType;
import afrl.cmasi.Location3D;
import afrl.cmasi.TurnType;
import afrl.cmasi.Waypoint;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Route {
    private List<Waypoint> waypoints = new ArrayList<Waypoint>();
    private long lastAssigned;
    private long lastRelased;
    private List<UAV> activeDrones = new ArrayList<UAV>();
    
    public Route(List<Location3D> locations, float speed, TurnType turnType) {
        for(Location3D location : locations) {
            waypoints.add(
                Route.CreateWaypoint(
                    location.getLatitude(),
                    location.getLongitude(),
                    location.getAltitude(),
                    location.getAltitudeType(),
                    RouteManager.getNextWaypointId(),
                    speed,
                    turnType
                ));
        }
        
        //join up the waypoints in a 
        for(int i = 0; i < waypoints.size(); i++){
            Waypoint waypoint = waypoints.get(i);
            if(i == waypoints.size()-1) {
                waypoint.setNextWaypoint(waypoints.get(0).getNumber());
            }else{
                waypoint.setNextWaypoint(waypoints.get(i+1).getNumber());
            }
        }
    }
    
    public void AssignToDrone(UAV uav, Main main) {
        for(Waypoint waypoint : waypoints) {
            waypoint.setSpeed(uav.targetSpeed);
        }
        uav.MoveToWayPoint(waypoints, waypoints.get(0).getNumber());
        activeDrones.add(uav);
        lastAssigned = main.getTime();
    }
    
    public long getFirstWaypointNumber() {
        return waypoints.get(0).getNumber();
    }
    
    public List<Waypoint> toWaypointList() {
        return waypoints;
    }
    
    public void UnassignDrone(UAV uav, Main main) {
        lastRelased = main.getTime();
        activeDrones.add(uav);
    }
    
    public boolean DroneOnRoute(UAV uav) {
        return activeDrones.contains(uav);
    }
    
    public static Waypoint CreateWaypoint(double lat, double lon, float altitude, AltitudeType altType, long number, float speed, TurnType turnType) {
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
    
    public long UrgencyScore(Main main) {
        long score = (main.getTime() - lastAssigned);
        return score - activeDrones.size();
    }
}
