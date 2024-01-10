package org.swezyn.rendering;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.BufferUtils.createFloatBuffer;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

enum Face {
    FRONT,
    BACK,
    LEFT,
    RIGHT,
    TOP,
    BOTTOM
}

public class Rendering {
    Camera camera;

    int shaderProgram;

    int vao;
    int vbo;

    public Rendering(Camera _camera) {
        camera = _camera;

        GL.createCapabilities();

        shaderProgram = Shader.makeProgram("vertexShader.glsl", "fragShader.glsl");
        if (shaderProgram == -1) {
            System.err.println("Shader program failed to load!");
            System.exit(-1);
        }

        List<Float> vertices = GetCubeVertices();
        FloatBuffer verticesBuffer = GetVerticesBuffer(vertices);

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        glUseProgram(shaderProgram);
    }

    public void Render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUseProgram(shaderProgram);

        int projectionMatrixLocation = glGetUniformLocation(shaderProgram, "projectionMatrix");
        glUniformMatrix4fv(projectionMatrixLocation, false, camera.getProjectionMatrix().get(new float[16]));
        int viewMatrixLocation = glGetUniformLocation(shaderProgram, "viewMatrix");
        glUniformMatrix4fv(viewMatrixLocation, false, camera.getViewMatrix().get(new float[16]));

        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, 6 * 6);
        glBindVertexArray(0);
    }

    public FloatBuffer GetVerticesBuffer(List<Float> vertices) {
        FloatBuffer verticesBuffer = createFloatBuffer(vertices.size());
        for (Float vertex : vertices) {
            verticesBuffer.put(vertex);
        }
        verticesBuffer.flip();
        return verticesBuffer;
    }

    public List<Float> GetCubeVertices() {
        List<Float> vertices = new ArrayList<Float>();

        vertices.addAll(GetFacevectors(Face.TOP));
        vertices.addAll(GetFacevectors(Face.BOTTOM));
        vertices.addAll(GetFacevectors(Face.FRONT));
        vertices.addAll(GetFacevectors(Face.BACK));
        vertices.addAll(GetFacevectors(Face.LEFT));
        vertices.addAll(GetFacevectors(Face.RIGHT));

        return vertices;
    }

    public List<Float> GetFacevectors(Face face) {
        List<Vector3f> vectors = new ArrayList<Vector3f>();

        if (face == Face.FRONT) {
            vectors.add(new Vector3f(-0.5f, -0.5f, 0.5f));
            vectors.add(new Vector3f(0.5f, -0.5f, 0.5f));
            vectors.add(new Vector3f(0.5f, 0.5f, 0.5f));
            vectors.add(new Vector3f(-0.5f, 0.5f, 0.5f));
            vectors.add(new Vector3f(-0.5f, -0.5f, 0.5f));
            vectors.add(new Vector3f(0.5f, 0.5f, 0.5f));
        } else if (face == Face.BACK) {
            vectors.add(new Vector3f(-0.5f, -0.5f, -0.5f));
            vectors.add(new Vector3f(0.5f, 0.5f, -0.5f));
            vectors.add(new Vector3f(0.5f, -0.5f, -0.5f));
            vectors.add(new Vector3f(-0.5f, 0.5f, -0.5f));
            vectors.add(new Vector3f(0.5f, 0.5f, -0.5f));
            vectors.add(new Vector3f(-0.5f, -0.5f, -0.5f));
        } else if (face == Face.LEFT) {
            vectors.add(new Vector3f(-0.5f, -0.5f, -0.5f));
            vectors.add(new Vector3f(-0.5f, -0.5f, 0.5f));
            vectors.add(new Vector3f(-0.5f, 0.5f, 0.5f));
            vectors.add(new Vector3f(-0.5f, 0.5f, -0.5f));
            vectors.add(new Vector3f(-0.5f, -0.5f, -0.5f));
            vectors.add(new Vector3f(-0.5f, 0.5f, 0.5f));
        } else if (face == Face.RIGHT) {
            vectors.add(new Vector3f(0.5f, -0.5f, -0.5f));
            vectors.add(new Vector3f(0.5f, 0.5f, 0.5f));
            vectors.add(new Vector3f(0.5f, -0.5f, 0.5f));
            vectors.add(new Vector3f(0.5f, 0.5f, -0.5f));
            vectors.add(new Vector3f(0.5f, 0.5f, 0.5f));
            vectors.add(new Vector3f(0.5f, -0.5f, -0.5f));
        } else if (face == Face.TOP) {
            vectors.add(new Vector3f(-0.5f, 0.5f, 0.5f));
            vectors.add(new Vector3f(0.5f, 0.5f, 0.5f));
            vectors.add(new Vector3f(0.5f, 0.5f, -0.5f));
            vectors.add(new Vector3f(-0.5f, 0.5f, -0.5f));
            vectors.add(new Vector3f(-0.5f, 0.5f, 0.5f));
            vectors.add(new Vector3f(0.5f, 0.5f, -0.5f));
        } else if (face == Face.BOTTOM) {
            vectors.add(new Vector3f(-0.5f, -0.5f, 0.5f));
            vectors.add(new Vector3f(0.5f, -0.5f, -0.5f));
            vectors.add(new Vector3f(0.5f, -0.5f, 0.5f));
            vectors.add(new Vector3f(-0.5f, -0.5f, -0.5f));
            vectors.add(new Vector3f(0.5f, -0.5f, -0.5f));
            vectors.add(new Vector3f(-0.5f, -0.5f, 0.5f));
        }

        List<Float> vertices = new ArrayList<Float>();
        for (Vector3f vector : vectors) {
            vertices.add(vector.x);
            vertices.add(vector.y);
            vertices.add(vector.z);
        }

        return vertices;
    }
}
