// ===============================================================================
// Authors: AFRL/RQQA
// Organization: Air Force Research Laboratory, Aerospace Systems Directorate, Power and Control Division
// 
// Copyright (c) 2017 Government of the United State of America, as represented by
// the Secretary of the Air Force.  No copyright is claimed in the United States under
// Title 17, U.S. Code.  All Other Rights Reserved.
// ===============================================================================

// This file was auto-created by LmcpGen. Modifications will be overwritten.

package afrl.cmasi;


import avtas.lmcp.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**  Describes simulation status types */
public enum SimulationStatusType {

    /**  simulation is stopped  */
    Stopped(0),
    /**  simulation is running  */
    Running(1),
    /**  simulation paused  */
    Paused(2),
    /**  simulation is reset  */
    Reset(3);


    private final int val;

    /** creates a new enum of the specified value */
    SimulationStatusType(int val) {
        this.val = val;
    }

    /** returns the set value for this enum */
    public int getValue() {
        return val;
    }

    /** packs this enum into the LMCP buffer */
    public void pack(OutputStream out) throws IOException { LMCPUtil.putInt32(out, getValue()); }

    /** creates an enum for the value in the LMCP buffer */
    public static SimulationStatusType unpack(InputStream in) throws IOException{
        return getEnum( LMCPUtil.getInt32(in) );
    }

    /** returns a new instance of this enum that matches the passed value (null if value is not known) */
    public static SimulationStatusType getEnum(int val) {
        switch(val) {
            case 0 : return Stopped;
            case 1 : return Running;
            case 2 : return Paused;
            case 3 : return Reset;
            default: return Stopped;

        }
    }
}
