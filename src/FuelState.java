
import afrl.cmasi.AirVehicleState;
import afrl.cmasi.Location3D;
import mil.afrl.amase.util.CmasiNavUtils;

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
    private float batteryBuffer = 5; //percentage battery we pretend we don't have

    public void Update(AirVehicleState vehState, UAV uav, Location3D closestRefuel) {
        remaining = vehState.getEnergyAvailable() - 5;
        if(NeedToRefuel(uav, closestRefuel)) requiresRefuel = true;
        if(remaining >= upperThreshold) requiresRefuel = false;
    }
    
    public boolean NeedToRefuel(UAV uav, Location3D closestRefuel) {
        double timeToBase = TimeToBase(uav, closestRefuel);
        
        double fuelTimeRemaining = FlightTimeRemaining(uav);
        if(timeToBase + 40 > fuelTimeRemaining) {
            System.out.println("Need to refuel: " + timeToBase + ", " + fuelTimeRemaining);
        }
        return (timeToBase + 40 > fuelTimeRemaining);
    }
    
    public double TimeToBase(UAV uav, Location3D closestRefuel) {
        return CmasiNavUtils.distance(uav.airVehicleState.getLocation(), closestRefuel) / uav.airVehicleState.getAirspeed();
    }
    
    public double FlightTimeRemaining(UAV uav) {
        double consumptionRate = uav.airVehicleState.getActualEnergyRate();
        
        return  remaining / consumptionRate;
    }
    
    public double FlightDistanceRemaining(UAV uav) {
        // d = ts
        return uav.airVehicleState.getAirspeed() * FlightTimeRemaining(uav);
    }
    
    public double TimeToRefuelRemaining(UAV uav) {        
        return FlightTimeRemaining(uav) - timeSafetyNet;
    }
}
