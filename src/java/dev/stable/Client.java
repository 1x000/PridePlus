package dev.stable;

import dev.stable.commands.CommandHandler;
import dev.stable.config.ConfigManager;
import dev.stable.config.DragManager;
import dev.stable.event.EventProtocol;
import dev.stable.module.Module;
import dev.stable.module.ModuleCollection;
import dev.stable.scripting.api.ScriptManager;
import dev.stable.ui.altmanager.GuiAltManager;
import dev.stable.ui.searchbar.SearchBar;
import dev.stable.ui.sidegui.SideGUI;
import dev.stable.utils.Utils;
import dev.stable.utils.client.ReleaseType;
import dev.stable.utils.misc.DiscordRPC;
import dev.stable.utils.objects.DiscordAccount;
import dev.stable.utils.objects.Dragging;
import dev.stable.utils.server.PingerUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.inline.Wrapper;

import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static tech.skidonion.obfuscator.inline.Wrapper.getUsername;

@Getter
@Setter

@NativeObfuscation(virtualize = NativeObfuscation.VirtualMachine.TIGER_BLACK)
public class Client implements Utils {
    public static final Client INSTANCE = new Client();
    public static final String NAME = "PridePlus";
    public static final String VERSION = "1.0.0";
    public static final ReleaseType RELEASE = ReleaseType.RELEASE;
    public static Long userUID = Wrapper.getUserId().get();
    public static String username = Wrapper.getUsername().get();
    public static String userToken = Wrapper.getVerifyToken();

    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final File DIRECTORY = new File(mc.mcDataDir, "PridePlus");
    private final EventProtocol eventProtocol = new EventProtocol();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final SideGUI sideGui = new SideGUI();
    private final SearchBar searchBar = new SearchBar();
    private ModuleCollection moduleCollection;
    private ScriptManager scriptManager;
    private ConfigManager configManager;
    private GuiAltManager altManager;
    private CommandHandler commandHandler;
    private PingerUtils pingerUtils;
    private DiscordRPC discordRPC;
    private DiscordAccount discordAccount;

    public static boolean updateGuiScale;
    public static int prevGuiScale;
    public static void init() {
    }

    //public String getVersion() {
    //    return VERSION + (RELEASE != ReleaseType.RELEASE ? " (" + RELEASE.getName() + ")" : "");
    //}
    public String getVersion() {
        return VERSION + (RELEASE != ReleaseType.RELEASE ? " (" + RELEASE.getName() + ")" : "");
    }

    public final Color getClientColor() {
        return new Color(242, 153, 74);
    }

    public final Color getAlternateClientColor() {
        return new Color(242, 201, 76);
    }

    public boolean isEnabled(Class<? extends Module> c) {
        Module m = INSTANCE.moduleCollection.get(c);
        return m != null && m.isEnabled();
    }

    public Dragging createDrag(Module module, String name, float x, float y) {
        DragManager.draggables.put(name, new Dragging(module, name, x, y));
        return DragManager.draggables.get(name);
    }

}
