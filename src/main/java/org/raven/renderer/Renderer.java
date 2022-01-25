package org.raven.renderer;

import org.raven.objects.GameObject;
import org.raven.objects.components.SpriteRenderer;

import java.util.ArrayList;
import java.util.List;

public class Renderer {

    private static final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batchList;

    public Renderer() {
        this.batchList = new ArrayList<>();
    }

    public void add(GameObject go) {
        SpriteRenderer spriteRenderer = go.getComponent(SpriteRenderer.class);
        if (spriteRenderer != null) {
            add(spriteRenderer);
        }
    }

    private void add(SpriteRenderer spriteRenderer) {
        boolean added = false;
        for (RenderBatch batch : batchList) {
            Texture tex = spriteRenderer.getTexture();
            if (tex == null || (batch.hasTexture(tex) || batch.hasTextureRoom())) {
                batch.addSprite(spriteRenderer);
                added = true;
                break;
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE);
            newBatch.start();
            batchList.add(newBatch);
            newBatch.addSprite(spriteRenderer);
        }
    }

    public void render() {
        for (RenderBatch batch : batchList) {
            batch.render();
        }
    }
}
