package net.minecraft.client.renderer;


import cn.molokymc.prideplus.Pride;
import cn.molokymc.prideplus.module.impl.combat.KillAura;
import cn.molokymc.prideplus.module.impl.render.Animations;
import cn.molokymc.prideplus.module.impl.render.RemoveEffects;
import cn.molokymc.prideplus.utils.player.InventoryUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.optifine.DynamicLights;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;

public class ItemRenderer {
    private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");

    /**
     * A reference to the Minecraft object.
     */
    private final Minecraft mc;
    private final RenderManager renderManager;
    private final RenderItem itemRenderer;
    private ItemStack itemToRender;
    /**
     * How far the current item has been equipped (0 disequipped and 1 fully up)
     */
    private float equippedProgress;
    private float prevEquippedProgress;
    /**
     * The index of the currently held item (0-8, or -1 if not yet updated)
     */
    private int equippedItemSlot = -1;
    /**
     * Renders the active item in the player's hand when in first person mode. Args: partialTickTime
     */
    private long circleTicks;

    public ItemRenderer(Minecraft mcIn) {
        mc = mcIn;
        renderManager = mcIn.getRenderManager();
        itemRenderer = mcIn.getRenderItem();
    }

    /**
     * Returns true if given block is translucent
     */
    private boolean isBlockTranslucent(Block blockIn) {
        return blockIn != null && blockIn.getBlockLayer() == EnumWorldBlockLayer.TRANSLUCENT;
    }

    /**
     * Rotate the render around X and Y
     *
     * @param angleY The angle for the rotation arround Y
     */
    private void rotateArroundXAndY(float angle, float angleY) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(angleY, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    /**
     * Rotate the render according to the player's yaw and pitch
     */
    private void rotateWithPlayerRotations(EntityPlayerSP entityplayerspIn, float partialTicks) {
        float f = entityplayerspIn.prevRenderArmPitch + (entityplayerspIn.renderArmPitch - entityplayerspIn.prevRenderArmPitch) * partialTicks;
        float f1 = entityplayerspIn.prevRenderArmYaw + (entityplayerspIn.renderArmYaw - entityplayerspIn.prevRenderArmYaw) * partialTicks;
        GlStateManager.rotate((entityplayerspIn.rotationPitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((entityplayerspIn.rotationYaw - f1) * 0.1F, 0.0F, 1.0F, 0.0F);
    }

    /**
     * Return the angle to render the Map
     *
     * @param pitch The player's pitch
     */
    private float getMapAngleFromPitch(float pitch) {
        float f = 1.0F - pitch / 45.0F + 0.1F;
        f = MathHelper.clamp_float(f, 0.0F, 1.0F);
        f = -MathHelper.cos(f * (float) Math.PI) * 0.5F + 0.5F;
        return f;
    }

    /**
     * Rotate and translate render to show item consumption
     *
     * @param swingProgress The swing movement progress
     */
    private void doItemUsedTransformations(float swingProgress) {
        float f = (-0.4f) * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927f);
        float f2 = 0.2f * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927f * 2.0f);
        float f3 = (-0.2f) * MathHelper.sin(swingProgress * 3.1415927f);
        GL11.glTranslatef(f, f2, f3);
    }
    /**
     * Performs transformations prior to the rendering of a held item in first person.
     */
    private void transformFirstPersonItem(float equipProgress, float swingProgress) {
        GL11.glTranslatef(0.56f, -0.52f, -0.72f);
        GL11.glTranslatef(0.0f, equipProgress * (-0.6f), 0.0f);
        GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
        if (swingProgress > 0.0d) {
            float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927f);
            float f2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927f);
            GL11.glRotatef(f * (-20.0f), 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(f2 * (-20.0f), 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(f2 * (-80.0f), 1.0f, 0.0f, 0.0f);
        }
        float scale = 0.4f * Animations.scale.getValue().floatValue();
        GL11.glScalef(scale, scale, scale);
    }


    /**
     * Translate and rotate the render for holding a block
     */
    private void doBlockTransformations() {
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }

    public void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform) {
        if (heldStack != null) {
            Item item = heldStack.getItem();
            Block block = Block.getBlockFromItem(item);
            GlStateManager.pushMatrix();

            if (itemRenderer.shouldRenderItemIn3D(heldStack)) {
                GlStateManager.scale(2.0F, 2.0F, 2.0F);

                if (isBlockTranslucent(block) && (!Config.isShaders() || !Shaders.renderItemKeepDepthMask)) {
                    GlStateManager.depthMask(false);
                }
            }

            itemRenderer.renderItemModelForEntity(heldStack, entityIn, transform);

            if (isBlockTranslucent(block)) {
                GlStateManager.depthMask(true);
            }

            GlStateManager.popMatrix();
        }
    }

