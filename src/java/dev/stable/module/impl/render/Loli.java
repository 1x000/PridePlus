package dev.stable.module.impl.render;

import dev.stable.module.Category;
import dev.stable.module.Module;

public class Loli extends Module {
    public int heilY;
    /**
     * ModelBiped.java ♥
     */
    public static Loli getInstance;
    @Override
    public void onEnable() {
        heilY = 0;
    }
    public Loli() {
        super("Loli","变萝莉", Category.MISC, "成为小萝莉");
        getInstance = this;
    }
}