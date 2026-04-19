package org.aussiebox.starexpress.client.mixin;

import net.fabricmc.fabric.api.client.itemgroup.v1.FabricCreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.aussiebox.starexpress.client.gui.widget.GuidebookButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> implements FabricCreativeInventoryScreen {
    public CreativeModeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler abstractContainerMenu, PlayerInventory inventory, Text component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void renderGuidebookButton(CallbackInfo ci) {
        GuidebookButtonWidget child = new GuidebookButtonWidget(10, 10);
        this.addDrawableChild(child);
    }
}
