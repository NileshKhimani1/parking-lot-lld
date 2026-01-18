package managers;

import enums.VehicleType;
import models.ParkingSpot;
import models.ParkingTicket;
import models.Vehicle;

/**
 * Abstract base class for managing parking spaces for specific vehicle types.
 * Each vehicle type has its own manager with independent locking.
 */
public abstract class VehicleManager {
    private final VehicleType vehicleType;
    protected final Object lock = new Object();

    public VehicleManager(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    /**
     * Add a parking spot to this manager
     */
    public abstract void addSpot(ParkingSpot spot);

    /**
     * Attempt to park a vehicle
     */
    public abstract ParkingTicket parkVehicle(Vehicle vehicle, String ticketId);

    /**
     * Release a parked vehicle
     */
    public abstract void releaseVehicle(ParkingTicket ticket);

    /**
     * Check if there are free spots for this vehicle type
     */
    public abstract boolean hasAvailableSpot();

    /**
     * Get the number of free spots
     */
    public abstract int getAvailableSpotsCount();

    /**
     * Get the total number of spots managed by this manager
     */
    public abstract int getTotalSpotsCount();

    /**
     * Display the status of spots managed by this manager
     */
    public abstract void displayStatus();
}
