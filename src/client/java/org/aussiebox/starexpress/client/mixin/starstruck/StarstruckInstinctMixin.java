package org.aussiebox.starexpress.client.mixin.starstruck;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.aussiebox.starexpress.StarryExpressRoles;
import org.aussiebox.starexpress.cca.StarstruckComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WatheClient.class)
public abstract class StarstruckInstinctMixin {

    @Inject(method = "isInstinctEnabled", at = @At("HEAD"), cancellable = true)
    private static void isInstinctEnabled(CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().player != null) {
            GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
            StarstruckComponent component = StarstruckComponent.KEY.get(MinecraftClient.getInstance().player);
            if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, StarryExpressRoles.STARSTRUCK) && component.ticks > 0) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }

    @Inject(method = "getInstinctHighlight", at = @At("HEAD"), cancellable = true)
    private static void getInstinctHighlightColor(Entity target, CallbackInfoReturnable<Integer> cir) {
        if (target instanceof PlayerEntity) {
            if (!target.isSpectator()) {
                if (MinecraftClient.getInstance().player != null) {
                    GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
                    StarstruckComponent component = StarstruckComponent.KEY.get(MinecraftClient.getInstance().player);
                    if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, StarryExpressRoles.STARSTRUCK) && component.ticks > 0) {
                        cir.setReturnValue(StarryExpressRoles.STARSTRUCK.color());
                    }
                }
            }
        }
    }
}
