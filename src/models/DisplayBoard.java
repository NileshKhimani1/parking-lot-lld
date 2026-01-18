package models;

import enums.SpotType;

import java.util.EnumMap;
import java.util.Map;

public class DisplayBoard {
    private final Map<SpotType, Integer> freeSpots = new EnumMap<>(SpotType.class);

    public void update(SpotType type, int count) {
        freeSpots.put(type, count);
    }

    public void show() {
        System.out.println("---- Display Board ----");
        freeSpots.forEach((k, v) ->
                System.out.println(k + " : " + v + " free"));
    }
}
