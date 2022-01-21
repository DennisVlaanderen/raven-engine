package org.raven.scenes;

import org.raven.objects.GameObject;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();

    protected Scene() {

    }

    public void start() {
        for (GameObject go : gameObjects) {
            go.start();
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);
        } else {
            gameObjects.add(go);
            go.start();
        }
    }

    public abstract void update(float dt);

    public void init() {

    };

}
