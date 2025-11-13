package com.javarush.island.matsarskaya.simulation;

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

    private final String symbolTable;
    IconTable(String symbolTable) {
        this.symbolTable = symbolTable;}
    public String getSymbol() {
        return symbolTable;
    }
    @Override
    public String toString() {
        return symbolTable;
    }
}
