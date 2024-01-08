package org.swezyn;

import org.swezyn.utilities.ImageParser;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.swezyn.utilities.Quaternion;
import org.swezyn.utilities.Transform;
import org.swezyn.utilities.Vector3;
import org.joml.Vector3f;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {
    private static final int INITIAL_WIDTH = 1280;
    private static final int INITIAL_HEIGHT = 720;
    private static final String TITLE = "Minecraft";

    // The window handle
    private long window;

    private final Transform camera = new Transform(
            new Vector3f(0, 0, -1.5f)
    );

    private double[] lastMouseX = new double[1];
    private double[] lastMouseY = new double[1];

    public Window() {
        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(INITIAL_WIDTH, INITIAL_HEIGHT, TITLE, NULL, NULL);
        if ( window == NULL )
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
        try ( MemoryStack stack = stackPush() ) {
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
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glEnable(GL_DEPTH_TEST);

        Transform transform = new Transform(
                new Vector3f(0, 0, 0)
        );

        Transform transform2 = new Transform(
                new Vector3f(.5f, -.5f, 0),
                new Vector3f(0, 45, 0)
        );

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            double[] mouseX = new double[1];
            double[] mouseY = new double[1];
            glfwGetCursorPos(window, mouseX, mouseY);

            if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
                camera.rotation.x -= (float)(mouseY[0] - lastMouseY[0]) * 0.1f;
                camera.rotation.y -= (float)(mouseX[0] - lastMouseX[0]) * 0.1f;
            }

            lastMouseX[0] = mouseX[0];
            lastMouseY[0] = mouseY[0];


            Vector3f forward = camera.rotation;
            Vector3f right = camera.rotation;
            System.out.println(forward.toString());

            double speed = 0.1;

            if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
                camera.position = camera.position.add(forward);
            } else if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
                camera.position = camera.position.add(forward);
            } else if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
                camera.position = camera.position.add(right);
            } else if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
                camera.position = camera.position.add(right);
            }


            glLoadIdentity();
            glMultMatrixf(camera.getTransformationMatrix().get(new float[16]));

            glMatrixMode(GL_MODELVIEW);

            drawTransform(transform);
            drawTransform(transform2);

            glfwSwapBuffers(window); // swap the color buffers
            glfwPollEvents();
        }
    }

    private void drawTransform(Transform transform){
        glPushMatrix();

        // Apply object transformations
        glMultMatrixf(transform.getTransformationMatrix().get(new float[16]));

        // Draw the front face of the cube
        glBegin(GL_QUADS);
        glColor3f(1, 0, 0);
        glVertex3f(-0.5f, -0.5f, 0.5f);
        glVertex3f(0.5f, -0.5f, 0.5f);
        glVertex3f(0.5f, 0.5f, 0.5f);
        glVertex3f(-0.5f, 0.5f, 0.5f);
        glEnd();

        // Draw the top face of the cube
        glBegin(GL_QUADS);
        glColor3f(0, 1, 0);
        glVertex3f(-0.5f, 0.5f, 0.5f);
        glVertex3f(0.5f, 0.5f, 0.5f);
        glVertex3f(0.5f, 0.5f, -0.5f);
        glVertex3f(-0.5f, 0.5f, -0.5f);
        glEnd();

        // Draw the back face of the cube
        glBegin(GL_QUADS);
        glColor3f(0, 0, 1);
        glVertex3f(-0.5f, -0.5f, -0.5f);
        glVertex3f(0.5f, -0.5f, -0.5f);
        glVertex3f(0.5f, 0.5f, -0.5f);
        glVertex3f(-0.5f, 0.5f, -0.5f);
        glEnd();

        // Draw the bottom face of the cube
        glBegin(GL_QUADS);
        glColor3f(1, 1, 0);
        glVertex3f(-0.5f, -0.5f, -0.5f);
        glVertex3f(0.5f, -0.5f, -0.5f);
        glVertex3f(0.5f, -0.5f, 0.5f);
        glVertex3f(-0.5f, -0.5f, 0.5f);
        glEnd();

        // Draw the left face of the cube
        glBegin(GL_QUADS);
        glColor3f(1, 0, 1);
        glVertex3f(-0.5f, -0.5f, -0.5f);
        glVertex3f(-0.5f, -0.5f,  0.5f);
        glVertex3f(-0.5f,  0.5f,  0.5f);
        glVertex3f(-0.5f,  0.5f, -0.5f);
        glEnd();

        // Draw the right face of the cube
        glBegin(GL_QUADS);
        glColor3f(0, 1, 1);
        glVertex3f(0.5f, -0.5f,  0.5f);
        glVertex3f(0.5f, -0.5f, -0.5f);
        glVertex3f(0.5f,  0.5f, -0.5f);
        glVertex3f(0.5f,  0.5f,  0.5f);
        glEnd();

        glPopMatrix();
    }
}