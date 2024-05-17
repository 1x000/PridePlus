package cn.molokymc.prideplus.module.impl.player;

import cn.molokymc.prideplus.event.impl.player.MotionEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.impl.combat.KillAura;
import cn.molokymc.prideplus.module.settings.impl.BooleanSetting;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.player.InventoryUtils;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;


    public class AutoTool extends Module {

        private final BooleanSetting autoSword = new BooleanSetting("AutoSword", true);

        public AutoTool() {
            super("AutoTool","自动切工具", Category.PLAYER,"switches to the best tool");
            this.addSettings(autoSword);
        }

        @Override
        public void onMotionEvent(MotionEvent e) {
            if (e.isPre()) {
                if (mc.objectMouseOver != null && mc.gameSettings.keyBindAttack.isKeyDown()) {
                    MovingObjectPosition objectMouseOver = mc.objectMouseOver;
                    if (objectMouseOver.entityHit != null) {
                        switchSword();
                    } else if (objectMouseOver.getBlockPos() != null) {
                        Block block = mc.theWorld.getBlockState(objectMouseOver.getBlockPos()).getBlock();
                        updateItem(block);
                    }
                } else if (KillAura.target != null) {
                    switchSword();
                }
            }
        }

        private void updateItem(Block block) {
            float strength = 1.0F;
            int bestItem = -1;
            for (int i = 0; i < 9; i++) {
                ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
                if (itemStack == null) {
                    continue;
                }
                float strVsBlock = itemStack.getStrVsBlock(block);
                if (strVsBlock > strength) {
                    strength = strVsBlock;
                    bestItem = i;
                }
            }
            if (bestItem != -1) {
                mc.thePlayer.inventory.currentItem = bestItem;
            }
        }

        private void switchSword() {
            if (!autoSword.isEnabled()) return;
            float damage = 1;
            int bestItem = -1;
            for (int i = 0; i < 9; i++) {
                ItemStack is = mc.thePlayer.inventory.mainInventory[i];
                if (is != null && is.getItem() instanceof ItemSword && InventoryUtils.getSwordStrength(is) > damage) {
                    damage = InventoryUtils.getSwordStrength(is);
                    bestItem = i;
                }
            }
            if (bestItem != -1) {
                mc.thePlayer.inventory.currentItem = bestItem;
            }
        }

    }