package org.aussiebox.starexpress.client.mixin.starstruck;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.MoodRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.StarryExpressRoles;
import org.aussiebox.starexpress.cca.AbilityComponent;
import org.aussiebox.starexpress.cca.StarstruckComponent;
import org.aussiebox.starexpress.util.StarryExpressUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static dev.doctor4t.wathe.client.gui.MoodRenderer.ARROW_DOWN;
import static dev.doctor4t.wathe.client.gui.MoodRenderer.ARROW_UP;

@Mixin(MoodRenderer.class)
public class StarstruckMoodRendererMixin {

    @Unique private static final Identifier ABILITY_HAPPY = StarryExpress.id("hud/starstruck/ability_happy");
    @Unique private static final Identifier ABILITY_MID = StarryExpress.id("hud/starstruck/ability_mid");
    @Unique private static final Identifier ABILITY_DEPRESSIVE = StarryExpress.id("hud/starstruck/ability_depressive");
    @Unique private static final Identifier ABILITY_SPARKLES = StarryExpress.id("hud/starstruck/ability_sparkles");
    @Unique private static final Identifier HAPPY = StarryExpress.id("hud/starstruck/happy");
    @Unique private static final Identifier MID = StarryExpress.id("hud/starstruck/mid");
    @Unique private static final Identifier DEPRESSIVE = StarryExpress.id("hud/starstruck/depressive");
    @Unique private static final Identifier SPARKLES = StarryExpress.id("hud/starstruck/sparkles");
    @Unique private static float oldMood;

    @Shadow public static float moodRender;
    @Shadow public static float arrowProgress;
    @Shadow public static float moodAlpha;
    @Shadow public static float moodTextWidth;
    @Shadow public static float moodOffset;

    @ModifyVariable(
            method = "renderHud",
            at = @At(value = "STORE"),
            name = "oldMood"
    )
    private static float starexpress$getOldMood(float mood) {
        oldMood = mood;
        return mood;
    }

    @Inject(
            method = "renderHud",
            at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/client/gui/MoodRenderer;renderCivilian(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/client/gui/DrawContext;F)V"),
            cancellable = true
    )
    private static void starexpress$renderStarstruckMoodTextures(PlayerEntity player, TextRenderer textRenderer, DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (player == null) return;

        GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(player.getWorld());
        if (gameWorldComponent.getRole(player) == null) return;
        if (!gameWorldComponent.isRole(player, StarryExpressRoles.STARSTRUCK)) return;

        ci.cancel();
        StarstruckComponent starstruck = StarstruckComponent.KEY.get(player);
        Identifier mood;

        context.getMatrices().push();
        context.getMatrices().translate(0.0F, 3.0F * moodOffset, 0.0F);

        if (starstruck.ticks > 0) {
            mood = ABILITY_HAPPY;
            if (moodRender < 0.2F) {
                mood = ABILITY_DEPRESSIVE;
            } else if (moodRender < 0.55F) {
                mood = ABILITY_MID;
            }
        } else {
            mood = HAPPY;
            if (moodRender < 0.2F) {
                mood = DEPRESSIVE;
            } else if (moodRender < 0.55F) {
                mood = MID;
            }
        }

        if (arrowProgress < 0.1F) {
            if (oldMood >= 0.2F && moodRender < 0.2F) {
                arrowProgress = -1.0F;
            } else if (oldMood >= 0.55F && moodRender < 0.55F) {
                arrowProgress = -1.0F;
            }
        }

        context.drawGuiTexture(mood, 5, 6, 14, 17);
        if (Math.abs(arrowProgress) > 0.01F) {
            boolean up = arrowProgress > 0.0F;
            Identifier arrow = up ? ARROW_UP : ARROW_DOWN;
            context.getMatrices().push();
            if (!up) {
                context.getMatrices().translate(0.0F, 4.0F, 0.0F);
            }

            context.getMatrices().translate(0.0F, arrowProgress * 4.0F, 0.0F);
            context.drawSprite(7, 6, 0, 10, 13, MinecraftClient.getInstance().getGuiAtlasManager().getSprite(arrow), 1.0F, 1.0F, 1.0F, (float)Math.sin((double)Math.abs(arrowProgress) * Math.PI));
            context.getMatrices().pop();
        }
        if (starstruck.ticks > 0)
            context.drawGuiTexture(ABILITY_SPARKLES, 5, 6, 14, 17);
        else if (AbilityComponent.KEY.get(player).cooldown == 0)
            context.drawGuiTexture(SPARKLES, 5, 6, 14, 17);

        context.getMatrices().pop();
        context.getMatrices().push();
        context.getMatrices().translate(0.0F, 10.0F * moodOffset, 0.0F);
        MatrixStack pose = context.getMatrices();
        Objects.requireNonNull(textRenderer);
        pose.translate(26.0F, (float)(8 + 9), 0.0F);
        context.getMatrices().scale((moodTextWidth - 8.0F) * moodRender, 1.0F, 1.0F);
        context.fill(0, 0, 1, 1, StarryExpressUtil.lerpColor(0x271BAD, 0x6156E6, moodRender) | (int)(moodAlpha * 255.0F) << 24);
        context.getMatrices().pop();
    }
}
