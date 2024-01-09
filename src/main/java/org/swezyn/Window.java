package org.swezyn;

import org.lwjgl.BufferUtils;
import org.swezyn.rendering.Camera;
import org.swezyn.rendering.Shader;
import org.swezyn.utilities.ImageParser;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {
    private static final int INITIAL_WIDTH = 1280;
    private static final int INITIAL_HEIGHT = 720;
    private static final String TITLE = "Minecraft";

    // The window handle
    private long window;

    private final Camera camera = new Camera();

    public Window() {
        init();
        loop();
        cleanup();
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
        int shaderProgram = Shader.makeProgram("vertexShader.glsl", "fragShader.glsl");
        if (shaderProgram == -1) {
            System.err.println("Shader program failed to load!");
            System.exit(-1);
        }

        int vao;
        int vbo;
        try (MemoryStack stack = stackPush()) {
            FloatBuffer buffer = stackMallocFloat(3 * 24);
            // Front face
            buffer.put(-0.5f).put(-0.5f).put(0.5f);   // Vertex 1 (front-bottom-left)
            buffer.put(0.5f).put(-0.5f).put(0.5f);    // Vertex 2 (front-bottom-right)
            buffer.put(0.5f).put(0.5f).put(0.5f);     // Vertex 3 (front-top-right)
            buffer.put(-0.5f).put(0.5f).put(0.5f);    // Vertex 4 (front-top-left)

// Back face
            buffer.put(-0.5f).put(-0.5f).put(-0.5f);  // Vertex 5 (back-bottom-left)
            buffer.put(0.5f).put(-0.5f).put(-0.5f);   // Vertex 6 (back-bottom-right)
            buffer.put(0.5f).put(0.5f).put(-0.5f);    // Vertex 7 (back-top-right)
            buffer.put(-0.5f).put(0.5f).put(-0.5f);   // Vertex 8 (back-top-left)

// Left face
            buffer.put(-0.5f).put(-0.5f).put(-0.5f);  // Vertex 9 (left-bottom-back)
            buffer.put(-0.5f).put(-0.5f).put(0.5f);   // Vertex 10 (left-bottom-front)
            buffer.put(-0.5f).put(0.5f).put(0.5f);    // Vertex 11 (left-top-front)
            buffer.put(-0.5f).put(0.5f).put(-0.5f);   // Vertex 12 (left-top-back)

// Right face
            buffer.put(0.5f).put(-0.5f).put(-0.5f);   // Vertex 13 (right-bottom-back)
            buffer.put(0.5f).put(-0.5f).put(0.5f);    // Vertex 14 (right-bottom-front)
            buffer.put(0.5f).put(0.5f).put(0.5f);     // Vertex 15 (right-top-front)
            buffer.put(0.5f).put(0.5f).put(-0.5f);    // Vertex 16 (right-top-back)

// Top face
            buffer.put(-0.5f).put(0.5f).put(0.5f);    // Vertex 17 (top-front-left)
            buffer.put(0.5f).put(0.5f).put(0.5f);     // Vertex 18 (top-front-right)
            buffer.put(0.5f).put(0.5f).put(-0.5f);    // Vertex 19 (top-back-right)
            buffer.put(-0.5f).put(0.5f).put(-0.5f);   // Vertex 20 (top-back-left)

// Bottom face
            buffer.put(-0.5f).put(-0.5f).put(0.5f);   // Vertex 21 (bottom-front-left)
            buffer.put(0.5f).put(-0.5f).put(0.5f);    // Vertex 22 (bottom-front-right)
            buffer.put(0.5f).put(-0.5f).put(-0.5f);   // Vertex 23 (bottom-back-right)
            buffer.put(-0.5f).put(-0.5f).put(-0.5f);  // Vertex 24 (bottom-back-left)
            buffer.flip();

            vao = glGenVertexArrays();
            glBindVertexArray(vao);

            vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        }

        glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindVertexArray(0);
        glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        glUseProgram(shaderProgram);

        // Set defaults
        glClearColor(1.0f, 0.4f, 0.4f, 0.0f);
        //glEnable(GL_DEPTH_TEST);
        //glEnable(GL_LIGHTING);

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glUseProgram(shaderProgram);

            int projectionMatrixLocation = glGetUniformLocation(shaderProgram, "projectionMatrix");
            glUniformMatrix4fv(projectionMatrixLocation, false, camera.getProjectionMatrix().get(new float[16]));
            int viewMatrixLocation = glGetUniformLocation(shaderProgram, "viewMatrix");
            glUniformMatrix4fv(viewMatrixLocation, false, camera.getViewMatrix().get(new float[16]));

            glBindVertexArray(vao);
            glDrawArrays(GL_TRIANGLES, 0, 24);
            glBindVertexArray(0);

            glfwSwapBuffers(window); // swap the color buffers
            glfwPollEvents();

            int error = glGetError();
            if (error != GL_NO_ERROR) {
                System.err.println("OpenGL Error: " + error);
            }
        }
    }

    private void cleanup(){
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}