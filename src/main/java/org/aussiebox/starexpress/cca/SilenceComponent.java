package org.aussiebox.starexpress.cca;

import dev.doctor4t.wathe.Wathe;
import dev.doctor4t.wathe.game.GameFunctions;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.StarryExpressConstants;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.Objects;
import java.util.UUID;

public class SilenceComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<SilenceComponent> KEY = ComponentRegistry.getOrCreate(StarryExpress.id("silence"), SilenceComponent.class);

    private final PlayerEntity player;

    @Setter @Getter private boolean silenced;

    @Setter @Getter private UUID silencer;

    private int outsideTicks;

    @Setter @Getter private int tearChecks;

    @Getter private int silencedTicks;

    public SilenceComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void serverTick() {
        if (silenced) silencedTicks++;
        else silencedTicks = 0;

        if (Wathe.isSkyVisibleAdjacent(player) && silenced) outsideTicks++;
        else outsideTicks = 0;

        if (StarryExpress.SERVER_CONFIG.muzzlerConfig.suffocationTime() > 0) {
            if (outsideTicks >= StarryExpress.SERVER_CONFIG.muzzlerConfig.suffocationTime() * 20)
                GameFunctions.killPlayer(player, true, player.getWorld().getPlayerByUuid(silencer), StarryExpressConstants.SILENCED_OUTSIDE_DEATH_REASON);
        }

        this.sync();
    }

    public void reset() {
        this.silenced = false;
        this.silencer = null;
        this.outsideTicks = 0;
        this.tearChecks = 0;
        this.silencedTicks = 0;
        this.sync();
    }

    public void sync() {
        KEY.sync(this.player);
    }


    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup provider) {
        this.silenced = tag.contains("silenced") && tag.getBoolean("silenced");
        this.silencer = tag.contains("silencer") ? tag.getUuid("silencer") : null;
        this.outsideTicks = tag.contains("outside_ticks") ? tag.getInt("outside_ticks") : 0;
        this.tearChecks = tag.contains("tear_checks") ? tag.getInt("tear_checks") : 0;
        this.silencedTicks = tag.contains("silenced_ticks") ? tag.getInt("silenced_ticks") : 0;
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup provider) {
        tag.putBoolean("silenced", this.silenced);
        tag.putUuid("silencer", Objects.requireNonNullElseGet(this.silencer, () -> UUID.fromString("e1e89fbb-3beb-492a-b1be-46a4ce19c9d1")));
        tag.putInt("outside_ticks", this.outsideTicks);
        tag.putInt("tear_checks", this.tearChecks);
        tag.putInt("silenced_ticks", this.silencedTicks);
    }

}
