package org.swezyn.rendering;

import org.swezyn.utilities.FileUtil;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
    public static int load(String path, int type) {
        String shaderSource = FileUtil.readResourceSource("shaders/" + path);
        if (shaderSource == null) return -1;
        int shader = glCreateShader(type);
        glShaderSource(shader, shaderSource);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Failed to compile shader at " + path + ": " + glGetShaderInfoLog(shader));
        } else {
            System.out.println("Successfully compiled shader at " + path);
        }
        return shader;
    }

    public static int makeProgram(String vertexShaderPath, String fragmentShaderPath) {
        int vertexShader = Shader.load(vertexShaderPath, GL_VERTEX_SHADER);
        int fragmentShader = Shader.load(fragmentShaderPath, GL_FRAGMENT_SHADER);
        if (vertexShader == -1 || fragmentShader == -1) return -1;

        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        if(glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE)
        {
            int infoLogSize = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.err.println(glGetProgramInfoLog(shaderProgram, infoLogSize));
            System.err.println("Failed to link shader program!");
            return -1;
        } else {
            System.out.println("Successfully linked shader program");
        }
        glValidateProgram(shaderProgram);
        if (glGetProgrami(shaderProgram, GL_VALIDATE_STATUS) == GL_FALSE)
        {
            int infoLogSize = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.err.println(glGetProgramInfoLog(shaderProgram,infoLogSize));
            System.err.println("Failed to validate shader program!");
            return -1;
        } else {
            System.out.println("Successfully validated shader program");
        }

        return shaderProgram;
    }
}