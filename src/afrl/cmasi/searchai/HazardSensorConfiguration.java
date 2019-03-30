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
 A sensor that can detect hazards.  Field of view is fixed, but can be mounted on a gimbal.  
*/
public class HazardSensorConfiguration extends afrl.cmasi.PayloadConfiguration {
    
    public static final int LMCP_TYPE = 6;

    public static final String SERIES_NAME = "SEARCHAI";
    /** Series Name turned into a long for quick comparisons. */
    public static final long SERIES_NAME_ID = 6000273900112986441L;
    public static final int SERIES_VERSION = 5;


    private static final String TYPE_NAME = "HazardSensorConfiguration";

    private static final String FULL_LMCP_TYPE_NAME = "afrl.cmasi.searchai.HazardSensorConfiguration";

    /**  Max range that a hazard can be detected (Units: m)*/
    @LmcpType("real32")
    protected float MaxRange = (float)0;
    /**  Horizontal extents of the sensor (Units: degrees)*/
    @LmcpType("real32")
    protected float HorizontalFOV = (float)0;
    /**  Vertical extents of the sensor (Units: degrees)*/
    @LmcpType("real32")
    protected float VerticalFOV = (float)0;
    /**  Types of hazards that can be detected by this sensor (Units: None)*/
    @LmcpType("HazardType")
    protected java.util.ArrayList<afrl.cmasi.searchai.HazardType> DetectableHazardTypes = new java.util.ArrayList<afrl.cmasi.searchai.HazardType>();

    
    public HazardSensorConfiguration() {
    }

    public HazardSensorConfiguration(long PayloadID, String PayloadKind, float MaxRange, float HorizontalFOV, float VerticalFOV){
        this.PayloadID = PayloadID;
        this.PayloadKind = PayloadKind;
        this.MaxRange = MaxRange;
        this.HorizontalFOV = HorizontalFOV;
        this.VerticalFOV = VerticalFOV;
    }


    public HazardSensorConfiguration clone() {
        try {
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream( calcSize() );
            pack(bos);
            java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(bos.toByteArray());
            HazardSensorConfiguration newObj = new HazardSensorConfiguration();
            newObj.unpack(bis);
            return newObj;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
     
    /**  Max range that a hazard can be detected (Units: m)*/
    public float getMaxRange() { return MaxRange; }

    /**  Max range that a hazard can be detected (Units: m)*/
    public HazardSensorConfiguration setMaxRange( float val ) {
        MaxRange = val;
        return this;
    }

    /**  Horizontal extents of the sensor (Units: degrees)*/
    public float getHorizontalFOV() { return HorizontalFOV; }

    /**  Horizontal extents of the sensor (Units: degrees)*/
    public HazardSensorConfiguration setHorizontalFOV( float val ) {
        HorizontalFOV = val;
        return this;
    }

    /**  Vertical extents of the sensor (Units: degrees)*/
    public float getVerticalFOV() { return VerticalFOV; }

    /**  Vertical extents of the sensor (Units: degrees)*/
    public HazardSensorConfiguration setVerticalFOV( float val ) {
        VerticalFOV = val;
        return this;
    }

    public java.util.ArrayList<afrl.cmasi.searchai.HazardType> getDetectableHazardTypes() {
        return DetectableHazardTypes;
    }



    public int calcSize() {
        int size = super.calcSize();  
        size += 12; // accounts for primitive types
        
        size += 2 + 4 * DetectableHazardTypes.size();

        return size;
    }

    public void unpack(InputStream in) throws IOException {
        super.unpack(in);
        MaxRange = LMCPUtil.getReal32(in);

        HorizontalFOV = LMCPUtil.getReal32(in);

        VerticalFOV = LMCPUtil.getReal32(in);

        DetectableHazardTypes.clear();
        int DetectableHazardTypes_len = LMCPUtil.getUint16(in);
        for(int i=0; i<DetectableHazardTypes_len; i++){
        DetectableHazardTypes.add(afrl.cmasi.searchai.HazardType.unpack( in ));

        }

    }

    public void pack(OutputStream out) throws IOException {
        super.pack(out);
        LMCPUtil.putReal32(out, MaxRange);
        LMCPUtil.putReal32(out, HorizontalFOV);
        LMCPUtil.putReal32(out, VerticalFOV);
        LMCPUtil.putUint16(out, DetectableHazardTypes.size());
        for(int i=0; i<DetectableHazardTypes.size(); i++){
            DetectableHazardTypes.get(i).pack(out);
        }

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
        buf.append( ws + "<HazardSensorConfiguration Series=\"SEARCHAI\">\n");
        buf.append( ws + "  <MaxRange>" + String.valueOf(MaxRange) + "</MaxRange>\n");
        buf.append( ws + "  <HorizontalFOV>" + String.valueOf(HorizontalFOV) + "</HorizontalFOV>\n");
        buf.append( ws + "  <VerticalFOV>" + String.valueOf(VerticalFOV) + "</VerticalFOV>\n");
        buf.append( ws + "  <DetectableHazardTypes>\n");
        for (int i=0; i<DetectableHazardTypes.size(); i++) {
        buf.append( ws + "  <HazardType>" + String.valueOf(DetectableHazardTypes.get(i)) + "</HazardType>\n");
        }
        buf.append( ws + "  </DetectableHazardTypes>\n");
        buf.append( ws + "  <PayloadID>" + String.valueOf(PayloadID) + "</PayloadID>\n");
        buf.append( ws + "  <PayloadKind>" + String.valueOf(PayloadKind) + "</PayloadKind>\n");
        buf.append( ws + "  <Parameters>\n");
        for (int i=0; i<Parameters.size(); i++) {
            buf.append( Parameters.get(i) == null ? ( ws + "    <null/>\n") : (Parameters.get(i).toXML(ws + "    ")) + "\n");
        }
        buf.append( ws + "  </Parameters>\n");
        buf.append( ws + "</HazardSensorConfiguration>");

        return buf.toString();
    }

    public boolean equals(Object anotherObj) {
        if ( anotherObj == this ) return true;
        if ( anotherObj == null ) return false;
        if ( anotherObj.getClass() != this.getClass() ) return false;
        HazardSensorConfiguration o = (HazardSensorConfiguration) anotherObj;
        if (MaxRange != o.MaxRange) return false;
        if (HorizontalFOV != o.HorizontalFOV) return false;
        if (VerticalFOV != o.VerticalFOV) return false;
         if (!DetectableHazardTypes.equals( o.DetectableHazardTypes)) return false;

        return true;
    }

    public int hashCode() {
        int hash = 0;
        hash += 31 * (int)MaxRange;
        hash += 31 * (int)HorizontalFOV;
        hash += 31 * (int)VerticalFOV;

        return hash + super.hashCode();
    }
    
}
