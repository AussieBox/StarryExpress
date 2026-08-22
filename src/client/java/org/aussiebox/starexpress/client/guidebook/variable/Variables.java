package org.aussiebox.starexpress.client.guidebook.variable;

import dev.doctor4t.wathe.Wathe;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import org.agmas.harpymodloader.config.HarpyModLoaderConfig;
import org.agmas.harpymodloader.modifiers.HMLModifiers;
import org.agmas.harpymodloader.modifiers.Modifier;
import org.aussiebox.starexpress.StarryExpress;

public class Variables {
    public static Variable<Boolean> isOperator = VariableHandler.registerVariable(StarryExpress.id("is_operator"), false, (value -> {
        if (MinecraftClient.getInstance().player == null) return false;
        return MinecraftClient.getInstance().player.hasPermissionLevel(2);
    }));
    public static Variable<Boolean> isAlive = VariableHandler.registerVariable(StarryExpress.id("is_alive"), false, (value -> WatheClient.isPlayerAliveAndInSurvival()));
    public static Variable<Boolean> isKiller = VariableHandler.registerVariable(StarryExpress.id("is_killer"), false, (value -> WatheClient.isKiller()));
    public static Variable<Boolean> isSpectating = VariableHandler.registerVariable(StarryExpress.id("is_spectating"), false, (value -> WatheClient.isPlayerSpectatingOrCreative()));
    public static Variable<Boolean> isRatSupporter = VariableHandler.registerVariable(StarryExpress.id("is_rat_supporter"), false, (value -> {
        if (MinecraftClient.getInstance().player == null) return false;
        return Wathe.isSupporter(MinecraftClient.getInstance().player);
    }));

    public static class StarstruckConfig {
        public static Variable<Boolean> taskReducesCooldown = VariableHandler.registerVariable(StarryExpress.id("starstruck.task_reduces_cooldown"), false, (value -> StarryExpress.SERVER_CONFIG.starstruckConfig.taskReducesCooldown()));
        public static Variable<Integer> taskCooldownReduction = VariableHandler.registerVariable(StarryExpress.id("starstruck.task_cooldown_reduction"), 0, (value -> StarryExpress.SERVER_CONFIG.starstruckConfig.taskCooldownReduction()));
        public static Variable<Integer> abilityCooldown = VariableHandler.registerVariable(StarryExpress.id("starstruck.ability_cooldown"), 0, (value -> StarryExpress.SERVER_CONFIG.starstruckConfig.abilityCooldown()));
        public static Variable<Integer> abilityDuration = VariableHandler.registerVariable(StarryExpress.id("starstruck.ability_duration"), 0, (value -> StarryExpress.SERVER_CONFIG.starstruckConfig.abilityDuration()));
        public static Variable<Boolean> abilityAffectsMovementSpeed = VariableHandler.registerVariable(StarryExpress.id("starstruck.ability_affects_movement_speed"), false, (value -> StarryExpress.SERVER_CONFIG.starstruckConfig.abilityAffectsMovementSpeed()));
        public static Variable<Float> abilityWalkSpeed = VariableHandler.registerVariable(StarryExpress.id("starstruck.ability_walk_speed"), 0F, (value -> StarryExpress.SERVER_CONFIG.starstruckConfig.abilityWalkSpeed()));
        public static Variable<Float> abilitySprintSpeed = VariableHandler.registerVariable(StarryExpress.id("starstruck.ability_sprint_speed"), 0F, (value -> StarryExpress.SERVER_CONFIG.starstruckConfig.abilitySprintSpeed()));

        public static void init() {

        }
    }

