package managers;

import enums.VehicleType;
import models.MotorcycleSpot;
import models.ParkingSpot;
import models.ParkingTicket;
import models.Vehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager for two-wheeler vehicles (motorcycles)
 * Maintains its own list of motorcycle spots with independent locking
 */
public class TwoWheelerManager extends VehicleManager {
    private final List<ParkingSpot> spots = new ArrayList<>();

    public TwoWheelerManager() {
        super(VehicleType.MOTORCYCLE);
    }

    public void addSpot(ParkingSpot spot) {
        synchronized (lock) {
            spots.add(spot);
        }
    }

    @Override
    public ParkingTicket parkVehicle(Vehicle vehicle, String ticketId) {
        synchronized (lock) {
            if (vehicle.getType() != VehicleType.MOTORCYCLE) {
                throw new IllegalArgumentException("This manager only handles motorcycles");
            }

            ParkingSpot availableSpot = getFreeSpot();
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
            return getFreeSpot() != null;
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
            System.out.println("[TwoWheeler Manager] Total: " + spots.size() +
                             ", Available: " + getAvailableSpotsCount());
        }
    }

    private ParkingSpot getFreeSpot() {
        for (ParkingSpot spot : spots) {
            if (spot.isFree()) {
                return spot;
            }
        }
        return null;
    }
}
