package cn.molokymc.prideplus.module;

import cn.molokymc.prideplus.Client;
import cn.molokymc.prideplus.event.ListenerAdapter;
import cn.molokymc.prideplus.module.impl.movement.Flight;
import cn.molokymc.prideplus.module.impl.movement.Scaffold;
import cn.molokymc.prideplus.config.DragManager;
import cn.molokymc.prideplus.event.impl.game.GameCloseEvent;
import cn.molokymc.prideplus.event.impl.game.KeyPressEvent;
import cn.molokymc.prideplus.event.impl.game.TickEvent;
import cn.molokymc.prideplus.event.impl.game.WorldEvent;
import cn.molokymc.prideplus.event.impl.player.ChatReceivedEvent;
import cn.molokymc.prideplus.event.impl.player.EventMoveInput;
import cn.molokymc.prideplus.event.impl.render.Render2DEvent;
import cn.molokymc.prideplus.event.impl.render.ShaderEvent;
import cn.molokymc.prideplus.ui.mainmenu.CustomMainMenu;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.client.TimeLog;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.util.StringUtils;

import java.util.Arrays;

public class BackgroundProcess extends ListenerAdapter implements Utils {

    private final Scaffold scaffold = (Scaffold) Client.INSTANCE.getModuleCollection().get(Scaffold.class);

    @Override
    public void onKeyPressEvent(KeyPressEvent event) {

        // We should probably have a static arraylist of all the modules instead of creating a new on in getModules()
        for (Module module : Client.INSTANCE.getModuleCollection().getModules()) {
            if (module.getKeybind().getCode() == event.getKey()) {
                module.toggle();
            }
        }
    }

    @Override
    public void onGameCloseEvent(GameCloseEvent event) {
        Client.INSTANCE.getConfigManager().saveDefaultConfig();
        DragManager.saveDragData();
    }

    public void onMoveInput(EventMoveInput event) {
    }


    @Override
    public void onShaderEvent(ShaderEvent event) {
        if (mc.thePlayer != null) {
            scaffold.renderCounterBlur();
        }
    }

    @Override
    public void onRender2DEvent(Render2DEvent event) {
        if (mc.thePlayer != null) {
            scaffold.renderCounter();
        }
    }

    @Override
    public void onWorldEvent(WorldEvent event) {
        if (event instanceof WorldEvent.Load) {
            Flight.hiddenBlocks.clear();
        }
    }

}
