package engine;

import org.joml.Vector3f;

import java.lang.Math;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;

public class Player extends Entity {
    private static final float MOVEMENT_SPEED = 0.005f;
    private static final float TURN_SPEED = 0.040f;
    private static final float GRAVITY = -0.00005f;
    private static final float JUMP_POWER = 0.0175f;
    private static final float TERRAIN_HEIGHT = 0;

    private float curUpSpeed = 0;
    private boolean isInAir = false;

    public Player(String id, String modelId, Mesh mesh) {
        super(id, modelId, mesh);
    }

    public void input(long dt, Window window, Scene scene) {
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


        if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            if (!isInAir) {
                curUpSpeed = JUMP_POWER;
                isInAir = true;
            }
        }

        float strafe = 0;
        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            strafe = -MOVEMENT_SPEED;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            strafe = MOVEMENT_SPEED;
        }

        Vector3f oldPos = new Vector3f(getPosition());

        incrementRotation(0, curTurnSpeed * dt, 0);

        float horizontalMove = curSpeed * dt;

        float strafeMove = strafe * dt;
        float dx = (float) (horizontalMove * Math.sin(Math.toRadians(getRotation().y))); // right angle triangle | lado oposto
        float dz = (float) (horizontalMove * Math.cos(Math.toRadians(getRotation().y))); // lado adjacente
        float dy = curUpSpeed * dt;

        incrementPosition(dx + strafeMove, dy ,dz);

        curUpSpeed += GRAVITY * dt;

        if (getPosition().y < TERRAIN_HEIGHT + 1.5f) {
            setPosition(getPosition().x, TERRAIN_HEIGHT + 1.5f, getPosition().z);
            curUpSpeed = 0;
            isInAir = false;
        }

        scene.checkPlayerMove(oldPos);
    }
}
