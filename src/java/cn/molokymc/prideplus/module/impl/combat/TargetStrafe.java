package cn.molokymc.prideplus.module.impl.combat;

import cn.molokymc.prideplus.Client;
import cn.molokymc.prideplus.event.impl.player.MotionEvent;
import cn.molokymc.prideplus.event.impl.player.MoveEvent;
import cn.molokymc.prideplus.event.impl.render.Render3DEvent;
import cn.molokymc.prideplus.module.Category;
import cn.molokymc.prideplus.module.Module;
import cn.molokymc.prideplus.module.settings.impl.BooleanSetting;
import cn.molokymc.prideplus.module.settings.impl.ColorSetting;
import cn.molokymc.prideplus.module.settings.impl.ModeSetting;
import cn.molokymc.prideplus.module.settings.impl.NumberSetting;
import cn.molokymc.prideplus.utils.animations.Direction;
import cn.molokymc.prideplus.utils.animations.impl.DecelerateAnimation;
import cn.molokymc.prideplus.utils.player.MovementUtils;
import cn.molokymc.prideplus.utils.player.RotationUtils;
import cn.molokymc.prideplus.utils.render.RenderUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.optifine.util.MathUtils;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;

public class TargetStrafe extends Module {
    private final ModeSetting mode = new ModeSetting("Render", "Adapt","Adapt");
    private final BooleanSetting onlyGround = new BooleanSetting("only OnGround", false);
    private final BooleanSetting onlyhurttime = new BooleanSetting("only Damage", false);
    public static final BooleanSetting auto3rdPerson = new BooleanSetting("Auto 3rd Person", false);
    private final NumberSetting radius = new NumberSetting("Radius", 3.0, 5.0, 0.1, 0.1);
    private static final NumberSetting points = new NumberSetting("Points", 12, 30, 3, 1);
    private final ColorSetting color = new ColorSetting("Color", new Color(-16711712));
    private final DecelerateAnimation animation = new DecelerateAnimation(250, radius.getValue(), Direction.FORWARDS);
    private static boolean returnState;
    public TargetStrafe() {
        super("TargetStrafe", "自动转人",Category.MOVEMENT, "Move Around the a target.");
        this.addSettings(this.mode, this.onlyGround, this.onlyhurttime, auto3rdPerson, this.radius, points, color);
    }
    @Override
    public void onMotionEvent(MotionEvent event) {
        if (canStrafe()) {
        if (auto3rdPerson.isEnabled() && mc.gameSettings.thirdPersonView == 0) {
            mc.gameSettings.thirdPersonView = 1;
            returnState = true;
        } else if (auto3rdPerson.isEnabled() && mc.gameSettings.thirdPersonView != 0 && returnState) {
            mc.gameSettings.thirdPersonView = 0;
            returnState = false;
        }
      }
    }
    public static void targetStrafe(EntityLivingBase target, float direction_, float radius, MoveEvent event, int mathRadius) {
        if (MovementUtils.isMoving()) {
            float forward_ = 0.0F;
            float strafe_ = 0.0F;
            float speed_ = MathHelper.sqrt_float((float)(event.getX() * event.getX() + event.getZ() * event.getZ()));
            float _direction = 0.0F;
            if (!((double)speed_ <= 1.0E-4)) {
                if ((double)direction_ > 0.001) {
                    _direction = 1.0F;
                } else if ((double)direction_ < -0.001) {
                    _direction = -1.0F;
                }

                float curDistance = 0.01F;
                if (mathRadius == 1) {
                    curDistance = mc.thePlayer.getDistanceToEntity(target);
                } else if (mathRadius == 0) {
                    curDistance = (float)Math.sqrt((mc.thePlayer.posX - target.posX) * (mc.thePlayer.posX - target.posX) + (mc.thePlayer.posZ - target.posZ) * (mc.thePlayer.posZ - target.posZ));
                }

                if (curDistance < radius - speed_) {
                    forward_ = -1.0F;
                } else if (curDistance > radius + speed_) {
                    forward_ = 1.0F;
                } else {
                    forward_ = (curDistance - radius) / speed_;
                }

                if (curDistance < radius + speed_ * 2.0F && curDistance > radius - speed_ * 2.0F) {
                    strafe_ = 1.0F;
                }

                strafe_ *= _direction;
                float strafeYaw = RotationUtils.getRotationsNeeded(target)[0];
                float covert_ = (float)Math.sqrt((double)(forward_ * forward_ + strafe_ * strafe_));
                forward_ /= covert_;
                strafe_ /= covert_;
                float turnAngle = (float)Math.toDegrees((double)MathUtils.asin(strafe_));
                if (turnAngle > 0.0F) {
                    if (forward_ < 0.0F) {
                        turnAngle = 180.0F - turnAngle;
                    }
                } else if (forward_ < 0.0F) {
                    turnAngle = -180.0F - turnAngle;
                }

                strafeYaw = (float)Math.toRadians((double)(strafeYaw + turnAngle));
                event.setX(-Math.sin((double)strafeYaw) * (double)speed_);
                event.setZ((double)(MathHelper.cos(strafeYaw) * speed_));
                mc.thePlayer.motionX = event.getX();
                mc.thePlayer.motionZ = event.getZ();
            }
        }
    }
    public static boolean canStrafe() {
        KillAura killAura = Client.INSTANCE.getModuleCollection().getModule(KillAura.class);
        if (!Client.INSTANCE.isEnabled(TargetStrafe.class) || !killAura.isEnabled()
                || !MovementUtils.isMoving()) {
            return false;
        }
        return KillAura.target != null && killAura.isValid(KillAura.target);
    }
    private void drawCircle(float lineWidth, int color) {
        EntityLivingBase entity = KillAura.target;
        if (entity == null) return;

        glPushMatrix();
        RenderUtil.color(color, (float) ((animation.getOutput().floatValue() / radius.getValue()) / 2F));
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glLineWidth(lineWidth);
        glEnable(GL_BLEND);
        glEnable(GL_LINE_SMOOTH);

        glBegin(GL_LINE_STRIP);
        EntityLivingBase target = KillAura.target;
        float partialTicks = mc.timer.elapsedPartialTicks;
        double rad = radius.getValue();
        double d = (Math.PI * 2.0) / points.getValue();

        double posX = target.posX, posY = target.posY, posZ = target.posZ;
        double lastTickX = target.lastTickPosX, lastTickY = target.lastTickPosY, lastTickZ = target.lastTickPosZ;
        double renderPosX = mc.getRenderManager().renderPosX, renderPosY = mc.getRenderManager().renderPosY, renderPosZ = mc.getRenderManager().renderPosZ;

        double y = lastTickY + (posY - lastTickY) * partialTicks - renderPosY;
        for (double i = 0; i < Math.PI * 2.0; i += d) {
            double x = lastTickX + (posX - lastTickX) * partialTicks + StrictMath.sin(i) * rad - renderPosX,
                    z = lastTickZ + (posZ - lastTickZ) * partialTicks + StrictMath.cos(i) * rad - renderPosZ;
            glVertex3d(x, y, z);
        }
        double x = lastTickX + (posX - lastTickX) * partialTicks - renderPosX,
                z = lastTickZ + (posZ - lastTickZ) * partialTicks + rad - renderPosZ;
        glVertex3d(x, y, z);
        glEnd();

        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glColor4f(1, 1, 1, 1);
        glPopMatrix();
    }
    public void onRender3DEvent(Render3DEvent event) {
        //this.setSuffix(mode.getMode());
        switch (mode.getMode()) {
            case "Adapt":
            if (animation.getEndPoint() != radius.getValue()) animation.setEndPoint(radius.getValue());
            boolean canStrafe = canStrafe();
            animation.setDirection(canStrafe ? Direction.FORWARDS : Direction.BACKWARDS);
            if (canStrafe || !animation.isDone()) {
                drawCircle(5, 0xFF000000);
                drawCircle(3, color.getColor().getRGB());
            }
        }
    }
    public void onMoveEvent(MoveEvent event) {
        if ((!this.onlyGround.isEnabled() || mc.thePlayer.isOnGround()) && (!this.onlyhurttime.isEnabled() || mc.thePlayer.hurtTime >= 1) && Client.INSTANCE.isEnabled(KillAura.class)) {
            if (KillAura.target == null) {
                return;
            }

            targetStrafe(KillAura.target, -1.0F, this.radius.getValue().floatValue(), event, this.radius.getValue().intValue());
        }
    }
}
