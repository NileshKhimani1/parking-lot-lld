package models;

import enums.SpotType;
import enums.VehicleType;
import managers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ParkingFloor manages multiple vehicle managers.
 * Each vehicle type has its own manager with independent locking.
 */
public class ParkingFloor {
    private final String name;
    private final Map<VehicleType, VehicleManager> vehicleManagers = new HashMap<>();
    private final DisplayBoard displayBoard = new DisplayBoard();

    public ParkingFloor(String name) {
        this.name = name;
        initializeManagers();
    }

    /**
     * Initialize vehicle managers for all vehicle types
     */
    private void initializeManagers() {
        vehicleManagers.put(VehicleType.MOTORCYCLE, new TwoWheelerManager());
        vehicleManagers.put(VehicleType.CAR, new FourWheelerManager());
        vehicleManagers.put(VehicleType.ELECTRIC, new FourWheelerManager());
        vehicleManagers.put(VehicleType.TRUCK, new LargeVehicleManager());
        vehicleManagers.put(VehicleType.VAN, new LargeVehicleManager());
    }

    /**
     * Add a parking spot to the appropriate manager
     */
    public void addSpot(ParkingSpot spot) {
        // Determine which manager should handle this spot
        if (spot instanceof MotorcycleSpot) {
            vehicleManagers.get(VehicleType.MOTORCYCLE).addSpot(spot);
        } else if (spot instanceof CompactSpot || spot instanceof ElectricSpot) {
            // Use the same FourWheelerManager for both
            FourWheelerManager manager = (FourWheelerManager) vehicleManagers.get(VehicleType.CAR);
            manager.addSpot(spot);
        } else if (spot instanceof LargeSpot) {
            vehicleManagers.get(VehicleType.TRUCK).addSpot(spot);
        }
        refreshDisplay();
    }

    /**
     * Park a vehicle (only locks the specific vehicle type manager)
     */
    public ParkingTicket parkVehicle(Vehicle vehicle) {
        VehicleManager manager = getManagerForVehicle(vehicle);
        if (manager == null || !manager.hasAvailableSpot()) {
            return null;
        }
        return manager.parkVehicle(vehicle, UUID.randomUUID().toString());
    }

    /**
     * Release a parked vehicle (only locks the specific vehicle type manager)
     */
    public void releaseVehicle(ParkingTicket ticket) {
        VehicleManager manager = getManagerForVehicle(ticket.getVehicle());
        if (manager != null) {
            manager.releaseVehicle(ticket);
        }
        refreshDisplay();
    }

    @Deprecated
    public ParkingSpot getFreeSpot(VehicleType type) {
        // Legacy method for backward compatibility
        VehicleManager manager = vehicleManagers.get(type);
        if (manager != null && manager.hasAvailableSpot()) {
            ParkingTicket ticket = manager.parkVehicle(new Vehicle("TEMP", type), UUID.randomUUID().toString());
            if (ticket != null) {
                return ticket.getSpot();
            }
        }
        return null;
    }

    public void refreshDisplay() {
        for (SpotType type : SpotType.values()) {
            int count = 0;
            for (VehicleManager manager : vehicleManagers.values()) {
                // Count free spots by type (would need enhancement for production)
            }
            // Simplified: show manager statuses instead
        }
    }

    public void showDisplay() {
        System.out.println("\n=== " + name + " Status ===");
        for (VehicleManager manager : vehicleManagers.values()) {
            manager.displayStatus();
        }
        System.out.println("=======================\n");
    }

    /**
     * Get the appropriate manager for a vehicle
     */
    private VehicleManager getManagerForVehicle(Vehicle vehicle) {
        return vehicleManagers.get(vehicle.getType());
    }

    public String getName() {
        return name;
    }

    public Map<VehicleType, VehicleManager> getManagers() {
        return vehicleManagers;
    }
}
