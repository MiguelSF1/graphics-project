package engine;

import org.joml.*;

import java.lang.Math;

public class Camera {

    private Vector3f position;
    private Vector3f orientation; // like upside down or something else
    private float yaw = 0;        // angle to rotate horizontally
    private float pitch = 0;      // angle to rotate vertically

    private Player player;

    private float distanceFromPlayer = 5;

    public Camera(Player player) {
        position = new Vector3f(0, 0, 0);
        orientation = new Vector3f(0, 1, 0);
        this.player = player;
    }

    public Matrix4f getViewMatrix() {
        Vector3f lookPoint = new Vector3f(0, 0, -1); // is direction, need to make it relative to where camera is

        lookPoint.rotateY((float) Math.toRadians(yaw), lookPoint);
        lookPoint.rotateX((float) Math.toRadians(pitch), lookPoint);

        lookPoint.add(position);

        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.lookAt(position, lookPoint, orientation, viewMatrix);
        return viewMatrix;
    }

    public void move(float dt, Window window) {
        setLookDir((float) window.getMouseX(), (float) window.getMouseY());

        float horizDist = (float) (distanceFromPlayer * Math.cos(Math.toRadians(20))); // 20 degrees angle to look at player
        float vertiDist = (float) (distanceFromPlayer * Math.sin(Math.toRadians(20)));
        position.x = player.getPosition().x;
        position.y = player.getPosition().y + vertiDist;
        position.z = player.getPosition().z + horizDist;
    }

    public void move1stPerson(float dt, Window window) {
        setLookDir((float) window.getMouseX(), (float) window.getMouseY());

        position.x = player.getPosition().x;
        position.y = player.getPosition().y;
        position.z = player.getPosition().z - 1.0f;
    }

    // changing angles (needed to rotate camera)
    public void setLookDir(float mouseX, float mouseY) {
        yaw = mouseX * -0.1f;
        pitch = mouseY * -0.1f;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }
}
