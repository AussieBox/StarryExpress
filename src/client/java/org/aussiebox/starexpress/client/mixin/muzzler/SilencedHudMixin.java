package org.aussiebox.starexpress.client.mixin.muzzler;

import dev.doctor4t.wathe.client.gui.RoleNameRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.StarryExpressRoles;
import org.aussiebox.starexpress.cca.SilenceComponent;
import org.aussiebox.starexpress.client.StarryExpressClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RoleNameRenderer.class)
public class SilencedHudMixin {

    @Inject(
            method = "renderHud",
            at = @At("TAIL")
    )
    private static void silencedTip(TextRenderer renderer, ClientPlayerEntity player, DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (StarryExpressClient.target == null) return;

        SilenceComponent victimSilence = SilenceComponent.KEY.get(StarryExpressClient.target);

        if (!victimSilence.isSilenced() || victimSilence.getSilencedTicks() < StarryExpress.SERVER_CONFIG.muzzlerConfig.displaySilencedTipDelay() * 20) return;

        renderSilencedTip(renderer, context);
    }

    @Unique
    private static void renderSilencedTip(TextRenderer renderer, DrawContext context) {
        Text text = Text.translatable("tip.starexpress.muzzler.silenced");

        context.getMatrices().push();
        context.getMatrices().translate(context.getScaledWindowWidth() / 2.0F, context.getScaledWindowHeight() / 2.0f - 37.5F, 0.0F);
        context.getMatrices().scale(0.6F, 0.6F, 1.0F);

        context.drawTextWithShadow(
                renderer,
                text,
                -renderer.getWidth(text) / 2,
                32,
                StarryExpressRoles.MUZZLER.color()
        );

        context.getMatrices().pop();
    }

}
