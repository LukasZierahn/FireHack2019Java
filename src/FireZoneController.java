import afrl.cmasi.*;
import afrl.cmasi.searchai.HazardZoneDetection;
import afrl.cmasi.searchai.HazardZoneEstimateReport;
import mil.afrl.amase.util.CmasiNavUtils;

import java.util.*;

import static java.lang.Math.*;

class EstimatePoint implements Comparable<EstimatePoint> {
    public Location3D location;
    public long time;
    public long droneID;

    @Override
    public int compareTo(EstimatePoint u) {
        return toIntExact(time - u.time);
    }
}

public class FireZoneController {

    private static long zoneIDCounter = 0;

    private long zoneID;

    private Main main;

    public Map<Long, UAV> UAVMap = new HashMap<>();

    private List<EstimatePoint> estimatedHazardZone = new ArrayList<>();
    private Map<Long, List<EstimatePoint>> HazardZoneByDrones = null;

    private Location3D center = null;
    private float averageDistance = -1; //this is actually the square of the average distance
    private int targetQuadCount = 1;

    public FireZoneController(Main main, Location3D firstContact, List<Long> committedUAVS) {
        this.main = main;

        AddHazardZonePoint(firstContact, -1);

        for (Long ID : committedUAVS) {
            if (!main.getUAV(ID).fixedWing) {
                UAVMap.put(ID, main.getUAV(ID));
                main.getUAV(ID).fireZoneController = this;

                main.getUAV(ID).FollowEdge(true);
                break;
            }
        }

        //System.out.println(main.getUAVMap());

        FindQuads(2, firstContact);

        zoneIDCounter++;
        zoneID = zoneIDCounter;
    }

    public void FindQuads (int quads, Location3D near) {
        List<UAV> availableQuads = main.getDroneStore().ExtractUpToMultiNear(quads, near);
        for (UAV uav : availableQuads) {
            RequisitionUav(uav);
            System.out.println("FireZoneController.FindQuads() requisitioned " + uav.airVehicleState.getID());
        }
    }
    
    private void RequisitionUav(UAV uav){
        UAVMap.put(uav.airVehicleState.getID(), uav);
        uav.fireZoneController = this;
        ChaseFire(uav);
    }

    public void ChaseFire(UAV uav) {
        uav.currentTask = UAVTASKS.CHASING_FIRE;

        List<Waypoint> target = new ArrayList<>();
        target.add(Route.CreateWaypoint(estimatedHazardZone.get(0).location.getLatitude(), estimatedHazardZone.get(0).location.getLongitude(),  uav.targetHeight, AltitudeType.MSL, main.getNextWaypointID(),  30, TurnType.FlyOver));

        uav.MoveToWayPoint(target.get(0));
    }

    public void RequisitionUpToMaxDrones(){
        // clear out any drones not mapping fire
        for(UAV hazardUav : UAVMap.values()) {
            if(hazardUav.currentTask != UAVTASKS.CHASING_FIRE
            && hazardUav.currentTask != UAVTASKS.FOLLOW_EDGE_CLOCKWISE
            && hazardUav.currentTask != UAVTASKS.FOLLOW_EDGE_COUNTER_CLOCKWISE){
               UAVMap.remove(hazardUav.airVehicleState.getID());
               main.getDroneStore().AddToStore(hazardUav);
            }
        }
        
        // Try and requisition up to our target drone count
        int quadShortage = targetQuadCount - UAVMap.size();
        if(quadShortage > 0) {
            for(UAV uav : main.getDroneStore().ExtractUpToMultiNear(quadShortage, estimatedHazardZone.get(estimatedHazardZone.size() - 1).location)){
                RequisitionUav(uav);
            }
        }
        // TODO remove if too many? Or maybe add function to be able to offer drones back to the store if they're needed elsewhere
    }

