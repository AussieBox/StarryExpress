package org.aussiebox.starexpress.mixin;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import net.minecraft.entity.player.PlayerEntity;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.StarryExpressRoles;
import org.aussiebox.starexpress.cca.AbilityComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerMoodComponent.class)
public abstract class MoodMixin {

    @Shadow
    public abstract float getMood();

    @Shadow @Final
    private PlayerEntity player;

    @Inject(method = "setMood", at = @At("HEAD"))
    void setMood(float mood, CallbackInfo ci) {
        GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(player.getWorld());
        if (mood > getMood()) {
            if (gameWorldComponent.getRole(player) == StarryExpressRoles.STARSTRUCK) {
                if (!StarryExpress.SERVER_CONFIG.starstruckConfig.taskReducesCooldown()) return;
                AbilityComponent ability = AbilityComponent.KEY.get(player);
                ability.changeCooldown(-(StarryExpress.SERVER_CONFIG.starstruckConfig.taskCooldownReduction() * 20));
            }
        }
    }
}
