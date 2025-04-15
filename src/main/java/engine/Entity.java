package engine;

import org.joml.*;

import java.lang.Math;

public class Entity {
    private final String id;
    private final String modelId;

    private Vector3f position;
    private Vector3f rotation;
    private float scale;

    public Entity(String id, String modelId) {
        this.id = id;
        this.modelId = modelId;
        position = new Vector3f();
        rotation = new Vector3f();
        scale = 1;
    }

    public String getId() {
        return id;
    }

    public String getModelId() {
        return modelId;
    }

    // multiplica na ordem correta (translate, rotate, scale), does the 3 transformations on the model matrix to get world cord
    public Matrix4f getModelMatrix() {
        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.identity();
        modelMatrix.translate(position.x, position.y, position.z);
        modelMatrix.rotateX((float) Math.toRadians(rotation.x));
        modelMatrix.rotateY((float) Math.toRadians(rotation.y));
        modelMatrix.rotateZ((float) Math.toRadians(rotation.z));
        modelMatrix.scale(scale);

        return modelMatrix;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    public final void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void setRotation(float x, float y, float z) {
        // degrees
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void incrementPosition(float x, float y, float z) {
        position.x += x;
        position.y += y;
        position.z += z;
    }

    public void incrementRotation(float x, float y, float z) {
        rotation.x += x;
        rotation.y += y;
        rotation.z += z;
    }
}
