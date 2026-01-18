package models;

import enums.SpotType;
import enums.VehicleType;

public class ElectricSpot extends ParkingSpot {
    public ElectricSpot() {
        super(SpotType.ELECTRIC);
    }

    @Override
    public boolean canFit(VehicleType vehicleType) {
        return vehicleType == VehicleType.ELECTRIC;
    }
}
