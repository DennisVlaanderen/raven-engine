package org.raven.scenes;

import java.util.ArrayList;
import java.util.List;

public class SceneManager {

    List<Scene> scenes = new ArrayList<>();
    Scene currentScene = null;

    public SceneManager() {

    }

    public boolean addScene(Scene scene) {
        return scenes.add(scene);
    }

    public boolean removeScene(Scene scene) {
        return scenes.remove(scene);
    }

    public void setCurrentScene(Scene scene) {
        this.currentScene = scene;
        scene.init();
        scene.start();
    }

    public Scene getCurrentScene() {
        return currentScene;
    }
}
