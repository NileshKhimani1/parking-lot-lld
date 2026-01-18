package service;

import enums.PaymentMode;
import helpers.PricingStrategy;
import models.ParkingTicket;

public class PaymentService {
    private final PricingStrategy pricingStrategy;

    public PaymentService(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    public void pay(ParkingTicket ticket, PaymentMode mode) {
        long hours = ticket.getParkedHours();
        double amount = pricingStrategy.calculate(hours);
        ticket.markPaid();
        System.out.println("Paid $" + amount + " via " + mode);
    }
}
