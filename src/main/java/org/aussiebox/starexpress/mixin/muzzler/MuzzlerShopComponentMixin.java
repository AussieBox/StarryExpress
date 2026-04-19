package org.aussiebox.starexpress.mixin.muzzler;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.index.WatheSounds;
import dev.doctor4t.wathe.util.ShopEntry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.aussiebox.starexpress.StarryExpressConstants;
import org.aussiebox.starexpress.StarryExpressRoles;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerShopComponent.class)
public abstract class MuzzlerShopComponentMixin {
    @Shadow
    public int balance;

    @Shadow @Final
    private PlayerEntity player;

    @Shadow public abstract void sync();

    @Inject(method = "tryBuy", at = @At("HEAD"), cancellable = true)
    void tryBuy(int index, CallbackInfo ci) {
        GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(player.getWorld());
        if (gameWorldComponent.isRole(player, StarryExpressRoles.MUZZLER)) {

            if (index >= 0 && index < StarryExpressConstants.MUZZLER_SHOP.size()) {
                ShopEntry entry = StarryExpressConstants.MUZZLER_SHOP.get(index);
                if (FabricLoader.getInstance().isDevelopmentEnvironment() && this.balance < entry.price()) {
                    this.balance = entry.price() * 10;
                }

                if (this.balance >= entry.price() && !this.player.getItemCooldownManager().isCoolingDown(entry.stack().getItem()) && entry.onBuy(this.player)) {
                    this.balance -= entry.price();
                    PlayerEntity var6 = this.player;
                    if (var6 instanceof ServerPlayerEntity) {
                        ServerPlayerEntity player = (ServerPlayerEntity)var6;
                        player.networkHandler.sendPacket(new PlaySoundS2CPacket(Registries.SOUND_EVENT.getEntry(WatheSounds.UI_SHOP_BUY), SoundCategory.PLAYERS, player.getX(), player.getY(), player.getZ(), 1.0F, 0.9F + this.player.getRandom().nextFloat() * 0.2F, player.getRandom().nextLong()));
                    }
                } else {
                    this.player.sendMessage(Text.literal("Purchase Failed").formatted(Formatting.DARK_RED), true);
                    PlayerEntity var4 = this.player;
                    if (var4 instanceof ServerPlayerEntity) {
                        ServerPlayerEntity player = (ServerPlayerEntity)var4;
                        player.networkHandler.sendPacket(new PlaySoundS2CPacket(Registries.SOUND_EVENT.getEntry(WatheSounds.UI_SHOP_BUY_FAIL), SoundCategory.PLAYERS, player.getX(), player.getY(), player.getZ(), 1.0F, 0.9F + this.player.getRandom().nextFloat() * 0.2F, player.getRandom().nextLong()));
                    }
                }

                this.sync();
            }

            ci.cancel();
        }
    }

}
