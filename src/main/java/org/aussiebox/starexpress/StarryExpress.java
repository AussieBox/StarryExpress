package org.aussiebox.starexpress;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.aussiebox.starexpress.block.ModBlocks;
import org.aussiebox.starexpress.block.entity.ModBlockEntities;
import org.aussiebox.starexpress.cca.AbilityComponent;
import org.aussiebox.starexpress.cca.SilenceComponent;
import org.aussiebox.starexpress.cca.StarstruckComponent;
import org.aussiebox.starexpress.config.StarryExpressClientConfig;
import org.aussiebox.starexpress.config.StarryExpressServerConfig;
import org.aussiebox.starexpress.item.StarryExpressItems;
import org.aussiebox.starexpress.packet.AbilityC2SPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StarryExpress implements ModInitializer {

    public static String MOD_ID = "starexpress";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final StarryExpressServerConfig SERVER_CONFIG = StarryExpressServerConfig.createAndLoad();
    public static final StarryExpressClientConfig CLIENT_CONFIG = StarryExpressClientConfig.createAndLoad();

    public static final SimpleParticleType STARSTRUCK_SPARKLE = FabricParticleTypes.simple();

    @Override
    public void onInitialize() {
        ModSounds.init();
        ModBlockEntities.init();
        ModBlocks.init();
        StarryExpressItems.init();

        StarryExpressCommands.init();
        StarryExpressRoles.init();
        StarryExpressModifiers.init();

        PayloadTypeRegistry.playC2S().register(AbilityC2SPacket.TYPE, AbilityC2SPacket.CODEC);

        registerPackets();
        registerEvents();
        registerParticles();
    }

    public void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(AbilityC2SPacket.TYPE, (payload, context) -> {
            AbilityComponent abilityComponent = AbilityComponent.KEY.get(context.player());
            GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(context.player().getWorld());

            if (!GameFunctions.isPlayerAliveAndSurvival(context.player())) return;

            if (gameWorldComponent.isRole(context.player(), StarryExpressRoles.STARSTRUCK) && abilityComponent.cooldown <= 0) {
                abilityComponent.setCooldown(SERVER_CONFIG.starstruckConfig.abilityCooldown() * 20);
                StarstruckComponent.KEY.get(context.player()).setTicks(SERVER_CONFIG.starstruckConfig.abilityDuration() * 20);

                ServerWorld level = context.player().getServerWorld();
                level.playSound(null, BlockPos.ofFloored(context.player().getPos()), SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                level.spawnParticles(STARSTRUCK_SPARKLE, context.player().getX(), context.player().getY(), context.player().getZ(), 75,  0.5,  1.5,  0.5,  0.0);
            }

        });
    }

    public void registerEvents() {

        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {

            if (!(entity instanceof PlayerEntity victim)) return ActionResult.PASS;
            if (SERVER_CONFIG.muzzlerConfig.tapeTearCheckCount() == 0) return ActionResult.PASS;

            if (!player.getMainHandStack().isOf(StarryExpressItems.TAPE)) {
                SilenceComponent victimSilence = SilenceComponent.KEY.get(victim);
                if (!victimSilence.isSilenced()) return ActionResult.PASS;
                if (SilenceComponent.KEY.get(player).isSilenced()) return ActionResult.PASS;

                victimSilence.setTearChecks(victimSilence.getTearChecks() + 1);
                victim.getWorld().playSound(null, victim.getX(), victim.getY(), victim.getZ(), ModSounds.ITEM_TAPE_APPLY, SoundCategory.PLAYERS, 1.0F, 2.0F);

                if (victimSilence.getTearChecks() >= SERVER_CONFIG.muzzlerConfig.tapeTearCheckCount()) victimSilence.setSilenced(false);

                victimSilence.sync();

                PlayerMoodComponent victimMood = PlayerMoodComponent.KEY.get(victim);

                victimMood.setMood(victimMood.getMood() - SERVER_CONFIG.muzzlerConfig.tapeTearMoodChange());
                victimMood.sync();

                if (victimMood.getMood() <= 0.0F && SERVER_CONFIG.muzzlerConfig.killIfCheckedAtZero()) {
                    GameFunctions.killPlayer(victim, true, victim.getWorld().getPlayerByUuid(victimSilence.getSilencer()), StarryExpressConstants.SILENCED_TAPE_REMOVED_DEATH_REASON);
                }

                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });

    }

    public void registerParticles() {
        Registry.register(Registries.PARTICLE_TYPE, id("starstruck_sparkle"), STARSTRUCK_SPARKLE);
    }

    public static Identifier id(String key) {
        return Identifier.of(MOD_ID, key);
    }

}
