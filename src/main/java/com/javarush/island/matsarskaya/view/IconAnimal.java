package com.javarush.island.matsarskaya.view;

public enum IconAnimal {

    WOLF("🐺"),
    BOA("🐍"),
    FOX("🦊"),
    BEAR("🐻"),
    EAGLE("🦅"),

    HORSE("🐎"),
    DEER("🦌"),
    RABBIT("🐇"),
    MOUSE("🐁"),
    GOAT("🐐"),
    SHEEP("🐑"),
    BOAR("🐗"),
    BUFFALO("🐃"),
    DUCK("🦆"),
    CATERPILLAR("🐛"),

    PLANT("🌿"),
    EMPTY("▫️");

    private final String symbol;

    IconAnimal(String symbol) {
        this.symbol = symbol;
    }
    public String get() {
        return symbol;
    }
    @Override
    public String toString() {
        return symbol;
    }
}
