package org.aussiebox.starexpress.item.custom;

import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.aussiebox.starexpress.ModSounds;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.cca.SilenceComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TapeItem extends Item {
    public TapeItem(net.minecraft.item.Item.Settings properties) {
        super(properties);
    }

    @Override
    public int getMaxCount() {
        return 1;
    }

    @Override
    public @NotNull ActionResult useOnEntity(@NotNull ItemStack itemStack, @NotNull PlayerEntity player, @NotNull LivingEntity livingEntity, @NotNull Hand interactionHand) {
        super.useOnEntity(itemStack, player, livingEntity, interactionHand);

        if (!(livingEntity instanceof PlayerEntity victim)) return ActionResult.FAIL;
        if (!GameFunctions.isPlayerAliveAndSurvival(victim)) return ActionResult.FAIL;
        if (player.getItemCooldownManager().isCoolingDown(itemStack.getItem())) return ActionResult.FAIL;

        SilenceComponent victimSilence = SilenceComponent.KEY.get(victim);

        if (victimSilence.isSilenced()) return ActionResult.FAIL;

        player.getInventory().removeOne(itemStack);
        player.getItemCooldownManager().set(itemStack.getItem(), StarryExpress.SERVER_CONFIG.muzzlerConfig.tapeCooldown() * 20);

        player.playSound(ModSounds.ITEM_TAPE_APPLY,1.0F, 1.0F);

        victimSilence.setSilenced(true);
        victimSilence.setSilencer(player.getUuid());
        victimSilence.sync();

        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(@NotNull ItemStack itemStack, @NotNull TooltipContext context, @NotNull List<Text> tooltip, @NotNull TooltipType type) {
        super.appendTooltip(itemStack, context, tooltip, type);
        tooltip.add(Text.translatable("item.starexpress.tape.tooltip.1").withColor(0xAAAAAA));
        tooltip.add(Text.translatable("item.starexpress.tape.tooltip.2").withColor(0xAAAAAA));
        tooltip.add(Text.translatable("item.starexpress.tape.tooltip.3").withColor(0xAAAAAA));
    }
}
