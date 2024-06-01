package cn.molokymc.prideplus;

import cn.molokymc.prideplus.commands.CommandHandler;
import cn.molokymc.prideplus.commands.impl.*;
import cn.molokymc.prideplus.config.ConfigManager;
import cn.molokymc.prideplus.config.DragManager;
import cn.molokymc.prideplus.event.EventProtocol;
import cn.molokymc.prideplus.module.BackgroundProcess;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.ModuleCollection;
import cn.molokymc.prideplus.module.impl.combat.*;
import cn.molokymc.prideplus.module.impl.exploit.Disabler;
import cn.molokymc.prideplus.module.impl.exploit.Teams;
import cn.molokymc.prideplus.module.impl.misc.*;
import cn.molokymc.prideplus.module.impl.movement.*;
import cn.molokymc.prideplus.module.impl.player.*;
import cn.molokymc.prideplus.module.impl.render.*;
import cn.molokymc.prideplus.module.impl.render.killeffects.KillEffects;
import cn.molokymc.prideplus.scripting.api.ScriptManager;
import cn.molokymc.prideplus.ui.alt.AltManager;
import cn.molokymc.prideplus.ui.alt.GuiAltManager;
import cn.molokymc.prideplus.ui.mainmenu.CustomMainMenu;
import cn.molokymc.prideplus.ui.searchbar.SearchBar;
import cn.molokymc.prideplus.ui.sidegui.SideGUI;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.client.ReleaseType;
import cn.molokymc.prideplus.utils.misc.DiscordRPC;
import cn.molokymc.prideplus.utils.movementfix.BadPacketsComponent;
import cn.molokymc.prideplus.utils.movementfix.FallDistanceComponent;
import cn.molokymc.prideplus.utils.movementfix.Rise.RotationComponent;
import cn.molokymc.prideplus.utils.movementfix.SlotComponent;
import cn.molokymc.prideplus.utils.objects.DiscordAccount;
import cn.molokymc.prideplus.utils.objects.Dragging;
import cn.molokymc.prideplus.utils.render.Theme;
import cn.molokymc.prideplus.utils.server.PingerUtils;
import cn.molokymc.prideplus.viamcp.ViaMCP;
import cn.molokymc.prideplus.viamcp.common.ViaMCPCommon;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ForceUnicodeChat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Setter
public class Pride implements Utils {
    public static final Pride INSTANCE = new Pride();
    public static final ReleaseType RELEASE = ReleaseType.RELEASE;
    public static final String NAME = "PridePlus";
    public static final String VERSION = "0.0.1 " + RELEASE.getName();
    public static String username = "觉醒者";

    private static final HashMap<Object, Module> modules = new HashMap<>();

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

