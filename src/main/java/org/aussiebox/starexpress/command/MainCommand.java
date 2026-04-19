package org.aussiebox.starexpress.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.aussiebox.starexpress.cca.SilenceComponent;

public class MainCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("starexpress")
                        .then(CommandManager.literal("silenceme")
                                .requires(ctx -> ctx.hasPermissionLevel(2))
                                .executes(MainCommand::silenceMe)
                        )
        );
    }

    private static int silenceMe(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        ServerPlayerEntity player = source.getPlayer();

        SilenceComponent.KEY.get(player).setSilenced(true);
        SilenceComponent.KEY.get(player).setSilencer(player.getUuid());
        SilenceComponent.KEY.get(player).sync();

        return 1;
    }
}
