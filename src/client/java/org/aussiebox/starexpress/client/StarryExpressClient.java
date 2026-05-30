package org.aussiebox.starexpress.client;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import io.wispforest.owo.config.ui.ConfigScreen;
import io.wispforest.owo.config.ui.ConfigScreenProviders;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceType;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.StarryExpressRoles;
import org.aussiebox.starexpress.block.ModBlocks;
import org.aussiebox.starexpress.block.entity.ModBlockEntities;
import org.aussiebox.starexpress.client.gui.screen.NewGuidebookScreen;
import org.aussiebox.starexpress.client.guidebook.GuidebookEntryCollector;
import org.aussiebox.starexpress.client.guidebook.variable.Variable;
import org.aussiebox.starexpress.client.guidebook.variable.VariableHandler;
import org.aussiebox.starexpress.client.particle.StarstruckSparkleParticle;
import org.aussiebox.starexpress.client.render.blockentity.PlushBlockEntityRenderer;
import org.aussiebox.starexpress.packet.AbilityC2SPacket;
import org.aussiebox.starexpress.packet.OpenConfigS2CPacket;
import org.lwjgl.glfw.GLFW;

public class StarryExpressClient implements ClientModInitializer {

    public static PlayerEntity target;
    public static KeyBinding abilityBind;

    public NewGuidebookScreen SCREEN_INSTANCE = new NewGuidebookScreen();

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ModBlocks.CIRCUITWEAVER_PLUSH);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ModBlocks.JADE_PLUSH);
        BlockEntityRendererFactories.register(ModBlockEntities.PLUSH, PlushBlockEntityRenderer::new);

        abilityBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + StarryExpress.MOD_ID + ".ability", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.wathe.keybinds"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            KeyBinding bind = getAbilityBind();
            if (bind == null) return;
            if (bind.isPressed()) {
                client.execute(() -> {
                    if (MinecraftClient.getInstance().player == null) return;
                    GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());

                    boolean sendPacket = false;
                    Role[] rolesWithAbility = new Role[] {
                            StarryExpressRoles.STARSTRUCK
                    };

                    for (Role role : rolesWithAbility) {
                        if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, role)) sendPacket = true;
                    }

                    if (!sendPacket) return;
                    ClientPlayNetworking.send(new AbilityC2SPacket());
                });
            }
        });

        PayloadTypeRegistry.playS2C().register(OpenConfigS2CPacket.TYPE, OpenConfigS2CPacket.CODEC);

        registerPackets();
        registerParticles();

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(GuidebookEntryCollector.INSTANCE);

        Variable<Integer> variable = VariableHandler.registerVariable(StarryExpress.id("testvar"), 0, (value -> {
            if (MinecraftClient.getInstance().player == null) return 0;
            if (MinecraftClient.getInstance().player.hasPermissionLevel(2)) return 1;
            return 0;
        }));
    }

    public void registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(OpenConfigS2CPacket.TYPE, (payload, context) -> {

            if (MinecraftClient.getInstance().player == null) return;

            ConfigScreen screen = (ConfigScreen) ConfigScreenProviders.get("starexpress");
            if (MinecraftClient.getInstance().player.hasPermissionLevel(2)) MinecraftClient.getInstance().setScreen(screen);

        });
    }

    public void registerParticles() {
        ParticleFactoryRegistry.getInstance().register(StarryExpress.STARSTRUCK_SPARKLE, StarstruckSparkleParticle.Provider::new);
    }

    public static KeyBinding getAbilityBind() {
        if (abilityBind == null) return null;
        KeyBinding bind = abilityBind;

        if (FabricLoader.getInstance().isModLoaded("noellesroles")) bind = NoellesrolesClient.abilityBind;

        if (bind == null) bind = abilityBind;
        return bind;
    }
}
