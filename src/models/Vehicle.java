package models;

import enums.VehicleType;

public class Vehicle {
    private final String number;
    private final VehicleType type;

    public Vehicle(String number, VehicleType type) {
        this.number = number;
        this.type = type;
    }

    public VehicleType getType() {
        return type;
    }

    public String getNumber() {
        return number;
    }
}
