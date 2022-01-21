package org.raven.scenes;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.raven.Camera;
import org.raven.objects.GameObject;
import org.raven.objects.Transform;
import org.raven.objects.components.SpriteRenderer;

public class LevelEditorScene extends Scene {

    public LevelEditorScene() {
        // Empty constructor
    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f());

        int xOffset = 10;
        int yOffset = 10;

        float totalWidth = (600 - xOffset * 2);
        float totalHeight = (300 - yOffset * 2);
        float sizeX = totalWidth / 100.0f;
        float sizeY = totalHeight / 100.0f;

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                float xPos = xOffset + (i * sizeX);
                float yPos = yOffset + (j * sizeY);

                GameObject go = new GameObject("Obj" + i + j, new Transform(new Vector2f(xPos, yPos), new Vector2f(sizeX, sizeY)));
                go.addComponent(new SpriteRenderer(new Vector4f(xPos/totalWidth, yPos/totalHeight, 1, 1)));
                this.addGameObjectToScene(go);
            }
        }
    }

    @Override
    public void update(float dt) {
        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }
}