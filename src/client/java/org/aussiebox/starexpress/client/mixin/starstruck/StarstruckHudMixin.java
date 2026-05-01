package org.aussiebox.starexpress.client.mixin.starstruck;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.aussiebox.starexpress.StarryExpressRoles;
import org.aussiebox.starexpress.cca.AbilityComponent;
import org.aussiebox.starexpress.client.StarryExpressClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class StarstruckHudMixin {
    @Shadow public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("TAIL"))
    public void starstruckHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player == null) return;
        if (StarryExpressClient.abilityBind != null) return;

        GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
        AbilityComponent abilityComponent = AbilityComponent.KEY.get(MinecraftClient.getInstance().player);
        if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, StarryExpressRoles.STARSTRUCK)) {
            int drawY = context.getScaledWindowHeight();

            Text line = Text.translatable("tip.starexpress.starstruck", StarryExpressClient.abilityBind.getBoundKeyLocalizedText());

            if (abilityComponent.cooldown > 0) {
                line = Text.translatable("tip.starexpress.cooldown", abilityComponent.cooldown/20);
            }

            drawY -= getTextRenderer().getWrappedLinesHeight(line, 999999);
            context.drawTextWithShadow(getTextRenderer(), line, context.getScaledWindowWidth() - getTextRenderer().getWidth(line), drawY, StarryExpressRoles.STARSTRUCK.color());
        }
    }
}