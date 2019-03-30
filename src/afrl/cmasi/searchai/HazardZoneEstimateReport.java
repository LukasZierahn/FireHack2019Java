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
 Issued by an zone detection service that is computing the perceived location and vector of a             hazard.          
*/
public class HazardZoneEstimateReport extends avtas.lmcp.LMCPObject {
    
    public static final int LMCP_TYPE = 3;

    public static final String SERIES_NAME = "SEARCHAI";
    /** Series Name turned into a long for quick comparisons. */
    public static final long SERIES_NAME_ID = 6000273900112986441L;
    public static final int SERIES_VERSION = 5;


    private static final String TYPE_NAME = "HazardZoneEstimateReport";

    private static final String FULL_LMCP_TYPE_NAME = "afrl.cmasi.searchai.HazardZoneEstimateReport";

    /**  A unique ID used by the service providing the estimate. This is used to differentiate perceptions if there are multiple concurrent zones in the scenario. (Units: None)*/
    @LmcpType("uint32")
    protected long UniqueTrackingID = 0L;
    /**  Estimated shape of the zone. Can be null. If null, then the service is reporting that is is not estimating the shape of the zone. (Units: None)*/
    @LmcpType("AbstractGeometry")
    protected afrl.cmasi.AbstractGeometry EstimatedZoneShape = new afrl.cmasi.AbstractGeometry();
    /**  Estimated rate of change in the average radius of the zone. Can be negative. (Units: m/s)*/
    @LmcpType("real32")
    protected float EstimatedGrowthRate = (float)0;
    /**  Type of perceived zone being reported (Units: None)*/
    @LmcpType("HazardType")
    protected afrl.cmasi.searchai.HazardType PerceivedZoneType = afrl.cmasi.searchai.HazardType.Undefined;
    /**  Estimated true compass direction of movement of the hazard zone. (Units: Degree)*/
    @LmcpType("real32")
    protected float EstimatedZoneDirection = (float)0;
    /**  Estimated speed of the hazard zone. This is the speed of the centerpoint of the shape. (Units: m/s)*/
    @LmcpType("real32")
    protected float EstimatedZoneSpeed = (float)0;

    
    public HazardZoneEstimateReport() {
    }

    public HazardZoneEstimateReport(long UniqueTrackingID, afrl.cmasi.AbstractGeometry EstimatedZoneShape, float EstimatedGrowthRate, afrl.cmasi.searchai.HazardType PerceivedZoneType, float EstimatedZoneDirection, float EstimatedZoneSpeed){
        this.UniqueTrackingID = UniqueTrackingID;
        this.EstimatedZoneShape = EstimatedZoneShape;
        this.EstimatedGrowthRate = EstimatedGrowthRate;
        this.PerceivedZoneType = PerceivedZoneType;
        this.EstimatedZoneDirection = EstimatedZoneDirection;
        this.EstimatedZoneSpeed = EstimatedZoneSpeed;
    }


