package org.aussiebox.starexpress.mixin.allergic;

import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.cca.AllergicComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GameFunctions.class)
public class AllergicGameFunctionsMixin {

    @ModifyVariable(
            method = "killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V",
            at = @At("HEAD"),
            argsOnly = true,
            name = "arg3"
    )
    private static Identifier allergicDeath(Identifier deathReason, PlayerEntity victim, boolean spawnBody, PlayerEntity killer) {
        AllergicComponent component = AllergicComponent.KEY.get(victim);

        if (!component.isAllergic()) {
            return deathReason;
        }

        if (killer == victim && deathReason == GameConstants.DeathReasons.POISON) {
            deathReason = StarryExpress.id("allergies");
        }
        return deathReason;
    }

}
