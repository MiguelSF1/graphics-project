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
    Entity midArmEntity;
    Entity baseArmEntity;
    Entity endArmEntity;

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

        generateArm(scene);
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis) {
        scene.getPlayer().input(diffTimeMillis, window, scene);
        scene.getCamera().move(diffTimeMillis, window);

        if (window.isKeyPressed(GLFW_KEY_UP)) {
            scene.getPointLights().get(0).setPosition(scene.getPointLights().get(0).getPosition().x + 0.1f, scene.getPointLights().get(0).getPosition().y, scene.getPointLights().get(0).getPosition().z);
        }
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {
        scene.getParticleSystem().generateParticles(new Vector3f(40, 10, 15), diffTimeMillis);
        scene.getParticleSystem().checkParticleLifespan(diffTimeMillis);


        scene.setCurveTime(scene.getCurveTime() + diffTimeMillis/1000f);

        Vector3f dir = scene.getBezierCurve().evaluateTangentAtTime(scene.getCurveTime());
        Vector3f forward = new Vector3f(dir).normalize();
        float yaw = (float) Math.toDegrees(Math.atan2(-forward.x, -forward.z));
        scene.getMovingEntity().setRotation(0, yaw, 0);

        Vector3f pos = scene.getBezierCurve().evaluateAtTime(scene.getCurveTime());
        scene.getMovingEntity().setPosition(pos.x, pos.y, pos.z);

        midArmEntity.incrementRotation(0, 2, 0);
        endArmEntity.incrementRotation(1, 0, 0);
        baseArmEntity.incrementPosition(0, 0, 0.01f);

    }


    public void generateArm(Scene scene) {
        Material armMaterial = new Material();
        List<Material> armMaterialList = new ArrayList<>();
        armMaterialList.add(armMaterial);

        Mesh baseArmMesh = OBJLoader.loadMeshFromOBJ("resources/models/arm/arm-base.obj");
        armMaterial.getMeshList().add(baseArmMesh);
        Model baseArmModel = new Model("base-arm-model", armMaterialList);
        scene.addModel(baseArmModel);

        Material armMidMaterial = new Material();
        List<Material> armMidMaterialList = new ArrayList<>();
        armMidMaterialList.add(armMidMaterial);

        Mesh midArmMesh = OBJLoader.loadMeshFromOBJ("resources/models/arm/arm-mid.obj");
        armMidMaterial.getMeshList().add(midArmMesh);
        Model midArmModel = new Model("mid-arm-model", armMidMaterialList);
        scene.addModel(midArmModel);

        Material armEndMaterial = new Material();
        List<Material> armEndMaterialList = new ArrayList<>();
        armEndMaterialList.add(armEndMaterial);

        Mesh endArmMesh = OBJLoader.loadMeshFromOBJ("resources/models/arm/arm-end.obj");
        armEndMaterial.getMeshList().add(endArmMesh);
        Model endArmModel = new Model("end-arm-model", armEndMaterialList);
        scene.addModel(endArmModel);

        baseArmEntity = new Entity("base-arm-entity", "base-arm-model", baseArmMesh);
        baseArmEntity.setPosition(40, 5, 80);
        scene.addEntity(baseArmEntity);

        midArmEntity = new Entity("mid-arm-entity", "mid-arm-model", midArmMesh);
        midArmEntity.setParent(baseArmEntity);
        midArmEntity.setPosition(0, 1 ,0);
        scene.addEntity(midArmEntity);

        endArmEntity = new Entity("end-arm-entity", "end-arm-model", endArmMesh);
        endArmEntity.setParent(midArmEntity);
        endArmEntity.setPosition(0, 1, 0);
        scene.addEntity(endArmEntity);
    }
}
