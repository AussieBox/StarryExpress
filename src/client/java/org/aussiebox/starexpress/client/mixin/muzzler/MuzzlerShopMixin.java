package org.aussiebox.starexpress.client.mixin.muzzler;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.world.World;
import org.aussiebox.starexpress.StarryExpressConstants;
import org.aussiebox.starexpress.StarryExpressRoles;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(LimitedInventoryScreen.class)
public class MuzzlerShopMixin {

    @Shadow @Final public ClientPlayerEntity player;

    @ModifyVariable(
            method = "init",
            at = @At(
                    value = "STORE",
                    target = "Ljava/util/List;"
            ),
            name = "entries")
    private List<ShopEntry> modifyMuzzlerShop(List<ShopEntry> shopEntries) {
        World level = this.player.getWorld();
        GameWorldComponent game = GameWorldComponent.KEY.get(level);

        if (game.isRole(this.player, StarryExpressRoles.MUZZLER)) {
            shopEntries = StarryExpressConstants.MUZZLER_SHOP;
        }

        return shopEntries;
    }

}
