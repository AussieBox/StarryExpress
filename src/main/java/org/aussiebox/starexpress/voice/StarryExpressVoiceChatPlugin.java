package org.aussiebox.starexpress.voice;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.server.network.ServerPlayerEntity;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.cca.SilenceComponent;

import java.util.Objects;

public class StarryExpressVoiceChatPlugin implements VoicechatPlugin {

    @Override
    public String getPluginId() {
        return StarryExpress.MOD_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {
        VoicechatPlugin.super.initialize(api);
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::microphonePacketEvent, 524);
        VoicechatPlugin.super.registerEvents(registration);
    }

    public void microphonePacketEvent(MicrophonePacketEvent event) {
        ServerPlayerEntity sender = (ServerPlayerEntity) Objects.requireNonNull(event.getSenderConnection()).getPlayer().getPlayer();
        SilenceComponent silence = SilenceComponent.KEY.get(sender);

        if (silence.isSilenced()) {
            if (GameFunctions.isPlayerAliveAndSurvival(sender)) {
                event.cancel();
            }
        }
    }
}
