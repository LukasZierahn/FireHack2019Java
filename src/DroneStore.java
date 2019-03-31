
import afrl.cmasi.Location3D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author Sam
 */
public class DroneStore {
    private List<UAV> store = new ArrayList<UAV>();
    private final Predicate<UAV> fixedWingPredicate = uav -> uav.fixedWing;
    private final Predicate<UAV> multirotorPredicate = uav -> !uav.fixedWing;
    
    public boolean HaveDroneAvailable(){
        return !store.isEmpty();
    }
    
    public UAV ExtractAvailableUav(){
        return ExtractFromList(store);
    }
    
    private UAV ExtractFromList(List<UAV> uavs) {
        if(store.isEmpty()) return null;
        UAV uav = uavs.get(0);
        uavs.remove(uav);
        return uav;
    }
    
    public UAV ExtractAvailableUavCloseTo(Location3D position) {
        return ExtractClosestFromList(store, position);
    }
    
    private UAV ExtractClosestFromList(List<UAV> uavs, Location3D position) {
        UAV closest = null;
        double minDist = Double.MAX_VALUE;
        for(UAV uav : uavs) {
            double distance = FireZoneController.distance(position, uav.airVehicleState.getLocation());
            if(distance < minDist) {
                minDist = distance;
                closest = uav;
            }
        }
        
        store.remove(closest);
        
        return closest;
    }
    
    public UAV ExtractAvailableMatchingPredicate(Predicate<UAV> predicate) {        
        List<UAV> fixedWings = GetAllMatchingPredicate(predicate);
        UAV uav = ExtractFromList(fixedWings);
        store.remove(uav);
        
        return uav;
    }
    
    private List<UAV> GetAllMatchingPredicate(Predicate<UAV> predicate) {
        return store.stream().filter(predicate).collect(Collectors.toList());
    }
    
    public UAV GetAvailableMatchingPredicateNear(Predicate<UAV> predicate, Location3D location) {
        List<UAV> filtered = GetAllMatchingPredicate(predicate);
        return ExtractClosestFromList(filtered, location);
    }
    
    public UAV ExtractAvailableFixedWing() {
        return ExtractAvailableMatchingPredicate(fixedWingPredicate);
    }
    
    public void AddToStore(UAV uav) {
        if(store.contains(uav)) return;
        store.add(uav);
    }
    
    public UAV ExtractAvailableFixedWingNear(Location3D location) {
        return GetAvailableMatchingPredicateNear(fixedWingPredicate, location);
    }
    
    public UAV ExtractAvailableMultirotorNear(Location3D location) {
        return GetAvailableMatchingPredicateNear(multirotorPredicate, location);
    }
    
    public int Size() {
        return store.size();
    }
}
