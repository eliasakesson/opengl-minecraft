package org.swezyn;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.swezyn.rendering.Camera;
import org.swezyn.rendering.Rendering;
import org.swezyn.utilities.ImageParser;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private static final int initialWidth = 1280;
    private static final int initialHeight = 720;
    private static final String title = "Minecraft";

    // The window handle
    private long window;

    private final Camera camera = new Camera();

    public Window() {
        init();
        loop();
        cleanup();
    }

    public long getWindow() {
        return window;
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(initialWidth, initialHeight, title, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Icon
        GLFWImage image = GLFWImage.malloc();
        GLFWImage.Buffer imageBuffer = GLFWImage.malloc(1);
        ImageParser resource = ImageParser.loadImage("src/main/resources/icon.png");
        if (resource != null) {
            image.set(resource.getWidth(), resource.getHeight(), resource.getImage());
            imageBuffer.put(0, image);
            glfwSetWindowIcon(window, imageBuffer);
        } else {
            System.out.println("Icon failed to load.");
        }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            //if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
            //    glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        InputHandler inputHandler = new InputHandler(this, camera);

        Rendering rendering = new Rendering(camera);

        while (!glfwWindowShouldClose(window)) {
            inputHandler.handleInput();
            rendering.Render();

            glfwSwapBuffers(window); // swap the color buffers
            glfwPollEvents();

            int error = glGetError();
            if (error != GL_NO_ERROR) {
                System.err.println("OpenGL Error: " + error);
            }
        }
    }

    private void cleanup() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}