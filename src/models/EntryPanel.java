package models;

public class EntryPanel {
    /**
     * Issue a parking ticket for a vehicle
     * Uses vehicle manager's fine-grained locking
     */
    public ParkingTicket issueTicket(ParkingFloor floor, Vehicle vehicle) {
        return floor.parkVehicle(vehicle);
    }

    /**
     * Deprecated: Use issueTicket(ParkingFloor, Vehicle) instead
     * This method is kept for backward compatibility
     */
    @Deprecated
    public ParkingTicket issueTicket(ParkingFloor floor, Vehicle vehicle, String ticketId) {
        ParkingTicket ticket = floor.parkVehicle(vehicle);
        return ticket;
    }
}
