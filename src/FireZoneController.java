import afrl.cmasi.Location3D;
import afrl.cmasi.Polygon;
import afrl.cmasi.searchai.HazardZoneEstimateReport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FireZoneController {

    private static long zoneIDCounter = 0;

    private long zoneID;

    private Main main;

    private Map<Long, UAV> UAVMap = new HashMap<>();

    private Polygon estimatedHazardZone = new Polygon();

    public FireZoneController(Main main, List<Long> committedUAVS, Location3D firstContact) {
        this.main = main;

        estimatedHazardZone.getBoundaryPoints().add(firstContact);

        for (Long ID : committedUAVS) {
            UAVMap.put(ID, main.getUAV(ID));
        }

        zoneIDCounter++;
        zoneID = zoneIDCounter;
    }

    public void SendInHazardZone() {
        System.out.println("Sending in Hazard Zone: " + zoneID);

        HazardZoneEstimateReport o = new HazardZoneEstimateReport();
        o.setEstimatedZoneShape(estimatedHazardZone);
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
}
