package game;

import engine.*;
import engine.Scene;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Main implements EngineLogic {

    Entity tableEntity;

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

        Mesh tableMesh = OBJLoader.loadMeshFromOBJ("resources/models/table/table.obj");
        material.getMeshList().add(tableMesh);
        Model tableModel = new Model("table-model", materialList);
        scene.addModel(tableModel);

        tableEntity = new Entity("table-entity", "table-model", tableMesh);


        float yMax = tableEntity.getMesh().getAabbMax().y;
        float yMin = tableEntity.getMesh().getAabbMin().y;

        float tableHeight = scene.getTerrain().getTerrainHeight(40, 15) + (yMax - yMin) * 0.5f;

        tableEntity.setPosition(40, tableHeight, 15);
        scene.addEntity(tableEntity);
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis) {
        scene.getPlayer().input(diffTimeMillis, window, scene);
        scene.getCamera().move(diffTimeMillis, window);
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {

    }
}
