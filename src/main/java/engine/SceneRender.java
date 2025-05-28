package engine;


import org.joml.*;

import java.awt.*;
import java.lang.Math;
import java.util.Collection;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class SceneRender {

    private static final int MAX_POINT_LIGHTS = 5;

    private ShaderProgram shaderProgram;
    private ShaderProgram skyboxShaderProgram;
    private ShaderProgram particleShaderProgram;

    private UniformsMap uniformsMap;
    private UniformsMap skyboxUniformsMap;
    private UniformsMap particleUniformsMap;

    Font font;
    TextTexture scoreTexture;
    TextTexture starsTexture;
    TextTexture resultTexture;
    TextRender textRenderer;

    public SceneRender() {
        shaderProgram = new ShaderProgram("resources/shaders/scene.vert", "resources/shaders/scene.frag");
        createUniforms();

        skyboxShaderProgram = new ShaderProgram("resources/shaders/skybox.vert", "resources/shaders/skybox.frag");
        createSkyboxUniforms();

        particleShaderProgram = new ShaderProgram("resources/shaders/particle.vert", "resources/shaders/particle.frag");
        createParticleUniforms();

        createHUD();

    }

    public void cleanup() {
        shaderProgram.cleanup();
    }

    private void createUniforms() {
        uniformsMap = new UniformsMap(shaderProgram.getProgramId());
        uniformsMap.createUniform("projectionMatrix");
        uniformsMap.createUniform("viewMatrix");
        uniformsMap.createUniform("modelMatrix");
        uniformsMap.createUniform("txtSampler");

        uniformsMap.createUniform("material.ambient");
        uniformsMap.createUniform("material.diffuse");
        uniformsMap.createUniform("material.specular");
        uniformsMap.createUniform("material.reflectance");

        uniformsMap.createUniform("ambientLight.factor");
        uniformsMap.createUniform("ambientLight.color");

        uniformsMap.createUniform("dirLight.color");
        uniformsMap.createUniform("dirLight.direction");
        uniformsMap.createUniform("dirLight.intensity");

        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
            String name = "pointLights[" + i + "]";
            uniformsMap.createUniform(name + ".position");
            uniformsMap.createUniform(name + ".color");
            uniformsMap.createUniform(name + ".intensity");
            uniformsMap.createUniform(name + ".att.constant");
            uniformsMap.createUniform(name + ".att.linear");
            uniformsMap.createUniform(name + ".att.exponent");
        }
    }

    private void createSkyboxUniforms() {
        skyboxUniformsMap = new UniformsMap(skyboxShaderProgram.getProgramId());
        skyboxUniformsMap.createUniform("projectionMatrix");
        skyboxUniformsMap.createUniform("viewMatrix");
        skyboxUniformsMap.createUniform("cubeMap");
    }

    private void createParticleUniforms() {
        particleUniformsMap = new UniformsMap(particleShaderProgram.getProgramId());
        particleUniformsMap.createUniform("projectionMatrix");
        particleUniformsMap.createUniform("modelViewMatrix");
        particleUniformsMap.createUniform("particleText");
        particleUniformsMap.createUniform("elapsedTime");
    }

    public void render(Scene scene) {
        shaderProgram.bind();
        uniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjMatrix());
        uniformsMap.setUniform("viewMatrix", scene.getCamera().getViewMatrix());
        uniformsMap.setUniform("txtSampler", 0);


        updateLights(scene);

        Collection<Model> models = scene.getModelMap().values();
        TextureCache textureCache = scene.getTextureCache();
        for (Model model : models) {
            List<Entity> entities = model.getEntitiesList();

            for (Material material : model.getMaterialList()) {
                Texture texture = textureCache.getTexture(material.getTexturePath());
                glActiveTexture(GL_TEXTURE0);
                texture.bind();

                uniformsMap.setUniform("material.ambient", material.getAmbientColor());
                uniformsMap.setUniform("material.diffuse", material.getDiffuseColor());
                uniformsMap.setUniform("material.specular", material.getSpecularColor());
                uniformsMap.setUniform("material.reflectance", material.getReflectance());

                for (Mesh mesh : material.getMeshList()) {
                    glBindVertexArray(mesh.getVaoId());
                    for (Entity entity : entities) {
                        uniformsMap.setUniform("modelMatrix", entity.getWorldModelMatrix());
                        glDrawElements(GL_TRIANGLES, mesh.getNumVertices(), GL_UNSIGNED_INT, 0);
                    }
                }
            }
        }

        Texture texture = scene.getTextureCache().getTexture("resources/models/grass/grass.png");
        glActiveTexture(GL_TEXTURE0);
        texture.bind();

        glBindVertexArray(scene.getTerrainMesh().getVaoId());
        uniformsMap.setUniform("modelMatrix", scene.getTerrainEntity().getModelMatrix());
        glDrawElements(GL_TRIANGLES, scene.getTerrainMesh().getNumVertices(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        shaderProgram.unbind();

        skyboxShaderProgram.bind();
        skyboxUniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjMatrix());
        skyboxUniformsMap.setUniform("viewMatrix", new Matrix4f(new Matrix3f(scene.getCamera().getViewMatrix())));
        skyboxUniformsMap.setUniform("cubeMap", 0);
        scene.getSkybox().bind();

        scene.getSkybox().unbind();
        skyboxShaderProgram.unbind();

        particleShaderProgram.bind();
        particleUniformsMap.setUniform("particleText", 0);
        particleUniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjMatrix());
        for (Particle particle : scene.getParticles()) {
            particleUniformsMap.setUniform("elapsedTime", particle.getElapsedTime());
            Matrix4f viewMatrix = new Matrix4f(scene.getCamera().getViewMatrix());
            Matrix4f modelMatrix = new Matrix4f();
            modelMatrix.translate(particle.getPosition());
            // Canceling the camera rotation by transposing the rotation matrix (transform back to world space)
            modelMatrix.m00(viewMatrix.m00());
            modelMatrix.m01(viewMatrix.m10());
            modelMatrix.m02(viewMatrix.m20());
            modelMatrix.m10(viewMatrix.m01());
            modelMatrix.m11(viewMatrix.m11());
            modelMatrix.m12(viewMatrix.m21());
            modelMatrix.m20(viewMatrix.m02());
            modelMatrix.m21(viewMatrix.m12());
            modelMatrix.m22(viewMatrix.m22());
            // rotate 2d effect (around z-axis)
            modelMatrix.rotate((float) Math.toRadians(particle.getRotation()), new Vector3f(0, 0, 1), modelMatrix);
            modelMatrix.scale(new Vector3f(particle.getScale(), particle.getScale(), particle.getScale()), modelMatrix);
            Matrix4f modelViewMatrix = viewMatrix.mul(modelMatrix);
            particleUniformsMap.setUniform("modelViewMatrix", modelViewMatrix);

            particle.render();
        }
        particleShaderProgram.unbind();

        renderHUD(1024, 768, scene);
    }

    private void updateLights(Scene scene) {
        Matrix4f viewMatrix = scene.getCamera().getViewMatrix();

        AmbientLight ambientLight = scene.getAmbientLight();
        uniformsMap.setUniform("ambientLight.factor", ambientLight.getIntensity());
        uniformsMap.setUniform("ambientLight.color", ambientLight.getColor());

        // transform into view space coords (makes light relative to camera)
        DirLight dirLight = scene.getDirLight();
        Vector4f auxDir = new Vector4f(dirLight.getDirection(), 0); // w = 0 for direction (no translation)
        auxDir.mul(viewMatrix);
        Vector3f dir = new Vector3f(auxDir.x, auxDir.y, auxDir.z);
        uniformsMap.setUniform("dirLight.color", dirLight.getColor());
        uniformsMap.setUniform("dirLight.direction", dir);
        uniformsMap.setUniform("dirLight.intensity", dirLight.getIntensity());

        List<PointLight> pointLights = scene.getPointLights();
        int numPointLights = pointLights.size();
        PointLight pointLight;
        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
            if (i < numPointLights) {
                pointLight = pointLights.get(i);
            } else {
                pointLight = null;
            }
            String name = "pointLights[" + i + "]";
            updatePointLight(pointLight, name, viewMatrix);
        }
    }

    private void updatePointLight(PointLight pointLight, String prefix, Matrix4f viewMatrix) {
        Vector4f aux = new Vector4f();
        Vector3f lightPosition = new Vector3f();
        Vector3f color = new Vector3f();
        float intensity = 0.0f;
        float constant = 0.0f;
        float linear = 0.0f;
        float exponent = 0.0f;
        if (pointLight != null) {
            aux.set(pointLight.getPosition(), 1); // can translate
            aux.mul(viewMatrix); // view space coords
            lightPosition.set(aux.x, aux.y, aux.z);
            color.set(pointLight.getColor());
            intensity = pointLight.getIntensity();
            PointLight.Attenuation attenuation = pointLight.getAttenuation();
            constant = attenuation.getConstant();
            linear = attenuation.getLinear();
            exponent = attenuation.getExponent();
        }
        uniformsMap.setUniform(prefix + ".position", lightPosition);
        uniformsMap.setUniform(prefix + ".color", color);
        uniformsMap.setUniform(prefix + ".intensity", intensity);
        uniformsMap.setUniform(prefix + ".att.constant", constant);
        uniformsMap.setUniform(prefix + ".att.linear", linear);
        uniformsMap.setUniform(prefix + ".att.exponent", exponent);
    }

    private void createHUD() {
        ShaderProgram textShader = new ShaderProgram("resources/shaders/text.vert", "resources/shaders/text.frag");
        font = new Font("Arial", Font.BOLD, 32);
        scoreTexture = new TextTexture(256, 64);
        starsTexture = new TextTexture(256, 64);
        resultTexture = new TextTexture(256, 64);

        textRenderer = new TextRender(textShader);
    }

    public void renderHUD(int windowWidth, int windowHeight, Scene scene) {
        scoreTexture.updateText("Score: " + scene.getScore(), font, Color.WHITE);
        starsTexture.updateText("Stars: " + scene.getStarCount(), font, Color.WHITE);
        textRenderer.render(scoreTexture.getTextureId(), 20, 20, 256, 64, windowWidth, windowHeight);
        textRenderer.render(starsTexture.getTextureId(), 236, 20, 256, 64, windowWidth, windowHeight);

        if (scene.getResult() == 0) {
            resultTexture.updateText("DEFEAT!", font, Color.RED);

        } else if (scene.getResult() == 1) {
            resultTexture.updateText("VICTORY!", font, Color.GREEN);
        } else {
            resultTexture.updateText("", font, Color.WHITE);
        }

        textRenderer.render(resultTexture.getTextureId(), (windowWidth - 256) / 2f, (windowHeight - 64) / 2f, 256, 64, windowWidth, windowHeight);

    }
}