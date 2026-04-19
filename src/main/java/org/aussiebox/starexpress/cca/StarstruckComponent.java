package org.aussiebox.starexpress.cca;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.aussiebox.starexpress.StarryExpress;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class StarstruckComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<StarstruckComponent> KEY = ComponentRegistry.getOrCreate(StarryExpress.id("starstruck"), StarstruckComponent.class);
    private final PlayerEntity player;
    public int ticks = 0;

    public StarstruckComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void serverTick() {
        if (this.ticks > 0) {
            --this.ticks;
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.getServerWorld().spawnParticles(StarryExpress.STARSTRUCK_SPARKLE, serverPlayer.getX(), serverPlayer.getY()+0.2, serverPlayer.getZ(), player.getRandom().nextBetween(1, 2), 0.2, 0, 0.2, 0);
            }
            this.sync();
        }
    }

    public void sync() {
        KEY.sync(this.player);
    }

    public void reset() {
        this.ticks = 0;
        sync();
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
        this.sync();
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        tag.putInt("ticks", this.ticks);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        this.ticks = tag.contains("ticks") ? tag.getInt("ticks") : 0;
    }
}
