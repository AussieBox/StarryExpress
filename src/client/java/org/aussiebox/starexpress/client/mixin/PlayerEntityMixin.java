package org.aussiebox.starexpress.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.StarryExpressRoles;
import org.aussiebox.starexpress.cca.StarstruckComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PlayerEntity.class, priority = 1500)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World level) {
        super(entityType, level);
    }

    @ModifyReturnValue(
            method = {"getMovementSpeed()F"},
            at = {@At("RETURN")}
    )
    public float overrideMovementSpeed(float original) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (GameWorldComponent.KEY.get(player.getWorld()).isRole(player, StarryExpressRoles.STARSTRUCK) && StarstruckComponent.KEY.get(player).ticks > 0) {
            if (!StarryExpress.SERVER_CONFIG.starstruckConfig.abilityAffectsMovementSpeed()) return original;
            return this.isSprinting() ? StarryExpress.SERVER_CONFIG.starstruckConfig.abilitySprintSpeed() : StarryExpress.SERVER_CONFIG.starstruckConfig.abilityWalkSpeed();
        } else {
            return original;
        }
    }
}
