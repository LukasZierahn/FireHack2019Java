
import afrl.cmasi.AirVehicleState;
import afrl.cmasi.Location3D;

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
    
    private float timeSafetyNet = 40;
    private float upperThreshold = 99;

    public void Update(AirVehicleState vehState, UAV uav, Location3D closestRefuel) {
        remaining = vehState.getEnergyAvailable();
        if(NeedToRefuel(uav, closestRefuel)) requiresRefuel = true;
        if(remaining >= upperThreshold) requiresRefuel = false;
    }
    
    public boolean NeedToRefuel(UAV uav, Location3D closestRefuel) {
        double timeToBase = TimeToBase(uav, closestRefuel);
        
        double fuelTimeRemaining = FlightTimeRemaining(uav);
        
        return (timeToBase + 40 < fuelTimeRemaining);
    }
    
    public double TimeToBase(UAV uav, Location3D closestRefuel) {
        return FireZoneController.distance(uav.airVehicleState.getLocation(), closestRefuel) / uav.airVehicleState.getAirspeed();
    }
    
    public double FlightTimeRemaining(UAV uav) {
        double consumptionRate = uav.airVehicleState.getActualEnergyRate();
        
        return  uav.airVehicleState.getEnergyAvailable() / consumptionRate;
    }
    
    public double FlightDistanceRemaining(UAV uav) {
        // d = ts
        return uav.airVehicleState.getAirspeed() * FlightTimeRemaining(uav);
    }
    
    public double TimeToRefuelRemaining(UAV uav) {
        double flightTimeRemaining = FlightTimeRemaining(uav);
        
        return FlightTimeRemaining(uav) - timeSafetyNet;
    }
}
