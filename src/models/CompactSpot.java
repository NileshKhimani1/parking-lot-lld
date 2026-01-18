package models;

import enums.SpotType;
import enums.VehicleType;

public class CompactSpot extends ParkingSpot {
    public CompactSpot() {
        super(SpotType.COMPACT);
    }

    @Override
    public boolean canFit(VehicleType vehicleType) {
        return vehicleType == VehicleType.CAR
                || vehicleType == VehicleType.ELECTRIC;
    }
}
