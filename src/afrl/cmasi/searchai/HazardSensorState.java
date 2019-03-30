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
 Current state of a hazard sensor 
*/
public class HazardSensorState extends afrl.cmasi.CameraState {
    
    public static final int LMCP_TYPE = 7;

    public static final String SERIES_NAME = "SEARCHAI";
    /** Series Name turned into a long for quick comparisons. */
    public static final long SERIES_NAME_ID = 6000273900112986441L;
    public static final int SERIES_VERSION = 5;


    private static final String TYPE_NAME = "HazardSensorState";

    private static final String FULL_LMCP_TYPE_NAME = "afrl.cmasi.searchai.HazardSensorState";


    
    public HazardSensorState() {
    }



    public HazardSensorState clone() {
        try {
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream( calcSize() );
            pack(bos);
            java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(bos.toByteArray());
            HazardSensorState newObj = new HazardSensorState();
            newObj.unpack(bis);
            return newObj;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
     


    public int calcSize() {
        int size = super.calcSize();  
        size += 0; // accounts for primitive types

        return size;
    }

    public void unpack(InputStream in) throws IOException {
        super.unpack(in);

    }

    public void pack(OutputStream out) throws IOException {
        super.pack(out);

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
        buf.append( ws + "<HazardSensorState Series=\"SEARCHAI\">\n");
        buf.append( ws + "  <HorizontalFieldOfView>" + String.valueOf(HorizontalFieldOfView) + "</HorizontalFieldOfView>\n");
        buf.append( ws + "  <VerticalFieldOfView>" + String.valueOf(VerticalFieldOfView) + "</VerticalFieldOfView>\n");
        buf.append( ws + "  <Footprint>\n");
        for (int i=0; i<Footprint.size(); i++) {
            buf.append( Footprint.get(i) == null ? ( ws + "    <null/>\n") : (Footprint.get(i).toXML(ws + "    ")) + "\n");
        }
        buf.append( ws + "  </Footprint>\n");
        if (Centerpoint!= null){
           buf.append( ws + "  <Centerpoint>\n");
           buf.append( ( Centerpoint.toXML(ws + "    ")) + "\n");
           buf.append( ws + "  </Centerpoint>\n");
        }
        buf.append( ws + "  <PointingMode>" + String.valueOf(PointingMode) + "</PointingMode>\n");
        buf.append( ws + "  <Azimuth>" + String.valueOf(Azimuth) + "</Azimuth>\n");
        buf.append( ws + "  <Elevation>" + String.valueOf(Elevation) + "</Elevation>\n");
        buf.append( ws + "  <Rotation>" + String.valueOf(Rotation) + "</Rotation>\n");
        buf.append( ws + "  <PayloadID>" + String.valueOf(PayloadID) + "</PayloadID>\n");
        buf.append( ws + "  <Parameters>\n");
        for (int i=0; i<Parameters.size(); i++) {
            buf.append( Parameters.get(i) == null ? ( ws + "    <null/>\n") : (Parameters.get(i).toXML(ws + "    ")) + "\n");
        }
        buf.append( ws + "  </Parameters>\n");
        buf.append( ws + "</HazardSensorState>");

        return buf.toString();
    }

    public boolean equals(Object anotherObj) {
        if ( anotherObj == this ) return true;
        if ( anotherObj == null ) return false;
        if ( anotherObj.getClass() != this.getClass() ) return false;
        HazardSensorState o = (HazardSensorState) anotherObj;

        return true;
    }

    public int hashCode() {
        int hash = 0;

        return hash + super.hashCode();
    }
    
}
