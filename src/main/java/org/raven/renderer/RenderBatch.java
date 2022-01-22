package org.raven.renderer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.raven.Window;
import org.raven.objects.components.SpriteRenderer;
import org.raven.util.Asset;
import org.raven.util.AssetPool;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch {
    // Vertex
    // ======
    // Pos              // Color                        // Tex Coords   // TexID
    // float, float,    float, float, float, float,     float, float,   float
    private static final int POS_SIZE = 2;
    private static final int COLOR_SIZE = 4;
    private static final int TEX_COORDS_SIZE = 2;
    private static final int TEX_ID_SIZE = 1;

    private static final int POS_OFFSET = 0;
    private static final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private static final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private static final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;

    private static final int VERTEX_SIZE = 9;
    private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private List<Texture> textures;
    private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private int vaoID;
    private int vboID;
    private int maxBatchSize;
    private Shader shader;

    public RenderBatch(int maxBatchSize) {
        shader = AssetPool.getShader(Asset.SHADER_DEFAULT);
        shader.compileAndLink();
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        // 4 vertices quads
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
    }

    public void start() {
        // Generate and bind vertex array object (VAO)
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer to prevent index duplication in RenderTime
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);

    }

    private int[] generateIndices() {
        // 6 indices per quad (3 per tri)
        int[] elements = new int[6 * maxBatchSize];
        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }
        return elements;
    }

    public void addSprite(SpriteRenderer spr) {
        // Get index and add object
        int index = this.numSprites;
        this.sprites[index] = spr;
        this.numSprites++;

        // Check if sprite contains texture not yet added to texture list and add it.
        if (spr.getTexture() != null && !textures.contains(spr.getTexture()))
            textures.add(spr.getTexture());

        // Add properties to local vertex array
        loadVertexProperties(index);

        if (numSprites >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }

    public void render() {
        // For now, we will buffer all data every frame.
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        // Use shader
        // TODO GRASP the shader loading for projection and view matrices to the camera class
        Matrix4f projectionMatrix = Window.get().getSceneManager().getCurrentScene().getCamera().getProjectionMatrix();
        Matrix4f viewMatrix = Window.get().getSceneManager().getCurrentScene().getCamera().getViewMatrix();

        shader.use();
        shader.uploadMat4f("uProj", projectionMatrix);
        shader.uploadMat4f("uView", viewMatrix);
        for (int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadIntArray("uTextures", texSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i);
            textures.get(i).unbind();
        }
        shader.detach();
    }

    private void loadVertexProperties(int i) {
        SpriteRenderer sprite = this.sprites[i];

        // Find offset within array (4 per sprite)
        int offset = i * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        Vector2f[] textCoords = sprite.getTexCoords();

        int texID = 0;
        // Match Texture with sprite and set id to be the same.
        if (sprite.getTexture() != null) {
            texID = IntStream.range(0, textures.size())
                    .anyMatch(j -> textures.get(i) == sprite.getTexture()) ? i + 1 : 0;
        }

        // Add vertex with the appropriate properties
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for (int j = 0; j < 4; j++) {
            if (j == 1) yAdd = 0.0f;
            if (j == 2) xAdd = 0.0f;
            if (j == 3) yAdd = 1.0f;

            // Load positions
            float xPos = sprite.getGameObject().getTransform().getPosition().x;
            float yPos = sprite.getGameObject().getTransform().getPosition().y;
            float xScale = sprite.getGameObject().getTransform().getScale().x;
            float yScale = sprite.getGameObject().getTransform().getScale().y;
            vertices[offset] = xPos + (xAdd * xScale);
            vertices[offset + 1] = yPos + (yAdd * yScale);

            // Load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            // Load Texture Coords
            vertices[offset + 6] = textCoords[j].x;
            vertices[offset + 7] = textCoords[j].y;

            // Load Texture ID
            vertices[offset + 8] = texID;

            offset += VERTEX_SIZE;
        }
    }

    private void loadElementIndices(int[] elements, int i) {
        int offsetArrayIndex = 6* i;
        int offset = 4*i;

        // Tri 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset;
        elements[offsetArrayIndex + 3] = offset;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoom() {
        return hasRoom;
    }
}
