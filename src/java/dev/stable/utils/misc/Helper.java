package dev.stable.utils.misc;

import dev.stable.ui.notifications.Notification;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public enum Helper {
	INSTANCE;
	private static final ArrayList<Notification> notifications = new ArrayList<>();
	public static Minecraft mc = Minecraft.getMinecraft();
	public static boolean onetime = true;

	public static void showURL(String url) {
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public static boolean isBlockBetween(BlockPos start, BlockPos end) {
		int startX = start.getX();
		int startY = start.getY();
		int startZ = start.getZ();
		int endX = end.getX();
		int endY = end.getY();
		int endZ = end.getZ();
		double diffX = endX - startX;
		double diffY = endY - startY;
		double diffZ = endZ - startZ;
		double x = startX;
		double y = startY;
		double z = startZ;
		int STEPS = (int)Math.max(Math.abs(diffX), Math.max(Math.abs(diffY), Math.abs(diffZ))) * 4;

		for(int i = 0; i < STEPS - 1; ++i) {
			x += diffX / (double)STEPS;
			y += diffY / (double)STEPS;
			z += diffZ / (double)STEPS;
			if (x != (double)endX || y != (double)endY || z != (double)endZ) {
				BlockPos pos = new BlockPos(x, y, z);
				Block block = mc.theWorld.getBlockState(pos).getBlock();
				if (block.getMaterial() != Material.air && block.getMaterial() != Material.water && !(block instanceof BlockVine) && !(block instanceof BlockLadder)) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean onServer(String server) {
		return !mc.isSingleplayer() && Helper.mc.getCurrentServerData().serverIP.toLowerCase().contains(server.toLowerCase());
	}

	public static void sendMessageWithoutPrefix(String string) {
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(string));
	}
}
