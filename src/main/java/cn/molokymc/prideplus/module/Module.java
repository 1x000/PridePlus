package cn.molokymc.prideplus.module;

import cn.molokymc.prideplus.Pride;
import cn.molokymc.prideplus.event.ListenerAdapter;
import cn.molokymc.prideplus.module.settings.impl.*;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import cn.molokymc.prideplus.config.ConfigSetting;

import cn.molokymc.prideplus.event.impl.player.EventMoveInput;
import cn.molokymc.prideplus.module.impl.render.NotificationsMod;
import cn.molokymc.prideplus.module.settings.Setting;

import cn.molokymc.prideplus.ui.notifications.NotificationManager;
import cn.molokymc.prideplus.ui.notifications.NotificationType;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.animations.Animation;
import cn.molokymc.prideplus.utils.animations.Direction;
import cn.molokymc.prideplus.utils.animations.impl.DecelerateAnimation;
import cn.molokymc.prideplus.utils.misc.Multithreading;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Keyboard;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;


@Getter
@Setter
public class Module extends ListenerAdapter implements Utils {
    @Expose
    @SerializedName("name")
    private final String name;
    private final String cname;
    private final String description;
    private final Category category;
    public boolean state;
    private final CopyOnWriteArrayList<Setting> settingsList = new CopyOnWriteArrayList<>();
    private String suffix;
    private String author = "";

    @Expose
    @SerializedName("toggled")
    protected boolean enabled;
    @Expose
    @SerializedName("settings")
    public ConfigSetting[] cfgSettings;


    private boolean expanded;
    private final Animation animation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);

    public static int categoryCount;
    public static float allowedClickGuiHeight = 300;

    private final KeybindSetting keybind = new KeybindSetting(Keyboard.KEY_NONE);


    public Module(String name,String cname, Category category, String description) {
        this.name = name;
        this.cname = cname;
        this.category = category;
        this.description = description;
        addSettings(keybind);
    }

    public boolean isInGame() {
        return mc.theWorld != null && mc.thePlayer != null;
    }

    public void addSettings(Setting... settings) {
        settingsList.addAll(Arrays.asList(settings));
    }
    public boolean getState() {
        return this.state;
    }
    @Exclude(Strategy.NAME_REMAPPING)
    public void setToggled(boolean toggled) {
        this.enabled = toggled;
        if (toggled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void toggleSilent() {
        this.enabled = !this.enabled;
        if (this.enabled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void toggleSilent(boolean toggled) {
        this.enabled = toggled;
        if (this.enabled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

    public void toggle() {
        toggleSilent();
        if (NotificationsMod.toggleNotifications.isEnabled()) {
            String titleToggle = " ";
            String descriptionToggleOn =this.getName() + " " + "§aEnabled\r" + " §f";
            String descriptionToggleOff = this.getName()+ " " +"§cDisabled\r" + " §f";

            if (enabled) {
                Module.mc.theWorld.playSound(Module.mc.thePlayer.posX, Module.mc.thePlayer.posY, Module.mc.thePlayer.posZ, "random.click", 0.5f,0.6f, false);
                NotificationManager.post(NotificationType.SUCCESS, titleToggle, descriptionToggleOn);
            } else {
                mc.thePlayer.playSound("random.click", 0.5f, 0.5f);
                NotificationManager.post(NotificationType.DISABLE, titleToggle, descriptionToggleOff);
            }
        }
    }

    public boolean hasMode() {
        return suffix != null;
    }


    public void onEnable() {
        Pride.INSTANCE.getEventProtocol().register(this);
    }

    public void onDisable() {
        Multithreading.schedule(() -> Pride.INSTANCE.getEventProtocol().unregister(this), 250, TimeUnit.MILLISECONDS);
        Pride.INSTANCE.getEventProtocol().unregister(this);
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setKey(int code) {
        this.keybind.setCode(code);
    }


    @Exclude(Strategy.NAME_REMAPPING)
    public String getName() {
        return name;
    }
    public String getChineseName() {
        return cname;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public String getDescription() {
        return description;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public boolean isEnabled() {
        return enabled;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public int getKeybindCode() {
        return keybind.getCode();
    }


    @Exclude(Strategy.NAME_REMAPPING)
    public NumberSetting getNumberSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof NumberSetting && setting.getName().equalsIgnoreCase(name)) {
                return (NumberSetting) setting;
            }
        }
        return null;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public BooleanSetting getBooleanSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof BooleanSetting && setting.getName().equalsIgnoreCase(name)) {
                return (BooleanSetting) setting;
            }
        }
        return null;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public ModeSetting getModeSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof ModeSetting && setting.getName().equalsIgnoreCase(name)) {
                return (ModeSetting) setting;
            }
        }
        return null;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public StringSetting getStringSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof StringSetting && setting.getName().equalsIgnoreCase(name)) {
                return (StringSetting) setting;
            }
        }
        return null;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public MultipleBoolSetting getMultiBoolSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof MultipleBoolSetting && setting.getName().equalsIgnoreCase(name)) {
                return (MultipleBoolSetting) setting;
            }
        }
        return null;
    }


    @Exclude(Strategy.NAME_REMAPPING)
    public ColorSetting getColorSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof ColorSetting && setting.getName().equalsIgnoreCase(name)) {
                return (ColorSetting) setting;
            }
        }
        return null;
    }

    @Override
    public void onMoveInput(EventMoveInput event) {

    }

    }
