package org.swezyn.utilities;
import org.joml.Matrix4f;
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

    public Transform(Vector3f position){
        this.position = position;
        rotation = new Vector3f();
        scale = new Vector3f(1.0f, 1.0f, 1.0f);
    }

    public Transform(Vector3f position, Vector3f rotation){
        this.position = position;
        this.rotation = rotation;
        scale = new Vector3f(1.0f, 1.0f, 1.0f);
    }

    public Transform(Vector3f position, Vector3f rotation, Vector3f scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
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
