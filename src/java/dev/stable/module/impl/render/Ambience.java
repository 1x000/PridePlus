package dev.stable.module.impl.render;

import dev.stable.event.impl.game.TickEvent;
import dev.stable.event.impl.network.PacketReceiveEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import dev.stable.module.settings.impl.ModeSetting;
import dev.stable.module.settings.impl.NumberSetting;
import dev.stable.utils.Utils;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;

import java.util.Random;


public class Ambience extends Module {

    private final NumberSetting time = new NumberSetting("Time", 12000, 24000, 0, 1000);
    private final ModeSetting weather = new ModeSetting("Weather", "Clear", "Rain", "Thunder", "Clear", "Snow", "Don't Change");
    public static boolean overrideSnow = false;

    public Ambience() {
        super("Ambience","更改时间", Category.RENDER, "world time");
        this.addSettings(time, weather);
    }

    //A random value was used in the clear weather command so here's one to use
    private final int randomValue = (300 + (new Random()).nextInt(600)) * 20;

    String mode = "";

    @Override
    public void onTickEvent(TickEvent event) {
        if (Utils.mc.theWorld != null) {

            WorldInfo worldinfo = Utils.mc.theWorld.getWorldInfo();
            if (Utils.mc.isSingleplayer()) {
                World world = MinecraftServer.getServer().worldServers[0];
                worldinfo = world.getWorldInfo();
            }

            Utils.mc.theWorld.setWorldTime(time.getValue().longValue());

            switch (weather.getMode()) {
                case "Clear":
                    worldinfo.setCleanWeatherTime(randomValue);
                    worldinfo.setRainTime(0);
                    worldinfo.setThunderTime(0);
                    worldinfo.setRaining(false);
                    worldinfo.setThundering(false);

                    break;
                case "Rain":
                    //  worldinfo.setCleanWeatherTime(0);
                    worldinfo.setRainTime(Integer.MAX_VALUE);
                    worldinfo.setThunderTime(Integer.MAX_VALUE);
                    worldinfo.setRaining(true);
                    worldinfo.setThundering(false);


                    break;
                case "Thunder":
                    worldinfo.setCleanWeatherTime(0);
                    worldinfo.setRainTime(Integer.MAX_VALUE);
                    worldinfo.setThunderTime(Integer.MAX_VALUE);
                    worldinfo.setRaining(true);
                    worldinfo.setThundering(true);


                    break;
                case "Don't Change":
                    mode = "Don't Change";
                    break;
                case "Snow":
                    worldinfo.setCleanWeatherTime(0);
                    worldinfo.setRainTime(Integer.MAX_VALUE);
                    worldinfo.setThunderTime(Integer.MAX_VALUE);
                    worldinfo.setRaining(true);
                    worldinfo.setThundering(false);

                    break;
            }

            overrideSnow = weather.is("Snow");


        }
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent e) {
        if (e.getPacket() instanceof S03PacketTimeUpdate) {
            e.cancel();
        }
    }

    @Override
    public void onDisable() {
        overrideSnow = false;
        super.onDisable();
    }
}