    public void HandleHazardZoneDetection(HazardZoneDetection msg) {        
        UAV uav = UAVMap.get(msg.getDetectingEnitiyID());
        
        if (uav.fixedWing) {
            if (!uav.HasSeenFire()) {
                AddHazardZonePoint(msg.getDetectedLocation(), msg.getDetectingEnitiyID());
            }
        } else {
            AddHazardZonePoint(msg.getDetectedLocation(), msg.getDetectingEnitiyID());
            SendInHazardZone();

            if (uav.currentTask == UAVTASKS.CHASING_FIRE) {
                uav.FollowEdge(true);
            } else {
                float mean = 0;

                for (List<EstimatePoint> list : getHazardZoneByDrones().values()) {
                    mean += list.size();
                }
                mean = mean / getHazardZoneByDrones().size();

                if (getHazardZoneByDrones().get(msg.getDetectingEnitiyID()).size() >= mean) {
                    uav.targetSpeed = 10 + (10 / (1.0f * getHazardZoneByDrones().get(msg.getDetectingEnitiyID()).size() - mean));

                }
            }
        }
    }

    public void SendInHazardZone() {

        Polygon polygon = new Polygon();
        for (List<EstimatePoint> listToCheck : getHazardZoneByDrones().values()) {
            for (int i = 0; i < listToCheck.size(); i++) {
                polygon.getBoundaryPoints().add(listToCheck.get(i).location);
            }
        }
        HazardZoneEstimateReport o = new HazardZoneEstimateReport();
        o.setEstimatedZoneShape(polygon);
        o.setUniqueTrackingID(zoneID);
        o.setEstimatedGrowthRate(0);
        o.setPerceivedZoneType(afrl.cmasi.searchai.HazardType.Fire);
        o.setEstimatedZoneDirection(0);
        o.setEstimatedZoneSpeed(0);

        //Sending the Vehicle Action Command message to AMASE to be interpreted
        try {
            main.getOut().write(avtas.lmcp.LMCPFactory.packMessage(o, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void RemoveHazardZone() {

        Polygon polygon = new Polygon();
        HazardZoneEstimateReport o = new HazardZoneEstimateReport();
        o.setEstimatedZoneShape(polygon);
        o.setUniqueTrackingID(zoneID);
        o.setEstimatedGrowthRate(0);
        o.setPerceivedZoneType(afrl.cmasi.searchai.HazardType.Fire);
        o.setEstimatedZoneDirection(0);
        o.setEstimatedZoneSpeed(0);

        //Sending the Vehicle Action Command message to AMASE to be interpreted
        try {
            main.getOut().write(avtas.lmcp.LMCPFactory.packMessage(o, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void RemoveHazardZonePoint(int index) {
        estimatedHazardZone.remove(index);

        HazardZoneByDrones = null;
        center = null;
        averageDistance = -1;
    }

    public void RemoveHazardZonePoint(EstimatePoint point) {
        if (estimatedHazardZone.contains(point)) {
            RemoveHazardZonePoint(estimatedHazardZone.indexOf(point));
        }
    }

    public void AddHazardZonePoint(Location3D point, long droneID) {

        List<FireZoneController> hazardZones = main.getHazardZones();
        for (int i1 = 0; i1 < hazardZones.size(); i1++) {
            FireZoneController FZC = hazardZones.get(i1);
            if (FZC.zoneID == this.zoneID) {
                continue;
            }

            for (int i = 0; i < FZC.estimatedHazardZone.size(); i++) {
                if (0.000002 > distance(FZC.estimatedHazardZone.get(i).location, point)) {

                    InsertHazardPoint(point, -1, droneID);
                    Merge(FZC, i);
                }
            }
        }

        for (List<EstimatePoint> listToCheck : getHazardZoneByDrones().values()) {
            for (int i = listToCheck.size() - 1; i >= 0; i--) {
                System.out.println(i);
                if (0.00002 > distance(listToCheck.get(i).location, point)) {
                    if (20000 < main.getTime() - listToCheck.get(i).time) {
                        RemoveHazardZonePoint(listToCheck.get(i));
                    } else {
                        return;
                    }
                }
            }


            if (listToCheck.size() >= 5) {
                double heading1 = CmasiNavUtils.headingBetween(listToCheck.get(0).location, point);
                double heading2 = CmasiNavUtils.headingBetween(listToCheck.get(0).location, listToCheck.get(1).location);

                if (25 > abs(heading1 - heading2)) {
                    RemoveHazardZonePoint(listToCheck.get(0));
                }



                boolean changed = true;
                while (changed) {
                    changed = false;
                    for (int i = listToCheck.size() - 1; i > 0; i--) {
                        if (listToCheck.get(i).time < listToCheck.get(i - 1).time) {
                            System.out.println(i);
                            RemoveHazardZonePoint(listToCheck.get(i - 1));
                            listToCheck.remove(i - 1);
                            changed = true;
                            break;
                        }
                    }
                }

                for (int i = 1; i < min(3, listToCheck.size()); i++) {
                    if (1.7 * distance(point, listToCheck.get(0).location) < distance(point, listToCheck.get(i).location)) {
                        RemoveHazardZonePoint(listToCheck.get(0));
                    }
                }
            }
        }

        InsertWithRespectsToDroneID(point, droneID);
    }

    public void InsertWithRespectsToDroneID(Location3D point, long droneID) {
        if (estimatedHazardZone.isEmpty()) {
            InsertHazardPoint(point, -1, droneID);
        }

        Map<Long, List<EstimatePoint>> droneMap = getHazardZoneByDrones();

        System.out.println(droneID);
        System.out.println(droneMap.toString());

        int index = -1;

        if (!droneMap.containsKey(droneID)) {
            if (UAVMap.size() <= 2) {
                long oldest = main.getTime() + 1;
                for (EstimatePoint hPoint : estimatedHazardZone) {
                    if (hPoint.time < oldest) {
                        index = estimatedHazardZone.indexOf(hPoint);
                    }
                }
            } else {
                System.out.println("InsertWithRespectsToDroneID didn't find ID, not inserted");
                return;
            }

        } else {
            index = estimatedHazardZone.indexOf(droneMap.get(droneID).get(droneMap.get(droneID).size() - 1));
        }

        InsertHazardPoint(point,index , droneID);
    }

    public void Merge(FireZoneController target, int index) {

        System.out.println("Merging FZC");

        if (target.estimatedHazardZone.size() >= 10) {
            List<EstimatePoint> bufferHazard = new ArrayList<>();

            bufferHazard.addAll(estimatedHazardZone);

            if (index + 1 <= target.estimatedHazardZone.size()) {
                bufferHazard.addAll(target.estimatedHazardZone.subList(index + 1, target.estimatedHazardZone.size()));
            }

            bufferHazard.addAll(target.estimatedHazardZone.subList(0, index));

            estimatedHazardZone = bufferHazard;
        }

        for (UAV uav : target.UAVMap.values()) {
            uav.InitRefuelMission();
            uav.fireZoneController = null;
        }

        target.RemoveHazardZone();

        main.getHazardZones().remove(target);
    }

    public void InsertHazardPoint(Location3D point, int index, long droneID) {

        EstimatePoint newPoint = new EstimatePoint();
        newPoint.location = point;
        newPoint.time = main.getTime();
        newPoint.droneID = droneID;

        if (index == estimatedHazardZone.size() || index == -1) {
            estimatedHazardZone.add(newPoint);

            center = null;
            HazardZoneByDrones = null;
            averageDistance = -1;
            return;
        }

        List<EstimatePoint> bufferHazard = new ArrayList<>();

        for (int i = 0; i < index; i++) {
            bufferHazard.add(estimatedHazardZone.get(i));
        }

        bufferHazard.add(newPoint);

        for (int i = index; i < estimatedHazardZone.size(); i++) {
            bufferHazard.add(estimatedHazardZone.get(i));
        }

        estimatedHazardZone = bufferHazard;

        center = null;
        HazardZoneByDrones = null;
        averageDistance = -1;
    }

    public Map<Long, List<EstimatePoint>> getHazardZoneByDrones() {
        if (HazardZoneByDrones == null) {
            HazardZoneByDrones = new HashMap<>();

            for (int i = 0; i < estimatedHazardZone.size(); i++) {
                if (!HazardZoneByDrones.containsKey(estimatedHazardZone.get(i).droneID)) {
                    HazardZoneByDrones.put(estimatedHazardZone.get(i).droneID, new ArrayList<>());
                }
                HazardZoneByDrones.get(estimatedHazardZone.get(i).droneID).add(estimatedHazardZone.get(i));
            }

            for (List<EstimatePoint> listToCheck : HazardZoneByDrones.values()) {
                Collections.sort(listToCheck);
            }
        }

        return HazardZoneByDrones;
    }

    public static double distance(Location3D location1, Location3D location2) {
        return pow(location1.getLatitude() - location2.getLatitude(), 2) + pow(location1.getLongitude() - location2.getLongitude(), 2);
    }
}
