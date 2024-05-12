package dev.stable.module;

import dev.stable.Client;
import dev.stable.event.ListenerAdapter;
import dev.stable.module.impl.movement.Flight;
import dev.stable.module.impl.movement.Scaffold;
import dev.stable.config.DragManager;
import dev.stable.event.impl.game.GameCloseEvent;
import dev.stable.event.impl.game.KeyPressEvent;
import dev.stable.event.impl.game.TickEvent;
import dev.stable.event.impl.game.WorldEvent;
import dev.stable.event.impl.player.ChatReceivedEvent;
import dev.stable.event.impl.player.EventMoveInput;
import dev.stable.event.impl.render.Render2DEvent;
import dev.stable.event.impl.render.ShaderEvent;
import dev.stable.ui.mainmenu.CustomMainMenu;
import dev.stable.utils.Utils;
import dev.stable.utils.client.TimeLog;
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
