package models;

import enums.SpotType;
import enums.VehicleType;

public abstract class ParkingSpot {
    private final SpotType spotType;
    private boolean occupied;

    protected ParkingSpot(SpotType spotType) {
        this.spotType = spotType;
        this.occupied = false;
    }

    public boolean isFree() {
        return !occupied;
    }

    public void occupy() {
        this.occupied = true;
    }

    public void release() {
        this.occupied = false;
    }

    public SpotType getSpotType() {
        return spotType;
    }

    public abstract boolean canFit(VehicleType vehicleType);
}
