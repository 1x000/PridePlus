package cn.molokymc.prideplus.ui.clickguis.dropdown.components.settings;

import cn.molokymc.prideplus.module.impl.render.HUDMod;
import cn.molokymc.prideplus.module.settings.impl.StringSetting;
import cn.molokymc.prideplus.ui.clickguis.dropdown.components.SettingComponent;
import cn.molokymc.prideplus.utils.objects.TextField;
import cn.molokymc.prideplus.utils.skidfont.FontManager;

import java.awt.*;

public class StringComponent extends SettingComponent<StringSetting> {

    private final TextField textField = new TextField(productregular14);

    public StringComponent(StringSetting setting) {
        super(setting);
    }


    boolean setDefaultText = false;
    @Override
    public void initGui() {
        setDefaultText = false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        textField.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        float boxX = x + 6;
        float boxY = y + 12;
        float boxWidth = width - 12;
        float boxHeight = height - 16;

        if(!setDefaultText){
            textField.setText(getSetting().getString());
            textField.setCursorPositionZero();
            setDefaultText = true;
        }



        getSetting().setString(textField.getText());

        if(HUDMod.Language.equals("English")){textField.setBackgroundText("Type here...");}
        if(HUDMod.Language.equals("Chinese")){textField.setBackgroundText("在此输入...");}
        productregular12.drawString(getSetting().name, boxX, y + 3, new Color(0,0,0));


        textField.setXPosition(boxX);
        textField.setYPosition(boxY);
        textField.setWidth(boxWidth);
        textField.setHeight(boxHeight);
        //textField.setOutline(settingRectColor.brighter().brighter().brighter());
        textField.setFill(new Color(0,0,0,30));

        textField.drawTextBox();


        if(!typing) {
            typing = textField.isFocused();
        }


        countSize = 2f;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        textField.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
