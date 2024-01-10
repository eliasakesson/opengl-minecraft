package org.swezyn;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.swezyn.rendering.Camera;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {
    private static final float mouseSensitivity = 0.1f;
    private static final float moveSpeed = 0.1f;

    private final Window window;
    private final Camera camera;

    private double lastMouseX = 0;
    private double lastMouseY = 0;

    public InputHandler(Window window, Camera camera) {
        this.window = window;
        this.camera = camera;
    }

    public void handleInput() {
        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();

        glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        // Camera movement
        Vector3f movement = new Vector3f();
        Vector3f lookVector = camera.transform.getLookVector();
        Vector3f rightVector = camera.transform.getRightVector();
        if (glfwGetKey(window.getWindow(), GLFW_KEY_W) == GLFW_PRESS) {
            movement.add(lookVector);
        }
        if (glfwGetKey(window.getWindow(), GLFW_KEY_S) == GLFW_PRESS) {
            movement.add(lookVector.mul(-1));
        }
        if (glfwGetKey(window.getWindow(), GLFW_KEY_A) == GLFW_PRESS) {
            movement.add(rightVector.mul(-1));
        }
        if (glfwGetKey(window.getWindow(), GLFW_KEY_D) == GLFW_PRESS) {
            movement.add(rightVector);
        }
        if (glfwGetKey(window.getWindow(), GLFW_KEY_SPACE) == GLFW_PRESS) {
            movement.add(new Vector3f(0.0f, 1.0f, 0.0f));
        }
        if (glfwGetKey(window.getWindow(), GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS) {
            movement.add(new Vector3f(0.0f, -1.0f, 0.0f));
        }
        movement = movement.mul(moveSpeed);
        camera.addPosition(movement);

        // Camera rotation
        DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer posY = BufferUtils.createDoubleBuffer(1);

        glfwGetCursorPos(window.getWindow(), posX, posY);

        double mouseX = posX.get(0);
        double mouseY = posY.get(0);

        double mouseDX = mouseX - lastMouseX;
        double mouseDY = mouseY - lastMouseY;
        lastMouseX = mouseX;
        lastMouseY = mouseY;

        camera.addYaw((float) mouseDX * mouseSensitivity);
        camera.addPitch((float) -mouseDY * mouseSensitivity);
    }
}
