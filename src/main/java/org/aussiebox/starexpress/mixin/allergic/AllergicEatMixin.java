package org.aussiebox.starexpress.mixin.allergic;

import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import dev.doctor4t.wathe.item.CocktailItem;
import io.wispforest.owo.config.ConfigSynchronizer;
import io.wispforest.owo.config.Option;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.StarryExpressModifiers;
import org.aussiebox.starexpress.cca.AllergicComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(PlayerEntity.class)
public abstract class AllergicEatMixin extends LivingEntity {
    protected AllergicEatMixin(EntityType<? extends LivingEntity> entityType, World level) {
        super(entityType, level);
    }

    @Inject(
            method = {"eatFood(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/component/type/FoodComponent;)Lnet/minecraft/item/ItemStack;"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/HungerManager;eat(Lnet/minecraft/component/type/FoodComponent;)V",
                    shift = At.Shift.AFTER
            )}
    )
    private void allergicConsume(@NotNull World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {
        if (world.isClient) return;

        PlayerEntity player = (PlayerEntity) (Object) this;
        AllergicComponent allergy = AllergicComponent.KEY.get(player);

        if (!allergy.isAllergic()) return;
        if (Objects.equals(allergy.getAllergyType(), "food") && (stack.getItem() instanceof CocktailItem)) return;
        if (Objects.equals(allergy.getAllergyType(), "drink") && !(stack.getItem() instanceof CocktailItem)) return;

        List<String> effectList = new ArrayList<>();
        effectList.addAll(Collections.nCopies(StarryExpress.SERVER_CONFIG.allergicConfig.nothingChance(), "nothing"));
        effectList.addAll(Collections.nCopies(StarryExpress.SERVER_CONFIG.allergicConfig.instinctChance(), "instinct"));
        effectList.addAll(Collections.nCopies(StarryExpress.SERVER_CONFIG.allergicConfig.armorChance(), "armor"));
        effectList.addAll(Collections.nCopies(StarryExpress.SERVER_CONFIG.allergicConfig.poisonChance(), "poison"));

        Collections.shuffle(effectList);
        String effect = effectList.getFirst();

        Map<Option.Key, ?> config = ConfigSynchronizer.getClientOptions((ServerPlayerEntity) player, StarryExpress.CLIENT_CONFIG);
        if (config != null && config.get(new Option.Key("allergicConfig.noPoison")) instanceof Boolean bool && bool) effect = "poison";


        if (Objects.equals(effect, "poison")) {
            int poisonTicks = PlayerPoisonComponent.KEY.get(player).poisonTicks;
            if (poisonTicks == -1) {
                PlayerPoisonComponent.KEY.get(player).setPoisonTicks(world.getRandom().nextBetween(PlayerPoisonComponent.clampTime.getLeft(), PlayerPoisonComponent.clampTime.getRight()), player.getUuid());
            } else {
                PlayerPoisonComponent.KEY.get(player).setPoisonTicks(MathHelper.clamp(poisonTicks - world.getRandom().nextBetween(100, 300), 0, PlayerPoisonComponent.clampTime.getRight()), player.getUuid());
            }

            if (config != null && config.get(new Option.Key("allergicConfig.noPoison")) instanceof Boolean bool && bool)
                player.sendMessage(
                        Text.translatable(
                                "hud.allergic.effect.forced_poison"
                        ).withColor(StarryExpressModifiers.ALLERGIC.color()),
                        true
                );
            else
                player.sendMessage(
                        Text.translatable(
                                "hud.allergic.effect.poison"
                        ).withColor(StarryExpressModifiers.ALLERGIC.color()),
                        true
                );
        }
        if (Objects.equals(effect, "instinct")) {
            allergy.setGlowTicks(StarryExpress.SERVER_CONFIG.allergicConfig.instinctDuration() * 20);
            allergy.sync();

            player.sendMessage(
                    Text.translatable(
                            "hud.allergic.effect.instinct"
                    ).withColor(StarryExpressModifiers.ALLERGIC.color()),
                    true
            );
        }
        if (Objects.equals(effect, "armor")) {
            allergy.giveArmor();

            player.sendMessage(
                    Text.translatable(
                            "hud.allergic.effect.armor"
                    ).withColor(StarryExpressModifiers.ALLERGIC.color()),
                    true
            );
        }
    }
}
