package game;

import engine.*;
import engine.Scene;

public class Main implements EngineLogic {


    public static void main(String[] args) {
        Main main = new Main();
        Engine gameEng = new Engine("Graphics Project", main);
        gameEng.start();
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void init(Window window, Scene scene, Render render) {
        Entity cubeEntity = new Entity("cube-entity", "cube-model");
        cubeEntity.setPosition(0, 0, -10);
        scene.addEntity(cubeEntity);
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis) {
        scene.getPlayer().input(diffTimeMillis, window);
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {

    }
}
