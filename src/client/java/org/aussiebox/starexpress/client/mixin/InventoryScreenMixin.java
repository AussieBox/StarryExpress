package org.aussiebox.starexpress.client.mixin;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.aussiebox.starexpress.client.gui.widget.GuidebookButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {
    public InventoryScreenMixin(PlayerScreenHandler abstractContainerMenu, PlayerInventory inventory, Text component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void renderGuidebookButton(CallbackInfo ci) {
        GuidebookButtonWidget child = new GuidebookButtonWidget(10, 10);
        this.addDrawableChild(child);
    }
}
