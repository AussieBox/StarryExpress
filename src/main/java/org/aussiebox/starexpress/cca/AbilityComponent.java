package org.aussiebox.starexpress.cca;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.aussiebox.starexpress.StarryExpress;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class AbilityComponent implements AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent {
    public static final ComponentKey<AbilityComponent> KEY = ComponentRegistry.getOrCreate(StarryExpress.id("ability"), AbilityComponent.class);
    private final PlayerEntity player;
    public int cooldown = 0;


    public AbilityComponent(PlayerEntity player) {
        this.player = player;
    }

    public void clientTick() {
    }

    public void serverTick() {
        if (this.cooldown > 0) {
            --this.cooldown;
            this.sync();
        }

    }

    public void setCooldown(int ticks) {
        this.cooldown = ticks;
        this.sync();
    }

    public void changeCooldown(int ticks) {
        this.cooldown += ticks;
        if (this.cooldown < 0) {
            this.cooldown = 0;
        }
        this.sync();
    }

    public void sync() {
        KEY.sync(this.player);
    }

    public void reset() {
        this.cooldown = 0;
        this.sync();
    }

    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        tag.putInt("cooldown", this.cooldown);
    }

    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        this.cooldown = tag.contains("cooldown") ? tag.getInt("cooldown") : 0;
    }
}
