
import afrl.cmasi.AirVehicleState;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sam
 */
public class FuelState {
    public float remaining = 100;
    public boolean requiresRefuel = false;
    
    private float lowerThreshold = 0;
    private float upperThreshold = 99;

    public void Update(AirVehicleState vehState) {
        remaining = vehState.getEnergyAvailable();
        System.out.println("Remaining - " + remaining + ", " + vehState.getID());
        if(remaining < lowerThreshold) requiresRefuel = true;
        if(remaining > upperThreshold) requiresRefuel = false;
    }
}
