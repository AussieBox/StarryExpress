package org.aussiebox.starexpress.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.aussiebox.starexpress.StarryExpress;

public class StarryExpressUtil {

    public static int lerpColor(int color1, int color2, float delta) {
        delta = MathHelper.clamp(delta, 0.0F, 1.0F);

        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r = (int) MathHelper.lerp(delta, (float) r1, (float) r2);
        int g = (int) MathHelper.lerp(delta, (float) g1, (float) g2);
        int b = (int) MathHelper.lerp(delta, (float) b1, (float) b2);

        return (r << 16) | (g << 8) | b;
    }

    public static ItemStack parseItemStackFromJson(JsonElement jsonElement) {
        return ItemStack.VALIDATED_CODEC.parse(JsonOps.INSTANCE, jsonElement)
                .resultOrPartial(error -> StarryExpress.LOGGER.error("Failed to decode ItemStack: {}", error))
                .orElse(ItemStack.EMPTY);
    }

    public static BlockState parseBlockStateFromJson(JsonElement jsonElement) {
        return BlockState.CODEC.parse(JsonOps.INSTANCE, jsonElement)
                .resultOrPartial(error -> StarryExpress.LOGGER.error("Failed to decode BlockState: {}", error))
                .orElse(Blocks.AIR.getDefaultState());
    }

}
