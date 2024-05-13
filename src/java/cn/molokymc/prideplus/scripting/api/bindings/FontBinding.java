package cn.molokymc.prideplus.scripting.api.bindings;

import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.font.AbstractFontRenderer;
import cn.molokymc.prideplus.utils.font.FontUtil;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

import java.util.Arrays;

@Exclude(Strategy.NAME_REMAPPING)
public class FontBinding implements Utils {

    public AbstractFontRenderer getCustomFont(String fontName, int fontSize) {
        FontUtil.FontType fontType = Arrays.stream(FontUtil.FontType.values()).filter(fontType1 -> fontType1.name().equals(fontName)).findFirst().orElse(FontUtil.FontType.RUBIK);
        return fontType.size(fontSize);
    }

    public AbstractFontRenderer getMinecraftFontRenderer() {
        return mc.fontRendererObj;
    }


    public AbstractFontRenderer getrubikFont14() {return rubikFont14; }
    public AbstractFontRenderer getrubikFont16() {return rubikFont16; }
    public AbstractFontRenderer getrubikFont18() {return rubikFont18; }
    public AbstractFontRenderer getrubikFont20() {return rubikFont20; }
    public AbstractFontRenderer getrubikFont22() {return rubikFont22; }
    public AbstractFontRenderer getrubikFont24() {return rubikFont24; }
    public AbstractFontRenderer getrubikFont26() {return rubikFont26; }
    public AbstractFontRenderer getrubikFont28() {return rubikFont28; }
    public AbstractFontRenderer getrubikFont32() {return rubikFont32; }
    public AbstractFontRenderer getrubikFont40() {return rubikFont40; }
    public AbstractFontRenderer getrubikFont80() {return rubikFont80; }
}
