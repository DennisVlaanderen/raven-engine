package org.raven.objects.components;

import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.raven.objects.Component;
import org.raven.renderer.Texture;
import org.raven.renderer.Transform;

public class SpriteRenderer extends Component {

    private Vector4f color;
    private Sprite sprite;

    private boolean dirty = false;

    private Transform lastTransform;

    public SpriteRenderer(Vector4f color) {
        this.color = color;
        this.sprite = new Sprite(null);
        this.dirty = true;
    }

    public SpriteRenderer(Sprite sprite) {
        this.sprite = sprite;
        this.color = new Vector4f(1,1,1,1);
        this.dirty = true;
    }

    @Override
    public void start() {
        this.lastTransform = getGameObject().getTransform().copy();
    }

    @Override
    public void update(float dt) {
        if (!this.lastTransform.equals(this.getGameObject().getTransform())) {
            this.getGameObject().getTransform().copy(this.lastTransform);
            dirty = true;
        }
    }

    @Override
    public void imgui() {
        float[] imColors = {color.x, color.y, color.z, color.w};
        if (ImGui.colorPicker4("Color Picker: ", imColors)) {
           this.color.set(imColors[0], imColors[1], imColors[2], imColors[3]);
           this.dirty = true;
        }
    }

    public Vector4f getColor() {
        return color;
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public Vector2f[] getTexCoords() {
        return sprite.getTexCoords();
    }

    public void setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            this.dirty = true;
            this.color.set(color);
        }
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.dirty = true;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void clean() {
        this.dirty = false;
    }
}
