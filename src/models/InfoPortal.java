package models;

import enums.PaymentMode;
import service.PaymentService;

public class InfoPortal {
    private final PaymentService paymentService;

    public InfoPortal(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void pay(ParkingTicket ticket, PaymentMode mode) {
        paymentService.pay(ticket, mode);
    }
}

