import afrl.cmasi.AirVehicleConfiguration;
import afrl.cmasi.AirVehicleState;

public class UAV {

    public AirVehicleState airVehicleState;
    public AirVehicleConfiguration airVehicleConfiguration;

    public UAV(AirVehicleConfiguration airVehicleConfiguration) {
        this.airVehicleConfiguration = airVehicleConfiguration;
    }
}
