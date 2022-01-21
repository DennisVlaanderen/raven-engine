package org.raven.scenes;

import org.raven.Camera;
import org.raven.objects.GameObject;
import org.raven.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    private boolean isRunning = false;
    protected Renderer renderer = new Renderer();

    protected List<GameObject> gameObjects = new ArrayList<>();
    protected Camera camera;

    protected Scene() {

    }

    public void start() {
        for (GameObject go : gameObjects) {
            go.start();
            this.renderer.add(go);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);
        } else {
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
        }
    }

    public abstract void update(float dt);

    public void init() {

    }

    public Camera getCamera() {
        return camera;
    }

}
