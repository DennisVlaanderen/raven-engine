package org.raven.scenes;

import org.joml.Vector2f;
import org.raven.Camera;
import org.raven.objects.GameObject;
import org.raven.objects.components.Spritesheet;
import org.raven.renderer.Transform;
import org.raven.objects.components.SpriteRenderer;
import org.raven.util.Asset;
import org.raven.util.AssetPool;

public class LevelEditorScene extends Scene {

    private GameObject obj1;

    public LevelEditorScene() {
        // Empty constructor
    }

    @Override
    public void init() {

        loadResources();

        this.camera = new Camera(new Vector2f(-20, -30));

        Spritesheet sprites = AssetPool.getSpritesheet("assets/images/spritesheet.png");

        obj1 = new GameObject("obj1", new Transform(new Vector2f(100,100), new Vector2f(42, 42)), -1);
        obj1.addComponent(new SpriteRenderer(sprites.getSprite(36)));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("obj2", new Transform(new Vector2f(200,100), new Vector2f(42, 42)), 2);
        obj2.addComponent(new SpriteRenderer(sprites.getSprite(15)));
        this.addGameObjectToScene(obj2);

    }

    private void loadResources() {
        AssetPool.getShader(Asset.SHADER_DEFAULT);

        AssetPool.addSpritesheet("assets/images/spritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheet.png"), 21, 21, 44, 0));
    }

    @Override
    public void update(float dt) {
        Vector2f position = obj1.getTransform().getPosition();
        float x = position.x += (10 * dt);
        obj1.getTransform().getPosition().set(x, position.y);

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }
}
