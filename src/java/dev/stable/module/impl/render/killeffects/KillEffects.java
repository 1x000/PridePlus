package dev.stable.module.impl.render.killeffects;

import dev.stable.module.impl.render.SessionHUD;
import dev.stable.module.settings.impl.ModeSetting;
import dev.stable.event.impl.game.WorldEvent;
import dev.stable.event.impl.player.ChatReceivedEvent;
import dev.stable.event.impl.player.LivingDeathEvent;
import dev.stable.event.impl.render.RendererLivingEntityEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import lombok.AllArgsConstructor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author senoe
 * @since 6/14/2022
 */
public class KillEffects extends Module {

    public static final ModeSetting killEffect = new ModeSetting("Kill Effect", "Blood Explosion", "Blood Explosion", "Lightning Bolt");
    private final Map<String, Location> players = new ConcurrentHashMap<>();
    private final EffectManager effectManager = new EffectManager();

    public KillEffects() {
        super("KillEffects","击杀特效", Category.RENDER, "Plays animation on killing another player");
        addSettings(killEffect);
    }

    @Override
    public void onLivingDeathEvent(LivingDeathEvent event) {
        if (event.getSource().getEntity() != null && event.getSource().getEntity().equals(mc.thePlayer) && event.getEntity() != mc.thePlayer) {
            String name = event.getEntity().getName();
            Location location = players.remove(name);
            if (location != null) {
                effectManager.playKillEffect(location);
                SessionHUD.Kills++;
            } else {
                EntityLivingBase ent = event.getEntity();
                effectManager.playKillEffect(new Location(ent.posX, ent.posY, ent.posZ, ent.getEyeHeight()));
                SessionHUD.Kills++;
            }
        }
    }

    @Override
    public void onChatReceivedEvent(ChatReceivedEvent event) {
        String message = StringUtils.stripControlCodes(event.message.getUnformattedText());
        if (mc.thePlayer != null && !message.contains(":") && message.contains("by " + mc.thePlayer.getName())) {
            String killedPlayer = message.trim().split(" ")[0];
            Location location = players.remove(killedPlayer);
            if (location != null) {
                effectManager.playKillEffect(location);
                SessionHUD.Kills++;
            }
        }
    }

    @Override
    public void onRendererLivingEntityEvent(RendererLivingEntityEvent event) {
        if (event.isPost() && event.getEntity() instanceof EntityPlayer && event.getEntity() != mc.thePlayer) {
            EntityLivingBase ent = event.getEntity();
            players.put(ent.getName(), new Location(ent.posX, ent.posY, ent.posZ, ent.getEyeHeight()));
        }
    }

    @Override
    public void onWorldEvent(WorldEvent event) {
        players.clear();
    }

    @AllArgsConstructor
    public static class Location {
        public double x, y, z, eyeHeight;
    }

}
