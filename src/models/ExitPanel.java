package models;

public class ExitPanel {
    /**
     * Process vehicle exit with payment verification
     * Uses vehicle manager's fine-grained locking
     */
    public void exit(ParkingTicket ticket, ParkingFloor floor) {
        if (!ticket.isPaid()) {
            throw new IllegalStateException("Payment required before exit");
        }
        floor.releaseVehicle(ticket);
        System.out.println("Exit successful");
    }
}
