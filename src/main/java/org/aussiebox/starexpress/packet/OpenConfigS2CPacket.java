package org.aussiebox.starexpress.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.aussiebox.starexpress.StarryExpress;
import org.jetbrains.annotations.NotNull;

public record OpenConfigS2CPacket() implements CustomPayload {
    public static final Identifier OPEN_CONFIG_PAYLOAD_ID = StarryExpress.id("open_config");
    public static final CustomPayload.Id<OpenConfigS2CPacket> TYPE = new CustomPayload.Id<>(OPEN_CONFIG_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, OpenConfigS2CPacket> CODEC = PacketCodec.ofStatic(
            OpenConfigS2CPacket::write,
            OpenConfigS2CPacket::read
    );

    public OpenConfigS2CPacket() {
    }

    public CustomPayload.@NotNull Id<? extends CustomPayload> getId() {
        return TYPE;
    }

    public static void write(PacketByteBuf buf, OpenConfigS2CPacket packet) {

    }

    public static OpenConfigS2CPacket read(PacketByteBuf buf) {
        return new OpenConfigS2CPacket();
    }
}
