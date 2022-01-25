package org.raven.objects;

public abstract class Component {

    private GameObject gameObject = null;

    public void start() {
        // Empty boilerplate in case start method is not needed for specific component.
    }

    public  void update(float dt){}

    public GameObject getGameObject() {
        return gameObject;
    }

    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public void imgui() {}
}
