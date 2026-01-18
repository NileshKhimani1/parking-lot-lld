package managers;

import enums.VehicleType;
import models.CompactSpot;
import models.ElectricSpot;
import models.ParkingSpot;
import models.ParkingTicket;
import models.Vehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager for four-wheeler vehicles (cars and electric vehicles)
 * Maintains its own list of compact and electric spots with independent locking
 */
public class FourWheelerManager extends VehicleManager {
    private final List<ParkingSpot> spots = new ArrayList<>();

    public FourWheelerManager() {
        super(VehicleType.CAR);
    }

    public void addSpot(ParkingSpot spot) {
        synchronized (lock) {
            spots.add(spot);
        }
    }

    @Override
    public ParkingTicket parkVehicle(Vehicle vehicle, String ticketId) {
        synchronized (lock) {
            if (vehicle.getType() != VehicleType.CAR && vehicle.getType() != VehicleType.ELECTRIC) {
                throw new IllegalArgumentException("This manager only handles cars and electric vehicles");
            }

            ParkingSpot availableSpot = getFreeSpotForVehicle(vehicle.getType());
            if (availableSpot == null) {
                return null; // No available spot
            }

            availableSpot.occupy();
            return new ParkingTicket(ticketId, vehicle, availableSpot);
        }
    }

    @Override
    public void releaseVehicle(ParkingTicket ticket) {
        synchronized (lock) {
            ticket.getSpot().release();
        }
    }

    @Override
    public boolean hasAvailableSpot() {
        synchronized (lock) {
            for (ParkingSpot spot : spots) {
                if (spot.isFree() && spot.canFit(VehicleType.CAR)) {
                    return true;
                }
                if (spot.isFree() && spot.canFit(VehicleType.ELECTRIC)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public int getAvailableSpotsCount() {
        synchronized (lock) {
            int count = 0;
            for (ParkingSpot spot : spots) {
                if (spot.isFree()) {
                    count++;
                }
            }
            return count;
        }
    }

    @Override
    public int getTotalSpotsCount() {
        synchronized (lock) {
            return spots.size();
        }
    }

    @Override
    public void displayStatus() {
        synchronized (lock) {
            System.out.println("[FourWheeler Manager] Total: " + spots.size() +
                             ", Available: " + getAvailableSpotsCount());
        }
    }

    private ParkingSpot getFreeSpotForVehicle(VehicleType vehicleType) {
        for (ParkingSpot spot : spots) {
            if (spot.isFree() && spot.canFit(vehicleType)) {
                return spot;
            }
        }
        return null;
    }
}