    public static class AllergicConfig {
        public static Variable<Integer> nothingChance = VariableHandler.registerVariable(StarryExpress.id("allergic.chance.nothing"), 0, (value -> StarryExpress.SERVER_CONFIG.allergicConfig.nothingChance()));
        public static Variable<Integer> armorChance = VariableHandler.registerVariable(StarryExpress.id("allergic.chance.armor"), 0, (value -> StarryExpress.SERVER_CONFIG.allergicConfig.armorChance()));
        public static Variable<Integer> instinctChance = VariableHandler.registerVariable(StarryExpress.id("allergic.chance.instinct"), 0, (value -> StarryExpress.SERVER_CONFIG.allergicConfig.instinctChance()));
        public static Variable<Integer> poisonChance = VariableHandler.registerVariable(StarryExpress.id("allergic.chance.poison"), 0, (value -> StarryExpress.SERVER_CONFIG.allergicConfig.poisonChance()));
        public static Variable<Integer> totalChance = VariableHandler.registerVariable(StarryExpress.id("allergic.chance.total"), 0, (value -> StarryExpress.SERVER_CONFIG.allergicConfig.nothingChance() + StarryExpress.SERVER_CONFIG.allergicConfig.armorChance() + StarryExpress.SERVER_CONFIG.allergicConfig.instinctChance() + StarryExpress.SERVER_CONFIG.allergicConfig.poisonChance()));
        public static Variable<Integer> instinctDuration = VariableHandler.registerVariable(StarryExpress.id("allergic.instinct_duration"), 0, (value -> StarryExpress.SERVER_CONFIG.allergicConfig.instinctDuration()));

        public static Variable<Boolean> noPoison = VariableHandler.registerVariable(StarryExpress.id("allergic.no_poision"), false, (value -> StarryExpress.CLIENT_CONFIG.allergicConfig.noPoison()));

        public static void init() {

        }
    }

    public static class MuzzlerConfig {
        public static Variable<Integer> displaySilencedTipDelay = VariableHandler.registerVariable(StarryExpress.id("muzzler.display_silenced_tip_delay"), 0, (value -> StarryExpress.SERVER_CONFIG.muzzlerConfig.displaySilencedTipDelay()));
        public static Variable<Integer> suffocationTime = VariableHandler.registerVariable(StarryExpress.id("muzzler.suffocation_time"), 0, (value -> StarryExpress.SERVER_CONFIG.muzzlerConfig.suffocationTime()));
        public static Variable<Integer> tapeCooldown = VariableHandler.registerVariable(StarryExpress.id("muzzler.tape_cooldown"), 0, (value -> StarryExpress.SERVER_CONFIG.muzzlerConfig.tapeCooldown()));
        public static Variable<Integer> tapeTearCheckCount = VariableHandler.registerVariable(StarryExpress.id("muzzler.tape_tear_check_count"), 0, (value -> StarryExpress.SERVER_CONFIG.muzzlerConfig.tapeTearCheckCount()));
        public static Variable<Boolean> killIfCheckedAtZero = VariableHandler.registerVariable(StarryExpress.id("muzzler.kill_if_checked_at_zero"), true, (value -> StarryExpress.SERVER_CONFIG.muzzlerConfig.killIfCheckedAtZero()));
        public static Variable<Float> tapeTearMoodChange = VariableHandler.registerVariable(StarryExpress.id("muzzler.tape_tear_mood_change"), 0F, (value -> StarryExpress.SERVER_CONFIG.muzzlerConfig.tapeTearMoodChange()));

        public static void init() {

        }
    }

    public static void init() {
        StarstruckConfig.init();
        AllergicConfig.init();
        MuzzlerConfig.init();

        // Register "enabled" variables for each role
        for (Role role : WatheRoles.ROLES) VariableHandler.registerVariable(role.identifier().withSuffixedPath(".enabled"), true, (value -> !HarpyModLoaderConfig.HANDLER.instance().disabled.contains(role.identifier().toString())));
        for (Modifier modifier : HMLModifiers.MODIFIERS) VariableHandler.registerVariable(modifier.identifier().withSuffixedPath(".enabled"), true, (value -> !HarpyModLoaderConfig.HANDLER.instance().disabledModifiers.contains(modifier.identifier().toString())));
    }
}
