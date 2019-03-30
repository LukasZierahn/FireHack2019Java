import afrl.cmasi.*;
import afrl.cmasi.searchai.HazardZoneDetection;
import afrl.cmasi.searchai.HazardZoneEstimateReport;
import mil.afrl.amase.util.CmasiNavUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.pow;

public class FireZoneController {

    private static long zoneIDCounter = 0;

    private long zoneID;

    private Main main;

    private Map<Long, UAV> UAVMap = new HashMap<>();

    private List<Location3D> estimatedHazardZone = new ArrayList<>();
    private List<Long> hazardZoneTimes = new ArrayList<>();

    private int QuadCount = 0;

    private Location3D center = null;
    private float averageDistance = -1; //this is actually the square of the average distance

    public FireZoneController(Main main, Location3D firstContact, List<Long> committedUAVS) {
        this.main = main;

        AddHazardZonePoint(firstContact);

        for (Long ID : committedUAVS) {
            if (!main.getUAV(ID).fixedWing) {
                UAVMap.put(ID, main.getUAV(ID));
                main.getUAV(ID).fireZoneController = this;

                main.getUAV(ID).FollowEdge(true);
                QuadCount++;
                break;
            }
        }

        //System.out.println(main.getUAVMap());

        FindQuads(1);

        zoneIDCounter++;
        zoneID = zoneIDCounter;
    }

    public void FindQuads (int quads) {
        for (UAV uav : main.getUAVMap().values()) {
                if ((!uav.fixedWing) &&  (uav.currentTask == UAVTASKS.NO_TASK || uav.currentTask == UAVTASKS.STANDBY)) {
                    if (quads > QuadCount) {
                        UAVMap.put(uav.airVehicleState.getID(), uav);
                        uav.fireZoneController = this;
                        ChaseFire(uav);
                    } else {
                        break;
                    }
                }
            }

    }

    public void ChaseFire(UAV uav) {
        uav.currentTask = UAVTASKS.CHASING_FIRE;

        List<Waypoint> target = new ArrayList<>();
        target.add(uav.CreateWaypoint(estimatedHazardZone.get(0).getLatitude(), estimatedHazardZone.get(0).getLongitude(),  700, AltitudeType.MSL, main.getNextWaypointID(),  30, TurnType.FlyOver));

        uav.MoveToWayPoint(target.get(0));
    }


    public void HandleHazardZoneDetection(HazardZoneDetection msg) {
        UAV uav = UAVMap.get(msg.getDetectingEnitiyID());

        if (uav.fixedWing) {
            if (!uav.HasSeenFire()) {
                AddHazardZonePoint(msg.getDetectedLocation());
            }
        } else {
            AddHazardZonePoint(msg.getDetectedLocation());
            SendInHazardZone();

            if (uav.currentTask == UAVTASKS.CHASING_FIRE) {
                uav.FollowEdge(true);
            }
        }
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
        hazardZoneTimes.remove(index);

        center = null;
        averageDistance = -1;
    }

    public void AddHazardZonePoint(Location3D point) {

        List<FireZoneController> hazardZones = main.getHazardZones();
        for (int i1 = 0; i1 < hazardZones.size(); i1++) {
            FireZoneController FZC = hazardZones.get(i1);
            if (FZC.zoneID == this.zoneID) {
                continue;
            }

            for (int i = 0; i < FZC.estimatedHazardZone.size(); i++) {
                if (0.000002 > distance(FZC.estimatedHazardZone.get(i), point)) {

                    estimatedHazardZone.add(point);
                    hazardZoneTimes.add(main.getTime());
                    center = null;
                    averageDistance = -1;
                    Merge(FZC, i);
                }
            }
        }


        for (int i = estimatedHazardZone.size() - 1; i >= 0; i--) {
            if (0.00002 > distance(estimatedHazardZone.get(i), point)) {
                if (20000 < main.getTime() - hazardZoneTimes.get(i)) {
                    RemoveHazardZonePoint(i);
                } else {
                    return;
                }
            }
        }

        if (estimatedHazardZone.size() >= 5) {
            double heading1 = CmasiNavUtils.headingBetween(estimatedHazardZone.get(0), point);
            double heading2 = CmasiNavUtils.headingBetween(estimatedHazardZone.get(0), estimatedHazardZone.get(1));

            if (35 > abs(heading1 - heading2)) {
                RemoveHazardZonePoint(0);
            }

            boolean changed = true;
            while (changed) {
                changed = false;
                for (int i = estimatedHazardZone.size() - 1; i > 0; i--) {
                    if (hazardZoneTimes.get(i) < hazardZoneTimes.get(i - 1)) {
                        RemoveHazardZonePoint(i - 1);
                        changed = true;
                        break;
                    }
                }
            }

            for (int i = 1; i < min(3, estimatedHazardZone.size()); i++) {
                if (1.7 * distance(point, estimatedHazardZone.get(0)) < distance(point, estimatedHazardZone.get(i))) {
                    RemoveHazardZonePoint(0);
                }
            }
        }

        estimatedHazardZone.add(point);
        hazardZoneTimes.add(main.getTime());
        center = null;
        averageDistance = -1;
    }

    public void Merge(FireZoneController target, int index) {

        System.out.println("Merging FZC");

        if (target.estimatedHazardZone.size() >= 10) {
            List<Location3D> bufferHazard = new ArrayList<>();
            List<Long> bufferTimes = new ArrayList<>();

            bufferHazard.addAll(estimatedHazardZone);
            bufferTimes.addAll(hazardZoneTimes);

            if (index + 1 <= target.estimatedHazardZone.size()) {
                bufferHazard.addAll(target.estimatedHazardZone.subList(index + 1, target.estimatedHazardZone.size()));
                bufferTimes.addAll(target.hazardZoneTimes.subList(index + 1, target.hazardZoneTimes.size()));
            }

            bufferHazard.addAll(target.estimatedHazardZone.subList(0, index));
            bufferTimes.addAll(target.hazardZoneTimes.subList(0, index));

            estimatedHazardZone = bufferHazard;
            hazardZoneTimes = bufferTimes;
        }

        for (UAV uav : target.UAVMap.values()) {
            uav.InitRefuelMission();
            uav.fireZoneController = null;
        }

        target.RemoveHazardZone();

        main.getHazardZones().remove(target);
    }

    public void AddHazardZonePoint(Location3D point, boolean inFront) {

        if (inFront) {
            List<Location3D> bufferHazard = new ArrayList<>();
            List<Long> bufferTime = new ArrayList<>();

            bufferHazard.add(point);
            bufferTime.add(main.getTime());

            bufferHazard.addAll(estimatedHazardZone);
            estimatedHazardZone = bufferHazard;
            bufferTime.addAll(hazardZoneTimes);
            hazardZoneTimes = bufferTime;

        } else {
            estimatedHazardZone.add(point);
            hazardZoneTimes.add(main.getTime());
        }
        center = null;
        averageDistance = -1;
    }

    public static double distance(Location3D location1, Location3D location2) {
        return pow(location1.getLatitude() - location2.getLatitude(), 2) + pow(location1.getLongitude() - location2.getLongitude(), 2);
    }
}
