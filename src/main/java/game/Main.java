package game;

import engine.*;
import engine.Scene;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Main implements EngineLogic {

    Terrain terrain;

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
        Material material = new Material();
        List<Material> materialList = new ArrayList<>();
        materialList.add(material);

        Mesh mesh = OBJLoader.loadMeshFromOBJ("resources/models/table/table.obj");
        material.getMeshList().add(mesh);
        Model tableModel = new Model("table-model", materialList);
        scene.addModel(tableModel);

        Entity tableEntity = new Entity("table-entity", "table-model");
        tableEntity.setPosition(0, 0, -10);
        scene.addEntity(tableEntity);

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
        } else if (window.isKeyPressed(GLFW_KEY_V)) {
            scene.selectCamera(3);
        }

        if (scene.getCurCameraIdx() == 0) {
            scene.getCamera().move(diffTimeMillis, window);
        } else if (scene.getCurCameraIdx() == 3) {
            scene.getCamera().move1stPerson(diffTimeMillis, window);
        } else {
            scene.getCamera().setLookDir((float) window.getMouseX(), (float) window.getMouseY());
        }

    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {

    }
}
