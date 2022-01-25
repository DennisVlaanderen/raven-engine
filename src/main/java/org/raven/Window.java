package org.raven;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.raven.inputs.KeyListener;
import org.raven.inputs.MouseListener;
import org.raven.scenes.LevelEditorScene;
import org.raven.scenes.Scene;

import java.util.logging.Logger;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private int width;
    private int height;
    private String title;

    private long glfwWindow;

    private final SceneManager sceneManager;

    private static final Logger LOGGER = Logger.getLogger(Window.class.getName());

    private static Window window = null;


    private Window() {
        this.width = 1280;
        this.height = 720;
        this.title = "Raven Engine";
        this.sceneManager = new SceneManager();
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }
        return window;
    }

    public void run(){
        LOGGER.info("Hello Raven!");

        try (GLFWErrorCallback callback = GLFWErrorCallback.createPrint(System.err)) {
            // Setup error callback
            callback.set();

            init();
            loop();
        }
        // Free the memory at the end of loop
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the callback
        glfwTerminate();
    }

    private void init() {

        // Initialize GLFW
        if(!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create Window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW Window.");
        }

        // Setup Mouse Callbacks
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback); // Mouse Position Callback
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback); // Mouse Button Callback
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback); // Mouse Scroll Callback

        // Set Keyboard Callback
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // Make the OpenGL Context current
        glfwMakeContextCurrent(glfwWindow);

        // Enable V-Sync
        glfwSwapInterval(GLFW_TRUE);

        // Make window appear
        glfwShowWindow(glfwWindow);

        // Enabled OpenGL bindings
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // Register scenes
        Scene scene = new LevelEditorScene();
        sceneManager.addScene(scene);
        sceneManager.setCurrentScene(scene);

    }

    private void loop() {

        // Setup time variables
        float frameStartTime = (float) glfwGetTime();
        float frameEndTime;
        float dt = -1.0f;

        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll Events
            glfwPollEvents();

            glClearColor(0.5f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {
                sceneManager.getCurrentScene().update(dt);
            }

            // Swap Memory Buffers
            glfwSwapBuffers(glfwWindow);

            // Calculate Time Delta of Frame
            frameEndTime = (float) glfwGetTime();
            dt = frameEndTime - frameStartTime;
            frameStartTime = frameEndTime;
        }
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }
}
