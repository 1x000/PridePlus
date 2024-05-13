package net.minecraft.util;

import cn.molokymc.prideplus.Client;
import cn.molokymc.prideplus.event.impl.player.EventMoveInput;
import cn.molokymc.prideplus.module.impl.movement.Speed;
import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput {
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn) {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState() {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (this.gameSettings.keyBindForward.isKeyDown()) {
            ++this.moveForward;
        }

        if (this.gameSettings.keyBindBack.isKeyDown()) {
            --this.moveForward;
        }

        if (this.gameSettings.keyBindLeft.isKeyDown()) {
            ++this.moveStrafe;
        }

        if (this.gameSettings.keyBindRight.isKeyDown()) {
            --this.moveStrafe;
        }

        this.jump = this.gameSettings.keyBindJump.isKeyDown() && !(Client.INSTANCE.getModuleCollection().getModule(Speed.class).shouldPreventJumping());
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown();

        EventMoveInput event = new EventMoveInput(moveForward, moveStrafe, jump, sneak);

        Client.INSTANCE.getEventProtocol().handleEvent(event);

        if (event.isCancelled()) return;

        this.moveForward = event.getForward();
        this.moveStrafe = event.getStrafe();
        this.sneak = event.isSneak();
        this.jump = event.isJump();

        if (this.sneak) {
            this.moveStrafe = (float) ((double) this.moveStrafe * 0.3D);
            this.moveForward = (float) ((double) this.moveForward * 0.3D);
        }
    }
}
