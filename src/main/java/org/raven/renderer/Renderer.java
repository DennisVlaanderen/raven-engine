package org.raven.renderer;

import org.raven.objects.GameObject;
import org.raven.objects.components.SpriteRenderer;

import java.util.ArrayList;
import java.util.List;

public class Renderer {

    private final int maxBatchSize = 1000;
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
            if (batch.hasRoom()) {
                batch.addSprite(spriteRenderer);
                added = true;
                break;
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(maxBatchSize);
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
