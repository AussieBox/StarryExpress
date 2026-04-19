package org.aussiebox.starexpress.client.mixin;

import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedHandledScreen;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.aussiebox.starexpress.client.gui.widget.GuidebookButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LimitedInventoryScreen.class)
public abstract class LimitedInventoryScreenMixin extends LimitedHandledScreen<PlayerScreenHandler> {
    public LimitedInventoryScreenMixin(PlayerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    void renderGuidebookButton(CallbackInfo ci) {
        GuidebookButtonWidget child = new GuidebookButtonWidget(10, 10);
        this.addDrawableChild(child);
    }
}
