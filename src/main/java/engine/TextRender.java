package engine;

import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL30C.*;

public class TextRender {
    private int vaoId, vboId;
    private ShaderProgram shader;
    private UniformsMap uniforms;

    public TextRender(ShaderProgram shader) {
        this.shader = shader;
        uniforms = new UniformsMap(shader.getProgramId());

        uniforms.createUniform("projection");
        uniforms.createUniform("model");
        uniforms.createUniform("texSampler");

        float[] vertices = {
                // x, y, u, v
                0, 0, 0, 0,
                1, 0, 1, 0,
                1, 1, 1, 1,
                0, 1, 0, 1
        };

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);

        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);

        glBindVertexArray(0);
    }

    public void render(int textureId, float x, float y, float width, float height, int windowWidth, int windowHeight) {
        shader.bind();

        // Setup orthographic projection matrix
        Matrix4f ortho = new Matrix4f().ortho2D(0, windowWidth, windowHeight, 0);
        uniforms.setUniform("projection", ortho);
        uniforms.setUniform("model", new Matrix4f().translate(x, y, 0).scale(width, height, 1));

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
        uniforms.setUniform("texSampler", 0);

        glBindVertexArray(vaoId);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        glBindVertexArray(0);

        shader.unbind();
    }

    public void cleanup() {
        glDeleteVertexArrays(vaoId);
        glDeleteBuffers(vboId);
    }
}
