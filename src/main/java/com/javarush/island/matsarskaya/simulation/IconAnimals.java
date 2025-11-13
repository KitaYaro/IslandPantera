package com.javarush.island.matsarskaya.simulation;

public enum IconAnimals {
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

    private final String symbolAnimal;

    IconAnimals(String symbolAnimal) {
        this.symbolAnimal = symbolAnimal;
    }
    public String get() {
        return symbolAnimal;
    }
    @Override
    public String toString() {
        return symbolAnimal;
    }
}

