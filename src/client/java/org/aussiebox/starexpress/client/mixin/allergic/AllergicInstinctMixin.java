package org.aussiebox.starexpress.client.mixin.allergic;

import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.aussiebox.starexpress.cca.AllergicComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(WatheClient.class)
public abstract class AllergicInstinctMixin {

    @Inject(method = "isInstinctEnabled", at = @At("HEAD"), cancellable = true)
    private static void isInstinctEnabled(CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().player != null) {
            AllergicComponent allergy = AllergicComponent.KEY.get(MinecraftClient.getInstance().player);
            if (allergy.isAllergic() && allergy.getGlowTicks() > 0) {
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
                    AllergicComponent allergy = AllergicComponent.KEY.get(MinecraftClient.getInstance().player);
                    if (allergy.isAllergic() && allergy.getGlowTicks() > 0) {
                        cir.setReturnValue(Color.GREEN.getRGB());
                    }
                }
            }
        }
    }
}