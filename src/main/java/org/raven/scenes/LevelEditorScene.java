package org.raven.scenes;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.raven.Camera;
import org.raven.objects.GameObject;
import org.raven.renderer.Transform;
import org.raven.objects.components.SpriteRenderer;
import org.raven.util.Asset;
import org.raven.util.AssetPool;

public class LevelEditorScene extends Scene {

    public LevelEditorScene() {
        // Empty constructor
    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f(-20, -30));

        GameObject obj1 = new GameObject("obj1", new Transform(new Vector2f(100,100), new Vector2f(100, 100)));
        obj1.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage1.png")));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("obj2", new Transform(new Vector2f(200,200), new Vector2f(100, 100)));
        obj2.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage2.png")));
        this.addGameObjectToScene(obj2);

        loadResources();
    }

    private void loadResources() {
        AssetPool.getShader(Asset.SHADER_DEFAULT);
    }

    @Override
    public void update(float dt) {
        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }
}
