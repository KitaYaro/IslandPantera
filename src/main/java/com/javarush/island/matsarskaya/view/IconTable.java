package com.javarush.island.matsarskaya.view;

public enum IconTable {
    BORDER_TOP_LEFT("╔"),
    BORDER_TOP_RIGHT("╗"),
    BORDER_BOTTOM_LEFT("╚"),
    BORDER_BOTTOM_RIGHT("╝"),

    BORDER_HORIZONTAL("═"),
    BORDER_VERTICAL("║"),
    BORDER_TOP_SEPARATOR("╦"),
    BORDER_BOTTOM_SEPARATOR("╩"),
    BORDER_LEFT_SEPARATOR("╠"),
    BORDER_RIGHT_SEPARATOR("╣"),
    BORDER_CENTER_SEPARATOR("╬");

    private final String symbol;

    IconTable(String symbol) {
        this.symbol = symbol;
    }
    public String getSymbol() {
        return symbol;
    }
    @Override
    public String toString() {
        return symbol;
    }
}