    public static void start() {
        // ViaForgeMCP
        ViaMCPCommon.init(ViaMCP.PLATFORM);

        // Setup Intent API access
      //  Client.INSTANCE.setIntentAccount(GetUserInfo.loginFailure);
        INSTANCE.setModuleCollection(new ModuleCollection());

        // Combat
        modules.put(Antibot.class, new Antibot());
        modules.put(KillAura.class, new KillAura());
        modules.put(TargetStrafe.class, new TargetStrafe());
        modules.put(Velocity.class, new Velocity());
        modules.put(Criticals.class, new Criticals());
        //modules.put(AutoHead.class, new AutoHead());
        modules.put(AutoPot.class, new AutoPot());
        modules.put(KeepSprint.class, new KeepSprint());

        // Exploit
        modules.put(Disabler.class, new Disabler());
        //modules.put(Regen.class, new Regen());
        modules.put(Teams.class, new Teams());
        // Misc
        modules.put(LightningTracker.class, new LightningTracker());
        modules.put(HackerDetector.class, new HackerDetector());
        modules.put(AutoHypixel.class, new AutoHypixel());
        modules.put(MCF.class, new MCF());
        modules.put(Protocol.class,new Protocol());

        // Movement
        modules.put(Sprint.class, new Sprint());
        modules.put(Scaffold.class, new Scaffold());
        modules.put(Speed.class, new Speed());
        modules.put(Flight.class, new Flight());
        modules.put(LongJump.class, new LongJump());
        modules.put(Step.class, new Step());
        modules.put(InventoryMove.class, new InventoryMove());
        //modules.put(Jesus.class, new Jesus());

        // Player
        modules.put(Stealer.class, new Stealer());
        modules.put(Manager.class, new Manager());
        modules.put(AutoArmor.class, new AutoArmor());
        modules.put(SpeedMine.class, new SpeedMine());
        modules.put(Blink.class, new Blink());
        //modules.put(NoFall.class, new NoFall());
        modules.put(Timer.class, new Timer());
        //modules.put(Freecam.class, new Freecam());
        modules.put(FastPlace.class, new FastPlace());
        modules.put(SafeWalk.class, new SafeWalk());
        modules.put(NoSlowDown.class, new NoSlowDown());
        modules.put(AutoTool.class, new AutoTool());
        modules.put(AntiVoid.class, new AntiVoid());
        modules.put(KillEffects.class, new KillEffects());
        modules.put(Stuck.class, new Stuck());
        //modules.put(Breaker.class, new Breaker());

        // Render
        modules.put(MoBendsMod.class, new MoBendsMod());
        //modules.put(Radar.class, new Radar());
        modules.put(Dab.class, new Dab());
        modules.put(Loli.class, new Loli());
        modules.put(Animations.class, new Animations());
        modules.put(Ambience.class, new Ambience());
        modules.put(ChinaHat.class, new ChinaHat());
        modules.put(Brightness.class, new Brightness());
        modules.put(ESP2D.class, new ESP2D());
        modules.put(Health.class, new Health());
        modules.put(MotionBlur.class, new MotionBlur());
        modules.put(JumpCircle.class, new JumpCircle());
        //modules.put(Glint.class, new Glint());
        //modules.put(Streamer.class, new Streamer());
        modules.put(NoHurtCam.class, new NoHurtCam());
        modules.put(ItemPhysics.class, new ItemPhysics());
        //.put(XRay.class, new XRay());
        //modules.put(EntityCulling.class, new EntityCulling());
        //modules.put(DragonWings.class, new DragonWings());
        modules.put(CustomModel.class, new CustomModel());;
        modules.put(Chams.class, new Chams());
        modules.put(RemoveEffects.class, new RemoveEffects());
        modules.put(CameraClip.class, new CameraClip());
        modules.put(ForceUnicodeChat.class, new ForceUnicodeChat());

        //Display
        modules.put(ScoreboardMod.class, new ScoreboardMod());
        modules.put(PostProcessing.class, new PostProcessing());
        modules.put(ClickGUIMod.class, new ClickGUIMod());
        modules.put(ArrayListMod.class, new ArrayListMod());
        modules.put(NotificationsMod.class, new NotificationsMod());
        modules.put(PotionHUD.class, new PotionHUD());
        modules.put(HUDMod.class, new HUDMod());
        modules.put(InventoryHUD.class, new InventoryHUD());
        modules.put(SessionHUD.class, new SessionHUD());
        modules.put(TargetHUDMod.class, new TargetHUDMod());

        INSTANCE.getModuleCollection().setModules(modules);

        Theme.init();

//        SessionInfo.timeJoined = System.currentTimeMillis();

        INSTANCE.setPingerUtils(new PingerUtils());

        INSTANCE.setScriptManager(new ScriptManager());

        INSTANCE.setFallDistanceComponent(new FallDistanceComponent());
        INSTANCE.getFallDistanceComponent().register();

        CommandHandler commandHandler = new CommandHandler();
        commandHandler.commands.addAll(Arrays.asList(
                new FriendCommand(), new CopyNameCommand(), new BindCommand(), new UnbindCommand(),
                new ScriptCommand(), new SettingCommand(), new HelpCommand(),
                new VClipCommand(), new ClearBindsCommand(), new ClearConfigCommand(),
                new LoadConfigCommand(),
              new ToggleCommand()

        ));
        INSTANCE.setCommandHandler(commandHandler);


        INSTANCE.getEventProtocol().register(new SlotComponent());
        INSTANCE.getEventProtocol().register(new BadPacketsComponent());
        INSTANCE.getEventProtocol().register(new BackgroundProcess());
        INSTANCE.getEventProtocol().register(new RotationComponent());
        INSTANCE.setConfigManager(new ConfigManager());
        commandHandler.commands.addAll(Arrays.asList(new FriendCommand(), new CopyNameCommand(), new BindCommand(), new UnbindCommand(), new ScriptCommand(), new SettingCommand(), new HelpCommand(), new VClipCommand(), new ClearBindsCommand(), new ClearConfigCommand(), new LoadConfigCommand(),new ToggleCommand(), new LoadConfigCommand()));
        ConfigManager.defaultConfig = new File(Minecraft.getMinecraft().mcDataDir + "/PridePlus/Config.json");
        INSTANCE.getConfigManager().collectConfigs();
        if (ConfigManager.defaultConfig.exists()) {
            INSTANCE.getConfigManager().loadConfig(INSTANCE.getConfigManager().readConfigData(ConfigManager.defaultConfig.toPath()), true);
        }

        DragManager.loadDragData();

        try {
            AltManager.Instance.readAlt();
        } catch (IOException e) {
            e.printStackTrace();
        }

        INSTANCE.setAltManager(new GuiAltManager(new CustomMainMenu()));
    }

    @SafeVarargs
    private static void addModules(Class<? extends Module>... classes) {
        for (Class<? extends Module> moduleClass : classes) {
            try {
                modules.put(moduleClass, moduleClass.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getVersion() {
        return VERSION;
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
