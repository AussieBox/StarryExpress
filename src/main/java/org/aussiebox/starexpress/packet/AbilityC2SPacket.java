package org.aussiebox.starexpress.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.aussiebox.starexpress.StarryExpress;
import org.jetbrains.annotations.NotNull;

public record AbilityC2SPacket() implements CustomPayload {
    public static final Identifier ABILITY_PAYLOAD_ID = StarryExpress.id("ability");
    public static final CustomPayload.Id<AbilityC2SPacket> TYPE = new CustomPayload.Id<>(ABILITY_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, AbilityC2SPacket> CODEC = PacketCodec.ofStatic(
            AbilityC2SPacket::write,
            AbilityC2SPacket::read
    );

    public AbilityC2SPacket() {
    }

    public CustomPayload.@NotNull Id<? extends CustomPayload> getId() {
        return TYPE;
    }

    public static void write(PacketByteBuf buf, AbilityC2SPacket packet) {

    }

    public static AbilityC2SPacket read(PacketByteBuf buf) {
        return new AbilityC2SPacket();
    }
}
