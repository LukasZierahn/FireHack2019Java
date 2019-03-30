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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import avtas.lmcp.*;

/**
 Controls the growth/contraction of a zone and translation across the              scenario area.        
*/
public class HazardZoneChangeCommand extends avtas.lmcp.LMCPObject {
    
    public static final int LMCP_TYPE = 5;

    public static final String SERIES_NAME = "SEARCHAI";
    /** Series Name turned into a long for quick comparisons. */
    public static final long SERIES_NAME_ID = 6000273900112986441L;
    public static final int SERIES_VERSION = 5;


    private static final String TYPE_NAME = "HazardZoneChangeCommand";

    private static final String FULL_LMCP_TYPE_NAME = "afrl.cmasi.searchai.HazardZoneChangeCommand";

    /**  ID of zone to control. If zero, then this is treated as a default command for all zones not governed by a zone-specific message. (Units: None)*/
    @LmcpType("uint32")
    protected long ZoneID = 0L;
    /**  The rate of growth of the zone. How this affects the zone depends on its geometry type. (Units: m/s)*/
    @LmcpType("real32")
    protected float GrowthRate = (float)0;
    /**  Translation rate of the zone across the scenario area. (Units: m/s)*/
    @LmcpType("real32")
    protected float TranslationRate = (float)0;
    /**  True direction of translation of the zone. (Units: degrees)*/
    @LmcpType("real32")
    protected float TranslationDirection = (float)0;

    
    public HazardZoneChangeCommand() {
    }

    public HazardZoneChangeCommand(long ZoneID, float GrowthRate, float TranslationRate, float TranslationDirection){
        this.ZoneID = ZoneID;
        this.GrowthRate = GrowthRate;
        this.TranslationRate = TranslationRate;
        this.TranslationDirection = TranslationDirection;
    }


    public HazardZoneChangeCommand clone() {
        try {
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream( calcSize() );
            pack(bos);
            java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(bos.toByteArray());
            HazardZoneChangeCommand newObj = new HazardZoneChangeCommand();
            newObj.unpack(bis);
            return newObj;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
     
    /**  ID of zone to control. If zero, then this is treated as a default command for all zones not governed by a zone-specific message. (Units: None)*/
    public long getZoneID() { return ZoneID; }

    /**  ID of zone to control. If zero, then this is treated as a default command for all zones not governed by a zone-specific message. (Units: None)*/
    public HazardZoneChangeCommand setZoneID( long val ) {
        ZoneID = val;
        return this;
    }

    /**  The rate of growth of the zone. How this affects the zone depends on its geometry type. (Units: m/s)*/
    public float getGrowthRate() { return GrowthRate; }

    /**  The rate of growth of the zone. How this affects the zone depends on its geometry type. (Units: m/s)*/
    public HazardZoneChangeCommand setGrowthRate( float val ) {
        GrowthRate = val;
        return this;
    }

    /**  Translation rate of the zone across the scenario area. (Units: m/s)*/
    public float getTranslationRate() { return TranslationRate; }

    /**  Translation rate of the zone across the scenario area. (Units: m/s)*/
    public HazardZoneChangeCommand setTranslationRate( float val ) {
        TranslationRate = val;
        return this;
    }

    /**  True direction of translation of the zone. (Units: degrees)*/
    public float getTranslationDirection() { return TranslationDirection; }

    /**  True direction of translation of the zone. (Units: degrees)*/
    public HazardZoneChangeCommand setTranslationDirection( float val ) {
        TranslationDirection = val;
        return this;
    }



    public int calcSize() {
        int size = super.calcSize();  
        size += 16; // accounts for primitive types

        return size;
    }

    public void unpack(InputStream in) throws IOException {
        ZoneID = LMCPUtil.getUint32(in);

        GrowthRate = LMCPUtil.getReal32(in);

        TranslationRate = LMCPUtil.getReal32(in);

        TranslationDirection = LMCPUtil.getReal32(in);


    }

    public void pack(OutputStream out) throws IOException {
        LMCPUtil.putUint32(out, ZoneID);
        LMCPUtil.putReal32(out, GrowthRate);
        LMCPUtil.putReal32(out, TranslationRate);
        LMCPUtil.putReal32(out, TranslationDirection);

    }

    public int getLMCPType() { return LMCP_TYPE; }

    public String getLMCPSeriesName() { return SERIES_NAME; }

    public long getLMCPSeriesNameAsLong() { return SERIES_NAME_ID; }

    public int getLMCPSeriesVersion() { return SERIES_VERSION; };

    public String getLMCPTypeName() { return TYPE_NAME; }

    public String getFullLMCPTypeName() { return FULL_LMCP_TYPE_NAME; }

    public String toString() {
        return toXML("");
    }

    public String toXML(String ws) {
        StringBuffer buf = new StringBuffer();
        buf.append( ws + "<HazardZoneChangeCommand Series=\"SEARCHAI\">\n");
        buf.append( ws + "  <ZoneID>" + String.valueOf(ZoneID) + "</ZoneID>\n");
        buf.append( ws + "  <GrowthRate>" + String.valueOf(GrowthRate) + "</GrowthRate>\n");
        buf.append( ws + "  <TranslationRate>" + String.valueOf(TranslationRate) + "</TranslationRate>\n");
        buf.append( ws + "  <TranslationDirection>" + String.valueOf(TranslationDirection) + "</TranslationDirection>\n");
        buf.append( ws + "</HazardZoneChangeCommand>");

        return buf.toString();
    }

    public boolean equals(Object anotherObj) {
        if ( anotherObj == this ) return true;
        if ( anotherObj == null ) return false;
        if ( anotherObj.getClass() != this.getClass() ) return false;
        HazardZoneChangeCommand o = (HazardZoneChangeCommand) anotherObj;
        if (ZoneID != o.ZoneID) return false;
        if (GrowthRate != o.GrowthRate) return false;
        if (TranslationRate != o.TranslationRate) return false;
        if (TranslationDirection != o.TranslationDirection) return false;

        return true;
    }

    public int hashCode() {
        int hash = 0;
        hash += 31 * (int)ZoneID;
        hash += 31 * (int)GrowthRate;
        hash += 31 * (int)TranslationRate;
        hash += 31 * (int)TranslationDirection;

        return hash + super.hashCode();
    }
    
}
