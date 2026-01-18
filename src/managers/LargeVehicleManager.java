package managers;

import enums.VehicleType;
import models.LargeSpot;
import models.ParkingSpot;
import models.ParkingTicket;
import models.Vehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager for large vehicles (trucks, vans)
 * Maintains its own list of large spots with independent locking
 */
public class LargeVehicleManager extends VehicleManager {
    private final List<ParkingSpot> spots = new ArrayList<>();

    public LargeVehicleManager() {
        super(VehicleType.TRUCK);
    }

    public void addSpot(ParkingSpot spot) {
        synchronized (lock) {
            spots.add(spot);
        }
    }

    @Override
    public ParkingTicket parkVehicle(Vehicle vehicle, String ticketId) {
        synchronized (lock) {
            if (vehicle.getType() != VehicleType.TRUCK && vehicle.getType() != VehicleType.VAN) {
                throw new IllegalArgumentException("This manager only handles trucks and vans");
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
            System.out.println("[LargeVehicle Manager] Total: " + spots.size() +
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
