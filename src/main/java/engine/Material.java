package engine;

import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Material {
    private List<Mesh> meshList;
    private String texturePath;

    private Vector4f ambientColor;
    private Vector4f diffuseColor;
    private Vector4f specularColor;
    private float reflectance;

    public Material() {
        meshList = new ArrayList<>();
        ambientColor = new Vector4f(0.03f, 0.03f, 0.03f, 1.0f);
        diffuseColor = new Vector4f(0.05f, 0.03f, 0.03f, 1.0f);
        specularColor = new Vector4f(0.04f, 0.04f, 0.04f, 1.0f);
        reflectance = 1.0f;
    }

    public void cleanup() {
        meshList.forEach(Mesh::cleanup);
    }

    public List<Mesh> getMeshList() {
        return meshList;
    }

    public Vector4f getAmbientColor() {
        return ambientColor;
    }

    public float getReflectance() {
        return reflectance;
    }

    public Vector4f getSpecularColor() {
        return specularColor;
    }

    public void setAmbientColor(Vector4f ambientColor) {
        this.ambientColor = ambientColor;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public void setSpecularColor(Vector4f specularColor) {
        this.specularColor = specularColor;
    }

    public Vector4f getDiffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(Vector4f diffuseColor) {
        this.diffuseColor = diffuseColor;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }
}
