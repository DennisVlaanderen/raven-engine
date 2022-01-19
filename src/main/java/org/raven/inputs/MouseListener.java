package org.raven.inputs;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {

    // Instance
    private static MouseListener instance;

    // Position variables
    private double scrollX;
    private double scrollY;
    private double xPos;
    private double yPos;
    private double lastX;
    private double lastY;

    // Button Press states
    private boolean mouseButtonPressed[] = new boolean[3];

    // Dragging state
    private boolean isDragging;

    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    public static MouseListener get() {
        if (MouseListener.instance == null) {
            instance = new MouseListener();
        }
        return instance;
    }

    // Callback method for the mouse position.
    public static void mousePosCallback(long window, double xPos, double yPos) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xPos;
        get().yPos = yPos;
        for (boolean b : get().mouseButtonPressed) {
            get().isDragging = b;
        }
    }

    // Callback method for detecting mouse clicks.
    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS && button < get().mouseButtonPressed.length) {
            get().mouseButtonPressed[button] = true;
        } else if (action == GLFW_RELEASE && button < get().mouseButtonPressed.length) {
            get().mouseButtonPressed[button] = false;
            get().isDragging = false;
        }
    }

    // Callback method for detecting mouse scrolling.
    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    // Method to reset position variables at the end of a drawn frame.
    public static void endFrame() {
        get().scrollY = 0;
        get().scrollX = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    public static float getX() {
        return (float) get().xPos;
    }

    public static float getY() {
        return (float) get().yPos;
    }

    public static float getDx() {
        return (float) (get().lastX - get().xPos);
    }

    public static float getDy() {
        return (float) (get().lastY - get().yPos);
    }

    public static float getScrollX() {
        return (float) get().scrollX;
    }

    public static float getScrollY() {
        return (float) get().scrollY;
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int button) {
        if (button < get().mouseButtonPressed.length)
            return get().mouseButtonPressed[button];
        return false;
    }
}
