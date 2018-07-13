package com.accenture.salvo.model.ships;

public enum ShipType {
    CARRIER(5),
    BATTLESHIP(4),
    SUBMARINE(3),
    DESTROYER(3),
    PATROLBOAT(2);

    private final int lenght;

    ShipType(int lenght) {
        this.lenght = lenght;
    }

    public int getLenght() {
        return this.lenght;
    }
}
