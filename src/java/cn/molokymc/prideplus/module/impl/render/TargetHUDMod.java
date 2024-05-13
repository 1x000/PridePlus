package cn.molokymc.prideplus.module.impl.render;

import cn.molokymc.prideplus.Client;
import cn.molokymc.prideplus.module.settings.impl.BooleanSetting;
import cn.molokymc.prideplus.module.settings.impl.ModeSetting;
import cn.molokymc.prideplus.event.impl.render.PreRenderEvent;
import cn.molokymc.prideplus.event.impl.render.Render2DEvent;
import cn.molokymc.prideplus.event.impl.render.Render3DEvent;
import cn.molokymc.prideplus.event.impl.render.ShaderEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.impl.combat.KillAura;
import cn.molokymc.prideplus.module.impl.render.targethud.TargetHUD;
import cn.molokymc.prideplus.module.settings.ParentAttribute;
import cn.molokymc.prideplus.utils.animations.Animation;
import cn.molokymc.prideplus.utils.animations.Direction;
import cn.molokymc.prideplus.utils.animations.impl.*;
import cn.molokymc.prideplus.utils.objects.Dragging;
import cn.molokymc.prideplus.utils.objects.GradientColorWheel;
import cn.molokymc.prideplus.utils.render.ESPUtil;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import cn.molokymc.prideplus.utils.tuples.Pair;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.util.vector.Vector4f;

public class TargetHUDMod extends Module {

    private final ModeSetting targetHud = new ModeSetting("Mode", "Stable",  "Stable", "Rainy","Rise","Rise2", "Test", "Exhibition", "Naven");
    private final BooleanSetting trackTarget = new BooleanSetting("Track Target", false);
    private final ModeSetting trackingMode = new ModeSetting("Tracking Mode", "Middle", "Middle", "Top", "Left", "Right");
    //private final ModeSetting animationMode = new ModeSetting("Animation","1","1","2","3","4","5","6");

    public static boolean renderLayers = true;

    private final GradientColorWheel colorWheel = new GradientColorWheel();

    public TargetHUDMod() {
        super("TargetHUD","目标显示", Category.DISPLAY, "Displays info about the KillAura target");
        trackingMode.addParent(trackTarget, ParentAttribute.BOOLEAN_CONDITION);
        addSettings(targetHud, trackTarget, trackingMode, colorWheel.createModeSetting("Color Mode", "Dark"), colorWheel.getColorSetting());
        TargetHUD.init();
    }

    private EntityLivingBase target;
    private final Dragging drag = Client.INSTANCE.createDrag(this, "targetHud", 300, 300);

    public Animation openAnimation1 = new EaseBackIn(350, 1,(float)1.1);


    private KillAura killAura;

    private Vector4f targetVector;

    @Override
    public void onRender3DEvent(Render3DEvent event) {
        if (trackTarget.isEnabled() && target != null) {
            for (Entity entity : mc.theWorld.loadedEntityList) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                    if (target.equals(entityLivingBase)) {
                        targetVector = ESPUtil.getEntityPositionsOn2D(entity);
                    }
                }
            }

        }
    }

    @Override
    public void onPreRenderEvent(PreRenderEvent event) {
        TargetHUD currentTargetHUD = TargetHUD.get(targetHud.getMode());
        drag.setWidth(currentTargetHUD.getWidth());
        drag.setHeight(currentTargetHUD.getHeight());


        if (killAura == null) {
            killAura = (KillAura) Client.INSTANCE.getModuleCollection().get(KillAura.class);
        }


        if (!(mc.currentScreen instanceof GuiChat)) {
            if (!killAura.isEnabled()) {
                openAnimation1.setDirection(Direction.BACKWARDS);
                if (openAnimation1.finished(Direction.BACKWARDS)) {
                }
            }

            if (target == null && killAura.target != null) {
                target = killAura.target;
                openAnimation1.setDirection(Direction.FORWARDS);

            } else if (killAura.target == null || target != killAura.target) {
                openAnimation1.setDirection(Direction.BACKWARDS);
            }

            if (openAnimation1.finished(Direction.BACKWARDS)) {
                target = null;
            }
        } else {
            openAnimation1.setDirection(Direction.FORWARDS);
            target = mc.thePlayer;
            }
        }


    @Override
    public void onRender2DEvent(Render2DEvent e) {
        this.setSuffix(targetHud.getMode());
        boolean tracking = trackTarget.isEnabled() && targetVector != null && target != mc.thePlayer;

        TargetHUD currentTargetHUD = TargetHUD.get(targetHud.getMode());

        if (target != null) {


            float trackScale = 1;
            float x = drag.getX(), y = drag.getY();
            if (tracking) {
                float newWidth = (targetVector.getZ() - targetVector.getX()) * 1.4f;
                trackScale = Math.min(1, newWidth / currentTargetHUD.getWidth());

                Pair<Float, Float> coords = getTrackedCoords();
                x = coords.getFirst();
                y = coords.getSecond();
            }


            RenderUtil.scaleStart(x + drag.getWidth() / 2f, y + drag.getHeight() / 2f,
                    (float) (0 + openAnimation1.getOutput().floatValue()) * trackScale);
            float alpha = Math.min(1f, openAnimation1.getOutput().floatValue() * 3);

            currentTargetHUD.render(x, y, alpha, target);


            RenderUtil.scaleEnd();
        }
    }


    @Override
    public void onShaderEvent(ShaderEvent e) {
        float x = drag.getX(), y = drag.getY();
        float trackScale = 1;
        TargetHUD currentTargetHUD = TargetHUD.get(targetHud.getMode());
        if (trackTarget.isEnabled() && targetVector != null && target != mc.thePlayer) {
            Pair<Float, Float> coords = getTrackedCoords();
            x = coords.getFirst();
            y = coords.getSecond();

            float newWidth = (targetVector.getZ() - targetVector.getX()) * 1.4f;
            trackScale = Math.min(1, newWidth / currentTargetHUD.getWidth());
        }


        if (target != null) {

            boolean glow = e.getBloomOptions().getSetting("TargetHud").isEnabled();
            RenderUtil.scaleStart(x + drag.getWidth() / 2f, y + drag.getHeight() / 2f,
                    (float) (0 + openAnimation1.getOutput().floatValue()) * trackScale);
            float alpha = Math.min(1f, openAnimation1.getOutput().floatValue() * 3);

            currentTargetHUD.renderEffects(x, y, alpha, glow);

            RenderUtil.scaleEnd();
        }
    }


    @Override
    public void onEnable() {
        super.onEnable();

        target = null;
    }


    private Pair<Float, Float> getTrackedCoords() {
        ScaledResolution sr = new ScaledResolution(mc);
        float width = drag.getWidth(), height = drag.getHeight();
        float x = targetVector.getX(), y = targetVector.getY();
        float entityWidth = (targetVector.getZ() - targetVector.getX());
        float entityHeight = (targetVector.getW() - targetVector.getY());
        float middleX = x + entityWidth / 2f - width / 2f;
        float middleY = y + entityHeight / 2f - height / 2f;
        switch (trackingMode.getMode()) {
            case "Middle":
                return Pair.of(middleX, middleY);
            case "Top":
                return Pair.of(middleX, y - (height / 2f + height / 4f));
            case "Left":
                return Pair.of(x - (width / 2f + width / 4f), middleY);
            default:
                return Pair.of(x + entityWidth - (width / 4f), middleY);
        }
    }


}
