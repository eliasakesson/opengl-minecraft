package org.swezyn.utilities;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {
    public Vector3f position;
    public Vector3f rotation;
    public Vector3f scale;

    public Transform() {
        position = new Vector3f();
        rotation = new Vector3f();
        scale = new Vector3f(1.0f, 1.0f, 1.0f);
    }

    public Vector3f getLookVector() {
        Vector3f lookVector = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f upVector = new Vector3f(0.0f, 1.0f, 0.0f);
        Quaternionf orientation = new Quaternionf()
                .rotateAxis((float) Math.toRadians(rotation.y), upVector)
                .rotateAxis((float) Math.toRadians(0), new Vector3f(1.0f, 0.0f, 0.0f));
        lookVector.set(0.0f, 0.0f, -1.0f).rotate(orientation);
        upVector.set(0.0f, 1.0f, 0.0f).rotate(orientation);

        lookVector = lookVector.cross(upVector, new Vector3f()).normalize();

        return new Vector3f(-lookVector.x, lookVector.y, lookVector.z);
    }

    public Vector3f getRightVector() {
        Vector3f rightVector = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f upVector = new Vector3f(0.0f, 1.0f, 0.0f);
        Quaternionf orientation = new Quaternionf()
                .rotateAxis((float) Math.toRadians(rotation.y), upVector)
                .rotateAxis((float) Math.toRadians(0), new Vector3f(1.0f, 0.0f, 0.0f));
        rightVector.set(0.0f, 0.0f, -1.0f).rotate(orientation);
        upVector.set(0.0f, 1.0f, 0.0f).rotate(orientation);

        return new Vector3f(-rightVector.x, rightVector.y, rightVector.z);
    }

    public Matrix4f getTransformationMatrix() {
        return new Matrix4f()
                .translate(position)
                .rotateX((float) Math.toRadians(rotation.x))
                .rotateY((float) Math.toRadians(rotation.y))
                .rotateZ((float) Math.toRadians(rotation.z))
                .scale(scale);
    }
}
