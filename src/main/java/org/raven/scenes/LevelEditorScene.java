package org.raven.scenes;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.raven.objects.GameObject;
import org.raven.objects.components.FontRenderer;
import org.raven.objects.components.SpriteRenderer;
import org.raven.renderables.Camera;
import org.raven.renderables.Shader;
import org.raven.renderables.Texture;
import org.raven.util.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

    private Shader shader;
    private Camera camera;

    private Texture testTexture;

    private GameObject testGameObject;

    private float[] vertexArray = {
            // pos                   //col                      // UV Coordinates
             100.5f, 0.5f,    0.0f,  1.0f, 0.0f, 0.0f, 1.0f,    1, 0,// Bottom Right
             0.5f,   100.5f,  0.0f,  0.0f, 1.0f, 0.0f, 1.0f,    0, 1, // Top Left
             100.5f, 100.5f,  0.0f,  0.0f, 0.0f, 1.0f, 1.0f,    1, 1, // Top Right
             0.5f,   0.5f,    0.0f,  1.0f, 1.0f, 0.0f, 1.0f,    0, 0, // Bottom Left
    }; // Vertex for a single square

    // Important: Must be in CCW order
    private int[] elementArray = {
            2, 1, 0, // Top Right
            0, 1, 3, // Bottom Left
    }; // Triangle Data

    private int vaoID;
    private int vboID;
    private int eboID;

    private boolean firstTime = false;

    public LevelEditorScene() {
        // Empty constructor
    }

    @Override
    public void init() {
        this.testGameObject = new GameObject("testObj");
        this.testGameObject.addComponent(new SpriteRenderer());
        this.testGameObject.addComponent(new FontRenderer());
        this.addGameObjectToScene(this.testGameObject);

        this.camera = new Camera(new Vector2f(-200, -300));
        this.shader = new Shader("assets/shaders/default.glsl");

        this.shader.compileAndLink();

        this.testTexture = new Texture("assets/images/testImage.png");

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
        int uvSize = 2;
        int vertexSizeInBytes = (positionsSize + colorSize + uvSize) * Float.BYTES;

        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeInBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeInBytes, (long) positionsSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeInBytes, (long) (positionsSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {
        // Setup Shaders
        shader.use();

        //upload texture
        shader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

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

        // Enable texture blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        shader.detach();

        if (!firstTime) {
            GameObject testgo = new GameObject("New object");
            testgo.addComponent(new SpriteRenderer());
            this.addGameObjectToScene(testgo);
            firstTime = true;
        }

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }
    }
}
