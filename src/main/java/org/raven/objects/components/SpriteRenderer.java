package org.raven.objects.components;

import org.raven.objects.Component;

public class SpriteRenderer extends Component {

    private boolean firstTime = false;

    @Override
    public void start() {
    }

    @Override
    public void update(float dt) {
        if (!firstTime) {
            firstTime = true;
        }
    }
}
