package models;

import enums.SpotType;
import enums.VehicleType;

public class LargeSpot extends ParkingSpot {
    public LargeSpot() {
        super(SpotType.LARGE);
    }

    @Override
    public boolean canFit(VehicleType vehicleType) {
        return vehicleType == VehicleType.TRUCK
                || vehicleType == VehicleType.VAN;
    }
}

