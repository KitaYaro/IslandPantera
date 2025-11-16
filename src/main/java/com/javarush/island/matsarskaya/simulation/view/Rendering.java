package com.javarush.island.matsarskaya.simulation.view;

import com.javarush.island.matsarskaya.map.GameMap;

public class Rendering implements Runnable{
    private final GameMap gameMap;
    private final ConsoleRender consoleRender;

    public Rendering(GameMap gameMap) {
        this.gameMap = gameMap;
        this.consoleRender = new ConsoleRender();
    }

    @Override
    public void run() {
        if (gameMap != null) {
            consoleRender.renderMap(gameMap);
        }
    }
}
