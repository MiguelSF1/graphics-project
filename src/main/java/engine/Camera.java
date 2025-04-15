package engine;

import org.joml.*;

import java.lang.Math;

public class Camera {

    private Vector3f position;
    private Vector3f orientation; // like upside down or something else
    private float yaw = 0;        // angle to rotate horizontally
    private float pitch = 0;      // angle to rotate vertically

    public Camera() {
        position = new Vector3f(0, 0, 0);
        orientation = new Vector3f(0, 1, 0);
    }

    public void moveCamera(float x, float y, float z) {
        Vector3f offset = new Vector3f(x, y, z);
        offset.rotateY((float) Math.toRadians(yaw), offset); // make sure that when the camera is rotated the direction of the keyboard movement also changes

        position.x += offset.x;
        position.y += offset.y;
        position.z += offset.z;
    }

    // changing angles (needed to rotate camera)
    public void setLookDir(float mouseX, float mouseY) {
        yaw = mouseX * -0.1f;
        pitch = mouseY * -0.1f;
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

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }
}