    /**
     * Set the OpenGL LightMapTextureCoords based on the AbstractClientPlayer
     */
    private void setLightMapFromPlayer(AbstractClientPlayer clientPlayer) {
        int i = mc.theWorld.getCombinedLight(new BlockPos(clientPlayer.posX, clientPlayer.posY + clientPlayer.getEyeHeight(), clientPlayer.posZ), 0);

        if (Config.isDynamicLights()) {
            i = DynamicLights.getCombinedLight(mc.getRenderViewEntity(), i);
        }

        float f = (i & 65535);
        float f1 = (i >> 16);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
    }

    private void renderRightArm(RenderPlayer renderPlayerIn) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(54.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(64.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-62.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.25F, -0.85F, 0.75F);
        renderPlayerIn.renderRightArm(mc.thePlayer);
        GlStateManager.popMatrix();
    }

    private void renderLeftArm(RenderPlayer renderPlayerIn) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(41.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(-0.3F, -1.1F, 0.45F);
        renderPlayerIn.renderLeftArm(mc.thePlayer);
        GlStateManager.popMatrix();
    }

    private void renderPlayerArms(AbstractClientPlayer clientPlayer) {
        mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
        Render<AbstractClientPlayer> render = renderManager.getEntityRenderObject(mc.thePlayer);
        RenderPlayer renderplayer = (RenderPlayer) render;

        if (!clientPlayer.isInvisible()) {
            GlStateManager.disableCull();
            renderRightArm(renderplayer);
            renderLeftArm(renderplayer);
            GlStateManager.enableCull();
        }
    }

