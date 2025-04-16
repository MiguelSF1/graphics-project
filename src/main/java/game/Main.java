package game;

import engine.*;
import engine.Scene;

import static org.lwjgl.glfw.GLFW.*;

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

        if (window.isKeyPressed(GLFW_KEY_Z)) {
            scene.selectCamera(0);
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            scene.selectCamera(1);
        } else if (window.isKeyPressed(GLFW_KEY_C)) {
            scene.selectCamera(2);
        }

        if (scene.getCurCameraIdx() == 0) {
            scene.getCamera().move(diffTimeMillis, window);
        } else {
            scene.getCamera().setLookDir((float) window.getMouseX(), (float) window.getMouseY());
        }

    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {

    }
}
