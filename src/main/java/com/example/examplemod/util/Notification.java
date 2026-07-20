package com.example.examplemod.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;

public class Notification {
    /*
    * Sends an in-game client notification to the player associated with the given context.
    */
    public static void SendMessageToPlayer(UseOnContext context, String message)
    {
        if (context == null || message == null || message.isEmpty()) {
            return;
        }

        Player player = context.getPlayer();
        if (player != null) {
            player.displayClientMessage(Component.literal(message), true);
        }
    }
}