    private void renderItemMap(AbstractClientPlayer clientPlayer, float pitch, float equipmentProgress, float swingProgress) {
        float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2.0F);
        float f2 = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
        GlStateManager.translate(f, f1, f2);
        float f3 = getMapAngleFromPitch(pitch);
        GlStateManager.translate(0.0F, 0.04F, -0.72F);
        GlStateManager.translate(0.0F, equipmentProgress * -1.2F, 0.0F);
        GlStateManager.translate(0.0F, f3 * -0.5F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f3 * -85.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
        renderPlayerArms(clientPlayer);
        float f4 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float f5 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(f4 * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f5 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f5 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.38F, 0.38F, 0.38F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(-1.0F, -1.0F, 0.0F);
        GlStateManager.scale(0.015625F, 0.015625F, 0.015625F);
        mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GL11.glNormal3f(0.0F, 0.0F, -1.0F);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(-7.0D, 135.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
        worldrenderer.pos(135.0D, 135.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
        worldrenderer.pos(135.0D, -7.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
        worldrenderer.pos(-7.0D, -7.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        MapData mapdata = Items.filled_map.getMapData(itemToRender, mc.theWorld);

        if (mapdata != null) {
            mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
        }
    }

    /**
     * Render the player's arm
     *
     * @param equipProgress The progress of equiping the item
     * @param swingProgress The swing movement progression
     */
    private void renderPlayerArm(AbstractClientPlayer clientPlayer, float equipProgress, float swingProgress) {
        float f = -0.3F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        float f1 = 0.4F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2.0F);
        float f2 = -0.4F * MathHelper.sin(swingProgress * (float) Math.PI);
        GlStateManager.translate(f, f1, f2);
        GlStateManager.translate(0.64000005F, -0.6F, -0.71999997F);
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f3 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float f4 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(f4 * 70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f3 * -20.0F, 0.0F, 0.0F, 1.0F);
        mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
        GlStateManager.translate(-1.0F, 3.6F, 3.5F);
        GlStateManager.rotate(120.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(1.0F, 1.0F, 1.0F);
        GlStateManager.translate(5.6F, 0.0F, 0.0F);
        Render<AbstractClientPlayer> render = renderManager.getEntityRenderObject(mc.thePlayer);
        GlStateManager.disableCull();
        RenderPlayer renderplayer = (RenderPlayer) render;
        renderplayer.renderRightArm(mc.thePlayer);
        GlStateManager.enableCull();
    }

    /**
     * Perform the drinking animation movement
     *
     * @param partialTicks Partials ticks
     */
    private void performDrinking(AbstractClientPlayer clientPlayer, float partialTicks) {
        float f = clientPlayer.getItemInUseCount() - partialTicks + 1.0F;
        float f1 = f / itemToRender.getMaxItemUseDuration();
        float f2 = MathHelper.abs(MathHelper.cos(f / 4.0F * (float) Math.PI) * 0.1F);

        if (f1 >= 0.8F) {
            f2 = 0.0F;
        }

        GlStateManager.translate(0.0F, f2, 0.0F);
        float f3 = 1.0F - (float) Math.pow(f1, 27.0D);
        GlStateManager.translate(f3 * 0.6F, f3 * -0.5F, f3 * 0.0F);
        GlStateManager.rotate(f3 * 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f3 * 10.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f3 * 30.0F, 0.0F, 0.0F, 1.0F);
    }

    /**
     * Translate and rotate the render to look like holding a bow
     *
     * @param partialTicks Partial ticks
     */
    private void doBowTransformations(float partialTicks, AbstractClientPlayer clientPlayer) {
        GlStateManager.rotate(-18.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-12.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-8.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(-0.9F, 0.2F, 0.0F);
        float f = itemToRender.getMaxItemUseDuration() - (clientPlayer.getItemInUseCount() - partialTicks + 1.0F);
        float f1 = f / 20.0F;
        f1 = (f1 * f1 + f1 * 2.0F) / 3.0F;

        if (f1 > 1.0F) {
            f1 = 1.0F;
        }

        if (f1 > 0.1F) {
            float f2 = MathHelper.sin((f - 0.1F) * 1.3F);
            float f3 = f1 - 0.1F;
            float f4 = f2 * f3;
            GlStateManager.translate(f4 * 0.0F, f4 * 0.01F, f4 * 0.0F);
        }

        GlStateManager.translate(f1 * 0.0F, f1 * 0.0F, f1 * 0.1F);
        GlStateManager.scale(1.0F, 1.0F, 1.0F + f1 * 0.2F);
    }
    private void slide2(final float var10, final float var9) {
        GlStateManager.translate(0.56f, -0.52f, -0.71999997f);
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        final float var11 = MathHelper.sin(var9 * var9 * 3.1415927f);
        final float var12 = MathHelper.sin(MathHelper.sqrt_float(var9) * 3.1415927f);
        GlStateManager.rotate(var11 * 0.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(var12 * 0.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(var12 * -80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale((double)0.4, (double)0.4, (double)0.4);
    }
    public void renderItemInFirstPerson(float partialTicks) {

        if (!Config.isShaders() || !Shaders.isSkipRenderHand()) {
            float f = 1.0f - (this.prevEquippedProgress + ((this.equippedProgress - this.prevEquippedProgress) * partialTicks));
            EntityPlayerSP abstractclientplayer = this.mc.thePlayer;
            float f2 = abstractclientplayer.getSwingProgress(partialTicks);
            float f3 = abstractclientplayer.prevRotationPitch + ((abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks);
            float f4 = abstractclientplayer.prevRotationYaw + ((abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks);
            rotateArroundXAndY(f3, f4);
            setLightMapFromPlayer(abstractclientplayer);
            rotateWithPlayerRotations(abstractclientplayer, partialTicks);
            GlStateManager.enableRescaleNormal();
            GL11.glPushMatrix();
            if (this.itemToRender != null) {
                if (this.itemToRender.getItem() instanceof ItemMap) {
                    renderItemMap(abstractclientplayer, f3, f, f2);
                } else if (abstractclientplayer.getItemInUseCount() > 0 || (KillAura.blocking && InventoryUtils.getHeldItem() instanceof ItemSword)) {
                    EnumAction enumaction = this.itemToRender.getItemUseAction();
                    switch (enumaction) {
                        case NONE:
                            transformFirstPersonItem(f, f2);
                            break;
                        case EAT:
                        case DRINK:
                            performDrinking(abstractclientplayer, partialTicks);
                            transformFirstPersonItem(f, f2);
                            break;
                        case BLOCK:
                            if (Pride.INSTANCE.isEnabled(Animations.class)) {
                                switch (Animations.mode.getMode()) {

                                    case "SlideDown":
                                        this.slide2(0.1f, f2);
                                        this.doBlockTransformations();
                                        break;
                                    case "Loser":
                                        GL11.glTranslated(0.0d, 0.12d, 0.0d);
                                        transformFirstPersonItem(f, 0.0f);
                                        doBlockTransformations();
                                        break;
                                    case "Swong":
                                        float var15 = MathHelper.sin(MathHelper.sqrt_float(f2) * (float) Math.PI);
                                        this.transformFirstPersonItem(f / 2.5f, 0.0f);
                                        GL11.glTranslatef(0.1f, 0.4f, -0.1f);
                                        GL11.glRotated(-var15 * 20.0f, var15 / 2.0f, 0.0, 9.0);
                                        GL11.glRotated(-var15 * 50.0f, 0.8f, var15 / 2.0f, 0.0);
                                        doBlockTransformations();
                                        break;
                                    case "Swagg":
                                        float f62 = MathHelper.sin((float) (MathHelper.sqrt_float(f2) * 3.141592653589793d));
                                        GL11.glTranslated(0.05d, f62 * 0.062f, f62 * 0.0f);
                                        GL11.glTranslated(0.025d, 0.1115d, 0.0d);
                                        transformFirstPersonItem(f / 20.0f, 0.0f);
                                        GlStateManager.rotate((-f62) * 10.0f, (-f62) / 20.0f, (-f62) / (-20.0f), 3.0f);
                                        GlStateManager.rotate((-f62) * 50.0f, 10.0f, f62 / 20.0f, 1.0f);
                                        if (this.mc.thePlayer.isSneaking()) {
                                            GlStateManager.translate(0.0d, 0.15d, 0.0d);
                                        }
                                        doBlockTransformations();
                                        break;
                                    case "Lucky":
                                        float f63 = MathHelper.sin((float) (MathHelper.sqrt_float(f2) * 3.141592653589793d));
                                        GL11.glTranslated(0.05d, f63 * 0.062f, f63 * 0.0f);
                                        GL11.glTranslated(0.025d, 0.1115d, 0.0d);
                                        transformFirstPersonItem(f / 20.0f, 0.0f);
                                        GlStateManager.rotate((-f63) * (-2.0f), (-f63) / 20.0f, (-f63) / (-20.0f), 1.0f);
                                        GlStateManager.rotate((-f63) * 25.0f, 10.0f, f63 / 10.0f, 0.15f);
                                        if (this.mc.thePlayer.isSneaking()) {
                                            GlStateManager.translate(0.0d, 0.15d, 0.0d);
                                        }
                                        doBlockTransformations();
                                        break;
                                    case "Swing":
                                        float var16 = MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f);
                                        GL11.glTranslated(0.014999999664723873d, 0.11999999731779099d, 0.0d);
                                        GL11.glTranslated(0.014999999664723873d, var16 * 0.055d, 0.014999999664723873d);
                                        transformFirstPersonItem(f / 20.0f, f2);
                                        GlStateManager.rotate(var16 * 14.0f, -6.0f, (-var16) / 20.0f, 0.0f);
                                        GlStateManager.rotate(var16 * 21.0f, 2.0f, (-var16) / 4.0f, 0.0f);
                                        GL11.glTranslated(0.014999999664723873d, var16 * 0.05d, 0.014999999664723873d);
                                        doBlockTransformations();
                                        break;
                                    case "Nigga":
                                        GL11.glTranslated(-0.10000000149011612d, 0.15000000596046448d, 0.0d);
                                        GL11.glTranslated(0.10000000149011612d, -0.15000000596046448d, 0.0d);
                                        GL11.glTranslated(0.10000000149011612d, 0.1d, 0.0d);
                                        transformFirstPersonItem(f / 2.0f, this.mc.thePlayer.getSwingProgress(partialTicks));
                                        doBlockTransformations();
                                        break;
                                    case "Swang":
                                        float var152 = MathHelper.sin((float) (MathHelper.sqrt_float(f2) * 3.141592653589793d));
                                        GL11.glTranslated(0.014999999664723873d, 0.11999999731779099d, 0.0d);
                                        GL11.glTranslated(0.014999999664723873d, var152 * 0.0666d, 0.014999999664723873d);
                                        transformFirstPersonItem(f / 2.0f, f2);
                                        GlStateManager.rotate(var152 * 14.0f, 2.0f, (-var152) / 10.0f, 6.0f);
                                        GlStateManager.rotate(var152 * 35.0f, 0.98f, (-var152) / 1.5f, 0.0f);
                                        doBlockTransformations();
                                        GL11.glTranslated(0.014999999664723873d, var152 * 0.05d, 0.014999999664723873d);
                                        break;
                                    case "Swonk":
                                        float f64 = MathHelper.sin((float) (MathHelper.sqrt_float(f2) * 3.1d));
                                        GL11.glTranslated(0.0d, 0.12d, 0.0d);
                                        GL11.glTranslated(0.0d, f64 * 0.05d, 0.0d);
                                        transformFirstPersonItem(f / 2.0f, 0.0f);
                                        GlStateManager.rotate(((-f64) * 51.0f) / 10.0f, -1.0f, 0.0f, 0.0f);
                                        GlStateManager.rotate(((-f64) * 51.0f) / 5.0f, f64 / 5.0f, -2.0f, 0.0f);
                                        GlStateManager.rotate(((-f64) * 51.0f) / 3.0f, 1.0f, f64 / 2.0f, -1.0f);
                                        GlStateManager.rotate(((-f64) * 51.0f) / 30.0f, 0.0f, -1.0f, 0.0f);
                                        doBlockTransformations();
                                        if (this.mc.thePlayer.isSneaking()) {
                                            GlStateManager.translate(0.0d, 0.15d, 0.0d);
                                            break;
                                        }
                                        break;
                                    case "Swank":
                                        GL11.glTranslated(-0.10000000149011612d, 0.15000000596046448d, 0.0d);
                                        transformFirstPersonItem(f / 2.0f, f2);
                                        float var14 = MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f);
                                        GlStateManager.rotate(var14 * 30.0f, -var14, -0.0f, 9.0f);
                                        GlStateManager.rotate(var14 * 40.0f, 1.0f, -var14, -0.0f);
                                        doBlockTransformations();
                                        break;

                                    case "Swack":
                                        float sw0ng = MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f);
                                        transformFirstPersonItem(f, 0.0f);
                                        GL11.glScaled(0.7d, 0.7d, 0.7d);
                                        GL11.glTranslated(-0.9d, 0.7d, 0.1d);
                                        GL11.glRotatef((-sw0ng) * 29.0f, 7.0f, -0.6f, -0.0f);
                                        GL11.glRotatef(((-sw0ng) * 10.0f) / 2.0f, -12.0f, 0.0f, 9.0f);
                                        GL11.glRotatef((-sw0ng) * 17.0f, -1.0f, -0.6f, -0.7f);
                                        GL11.glTranslatef(sw0ng / 2.5f, 0.1f, sw0ng / 2.5f);
                                        doBlockTransformations();
                                        break;
                                    case "1.7":
                                        this.transformFirstPersonItem(f, f2);
                                        this.doBlockTransformations();
                                        break;

                                }
                            } else {
                                this.transformFirstPersonItem(f, 0.0F);
                                this.doBlockTransformations();
                            }
                            break;

                        case BOW:
                            this.transformFirstPersonItem(f, f2);
                            this.doBowTransformations(partialTicks, abstractclientplayer);
                    }
                } else if ( Pride.INSTANCE.isEnabled(Animations.class)&& Animations.every.get() && this.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                    GL11.glTranslated(0.0d, 0.0d, 0.0d);
                    GL11.glTranslated(0.0d, 0.12d, 0.0d);
                    transformFirstPersonItem(f, f2);
                    doBlockTransformations();
                } else {
                    if (Pride.INSTANCE.isEnabled(Animations.class)&& !Animations.swing.get()) {
                        doItemUsedTransformations(f2);
                    }
                    transformFirstPersonItem(f, f2);
                }
                renderItem(abstractclientplayer, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
            } else if (!abstractclientplayer.isInvisible()) {
                renderPlayerArm(abstractclientplayer, f, f2);
            }
            GL11.glPopMatrix();
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
        }
    }


    private void Jigsaw(float var2, float swingProgress) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, var2 * -0.6F, 0.0F);
        final float v = swingProgress * 0.8f - (swingProgress * swingProgress) * 0.8f;
        GlStateManager.rotate(45.0F, 0.0F, 2 + v * 0.5f, v * 3);
        GlStateManager.rotate(0f, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(0.37F, 0.37F, 0.37F);
    }


    /**
     * Renders all the overlays that are in first person mode. Args: partialTickTime
     */
    public void renderOverlays(float partialTicks) {
        GlStateManager.disableAlpha();

        if (this.mc.thePlayer.isEntityInsideOpaqueBlock()) {
            IBlockState iblockstate = this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer));
            BlockPos blockpos = new BlockPos(this.mc.thePlayer);
            EntityPlayer entityplayer = this.mc.thePlayer;

            for (int i = 0; i < 8; ++i) {
                double d0 = entityplayer.posX + (double) (((float) ((i >> 0) % 2) - 0.5F) * entityplayer.width * 0.8F);
                double d1 = entityplayer.posY + (double) (((float) ((i >> 1) % 2) - 0.5F) * 0.1F);
                double d2 = entityplayer.posZ + (double) (((float) ((i >> 2) % 2) - 0.5F) * entityplayer.width * 0.8F);
                BlockPos blockpos1 = new BlockPos(d0, d1 + (double) entityplayer.getEyeHeight(), d2);
                IBlockState iblockstate1 = this.mc.theWorld.getBlockState(blockpos1);

                if (iblockstate1.getBlock().isVisuallyOpaque()) {
                    iblockstate = iblockstate1;
                    blockpos = blockpos1;
                }
            }
        }

        if (!this.mc.thePlayer.isSpectator()) {
            if (this.mc.thePlayer.isInsideOfMaterial(Material.water)) {
                this.renderWaterOverlayTexture(partialTicks);
            }

            if (this.mc.thePlayer.isBurning()) {

                this.renderFireInFirstPerson();
            }
        }

        GlStateManager.enableAlpha();
    }


    private void transformFirstPersonSwordBlock(float swingProgress) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(f1 * -70.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    /**
     * Render the block in the player's hand
     *
     * @param atlas The TextureAtlasSprite to render
     */
    private void renderBlockInHand(TextureAtlasSprite atlas) {
        mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(0.1F, 0.1F, 0.1F, 0.5F);
        GlStateManager.pushMatrix();
        float f6 = atlas.getMinU();
        float f7 = atlas.getMaxU();
        float f8 = atlas.getMinV();
        float f9 = atlas.getMaxV();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(-1.0D, -1.0D, -0.5D).tex(f7, f9).endVertex();
        worldrenderer.pos(1.0D, -1.0D, -0.5D).tex(f6, f9).endVertex();
        worldrenderer.pos(1.0D, 1.0D, -0.5D).tex(f6, f8).endVertex();
        worldrenderer.pos(-1.0D, 1.0D, -0.5D).tex(f7, f8).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Renders a texture that warps around based on the direction the player is looking. Texture needs to be bound
     * before being called. Used for the water overlay. Args: parialTickTime
     *
     * @param partialTicks Partial ticks
     */
    private void renderWaterOverlayTexture(float partialTicks) {
        if (!Config.isShaders() || Shaders.isUnderwaterOverlay()) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            float f = mc.thePlayer.getBrightness(partialTicks);
            GlStateManager.color(f, f, f, 0.5F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.pushMatrix();
            float f7 = -mc.thePlayer.rotationYaw / 64.0F;
            float f8 = mc.thePlayer.rotationPitch / 64.0F;
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(-1.0D, -1.0D, -0.5D).tex(4.0F + f7, 4.0F + f8).endVertex();
            worldrenderer.pos(1.0D, -1.0D, -0.5D).tex(0.0F + f7, 4.0F + f8).endVertex();
            worldrenderer.pos(1.0D, 1.0D, -0.5D).tex(0.0F + f7, 0.0F + f8).endVertex();
            worldrenderer.pos(-1.0D, 1.0D, -0.5D).tex(4.0F + f7, 0.0F + f8).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
        }
    }

    /**
     * Renders the fire on the screen for first person mode. Arg: partialTickTime
     */
    private void renderFireInFirstPerson() {

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        Float colorAlpha;
        if (!Pride.INSTANCE.isEnabled(RemoveEffects.class)) {
            colorAlpha = 0.9F;
        } else {
            colorAlpha = 0F;
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, colorAlpha);
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        float f = 1.0F;

        for (int i = 0; i < 2; ++i) {
                GlStateManager.pushMatrix();
                TextureAtlasSprite textureatlassprite = mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
                mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                float f1 = textureatlassprite.getMinU();
                float f2 = textureatlassprite.getMaxU();
                float f3 = textureatlassprite.getMinV();
                float f4 = textureatlassprite.getMaxV();
                float f5 = (0.0F - f) / 2.0F;
                float f6 = f5 + f;
                float f7 = 0;
                f7 = 0.0F - f / 2.0F;
                float f8 = f7 + f;
                float f9 = -0.5F;
                GlStateManager.translate((-((i << 1) - 1)) * 0.24F, -0.3F, 0.0F);
                GlStateManager.rotate(((i << 1) - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
                worldrenderer.setSprite(textureatlassprite);
                worldrenderer.pos(f5, f7, f9).tex(f2, f4).endVertex();
                worldrenderer.pos(f6, f7, f9).tex(f1, f4).endVertex();
                worldrenderer.pos(f6, f8, f9).tex(f1, f3).endVertex();
                worldrenderer.pos(f5, f8, f9).tex(f2, f3).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();


        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
    }

    private void func_178103_d(float qq) {
        GlStateManager.translate(-0.5F, qq, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }

    public void updateEquippedItem() {
        this.prevEquippedProgress = this.equippedProgress;
        EntityPlayer entityplayer = this.mc.thePlayer;
        ItemStack itemstack = entityplayer.inventory.getCurrentItem();
        boolean flag = false;

        if (this.itemToRender != null && itemstack != null) {
            if (!this.itemToRender.getIsItemStackEqual(itemstack)) {
                flag = true;
            }
        } else if (this.itemToRender == null && itemstack == null) {
            flag = false;
        } else {
            flag = true;
        }

        float f2 = 0.4F;
        float f = flag ? 0.0F : 1.0F;
        float f1 = MathHelper.clamp_float(f - this.equippedProgress, -f2, f2);
        this.equippedProgress += f1;

        if (this.equippedProgress < 0.1F) {
            this.itemToRender = itemstack;
            this.equippedItemSlot = entityplayer.inventory.currentItem;

            if (Config.isShaders()) {
                Shaders.setItemToRenderMain(itemstack);
            }
        }
    }

    /**
     * Resets equippedProgress
     */
    public void resetEquippedProgress() {
        equippedProgress = 0.0F;
    }

    /**
     * Resets equippedProgress
     */
    public void resetEquippedProgress2() {
        equippedProgress = 0.0F;
    }

    static final class ItemRenderer$1 {
        static final int[] field_178094_a = new int[EnumAction.values().length];

        static {
            try {
                field_178094_a[EnumAction.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                field_178094_a[EnumAction.EAT.ordinal()] = 2;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                field_178094_a[EnumAction.DRINK.ordinal()] = 3;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                field_178094_a[EnumAction.BLOCK.ordinal()] = 4;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                field_178094_a[EnumAction.BOW.ordinal()] = 5;
            } catch (NoSuchFieldError ignored) {
            }
        }
    }
}
