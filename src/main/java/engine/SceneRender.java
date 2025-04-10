package engine;


import static org.lwjgl.opengl.GL30.*;

public class SceneRender {

    private ShaderProgram shaderProgram;

    private UniformsMap uniformsMap;

    public SceneRender() {
        shaderProgram = new ShaderProgram("resources/shaders/scene.vert", "resources/shaders/scene.frag");
        createUniforms();
    }

    public void cleanup() {
        shaderProgram.cleanup();
    }

    private void createUniforms() {
        uniformsMap = new UniformsMap(shaderProgram.getProgramId());
        uniformsMap.createUniform("projectionMatrix");
    }

    public void render(Scene scene) {
        shaderProgram.bind();

        uniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjMatrix());

        scene.getMeshMap().values().forEach(mesh -> {
                    glBindVertexArray(mesh.getVaoId());
                    glDrawElements(GL_TRIANGLES, mesh.getNumVertices(), GL_UNSIGNED_INT, 0);
                }
        );

        glBindVertexArray(0);

        shaderProgram.unbind();
    }
}