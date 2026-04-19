package org.aussiebox.starexpress.cca;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.aussiebox.starexpress.StarryExpress;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.UUID;

public class AllergicComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<AllergicComponent> KEY = ComponentRegistry.getOrCreate(StarryExpress.id("allergic"), AllergicComponent.class);

    private final PlayerEntity player;
    public int armor = 0;

    public String allergyType;

    private UUID allergic;

    private int glowTicks;

    public AllergicComponent(PlayerEntity player) {
        this.player = player;
    }

    public String getAllergyType() {
        return this.allergyType;
    }

    public void setAllergyType(String type) {
        this.allergyType = type;
        this.sync();
    }

    public UUID getAllergic() {
        return this.allergic;
    }

    public void setAllergic(UUID uuid) {
        this.allergic = uuid;
        this.sync();
    }

    public int getGlowTicks() {
        return this.glowTicks;
    }

    public void setGlowTicks(int ticks) {
        this.glowTicks = ticks;
        this.sync();
    }

    public void serverTick() {
        if (this.glowTicks > 0) {
            --this.glowTicks;
        }
        this.sync();
    }

    public void giveArmor() {
        this.armor = 1;
        this.sync();
    }

    public void reset() {
        this.allergic = null;
        this.allergyType = null;
        this.glowTicks = 0;
        this.armor = 0;
        this.sync();
    }

    public void sync() {
        KEY.sync(this.player);
    }

    public boolean isAllergic() {
        return this.allergic != null && !this.allergic.equals(UUID.fromString("e1e89fbb-3beb-492a-b1be-46a4ce19c9d1"));
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        this.allergic = tag.contains("allergic") ? tag.getUuid("allergic") : null;
        this.armor = tag.contains("armor") ? tag.getInt("armor") : 0;
        this.glowTicks = tag.contains("glow_ticks") ? tag.getInt("glow_ticks") : 0;
        this.allergyType = tag.contains("allergy_type") ? tag.getString("allergy_type") : "none";
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        tag.putUuid("allergic", this.allergic != null ? this.allergic : UUID.fromString("e1e89fbb-3beb-492a-b1be-46a4ce19c9d1"));
        tag.putInt("armor", this.armor);
        tag.putInt("glow_ticks", this.glowTicks);
        tag.putString("allergy_type", this.allergyType != null ? this.allergyType : "none");
    }
}
