package org.raven.objects;

import org.raven.renderer.Transform;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameObject {

    private String name;
    private List<Component> components;
    private Transform transform;

    private Logger logger;

    public GameObject(String name) {
        init(name, new ArrayList<>(), new Transform());
    }

    public GameObject(String name, Transform transform) {
        init(name, new ArrayList<>(), transform);
    }

    public void init(String name, List<Component> components, Transform transform) {
        this.name = name;
        this.logger = Logger.getLogger(this.getClass().getName());
        this.components = components;
        this.transform = transform;
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c: components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    logger.log(Level.WARNING, "Tried casting object to non-compliant parent class.");
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public <T extends Component> boolean removeComponent(Class<T> componentClass) {
        for (int i = components.size() - 1; i >= 0; i--) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                components.remove(i);
                return true;
            }
        }
        return false;
    }

    public void addComponent(Component c) {
        components.add(c);
        c.setGameObject(this);
    }

    public void update(float dt) {
        for (Component component : components) {
            component.update(dt);
        }
    }

    public void start() {
        for (Component component : components) {
            component.start();
        }
    }

    public Transform getTransform() {
        return transform;
    }
}
