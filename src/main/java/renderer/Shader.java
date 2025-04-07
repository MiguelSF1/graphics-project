package renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int shaderProgramID;

    private String vertexSource;
    private String fragmentSource;

    private boolean beingUsed = false;

    public Shader(String vertexPath, String fragmentPath) {

        try {
            this.vertexSource = new String(Files.readAllBytes(Paths.get(vertexPath)));
            this.fragmentSource = new String(Files.readAllBytes(Paths.get(fragmentPath)));
        } catch(IOException e) {
            System.out.println("Error loading shader");
            e.printStackTrace();
        }
    }

    public void compile() {
        int vertexID, fragmentID;

        vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: vertex shader compile failed");
            System.out.println(glGetShaderInfoLog(vertexID, len));
        }

        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: fragment shader compile failed");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
        }

        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: shader program link failed");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
        }
    }

    public void use() {
        if (!beingUsed) {
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }

    }

    public void detach() {
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMat4f(String name, Matrix4f mat4) {
        int location = glGetUniformLocation(shaderProgramID, name);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(location, false, matBuffer);
    }

    public void uploadMat3f(String name, Matrix3f mat3) {
        int location = glGetUniformLocation(shaderProgramID, name);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(location, false, matBuffer);
    }

    public void uploadVec4f(String name, Vector4f vec4) {
        int location = glGetUniformLocation(shaderProgramID, name);
        use();
        glUniform4f(location, vec4.x, vec4.y, vec4.z, vec4.w);
    }

    public void uploadVec3f(String name, Vector3f vec3) {
        int location = glGetUniformLocation(shaderProgramID, name);
        use();
        glUniform3f(location, vec3.x, vec3.y, vec3.z);
    }

    public void uploadVec2f(String name, Vector2f vec2) {
        int location = glGetUniformLocation(shaderProgramID, name);
        use();
        glUniform2f(location, vec2.x, vec2.y);
    }

    public void uploadFloat(String name, float f) {
        int location = glGetUniformLocation(shaderProgramID, name);
        use();
        glUniform1f(location, f);
    }

    public void uploadInt(String name, int i) {
        int location = glGetUniformLocation(shaderProgramID, name);
        use();
        glUniform1i(location, i);
    }

    public void uploadTexture(String name, int slot) {
        int location = glGetUniformLocation(shaderProgramID, name);
        use();
        glUniform1i(location, slot);
    }
}
