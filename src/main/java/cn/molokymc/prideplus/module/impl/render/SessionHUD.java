package cn.molokymc.prideplus.module.impl.render;

import cn.molokymc.prideplus.Pride;
import cn.molokymc.prideplus.event.impl.render.Render2DEvent;
import cn.molokymc.prideplus.event.impl.render.ShaderEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.settings.impl.BooleanSetting;
import cn.molokymc.prideplus.module.settings.impl.ModeSetting;
import cn.molokymc.prideplus.utils.client.TimeLog;
import cn.molokymc.prideplus.utils.objects.Dragging;
import cn.molokymc.prideplus.utils.render.ColorUtil;
import cn.molokymc.prideplus.utils.render.RoundedUtil;

import java.awt.*;

import static cn.molokymc.prideplus.module.impl.render.HUDMod.get;
import static cn.molokymc.prideplus.utils.skidfont.FontManager.*;

public class SessionHUD extends Module {


    private final Dragging SessionInfo_dragging = Pride.INSTANCE.createDrag(this, "sessioninfo", 5, 150);
    private final ModeSetting sessionMode = new ModeSetting("Session Mode", "Rainy", "Rainy","Rise");
    public static final BooleanSetting bar = new BooleanSetting("Bar", false);

    public TimeLog timelog = new TimeLog();
    public static int Wins, Kills, Dead = 0;
    private String time = "";
    private String kill = "";
    private String win = "";
    private String title = "";
    private String finaltitle = "";

    public SessionHUD() {
        super("SessionInfo","游戏信息显示", Category.DISPLAY, "SessionInfo");
        addSettings(sessionMode,bar);
    }

    @Override
    public void onShaderEvent(ShaderEvent event) {
        switch (sessionMode.getMode()){
            case "Rise":
                RoundedUtil.drawRoundOutline(SessionInfo_dragging.getX(), SessionInfo_dragging.getY(), SessionInfo_dragging.getWidth(), SessionInfo_dragging.getHeight(), 10,0.00000001f, ColorUtil.applyOpacity(Color.BLACK, 50), ColorUtil.applyOpacity(Color.BLACK, 50));
        }
    }
    @Override
    public void onRender2DEvent(Render2DEvent event) {
        switch (sessionMode.getMode()){
            case "Rise":
                float width;
                float height;
                height = 56;
                width = 130;
                time = getMins()+" minutes " + getSeconds() +" seconds";
                kill = "kills: "+Kills;
                win = "wins: "+Wins;
                title = "Session Stats";
                finaltitle = get(title);
                SessionInfo_dragging.setWidth(width);
                SessionInfo_dragging.setHeight(height);
                RoundedUtil.drawRoundOutline(SessionInfo_dragging.getX(), SessionInfo_dragging.getY(), SessionInfo_dragging.getWidth(), SessionInfo_dragging.getHeight(), 10,0.0000005f,new Color(0, 0, 0, 80),HUDMod.getColor(500));
                product_sans_bold_24.drawString(finaltitle, SessionInfo_dragging.getX()+SessionInfo_dragging.getWidth() / 2 - product_sans_bold_24.getStringWidth(finaltitle)/2 , SessionInfo_dragging.getY() + 5, HUDMod.getColor(0).getRGB());
                product_sans_bold_18.drawString(time,SessionInfo_dragging.getX()+SessionInfo_dragging.getWidth()/2-product_sans_bold_18.getStringWidth(time)/2,SessionInfo_dragging.getY()+25,Color.WHITE);
                product_sans_medium_18.drawString(kill,SessionInfo_dragging.getX()+SessionInfo_dragging.getWidth()/4-product_sans_medium_18.getStringWidth(kill)/2+4,SessionInfo_dragging.getY()+38,Color.WHITE);
                product_sans_medium_18.drawString(win,SessionInfo_dragging.getX()+SessionInfo_dragging.getWidth()/2+SessionInfo_dragging.getWidth()/4-product_sans_medium_18.getStringWidth(win)/2-4,SessionInfo_dragging.getY()+38,Color.WHITE);
        }
    }
    public String getHour() {
        String h = String.valueOf(timelog.getHour());
        String h2 = "";
        if (h.length() < 2) {
            h2 = "0" + h;
            return h2;
        } else {
            return h;
        }

    }

    public String getMins() {
        String m = String.valueOf(timelog.getMinute());
        String m2 = "";
        if (m.length() < 2) {
            m2 = "0" + m;
            return m2;
        } else {
            return m;
        }
    }
    public String getSeconds() {
        String s = String.valueOf(timelog.getSecond());
        String s2 = "";
        if (s.length() < 2) {
            s2 = "0" + s;
            return s2;
        } else {
            return s;
        }
    }
 }

