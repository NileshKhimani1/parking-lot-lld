package models;

import java.util.ArrayList;
import java.util.List;

public class ParkingLot {
    private final List<ParkingFloor> floors = new ArrayList<>();
    private final int capacity;
    private int occupied = 0;

    public ParkingLot(int capacity) {
        this.capacity = capacity;
    }

    public void addFloor(ParkingFloor floor) {
        floors.add(floor);
    }

    public boolean isFull() {
        return occupied >= capacity;
    }

    public void vehicleEntered() {
        occupied++;
    }

    public void vehicleExited() {
        occupied--;
    }

    public List<ParkingFloor> getFloors() {
        return floors;
    }
}

