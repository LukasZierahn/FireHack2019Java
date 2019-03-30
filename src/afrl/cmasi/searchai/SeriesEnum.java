// ===============================================================================
// Authors: AFRL/RQQA
// Organization: Air Force Research Laboratory, Aerospace Systems Directorate, Power and Control Division
// 
// Copyright (c) 2017 Government of the United State of America, as represented by
// the Secretary of the Air Force.  No copyright is claimed in the United States under
// Title 17, U.S. Code.  All Other Rights Reserved.
// ===============================================================================

// This file was auto-created by LmcpGen. Modifications will be overwritten.

package afrl.cmasi.searchai;


import avtas.lmcp.LMCPObject;
import java.util.Arrays;

public class SeriesEnum implements avtas.lmcp.LMCPEnum {
 
    public static final String SERIES_NAME = "SEARCHAI";
    /** Series Name turned into a long for quick comparisons. */
    public static final long SERIES_NAME_ID = 6000273900112986441L;
    public static final int SERIES_VERSION = 5;


    private static String[] name_list = new String[]{
        "HazardZone",
        "HazardZoneDetection",
        "HazardZoneEstimateReport",
        "RecoveryPoint",
        "HazardZoneChangeCommand",
        "HazardSensorConfiguration",
        "HazardSensorState"
    };

    public long getSeriesNameAsLong() { return SERIES_NAME_ID; }

    public String getSeriesName() { return SERIES_NAME; }

    public int getSeriesVersion() { return SERIES_VERSION; }

    public String getName(long type) {
        switch ((int) type) {
            case 1: return "HazardZone";
            case 2: return "HazardZoneDetection";
            case 3: return "HazardZoneEstimateReport";
            case 4: return "RecoveryPoint";
            case 5: return "HazardZoneChangeCommand";
            case 6: return "HazardSensorConfiguration";
            case 7: return "HazardSensorState";

        }
        
        return "";
    }

    public long getType(String name) {
       if ( name.equals("HazardZone")) return 1;
       if ( name.equals("HazardZoneDetection")) return 2;
       if ( name.equals("HazardZoneEstimateReport")) return 3;
       if ( name.equals("RecoveryPoint")) return 4;
       if ( name.equals("HazardZoneChangeCommand")) return 5;
       if ( name.equals("HazardSensorConfiguration")) return 6;
       if ( name.equals("HazardSensorState")) return 7;

       
       return -1;
    }

    public LMCPObject getInstance(long type) {
        switch ((int) type) {
            case 1: return new HazardZone();
            case 2: return new HazardZoneDetection();
            case 3: return new HazardZoneEstimateReport();
            case 4: return new RecoveryPoint();
            case 5: return new HazardZoneChangeCommand();
            case 6: return new HazardSensorConfiguration();
            case 7: return new HazardSensorState();

        }

        return null;
    }

    public java.util.Collection<String> getAllTypes() {
        return Arrays.asList(name_list);
    }



}
