package dev.stable.utils.fshShader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL20;

public class FshShaderLoader {
    int shaderProgram;

    public String readInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String line;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public FshShaderLoader(String fragmentSource, boolean isFile) {
        int shaderProgram = GL20.glCreateProgram();
        int fragmentShader = GL20.glCreateShader((int)35632);
        try {
            GL20.glShaderSource((int)fragmentShader, (CharSequence)(isFile ? this.readInputStream(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(fragmentSource)).getInputStream()) : fragmentSource));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        GL20.glAttachShader((int)shaderProgram, (int)fragmentShader);
        GL20.glCompileShader((int)fragmentShader);
        int vertexShader = GL20.glCreateShader((int)35633);
        GL20.glShaderSource((int)vertexShader, (CharSequence)" #version 120\nvoid main() {\ngl_TexCoord[0] = gl_MultiTexCoord0;\ngl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n }\n");
        GL20.glCompileShader((int)vertexShader);
        GL20.glAttachShader((int)shaderProgram, (int)vertexShader);
        GL20.glDeleteShader((int)fragmentShader);
        GL20.glDeleteShader((int)vertexShader);
        GL20.glLinkProgram((int)shaderProgram);
        this.shaderProgram = shaderProgram;
    }

    public FshShaderLoader(String fragmentSource, String vertexSource, boolean isFile) {
        int shaderProgram = GL20.glCreateProgram();
        int fragmentShader = GL20.glCreateShader((int)35632);
        try {
            GL20.glShaderSource((int)fragmentShader, (CharSequence)(isFile ? this.readInputStream(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(fragmentSource)).getInputStream()) : fragmentSource));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        GL20.glAttachShader((int)shaderProgram, (int)fragmentShader);
        GL20.glCompileShader((int)fragmentShader);
        int vertexShader = GL20.glCreateShader((int)35633);
        try {
            GL20.glShaderSource((int)vertexShader, (CharSequence)(isFile ? this.readInputStream(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(vertexSource)).getInputStream()) : vertexSource));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        GL20.glCompileShader((int)vertexShader);
        GL20.glAttachShader((int)shaderProgram, (int)vertexShader);
        GL20.glDeleteShader((int)fragmentShader);
        GL20.glDeleteShader((int)vertexShader);
        GL20.glLinkProgram((int)shaderProgram);
        this.shaderProgram = shaderProgram;
    }

    public void useProgram() {
        GL20.glUseProgram((int)this.shaderProgram);
    }

    public void unloadProgram() {
        GL20.glUseProgram((int)0);
    }

    public void setupUniform1f(String uniform, float x) {
        int vertexColorLocation = GL20.glGetUniformLocation((int)this.shaderProgram, (CharSequence)uniform);
        GL20.glUniform1f((int)vertexColorLocation, (float)x);
    }

    public void setupUniform2f(String uniform, float x, float y) {
        int vertexColorLocation = GL20.glGetUniformLocation((int)this.shaderProgram, (CharSequence)uniform);
        GL20.glUniform2f((int)vertexColorLocation, (float)x, (float)y);
    }

    public void setupUniform3f(String uniform, float x, float y, float z) {
        int vertexColorLocation = GL20.glGetUniformLocation((int)this.shaderProgram, (CharSequence)uniform);
        GL20.glUniform3f((int)vertexColorLocation, (float)x, (float)y, (float)z);
    }

    public void setupUniform4f(String uniform, float x, float y, float z, float w) {
        int vertexColorLocation = GL20.glGetUniformLocation((int)this.shaderProgram, (CharSequence)uniform);
        GL20.glUniform4f((int)vertexColorLocation, (float)x, (float)y, (float)z, (float)w);
    }

    public void setupUniform1i(String uniform, int x) {
        int vertexColorLocation = GL20.glGetUniformLocation((int)this.shaderProgram, (CharSequence)uniform);
        GL20.glUniform1i((int)vertexColorLocation, (int)x);
    }

    public void setupUniform2i(String uniform, int x, int y) {
        int vertexColorLocation = GL20.glGetUniformLocation((int)this.shaderProgram, (CharSequence)uniform);
        GL20.glUniform2i((int)vertexColorLocation, (int)x, (int)y);
    }

    public void setupUniform3i(String uniform, int x, int y, int z) {
        int vertexColorLocation = GL20.glGetUniformLocation((int)this.shaderProgram, (CharSequence)uniform);
        GL20.glUniform3i((int)vertexColorLocation, (int)x, (int)y, (int)z);
    }

    public void setupUniform4i(String uniform, int x, int y, int z, int w) {
        int vertexColorLocation = GL20.glGetUniformLocation((int)this.shaderProgram, (CharSequence)uniform);
        GL20.glUniform4i((int)vertexColorLocation, (int)x, (int)y, (int)z, (int)w);
    }

    public int getUniform(String name) {
        return GL20.glGetUniformLocation((int)this.shaderProgram, (CharSequence)name);
    }
}

