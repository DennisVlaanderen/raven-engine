package org.raven.util;

import org.raven.objects.components.Spritesheet;
import org.raven.renderer.Shader;
import org.raven.renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {

    private AssetPool() {
        // Private constructor to prevent initialisation.
    }

    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, Spritesheet> spritesheets = new HashMap<>();

    public static Shader getShader(String name) {
        File file = new File(name);
        if (AssetPool.shaders.containsKey(file.getAbsolutePath())) {
            return AssetPool.shaders.get(file.getAbsolutePath());
        } else {
            Shader shader = new Shader(name);
            shader.compileAndLink();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }

    public static Texture getTexture (String name) {
        File file = new File(name);
        if (AssetPool.textures.containsKey(file.getAbsolutePath())) {
           return AssetPool.textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture(name);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }

    public static void addSpritesheet(String resourcePath, Spritesheet spritesheet) {
        File file = new File(resourcePath);
        if (!AssetPool.spritesheets.containsKey(file.getAbsolutePath())) {
            AssetPool.spritesheets.put(file.getAbsolutePath(), spritesheet);
        }
    }

    public static Spritesheet getSpritesheet(String resourcePath) {
        File file = new File(resourcePath);
        if (!AssetPool.spritesheets.containsKey(file.getAbsolutePath())) {
            throw new AssertionError("Path to spritesheet doesn't match any known spritesheet.");
        }
        return AssetPool.spritesheets.getOrDefault(file.getAbsolutePath(), null);
    }

}
