package engine;

import org.joml.*;

import java.lang.Math;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;

public class Player extends Entity {
    private static final float MOVEMENT_SPEED = 0.005f;

    private static final float DISTANCE_FROM_CAMERA = 5f;
    private static final float PITCH = 20f;

    float verticalDistanceFromCamera = (float) (DISTANCE_FROM_CAMERA * Math.sin(Math.toRadians(PITCH)));
    float horizontalDistanceFromCamera = (float) (DISTANCE_FROM_CAMERA * Math.cos(Math.toRadians(PITCH)));


    private Camera camera;

    public Player(String id, String modelId) {
        super(id, modelId);
        camera = new Camera();
        camera.setPosition(getPosition().x, getPosition().y + verticalDistanceFromCamera, getPosition().z + horizontalDistanceFromCamera);
    }

    public void input(long dt, Window window) {
        float distance = dt * MOVEMENT_SPEED;

        if (window.isKeyPressed(GLFW_KEY_W)) {
            setPosition(getPosition().x, getPosition().y, getPosition().z - distance);
            camera.moveForward(distance);
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            setPosition(getPosition().x, getPosition().y, getPosition().z + distance);
            camera.moveBackwards(distance);
        }

        if (window.isKeyPressed(GLFW_KEY_A)) {
            setPosition(getPosition().x - distance, getPosition().y, getPosition().z);
            camera.moveLeft(distance);
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            setPosition(getPosition().x + distance, getPosition().y, getPosition().z);
            camera.moveRight(distance);
        }

        if (window.isKeyPressed(GLFW_KEY_UP)) {
            setPosition(getPosition().x, getPosition().y + distance, getPosition().z);
            camera.moveUp(distance);
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            setPosition(getPosition().x, getPosition().y - distance, getPosition().z);
            camera.moveDown(distance);
        }
    }

    public Camera getCamera() {
        return camera;
    }
}
