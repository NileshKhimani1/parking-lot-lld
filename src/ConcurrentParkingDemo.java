import enums.PaymentMode;
import enums.VehicleType;
import helpers.HourlyPricingStrategy;
import models.*;
import service.PaymentService;

/**
 * Demonstration of the Vehicle Manager Pattern
 * Shows concurrent parking operations with fine-grained locking
 */
public class ConcurrentParkingDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Parking Lot LLD - Vehicle Manager Pattern Demo ===\n");

        // Setup
        ParkingLot lot = new ParkingLot(100);
        ParkingFloor floor1 = new ParkingFloor("F1");
        ParkingFloor floor2 = new ParkingFloor("F2");

        // Add spots to Floor 1
        floor1.addSpot(new CompactSpot());
        floor1.addSpot(new CompactSpot());
        floor1.addSpot(new ElectricSpot());
        floor1.addSpot(new MotorcycleSpot());
        floor1.addSpot(new MotorcycleSpot());
        floor1.addSpot(new LargeSpot());

        // Add spots to Floor 2
        floor2.addSpot(new CompactSpot());
        floor2.addSpot(new ElectricSpot());
        floor2.addSpot(new MotorcycleSpot());
        floor2.addSpot(new LargeSpot());

        lot.addFloor(floor1);
        lot.addFloor(floor2);

        PaymentService paymentService = new PaymentService(new HourlyPricingStrategy());
        InfoPortal portal = new InfoPortal(paymentService);
        EntryPanel entry = new EntryPanel();
        ExitPanel exit = new ExitPanel();

        System.out.println("--- Initial Floor 1 Status ---");
        floor1.showDisplay();

        System.out.println("--- Initial Floor 2 Status ---");
        floor2.showDisplay();

        // Create threads for concurrent vehicle parking
        Thread[] parkingThreads = new Thread[9];
        ParkingTicket[] tickets = new ParkingTicket[9];
        ParkingFloor[] ticketFloors = new ParkingFloor[9]; // Track which floor each ticket is on

        // Thread 1-3: Park cars on Floor 1
        for (int i = 0; i < 3; i++) {
            final int index = i;
            final ParkingFloor floor = floor1;
            parkingThreads[i] = new Thread(() -> {
                Vehicle car = new Vehicle("CAR-" + index, VehicleType.CAR);
                System.out.println("Thread-" + Thread.currentThread().getId() + " attempting to park car: " + car.getNumber());
                ParkingTicket ticket = entry.issueTicket(floor, car);
                if (ticket != null) {
                    tickets[index] = ticket;
                    ticketFloors[index] = floor;
                    System.out.println("✓ Car parked: " + car.getNumber());
                } else {
                    System.out.println("✗ No space for car: " + car.getNumber());
                }
            });
        }

        // Thread 4-5: Park motorcycles on Floor 1
        for (int i = 3; i < 5; i++) {
            final int index = i;
            final ParkingFloor floor = floor1;
            parkingThreads[i] = new Thread(() -> {
                Vehicle bike = new Vehicle("BIKE-" + index, VehicleType.MOTORCYCLE);
                System.out.println("Thread-" + Thread.currentThread().getId() + " attempting to park motorcycle: " + bike.getNumber());
                ParkingTicket ticket = entry.issueTicket(floor, bike);
                if (ticket != null) {
                    tickets[index] = ticket;
                    ticketFloors[index] = floor;
                    System.out.println("✓ Motorcycle parked: " + bike.getNumber());
                } else {
                    System.out.println("✗ No space for motorcycle: " + bike.getNumber());
                }
            });
        }

        // Thread 6-8: Park vehicles on Floor 2
        for (int i = 5; i < 8; i++) {
            final int index = i;
            final ParkingFloor floor = floor2;
            VehicleType type = i % 2 == 0 ? VehicleType.CAR : VehicleType.MOTORCYCLE;
            parkingThreads[i] = new Thread(() -> {
                Vehicle vehicle = new Vehicle("VEHICLE-" + index, type);
                System.out.println("Thread-" + Thread.currentThread().getId() + " attempting to park on Floor 2: " + vehicle.getNumber());
                ParkingTicket ticket = entry.issueTicket(floor, vehicle);
                if (ticket != null) {
                    tickets[index] = ticket;
                    ticketFloors[index] = floor;
                    System.out.println("✓ Vehicle parked on Floor 2: " + vehicle.getNumber());
                } else {
                    System.out.println("✗ No space on Floor 2: " + vehicle.getNumber());
                }
            });
        }

        // Thread 9: Try to park a large vehicle
        final int largeVehicleIndex = 8;
        parkingThreads[largeVehicleIndex] = new Thread(() -> {
            Vehicle truck = new Vehicle("TRUCK-1", VehicleType.TRUCK);
            System.out.println("Thread-" + Thread.currentThread().getId() + " attempting to park truck: " + truck.getNumber());
            ParkingTicket ticket = entry.issueTicket(floor1, truck);
            if (ticket != null) {
                tickets[largeVehicleIndex] = ticket;
                ticketFloors[largeVehicleIndex] = floor1;
                System.out.println("✓ Truck parked: " + truck.getNumber());
            } else {
                System.out.println("✗ No space for truck: " + truck.getNumber());
            }
        });

        // Start all threads
        System.out.println("\n--- Starting concurrent parking operations ---\n");
        for (Thread t : parkingThreads) {
            t.start();
        }

        // Wait for all threads to complete
        for (Thread t : parkingThreads) {
            t.join();
        }

        System.out.println("\n--- After Parking Operations ---");
        System.out.println("\nFloor 1 Status:");
        floor1.showDisplay();

        System.out.println("Floor 2 Status:");
        floor2.showDisplay();

        // Process payments and exits
        System.out.println("\n--- Processing Payments and Exits ---\n");
        for (int i = 0; i < tickets.length; i++) {
            if (tickets[i] != null) {
                System.out.println("Processing payment and exit for Vehicle-" + i);
                portal.pay(tickets[i], i % 2 == 0 ? PaymentMode.CARD : PaymentMode.CASH);
                exit.exit(tickets[i], ticketFloors[i]);
            }
        }

        System.out.println("\n--- Final Status ---");
        System.out.println("\nFloor 1 Status:");
        floor1.showDisplay();

        System.out.println("Floor 2 Status:");
        floor2.showDisplay();

        System.out.println("\n=== Demo Complete ===");
        System.out.println("\nKey Benefits of Vehicle Manager Pattern:");
        System.out.println("1. ✓ Fine-grained locking: Only specific vehicle type managers are locked");
        System.out.println("2. ✓ High concurrency: Different vehicle types can park simultaneously");
        System.out.println("3. ✓ Scalable: Easy to add new vehicle managers for new vehicle types");
        System.out.println("4. ✓ Multiple floors: Each floor has independent managers");
        System.out.println("5. ✓ No bottleneck: Parking lot or floor-level locks are eliminated");
    }
}
