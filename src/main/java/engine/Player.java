package engine;

import java.lang.Math;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;

public class Player extends Entity {
    private static final float MOVEMENT_SPEED = 0.005f;
    private static final float TURN_SPEED = 0.040f;

    public Player(String id, String modelId, Mesh mesh) {
        super(id, modelId, mesh);
    }

    public void input(long dt, Window window) {
        float curSpeed = 0;
        if (window.isKeyPressed(GLFW_KEY_W)) {
            curSpeed = -MOVEMENT_SPEED;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            curSpeed = MOVEMENT_SPEED;
        }

        float curTurnSpeed = 0;
        if (window.isKeyPressed(GLFW_KEY_A)) {
            curTurnSpeed = TURN_SPEED;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            curTurnSpeed = -TURN_SPEED;
        }

        float curUpSpeed = 0;
        if (window.isKeyPressed(GLFW_KEY_UP)) {
            curUpSpeed = MOVEMENT_SPEED;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            curUpSpeed = -MOVEMENT_SPEED;
        }

        float strafe = 0;
        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            strafe = -MOVEMENT_SPEED;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            strafe = MOVEMENT_SPEED;
        }

        incrementRotation(0, curTurnSpeed * dt, 0);

        float distance = dt * curSpeed;
        float jump = dt * curUpSpeed;
        strafe *= dt;

        float dx = (float) (distance * Math.sin(Math.toRadians(getRotation().y))); // right angle triangle | lado oposto
        float dz = (float) (distance * Math.cos(Math.toRadians(getRotation().y))); // lado adjacente

        incrementPosition(dx + strafe, jump, dz);
    }
}
