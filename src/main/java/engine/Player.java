package engine;

import org.joml.*;

import java.lang.Math;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;

public class Player extends Entity {
    private static final float MOVEMENT_SPEED = 0.005f;
    private static final float TURN_SPEED = 0.040f;
    private static final float DISTANCE_FROM_CAMERA = 5f;

    private float verticalDistanceFromCamera;
    private float horizontalDistanceFromCamera;

    private Camera camera;

    public Player(String id, String modelId) {
        super(id, modelId);
        camera = new Camera();
        //verticalDistanceFromCamera = (float) (DISTANCE_FROM_CAMERA * Math.sin(Math.toRadians(camera.getPitch())));
        //horizontalDistanceFromCamera = (float) (DISTANCE_FROM_CAMERA * Math.cos(Math.toRadians(camera.getPitch())));
        //camera.setPosition(getPosition().x, getPosition().y + verticalDistanceFromCamera, getPosition().z + horizontalDistanceFromCamera);
    }

    public void input(long dt, Window window) {
        float distance = dt * MOVEMENT_SPEED;
        float turn = dt * TURN_SPEED;

        float dx = (float) (distance * Math.sin(Math.toRadians(getRotation().x)));
        float dz = (float) (distance * Math.cos(Math.toRadians(getRotation().y)));

        //incrementRotation(0, turn, 0);

        if (window.isKeyPressed(GLFW_KEY_W)) {
            incrementPosition(dx, 0, -dz);
            //setPosition(getPosition().x, getPosition().y, getPosition().z + distance);
            //camera.moveCamera(0,0, -distance);
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            incrementPosition(dx, 0, dz);
            //setPosition(getPosition().x, getPosition().y, getPosition().z + distance);
            //camera.moveCamera(0,0, distance);
        }

        if (window.isKeyPressed(GLFW_KEY_A)) {
            incrementPosition(-dx, 0, dz);
            //setPosition(getPosition().x - distance, getPosition().y, getPosition().z);
            //camera.moveCamera(-distance, 0, 0);
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            incrementPosition(dx, 0, dz);
           //setPosition(getPosition().x + distance, getPosition().y, getPosition().z);
            //camera.moveCamera(distance, 0, 0);
        }

        if (window.isKeyPressed(GLFW_KEY_UP)) {
            incrementPosition(0, distance, 0);
            //setPosition(getPosition().x, getPosition().y + distance, getPosition().z);
            //camera.moveCamera(0, distance, 0);
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            incrementPosition(0, -distance, 0);
            //setPosition(getPosition().x, getPosition().y - distance, getPosition().z);
            //camera.moveCamera(0, -distance, 0);
        }

        camera.setLookDir((float) window.getMouseX(), (float) window.getMouseY());

        verticalDistanceFromCamera = (float) (DISTANCE_FROM_CAMERA * Math.sin(Math.toRadians(camera.getPitch())));
        horizontalDistanceFromCamera = (float) (DISTANCE_FROM_CAMERA * Math.cos(Math.toRadians(camera.getPitch())));
        setCameraPosition();
        camera.setYaw(180 - (getRotation().y + camera.getYaw()));
    }

    public Camera getCamera() {
        return camera;
    }

    private void setCameraPosition() {
        float theta = getRotation().y + camera.getYaw();
        float offsetX = (float) (horizontalDistanceFromCamera * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistanceFromCamera * Math.cos(Math.toRadians(theta)));
        camera.setPosition(getPosition().x - offsetX, getPosition().y + verticalDistanceFromCamera, getPosition().z - offsetZ);
    }
}
