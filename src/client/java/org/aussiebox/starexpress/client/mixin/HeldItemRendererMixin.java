package org.aussiebox.starexpress.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.aussiebox.starexpress.item.StarryExpressItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({HeldItemFeatureRenderer.class})
public class HeldItemRendererMixin {

    @WrapOperation(
            method = {"render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getMainHandStack()Lnet/minecraft/item/ItemStack;"
            )}
    )
    public ItemStack hideItemsInHand(LivingEntity instance, Operation<ItemStack> original) {
        ItemStack stack = original.call(instance);
        if (stack.isOf(StarryExpressItems.TAPE)) {
            stack = ItemStack.EMPTY;
        }

        return stack;
    }

}
