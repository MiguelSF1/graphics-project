package engine;

import org.joml.*;

import java.lang.Math;

public class Camera {
    private Vector3f position;
    private float yaw = 0f;                 // horizontal angle
    private float pitch = 25f;              // vertical angle

    private final Player player;

    private float prevMouseX = 0f;
    private float prevMouseY = 0f;

    private boolean celebrating = false;
    private float celebrationTimer = 0f;
    private final float MAX_CELEBRATION_TIME = 2000f;
    private float baseYaw = 0f;

    public Camera(Player player) {
        this.player = player;
        this.position = new Vector3f();
    }

    public void move(float dt, Window window) {
        if (celebrating) {
            celebrationTimer += dt;

            float progress = celebrationTimer / MAX_CELEBRATION_TIME;
            float angle = baseYaw + 360f * progress;
            float distanceFromPlayer = 10f;
            float pitchAngle = 25f;

            float horizontalDist = (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitchAngle)));
            float verticalDist = (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitchAngle)));

            float offsetX = (float) (horizontalDist * Math.sin(Math.toRadians(angle)));
            float offsetZ = (float) (horizontalDist * Math.cos(Math.toRadians(angle)));

            Vector3f playerPos = player.getPosition();
            position.x = playerPos.x - offsetX;
            position.y = playerPos.y + verticalDist;
            position.z = playerPos.z + offsetZ;

            if (celebrationTimer >= MAX_CELEBRATION_TIME) {
                celebrating = false;
                celebrationTimer = 0f;
            }

            return;
        }


        float mouseX = (float) window.getMouseX();
        float mouseY = (float) window.getMouseY();
        float dx = mouseX - prevMouseX;
        float dy = mouseY - prevMouseY;
        prevMouseX = mouseX;
        prevMouseY = mouseY;

        float mouseSens = 0.1f;
        yaw   += dx * mouseSens;
        pitch -= dy * mouseSens;
        pitch = Math.max(10, Math.min(80, pitch));

        // right-angle triangle
        float distanceFromPlayer = 10f;
        float horizontalDist = (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
        float verticalDist   = (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));

        // where the camera should be on the XZ-plane calculated from the yaw
        float offsetX = (float) (horizontalDist * Math.sin(Math.toRadians(yaw)));
        float offsetZ = (float) (horizontalDist * Math.cos(Math.toRadians(yaw)));

        Vector3f playerPos = player.getPosition();
        position.x = playerPos.x - offsetX;
        position.y = playerPos.y + verticalDist;
        position.z = playerPos.z + offsetZ;
    }

    public Matrix4f getViewMatrix() {
        Vector3f up = new Vector3f(0, 1, 0);
        Matrix4f view = new Matrix4f();
        view.lookAt(position, player.getPosition(), up); // position target up
        return view;
    }

    public void startCelebration() {
        celebrating = true;
        celebrationTimer = 0f;
        baseYaw = yaw;
    }
}

