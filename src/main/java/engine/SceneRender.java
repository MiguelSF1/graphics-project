package engine;


import org.joml.*;

import java.util.Collection;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class SceneRender {

    private ShaderProgram shaderProgram;
    private ShaderProgram skyboxShaderProgram;

    private UniformsMap uniformsMap;
    private UniformsMap skyboxUniformsMap;

    public SceneRender() {
        shaderProgram = new ShaderProgram("resources/shaders/scene.vert", "resources/shaders/scene.frag");
        createUniforms();
        skyboxShaderProgram = new ShaderProgram("resources/shaders/skybox.vert", "resources/shaders/skybox.frag");
        createSkyboxUniforms();
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
    }

    private void createSkyboxUniforms() {
        skyboxUniformsMap = new UniformsMap(skyboxShaderProgram.getProgramId());
        skyboxUniformsMap.createUniform("projectionMatrix");
        skyboxUniformsMap.createUniform("viewMatrix");
        skyboxUniformsMap.createUniform("cubeMap");
    }

    public void render(Scene scene) {
        shaderProgram.bind();
        uniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjMatrix());
        uniformsMap.setUniform("viewMatrix", scene.getCamera().getViewMatrix());
        uniformsMap.setUniform("txtSampler", 0);

        Collection<Model> models = scene.getModelMap().values();
        TextureCache textureCache = scene.getTextureCache();
        for (Model model : models) {
            List<Entity> entities = model.getEntitiesList();

            for (Material material : model.getMaterialList()) {
                Texture texture = textureCache.getTexture(material.getTexturePath());
                glActiveTexture(GL_TEXTURE0);
                texture.bind();

                for (Mesh mesh : material.getMeshList()) {
                    glBindVertexArray(mesh.getVaoId());
                    for (Entity entity : entities) {
                        uniformsMap.setUniform("modelMatrix", entity.getModelMatrix());
                        glDrawElements(GL_TRIANGLES, mesh.getNumVertices(), GL_UNSIGNED_INT, 0);
                    }
                }
            }
        }

        glBindVertexArray(0);
        shaderProgram.unbind();

        skyboxShaderProgram.bind();
        skyboxUniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjMatrix());
        skyboxUniformsMap.setUniform("viewMatrix", new Matrix4f(new Matrix3f(scene.getCamera().getViewMatrix())));
        skyboxUniformsMap.setUniform("cubeMap", 0);
        scene.getSkybox().bind();

        scene.getSkybox().unbind();
        skyboxShaderProgram.unbind();

    }
}