    public HazardZoneEstimateReport clone() {
        try {
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream( calcSize() );
            pack(bos);
            java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(bos.toByteArray());
            HazardZoneEstimateReport newObj = new HazardZoneEstimateReport();
            newObj.unpack(bis);
            return newObj;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
     
    /**  A unique ID used by the service providing the estimate. This is used to differentiate perceptions if there are multiple concurrent zones in the scenario. (Units: None)*/
    public long getUniqueTrackingID() { return UniqueTrackingID; }

    /**  A unique ID used by the service providing the estimate. This is used to differentiate perceptions if there are multiple concurrent zones in the scenario. (Units: None)*/
    public HazardZoneEstimateReport setUniqueTrackingID( long val ) {
        UniqueTrackingID = val;
        return this;
    }

    /**  Estimated shape of the zone. Can be null. If null, then the service is reporting that is is not estimating the shape of the zone. (Units: None)*/
    public afrl.cmasi.AbstractGeometry getEstimatedZoneShape() { return EstimatedZoneShape; }

    /**  Estimated shape of the zone. Can be null. If null, then the service is reporting that is is not estimating the shape of the zone. (Units: None)*/
    public HazardZoneEstimateReport setEstimatedZoneShape( afrl.cmasi.AbstractGeometry val ) {
        EstimatedZoneShape = val;
        return this;
    }

    /**  Estimated rate of change in the average radius of the zone. Can be negative. (Units: m/s)*/
    public float getEstimatedGrowthRate() { return EstimatedGrowthRate; }

    /**  Estimated rate of change in the average radius of the zone. Can be negative. (Units: m/s)*/
    public HazardZoneEstimateReport setEstimatedGrowthRate( float val ) {
        EstimatedGrowthRate = val;
        return this;
    }

    /**  Type of perceived zone being reported (Units: None)*/
    public afrl.cmasi.searchai.HazardType getPerceivedZoneType() { return PerceivedZoneType; }

    /**  Type of perceived zone being reported (Units: None)*/
    public HazardZoneEstimateReport setPerceivedZoneType( afrl.cmasi.searchai.HazardType val ) {
        PerceivedZoneType = val;
        return this;
    }

    /**  Estimated true compass direction of movement of the hazard zone. (Units: Degree)*/
    public float getEstimatedZoneDirection() { return EstimatedZoneDirection; }

    /**  Estimated true compass direction of movement of the hazard zone. (Units: Degree)*/
    public HazardZoneEstimateReport setEstimatedZoneDirection( float val ) {
        EstimatedZoneDirection = val;
        return this;
    }

    /**  Estimated speed of the hazard zone. This is the speed of the centerpoint of the shape. (Units: m/s)*/
    public float getEstimatedZoneSpeed() { return EstimatedZoneSpeed; }

    /**  Estimated speed of the hazard zone. This is the speed of the centerpoint of the shape. (Units: m/s)*/
    public HazardZoneEstimateReport setEstimatedZoneSpeed( float val ) {
        EstimatedZoneSpeed = val;
        return this;
    }



    public int calcSize() {
        int size = super.calcSize();  
        size += 20; // accounts for primitive types
        size += LMCPUtil.sizeOf(EstimatedZoneShape);

        return size;
    }

    public void unpack(InputStream in) throws IOException {
        UniqueTrackingID = LMCPUtil.getUint32(in);

            EstimatedZoneShape = (afrl.cmasi.AbstractGeometry) LMCPUtil.getObject(in);
        EstimatedGrowthRate = LMCPUtil.getReal32(in);

        PerceivedZoneType = afrl.cmasi.searchai.HazardType.unpack( in );

        EstimatedZoneDirection = LMCPUtil.getReal32(in);

        EstimatedZoneSpeed = LMCPUtil.getReal32(in);


    }

    public void pack(OutputStream out) throws IOException {
        LMCPUtil.putUint32(out, UniqueTrackingID);
        LMCPUtil.putObject(out, EstimatedZoneShape);
        LMCPUtil.putReal32(out, EstimatedGrowthRate);
        PerceivedZoneType.pack(out);
        LMCPUtil.putReal32(out, EstimatedZoneDirection);
        LMCPUtil.putReal32(out, EstimatedZoneSpeed);

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
        buf.append( ws + "<HazardZoneEstimateReport Series=\"SEARCHAI\">\n");
        buf.append( ws + "  <UniqueTrackingID>" + String.valueOf(UniqueTrackingID) + "</UniqueTrackingID>\n");
        if (EstimatedZoneShape!= null){
           buf.append( ws + "  <EstimatedZoneShape>\n");
           buf.append( ( EstimatedZoneShape.toXML(ws + "    ")) + "\n");
           buf.append( ws + "  </EstimatedZoneShape>\n");
        }
        buf.append( ws + "  <EstimatedGrowthRate>" + String.valueOf(EstimatedGrowthRate) + "</EstimatedGrowthRate>\n");
        buf.append( ws + "  <PerceivedZoneType>" + String.valueOf(PerceivedZoneType) + "</PerceivedZoneType>\n");
        buf.append( ws + "  <EstimatedZoneDirection>" + String.valueOf(EstimatedZoneDirection) + "</EstimatedZoneDirection>\n");
        buf.append( ws + "  <EstimatedZoneSpeed>" + String.valueOf(EstimatedZoneSpeed) + "</EstimatedZoneSpeed>\n");
        buf.append( ws + "</HazardZoneEstimateReport>");

        return buf.toString();
    }

    public boolean equals(Object anotherObj) {
        if ( anotherObj == this ) return true;
        if ( anotherObj == null ) return false;
        if ( anotherObj.getClass() != this.getClass() ) return false;
        HazardZoneEstimateReport o = (HazardZoneEstimateReport) anotherObj;
        if (UniqueTrackingID != o.UniqueTrackingID) return false;
        if (EstimatedZoneShape == null && o.EstimatedZoneShape != null) return false;
        if ( EstimatedZoneShape!= null && !EstimatedZoneShape.equals(o.EstimatedZoneShape)) return false;
        if (EstimatedGrowthRate != o.EstimatedGrowthRate) return false;
        if (PerceivedZoneType != o.PerceivedZoneType) return false;
        if (EstimatedZoneDirection != o.EstimatedZoneDirection) return false;
        if (EstimatedZoneSpeed != o.EstimatedZoneSpeed) return false;

        return true;
    }

    public int hashCode() {
        int hash = 0;
        hash += 31 * (int)UniqueTrackingID;
        hash += 31 * (int)EstimatedGrowthRate;
        hash += 31 * (int)EstimatedZoneDirection;

        return hash + super.hashCode();
    }
    
}
