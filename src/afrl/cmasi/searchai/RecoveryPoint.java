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
 A named location for recovering UAVs  
*/
public class RecoveryPoint extends afrl.cmasi.AbstractZone {
    
    public static final int LMCP_TYPE = 4;

    public static final String SERIES_NAME = "SEARCHAI";
    /** Series Name turned into a long for quick comparisons. */
    public static final long SERIES_NAME_ID = 6000273900112986441L;
    public static final int SERIES_VERSION = 5;


    private static final String TYPE_NAME = "RecoveryPoint";

    private static final String FULL_LMCP_TYPE_NAME = "afrl.cmasi.searchai.RecoveryPoint";

    /**  Name of Location (Units: None)*/
    @LmcpType("string")
    protected String LocationName = "";

    
    public RecoveryPoint() {
    }

    public RecoveryPoint(long ZoneID, float MinAltitude, afrl.cmasi.AltitudeType MinAltitudeType, float MaxAltitude, afrl.cmasi.AltitudeType MaxAltitudeType, long StartTime, long EndTime, float Padding, String Label, afrl.cmasi.AbstractGeometry Boundary, String LocationName){
        this.ZoneID = ZoneID;
        this.MinAltitude = MinAltitude;
        this.MinAltitudeType = MinAltitudeType;
        this.MaxAltitude = MaxAltitude;
        this.MaxAltitudeType = MaxAltitudeType;
        this.StartTime = StartTime;
        this.EndTime = EndTime;
        this.Padding = Padding;
        this.Label = Label;
        this.Boundary = Boundary;
        this.LocationName = LocationName;
    }


    public RecoveryPoint clone() {
        try {
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream( calcSize() );
            pack(bos);
            java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(bos.toByteArray());
            RecoveryPoint newObj = new RecoveryPoint();
            newObj.unpack(bis);
            return newObj;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
     
    /**  Name of Location (Units: None)*/
    public String getLocationName() { return LocationName; }

    /**  Name of Location (Units: None)*/
    public RecoveryPoint setLocationName( String val ) {
        LocationName = val;
        return this;
    }



    public int calcSize() {
        int size = super.calcSize();  
        size += 0; // accounts for primitive types
        size += LMCPUtil.sizeOfString(LocationName);

        return size;
    }

    public void unpack(InputStream in) throws IOException {
        super.unpack(in);
        LocationName = LMCPUtil.getString(in);


    }

    public void pack(OutputStream out) throws IOException {
        super.pack(out);
        LMCPUtil.putString(out, LocationName);

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
        buf.append( ws + "<RecoveryPoint Series=\"SEARCHAI\">\n");
        buf.append( ws + "  <LocationName>" + String.valueOf(LocationName) + "</LocationName>\n");
        buf.append( ws + "  <ZoneID>" + String.valueOf(ZoneID) + "</ZoneID>\n");
        buf.append( ws + "  <MinAltitude>" + String.valueOf(MinAltitude) + "</MinAltitude>\n");
        buf.append( ws + "  <MinAltitudeType>" + String.valueOf(MinAltitudeType) + "</MinAltitudeType>\n");
        buf.append( ws + "  <MaxAltitude>" + String.valueOf(MaxAltitude) + "</MaxAltitude>\n");
        buf.append( ws + "  <MaxAltitudeType>" + String.valueOf(MaxAltitudeType) + "</MaxAltitudeType>\n");
        buf.append( ws + "  <AffectedAircraft>\n");
        for (int i=0; i<AffectedAircraft.size(); i++) {
        buf.append( ws + "  <int64>" + String.valueOf(AffectedAircraft.get(i)) + "</int64>\n");
        }
        buf.append( ws + "  </AffectedAircraft>\n");
        buf.append( ws + "  <StartTime>" + String.valueOf(StartTime) + "</StartTime>\n");
        buf.append( ws + "  <EndTime>" + String.valueOf(EndTime) + "</EndTime>\n");
        buf.append( ws + "  <Padding>" + String.valueOf(Padding) + "</Padding>\n");
        buf.append( ws + "  <Label>" + String.valueOf(Label) + "</Label>\n");
        if (Boundary!= null){
           buf.append( ws + "  <Boundary>\n");
           buf.append( ( Boundary.toXML(ws + "    ")) + "\n");
           buf.append( ws + "  </Boundary>\n");
        }
        buf.append( ws + "</RecoveryPoint>");

        return buf.toString();
    }

    public boolean equals(Object anotherObj) {
        if ( anotherObj == this ) return true;
        if ( anotherObj == null ) return false;
        if ( anotherObj.getClass() != this.getClass() ) return false;
        RecoveryPoint o = (RecoveryPoint) anotherObj;
        if (LocationName == null && o.LocationName != null) return false;
        if ( LocationName!= null && !LocationName.equals(o.LocationName)) return false;

        return true;
    }

    public int hashCode() {
        int hash = 0;

        return hash + super.hashCode();
    }
    
}
