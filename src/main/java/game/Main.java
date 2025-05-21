package game;

import engine.*;
import engine.Scene;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
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

        Material starMaterial = new Material();
        scene.getTextureCache().createTexture("resources/models/star/star.png");
        starMaterial.setTexturePath("resources/models/star/star.png");
        List<Material> starMaterialList = new ArrayList<>();
        starMaterialList.add(starMaterial);
        Mesh starMesh = OBJLoader.loadMeshFromOBJ("resources/models/star/star.obj");
        starMaterial.getMeshList().add(starMesh);
        Model starModel = new Model("star-model", starMaterialList);
        scene.addModel(starModel);
        Entity starEntity = new Entity("star-entity", "star-model", starMesh);
        starEntity.setPosition(50, 2, 90);
        starEntity.setRotation(90, 0, 0);
        scene.addEntity(starEntity);
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis) {
        scene.getPlayer().input(diffTimeMillis, window, scene);
        scene.getCamera().move(diffTimeMillis, window);
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {
        scene.getParticleSystem().generateParticles(new Vector3f(40, 10, 15), diffTimeMillis);
        scene.getParticleSystem().checkParticleLifespan(diffTimeMillis);
    }
}
