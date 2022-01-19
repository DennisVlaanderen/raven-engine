package org.raven.renderables.scenes;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.raven.renderables.Camera;
import org.raven.renderables.Shader;
import org.raven.util.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene implements Scene {

    private Shader shader;
    private Camera camera;

    private float[] vertexArray = {
            // pos                   //col
             100.5f, 0.5f,    0.0f,  1.0f, 0.0f, 0.0f, 1.0f, // Bottom Right
             0.5f,   100.5f,  0.0f,  0.0f, 1.0f, 0.0f, 1.0f, // Top Left
             100.5f, 100.5f,  0.0f,  0.0f, 0.0f, 1.0f, 1.0f, // Top Right
             0.5f,   0.5f,    0.0f,  1.0f, 1.0f, 0.0f, 1.0f, // Bottom Left
    }; // Vertex for a single square

    // Important: Must be in CCW order
    private int[] elementArray = {
            2, 1, 0, // Top Right
            0, 1, 3, // Bottom Left
    }; // Triangle Data

    private int vaoID;
    private int vboID;
    private int eboID;

    public LevelEditorScene() {
        this.shader = new Shader("assets/shaders/default.glsl");
    }

    @Override
    public void update(float dt) {
        // Setup Shaders
        shader.use();

        shader.uploadMat4f("uProj", camera.getProjectionMatrix());
        shader.uploadMat4f("uView", camera.getViewMatrix());
        shader.uploadFloat("uTime", Time.getTime());

        // Bind the VAO
        glBindVertexArray(vaoID);

        // Enable attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        shader.detach();
    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f());


        shader.compileAndLink();

        // Generate VAO, VBO and EBO for rendering
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create float vertices buffer
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create Element indices buffer
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeInBytes = (positionsSize + colorSize) * floatSizeBytes;

        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeInBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeInBytes, (long) positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);

    }
}
