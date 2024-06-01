package cn.molokymc.prideplus;

import cn.molokymc.prideplus.commands.CommandHandler;
import cn.molokymc.prideplus.config.ConfigManager;
import cn.molokymc.prideplus.config.DragManager;
import cn.molokymc.prideplus.event.EventProtocol;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.ModuleCollection;
import cn.molokymc.prideplus.scripting.api.ScriptManager;
import cn.molokymc.prideplus.ui.alt.GuiAltManager;
import cn.molokymc.prideplus.ui.searchbar.SearchBar;
import cn.molokymc.prideplus.ui.sidegui.SideGUI;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.client.ReleaseType;
import cn.molokymc.prideplus.utils.misc.DiscordRPC;
import cn.molokymc.prideplus.utils.movementfix.FallDistanceComponent;
import cn.molokymc.prideplus.utils.objects.DiscordAccount;
import cn.molokymc.prideplus.utils.objects.Dragging;
import cn.molokymc.prideplus.utils.server.PingerUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Setter
public class Pride implements Utils {
    public static final Pride INSTANCE = new Pride();
    public static final String NAME = "PridePlus";
    public static final ReleaseType RELEASE = ReleaseType.RELEASE;
    public static final String VERSION = "0.0.1 " + RELEASE.getName();
    public static String username = "觉醒者";

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
    private FallDistanceComponent fallDistanceComponent;
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
