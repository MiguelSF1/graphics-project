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

        scene.updateEnemyMovement();

        Vector3f dir = scene.getBezierCurve().evaluateTangentAtTime(scene.getCurveTime());
        Vector3f forward = new Vector3f(dir).normalize();
        float yaw = (float) Math.toDegrees(Math.atan2(-forward.x, -forward.z));
        scene.getMovingEntity().setRotation(0, yaw, 0);

        Vector3f pos = scene.getBezierCurve().evaluateAtTime(scene.getCurveTime());
        scene.getMovingEntity().setPosition(pos.x, pos.y, pos.z);


        float time = scene.getCurveTime();
        float walkSpeed = 3f;
        float walkAmplitude = 30f;

        for (int i = 0; i < scene.getFootEntitiesL().size(); i++) {
            Entity leftFoot = scene.getFootEntitiesL().get(i);
            Entity rightFoot = scene.getFootEntitiesR().get(i);

            // Sinusoidal rotation || angle of rotation changes in a smooth way
            float angleL = (float)Math.sin(time * walkSpeed * 2 * Math.PI) * walkAmplitude;
            float angleR = (float)Math.sin(time * walkSpeed * 2 * Math.PI + Math.PI) * walkAmplitude;

            leftFoot.setRotation(angleL, 90, 0);
            rightFoot.setRotation(angleR, 90, 0);
        }

    }
}
