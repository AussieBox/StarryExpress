package org.aussiebox.starexpress.client.mixin;

import dev.doctor4t.wathe.client.gui.RoleNameRenderer;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.aussiebox.starexpress.client.StarryExpressClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RoleNameRenderer.class)
public class GetTargetMixin {

    @Inject(method = "renderHud", at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/game/GameFunctions;isPlayerSpectatingOrCreative(Lnet/minecraft/entity/player/PlayerEntity;)Z"))
    private static void playerRaycast(TextRenderer renderer, ClientPlayerEntity player, DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        float range = GameFunctions.isPlayerSpectatingOrCreative(player) ? 8.0F : 2.0F;
        HitResult line = ProjectileUtil.getCollision(player, entity -> entity instanceof PlayerEntity, range);
        StarryExpressClient.target = null;
        if (!(line instanceof EntityHitResult ehr)) {
            return;
        }
        if (!(ehr.getEntity() instanceof PlayerEntity victim)) {
            return;
        }
        StarryExpressClient.target = victim;
    }

}
