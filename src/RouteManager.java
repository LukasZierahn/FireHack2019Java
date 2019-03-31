import afrl.cmasi.AltitudeType;
import afrl.cmasi.Location3D;
import afrl.cmasi.TurnType;
import afrl.cmasi.Waypoint;
import java.awt.geom.Point2D;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;

public class RouteManager {

    public Location3D StartPoint;

    public List<Route> routes = new ArrayList<>();
    public List<Integer> SquareRoutes = new ArrayList<>();
    public List<Integer> CCSquareRoutes = new ArrayList<>();

    private boolean clockwise = false;

    private static int lastWaypointID = 0;
    private FireMap firemap;

    public RouteManager(FireMap firemap) {
        this.firemap = firemap;

        for (int i = 0; i < (max(firemap.width, firemap.height)/ 3000); i += 2) {
            routes.add(CreateSquareRoute(0.1f + 2400 * i / (float) max(firemap.width, firemap.height)));
            SquareRoutes.add(routes.size() - 1);
            routes.add(CreateCCSquareRoute(0.1f + 2400 * i / (float) max(firemap.width, firemap.height)));
            CCSquareRoutes.add(routes.size() - 1);
        }
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

    private Route CreateCCSquareRoute(float scale) {
        float inverseScale = 1f - scale;
        List<Location3D> locations = new ArrayList<>();
        locations.add(firemap.CoordToLocation(new Point2D.Float(firemap.width * inverseScale, firemap.height * scale)));
        locations.add(firemap.CoordToLocation(new Point2D.Float(firemap.width * inverseScale, firemap.height * inverseScale)));
        locations.add(firemap.CoordToLocation(new Point2D.Float(firemap.width * scale, firemap.height * inverseScale)));
        locations.add(firemap.CoordToLocation(new Point2D.Float(firemap.width * scale, firemap.height * scale)));

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
            if(urgency > topScore) {
                topScore = urgency;
                maxRoute = route;
            }
        }

        if (SquareRoutes.contains(routes.indexOf(maxRoute))) {
            int indexRoutes = routes.indexOf(maxRoute);
            int indexSquares = SquareRoutes.indexOf(indexRoutes);
            if (indexSquares != 0) {
                routes.get(SquareRoutes.get(indexSquares - 1)).scoreOffset -= 100;
            }

            if (indexSquares != SquareRoutes.size() - 1) {
                routes.get(SquareRoutes.get(indexSquares + 1)).scoreOffset -= 100;
            }

            routes.get(CCSquareRoutes.get(indexSquares)).scoreOffset -= 800;
            for (int index : SquareRoutes) {
                routes.get(index).scoreOffset -= 100;
            }
        }

        if (CCSquareRoutes.contains(routes.indexOf(maxRoute))) {
            int indexRoutes = routes.indexOf(maxRoute);
            int indexSquares = CCSquareRoutes.indexOf(indexRoutes);
            if (indexSquares != 0) {
                routes.get(CCSquareRoutes.get(indexSquares - 1)).scoreOffset -= 100;
            }

            if (indexSquares != CCSquareRoutes.size() - 1) {
                routes.get(CCSquareRoutes.get(indexSquares + 1)).scoreOffset -= 100;
            }

            routes.get(SquareRoutes.get(indexSquares)).scoreOffset -= 800;
            for (int index : CCSquareRoutes) {
                routes.get(index).scoreOffset -= 100;
            }
        }

        if(maxRoute != null) {
            maxRoute.AssignToDrone(uav, main);
        }

        maxRoute.scoreOffset -= 1000;
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
