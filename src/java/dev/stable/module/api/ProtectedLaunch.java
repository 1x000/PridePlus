package dev.stable.module.api;

import dev.stable.Client;
import dev.stable.commands.CommandHandler;
import dev.stable.commands.impl.*;
import dev.stable.config.ConfigManager;
import dev.stable.config.DragManager;
import dev.stable.module.BackgroundProcess;
import dev.stable.module.Module;
import dev.stable.module.ModuleCollection;
import dev.stable.module.impl.combat.*;
import dev.stable.module.impl.exploit.Disabler;
import dev.stable.module.impl.exploit.Teams;
import dev.stable.module.impl.misc.*;
import dev.stable.module.impl.movement.*;
import dev.stable.module.impl.player.*;
import dev.stable.module.impl.render.*;
import dev.stable.module.impl.render.killeffects.KillEffects;
import dev.stable.scripting.api.ScriptManager;
import dev.stable.ui.altmanager.GuiAltManager;
import dev.stable.utils.movementfix.BadPacketsComponent;
import dev.stable.utils.movementfix.Rise.RotationComponent;
import dev.stable.utils.movementfix.SlotComponent;
import dev.stable.utils.render.Theme;
import dev.stable.utils.server.PingerUtils;
import dev.stable.viamcp.ViaMCP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ForceUnicodeChat;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class ProtectedLaunch {

    private static final HashMap<Object, Module> modules = new HashMap<>();

    public static void start() {
        // Setup Intent API access
      //  Client.INSTANCE.setIntentAccount(GetUserInfo.loginFailure);
        Client.INSTANCE.setModuleCollection(new ModuleCollection());

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

        Client.INSTANCE.getModuleCollection().setModules(modules);

        Theme.init();

//        SessionInfo.timeJoined = System.currentTimeMillis();

        Client.INSTANCE.setPingerUtils(new PingerUtils());

        Client.INSTANCE.setScriptManager(new ScriptManager());

        CommandHandler commandHandler = new CommandHandler();
        commandHandler.commands.addAll(Arrays.asList(
                new FriendCommand(), new CopyNameCommand(), new BindCommand(), new UnbindCommand(),
                new ScriptCommand(), new SettingCommand(), new HelpCommand(),
                new VClipCommand(), new ClearBindsCommand(), new ClearConfigCommand(),
                new LoadConfigCommand(),
              new ToggleCommand()

        ));
        Client.INSTANCE.setCommandHandler(commandHandler);


        Client.INSTANCE.getEventProtocol().register(new SlotComponent());
        Client.INSTANCE.getEventProtocol().register(new BadPacketsComponent());
        Client.INSTANCE.getEventProtocol().register(new BackgroundProcess());
        Client.INSTANCE.getEventProtocol().register(new RotationComponent());
        Client.INSTANCE.setConfigManager(new ConfigManager());
        commandHandler.commands.addAll(Arrays.asList(new FriendCommand(), new CopyNameCommand(), new BindCommand(), new UnbindCommand(), new ScriptCommand(), new SettingCommand(), new HelpCommand(), new VClipCommand(), new ClearBindsCommand(), new ClearConfigCommand(), new LoadConfigCommand(),new ToggleCommand(), new LoadConfigCommand()));
        ConfigManager.defaultConfig = new File(Minecraft.getMinecraft().mcDataDir + "/PridePlus/Config.json");
        Client.INSTANCE.getConfigManager().collectConfigs();
        if (ConfigManager.defaultConfig.exists()) {
            Client.INSTANCE.getConfigManager().loadConfig(Client.INSTANCE.getConfigManager().readConfigData(ConfigManager.defaultConfig.toPath()), true);
        }

        DragManager.loadDragData();

        Client.INSTANCE.setAltManager(new GuiAltManager());

        try {
            ViaMCP.create();
            ViaMCP.INSTANCE.initAsyncSlider();

            // In case you want a version slider like in the Minecraft options, you can use this code here, please choose one of those:
            // For top left aligned slider
        } catch (Exception e) {
            e.printStackTrace();
        }

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

}
