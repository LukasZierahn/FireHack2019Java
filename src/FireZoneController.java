import afrl.cmasi.*;
import afrl.cmasi.searchai.HazardZoneDetection;
import afrl.cmasi.searchai.HazardZoneEstimateReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.pow;

public class FireZoneController {

    private static long zoneIDCounter = 0;

    private long zoneID;

    private Main main;

    private Map<Long, UAV> UAVMap = new HashMap<>();

    private List<Location3D> estimatedHazardZone = new ArrayList<>();
    private List<Long> hazardZoneTimes = new ArrayList<>();

    private Location3D center = null;
    private float averageDistance = -1; //this is actually the square of the average distance

    public FireZoneController(Main main, Location3D firstContact, List<Long> committedUAVS) {
        this.main = main;

        AddHazardZonePoint(firstContact);

        for (Long ID : committedUAVS) {
            UAVMap.put(ID, main.getUAV(ID));
            main.getUAV(ID).fireZoneController = this;
            if (main.getUAV(ID).fixedWing) {
                main.getUAV(ID).FlyThrough(getCenter());
            } else {
                main.getUAV(ID).FollowEdge(true);
            }
        }

        zoneIDCounter++;
        zoneID = zoneIDCounter;
    }

    public void HandleHazardZoneDetection(HazardZoneDetection msg) {
        UAVMap.get(msg.getDetectingEnitiyID()).SawFire();

        center = null;
        AddHazardZonePoint(msg.getDetectedLocation());
        SendInHazardZone();
    }

    public boolean CheckAndMerge(FireZoneController target) {

        if (Check(target)) {
            Merge(target);

            center = null;
            averageDistance = -1;

            UAVMap.putAll(target.UAVMap);
            for (UAV uav : UAVMap.values()) {
                uav.fireZoneController = this;
            }

            return true;
        }
        return false;
    }

    private boolean Check(FireZoneController target) {

        //System.out.println(getCenter().getLatitude() + " " + getCenter().getLongitude() + " " + getAverageDistance());
        for (Location3D location : target.estimatedHazardZone) {
            if (averageDistance > (pow(location.getLatitude() - getCenter().getLatitude(), 2) + pow(location.getLongitude() - getCenter().getLongitude(), 2))) {
                return true;
            }
        }

        return false;
    }

    private void Merge(FireZoneController target) {
        List<Location3D> bufferEstimatedHazardZone = new ArrayList<>();
        List<Long> bufferHazardZoneTimes = new ArrayList<>();


        int cut = 0;
        for (int i = 0; i < target.estimatedHazardZone.size(); i++) {
            if (averageDistance <= (pow(target.estimatedHazardZone.get(i).getLatitude() - getCenter().getLatitude(), 2) + pow(target.estimatedHazardZone.get(i).getLongitude() - getCenter().getLongitude(), 2))) {
                bufferEstimatedHazardZone.add(target.estimatedHazardZone.get(i));
                bufferHazardZoneTimes.add(target.hazardZoneTimes.get(i));
                cut = i;
            } else {
                break;
            }
        }

        boolean cutOne = false;
        for (int i = 0; i < estimatedHazardZone.size(); i++) {
            if (target.getAverageDistance() <= (pow(estimatedHazardZone.get(i).getLatitude() - target.getCenter().getLatitude(), 2) + pow(estimatedHazardZone.get(i).getLongitude() - target.getCenter().getLongitude(), 2))) {
                if (cutOne) {
                    bufferEstimatedHazardZone.add(estimatedHazardZone.get(i));
                    bufferHazardZoneTimes.add(hazardZoneTimes.get(i));
                }
            } else {
                cutOne = true;
            }
        }

        for (int i = 0; i < estimatedHazardZone.size(); i++) {
            if (target.getAverageDistance() <= (pow(estimatedHazardZone.get(i).getLatitude() - target.getCenter().getLatitude(), 2) + pow(estimatedHazardZone.get(i).getLongitude() - target.getCenter().getLongitude(), 2))) {
                bufferEstimatedHazardZone.add(estimatedHazardZone.get(i));
                bufferHazardZoneTimes.add(hazardZoneTimes.get(i));
            } else {
                break;
            }
        }

        for (int i = cut; i < target.estimatedHazardZone.size(); i++) {
            if (averageDistance <= (pow(target.estimatedHazardZone.get(i).getLatitude() - getCenter().getLatitude(), 2) + pow(target.estimatedHazardZone.get(i).getLongitude() - getCenter().getLongitude(), 2))) {
                bufferEstimatedHazardZone.add(target.estimatedHazardZone.get(i));
                bufferHazardZoneTimes.add(target.hazardZoneTimes.get(i));
            }
        }
        estimatedHazardZone = bufferEstimatedHazardZone;
        hazardZoneTimes = bufferHazardZoneTimes;
    }

    public Location3D getCenter() {
        if (center != null) {
            return center;
        }

        center = new Location3D();
        for (Location3D point : estimatedHazardZone) {
            center.setLatitude(center.getLatitude() + point.getLatitude());
            center.setLongitude(center.getLongitude() + point.getLongitude());
        }

        center.setLatitude(center.getLatitude() / estimatedHazardZone.size());
        center.setLongitude(center.getLongitude() / estimatedHazardZone.size());

        return center;
    }

    public float getAverageDistance() {
        if (averageDistance != -1) {
            return averageDistance;
        }

        averageDistance = 0.0f;
        for (Location3D point : estimatedHazardZone) {
            averageDistance += pow(point.getLatitude() - getCenter().getLatitude(), 2) + pow(point.getLongitude() - getCenter().getLongitude(), 2);
        }

        averageDistance = averageDistance / estimatedHazardZone.size();

        averageDistance = averageDistance * 10;

        return averageDistance;
    }

    public void SendInHazardZone() {

        Polygon polygon = new Polygon();
        for (Location3D point : estimatedHazardZone) {
            polygon.getBoundaryPoints().add(point);
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

    public void RemoiveHazardZonePoint(int index) {
        estimatedHazardZone.remove(index);
        hazardZoneTimes.remove(index);

        center = null;
        averageDistance = -1;
    }

    public void AddHazardZonePoint(Location3D point) {
        for (int i = 0; i < estimatedHazardZone.size(); i++) {
            if (0.00002 > distance(estimatedHazardZone.get(i), point)) {
                return;
            }
        }

        

        estimatedHazardZone.add(point);
        hazardZoneTimes.add(main.getTime());
        center = null;
        averageDistance = -1;
    }

    public static double distance(Location3D location1, Location3D location2) {
        return pow(location1.getLatitude() - location2.getLatitude(), 2) + pow(location1.getLongitude() - location2.getLongitude(), 2);
    }
}
