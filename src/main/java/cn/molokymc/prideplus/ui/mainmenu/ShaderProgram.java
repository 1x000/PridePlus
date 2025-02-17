package cn.molokymc.prideplus.ui.mainmenu;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    protected static final Minecraft mc = Minecraft.getMinecraft();

    private final String vertexName, fragmentName;

    private final int vertexShaderID, fragmentShaderID;
    @Getter
    private final int programID;

    private boolean initiated;

    public ShaderProgram(String vertexName, String fragmentName) {
        this.vertexName = vertexName;
        this.fragmentName = fragmentName;

        int program = glCreateProgram();

        final String vertexSource = readShader(vertexName);
        vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderID, vertexSource);
        glCompileShader(vertexShaderID);

        if (glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println(glGetShaderInfoLog(vertexShaderID, 4096));
            throw new IllegalStateException(String.format("Vertex Shader (%s) failed to compile!", GL_VERTEX_SHADER));
        }

        final String fragmentSource = readShader(fragmentName);
        fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderID, fragmentSource);
        glCompileShader(fragmentShaderID);

        if (glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println(glGetShaderInfoLog(fragmentShaderID, 4096));
            throw new IllegalStateException(String.format("Fragment Shader (%s) failed to compile!", GL_FRAGMENT_SHADER));
        }

        glAttachShader(program, vertexShaderID);
        glAttachShader(program, fragmentShaderID);
        glLinkProgram(program);
        programID = program;
    }

    public ShaderProgram(String fragmentName) {
        this("vertex/vertex.vert", fragmentName);
    }

    private static String readShader(String fileName) {
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            final InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(ShaderProgram.class.getClassLoader().getResourceAsStream(
                    String.format("assets/minecraft/freeshaders/shaders/%s", fileName))));
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line).append('\n');
            bufferedReader.close();
            inputStreamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    public void renderCanvas(final ScaledResolution scaledResolution) {
        float width = scaledResolution.getScaledWidth();
        float height = scaledResolution.getScaledHeight();
        renderCanvas(0, 0, width, height);
    }

    public void renderCanvas(float x, float y, float width, float height) {
        glDisable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(x, y);
        glTexCoord2f(0, 0);
        glVertex2f(x, height);
        glTexCoord2f(1, 0);
        glVertex2f(width, height);
        glTexCoord2f(1, 1);
        glVertex2f(width, y);
        glEnd();
        glDisable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
    }

    public void deleteShaderProgram() {
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        glDeleteProgram(programID);
    }

    public void init() {
        glUseProgram(programID);
    }

    public void uninit() {
        glUseProgram(0);
    }

    public int getUniform(String name) {
        return glGetUniformLocation(programID, name);
    }

    public void setUniformf(String name, float... args) {
        int loc = glGetUniformLocation(programID, name);
        if (args.length > 1) {
            if (args.length > 2) {
                if (args.length > 3) glUniform4f(loc, args[0], args[1], args[2], args[3]);
                else glUniform3f(loc, args[0], args[1], args[2]);
            } else glUniform2f(loc, args[0], args[1]);
        } else glUniform1f(loc, args[0]);
    }


    public void setUniformi(String name, int... args) {
        int loc = glGetUniformLocation(programID, name);
        if (args.length > 1) glUniform2i(loc, args[0], args[1]);
        else glUniform1i(loc, args[0]);
    }

    @Override
    public String toString() {
        return "ShaderProgram{" +
                "programID=" + programID +
                ", vertexName='" + vertexName + '\'' +
                ", fragmentName='" + fragmentName + '\'' +
                '}';
    }
}