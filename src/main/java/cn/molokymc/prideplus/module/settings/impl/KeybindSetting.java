package cn.molokymc.prideplus.module.settings.impl;

import cn.molokymc.prideplus.module.settings.Setting;
import org.lwjgl.input.Keyboard;

public class KeybindSetting extends Setting {

    private int code;

    public KeybindSetting(int code) {
        this.name = "Keybind";
        this.code = code;
    }

    public int getCode() {
        return code == -1 ? Keyboard.KEY_NONE : code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public Integer getConfigValue() {
        return this.getCode();
    }

}
