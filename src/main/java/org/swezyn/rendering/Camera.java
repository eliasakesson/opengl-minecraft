package org.swezyn.rendering;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.swezyn.utilities.Transform;

public class Camera {
    public final Transform transform;

    public Camera() {
        transform = new Transform();
    }

    public void addYaw(float yaw) {
        while (yaw > 180) yaw -= 360;
        while (yaw < 180) yaw += 360;
        transform.rotation.y += yaw;
        transform.rotation.y = (transform.rotation.y + 180.0f) % 360.0f - 180.0f;
    }

    public void addPitch(float pitch) {
        float min = -85;
        float max = 85;
        transform.rotation.x += pitch;
        transform.rotation.x = Math.max(min, Math.min(max, transform.rotation.x));
    }

    public void addPosition(Vector3f position) {
        transform.position.add(position);
    }

    public Matrix4f getProjectionMatrix() {
        float fov = 70.0f;
        float aspectRatio = 16.0f / 9.0f;
        float near = 0.1f;
        float far = 1000.0f;

        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.perspective(fov, aspectRatio, near, far);

        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().setLookAt(
                transform.position,
                new Vector3f(
                        (float) (transform.position.x + Math.cos(Math.toRadians(transform.rotation.y + 180)) * Math.cos(Math.toRadians(transform.rotation.x))),
                        (float) (transform.position.y + Math.sin(Math.toRadians(transform.rotation.x))),
                        (float) (transform.position.z + Math.sin(Math.toRadians(transform.rotation.y + 180)) * Math.cos(Math.toRadians(transform.rotation.x)))
                ),
                new Vector3f(0.0f, 1.0f, 0.0f)
        );
    }
}