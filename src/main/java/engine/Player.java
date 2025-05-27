package engine;

import org.joml.Vector3f;

import java.lang.Math;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity {
    private static final float MOVEMENT_SPEED = 0.01f;
    private static final float TURN_SPEED = 0.040f;
    private static final float GRAVITY = -0.00005f;
    private static final float JUMP_POWER = 0.0175f;

    private float curUpSpeed = 0;
    private boolean isInAir = false;

    private Terrain terrain;

    public Player(String id, String modelId, Mesh mesh, Terrain terrain) {
        super(id, modelId, mesh);
        this.terrain = terrain;
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

        if (dx == 0 && strafeMove == 0 && dy == 0 && dz == 0) {
            return;
        }

        incrementPosition(dx + strafeMove, 0, 0);
        scene.checkPlayerCollisions(oldPos, 'X');

        incrementPosition(0, dy , 0);
        scene.checkPlayerCollisions(oldPos, 'Y');

        incrementPosition(0, 0, dz);
        scene.checkPlayerCollisions(oldPos, 'Z');

        curUpSpeed += GRAVITY * dt;

        float minPlayerHeight = terrain.getTerrainHeight(getPosition().x, getPosition().z) + (getMesh().getAabbMax().y - getMesh().getAabbMin().y) * 0.5f;
        if (getPosition().y < minPlayerHeight + 0.4) {
            setPosition(getPosition().x, minPlayerHeight + 0.4f, getPosition().z);
            curUpSpeed = 0;
            isInAir = false;
        }

    }

    public void setCurUpSpeed(float curUpSpeed) {
        this.curUpSpeed = curUpSpeed;
    }

    public void setIsInAir(boolean isInAir) {
        this.isInAir = isInAir;
    }
}
