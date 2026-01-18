//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import enums.PaymentMode;
import enums.VehicleType;
import helpers.HourlyPricingStrategy;
import models.*;
import service.PaymentService;

void main() {

    ParkingLot lot = new ParkingLot(10);

    ParkingFloor floor1 = new ParkingFloor("F1");
    floor1.addSpot(new CompactSpot());
    floor1.addSpot(new ElectricSpot());
    floor1.addSpot(new MotorcycleSpot());

    lot.addFloor(floor1);

    EntryPanel entry = new EntryPanel();
    ExitPanel exit = new ExitPanel();

    PaymentService paymentService =
            new PaymentService(new HourlyPricingStrategy());
    InfoPortal portal = new InfoPortal(paymentService);

    Vehicle car = new Vehicle("KA01AB1234", VehicleType.CAR);

    ParkingTicket ticket = entry.issueTicket(floor1, car);
    if (ticket == null) {
        System.out.println("Parking Full");
        return;
    }

    portal.pay(ticket, PaymentMode.CARD);
    exit.exit(ticket, floor1);

    floor1.showDisplay();
}
