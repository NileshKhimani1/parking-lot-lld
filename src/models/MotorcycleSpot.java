package models;

import enums.SpotType;
import enums.VehicleType;

public class MotorcycleSpot extends ParkingSpot {
    public MotorcycleSpot() {
        super(SpotType.MOTORCYCLE);
    }

    @Override
    public boolean canFit(VehicleType vehicleType) {
        return vehicleType == VehicleType.MOTORCYCLE;
    }
}
