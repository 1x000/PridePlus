package net.minecraft.client.renderer;

import dev.stable.event.impl.network.PacketEvent;
import dev.stable.event.impl.network.PacketReceiveEvent;
import dev.stable.module.Category;
import dev.stable.module.Module;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class ForceUnicodeChat extends Module {
    public ForceUnicodeChat() {
        super("ForceUnicodeChat","强制发送Unicode字符", Category.RENDER,"unicode");
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        if(event.getPacket() instanceof C01PacketChatMessage) {

            final C01PacketChatMessage chatMessage = (C01PacketChatMessage) event.getPacket();

            final String message = chatMessage.getMessage();
            final StringBuilder stringBuilder = new StringBuilder();

            for(char c : message.toCharArray())
                if(c >= 33 && c <= 128)
                    stringBuilder.append(Character.toChars(c + 65248));
                else
                    stringBuilder.append(c);

            chatMessage.message = stringBuilder.toString();
        }
    }
}
