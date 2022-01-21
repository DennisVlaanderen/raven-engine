package org.raven.renderables;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private static final Logger LOGGER = Logger.getLogger(Shader.class.getName());

    private String vertexSource;
    private String fragmentSource;
    private final String filepath;

    private int shaderProgramID;
    private boolean beingUsed = false;

    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // Find Shader Patterns from source string.
            // '#type <patternName>\r\n'
            int keyOffset = 6;
            int index = source.indexOf("#type") + keyOffset;
            int eol = source.indexOf("\r\n", index);
            int secondIndex = source.indexOf("#type", eol) + keyOffset;

            String firstPattern = retrievePattern(source, index);
            String secondPattern = retrievePattern(source, secondIndex);

            assertPattern(firstPattern, splitString,1);
            assertPattern(secondPattern, splitString,2);

            LOGGER.log(Level.INFO, "Shaders successfully loaded.");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not find shader on location: {0}", this.filepath);
            e.printStackTrace();
            assert false : "";
        }
    }

    public void compileAndLink() {

        // Setup vertex shaders
        int vertexID = glCreateShader(GL_VERTEX_SHADER); // Load and compile shader
        glShaderSource(vertexID, vertexSource); // Link shader source to vertex shader
        glCompileShader(vertexID); // Compile the shader

        // Error check vertex shaders
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            LOGGER.log(Level.SEVERE, "{0} | Vertex shader compilation failed.", this.filepath);
            LOGGER.log(Level.SEVERE, "{0}", glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        // Compile Fragment Shaders
        int fragmentID = glCreateShader(GL_FRAGMENT_SHADER); // Load and compile shader
        glShaderSource(fragmentID, fragmentSource); // Link shader source to fragment shader
        glCompileShader(fragmentID); // Compile the shader

        // Error check fragment shaders
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            LOGGER.log(Level.SEVERE, "{0} | Fragment shader compilation failed.", this.filepath);
            LOGGER.log(Level.SEVERE, "{0}", glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        // Link up vertex shaders with fragment shaders
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        // Check for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            LOGGER.log(Level.SEVERE, "{0} | Linking shaders failed.", this.filepath);
            LOGGER.log(Level.SEVERE, "{0}", glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }
    }

    public void use() {
        // Bind shader program
        if(!beingUsed) {
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }

    public void detach() {
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3f(String varName, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vector4f) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vector4f.x, vector4f.y, vector4f.z, vector4f.w);
    }

    public void uploadVec3f(String varName, Vector3f vector4f) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vector4f.x, vector4f.y, vector4f.z);
    }

    public void uploadVec2f(String varName, Vector2f vector4f) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vector4f.x, vector4f.y);
    }

    public void uploadFloat(String varName, float val){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String varName, int val){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }

    public void uploadTexture(String varName, int slot) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, slot);
    }

    private String retrievePattern(String source, int index) {
        int eol = source.indexOf("\r\n", index);
        return source.substring(index, eol).trim();
    }

    private void assertPattern(String pattern, String[] fileContents, int contentIndex) throws IOException {
        switch (pattern) {
            case "vertex" -> assignVertexSource(fileContents[contentIndex]);
            case "fragment" -> assignFragmentSource(fileContents[contentIndex]);
            default -> throw new IOException("Unexpected token '" + pattern + "'.");
        }
    }

    private void assignFragmentSource(String source) {
        fragmentSource = source;
    }

    private void assignVertexSource(String source) {
        vertexSource = source;
    }
}
