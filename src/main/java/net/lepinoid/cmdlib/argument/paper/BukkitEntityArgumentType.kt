package net.lepinoid.cmdlib.argument.paper

import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.EntityArgument
import org.bukkit.entity.Player

object BukkitEntityArgumentType {
        @JvmStatic
        fun bukkitPlayer(): EntityArgument = EntityArgument.player()

        @JvmStatic
        fun getBukkitPlayer(context: CommandContext<CommandSourceStack>, name: String): Player =
            EntityArgument.getPlayer(context, name).bukkitEntity

        @JvmStatic
        fun bukkitPlayers(): EntityArgument = EntityArgument.players()

        @JvmStatic
        fun getBukkitPlayers(context: CommandContext<CommandSourceStack>, name: String): Collection<Player> =
            EntityArgument.getPlayers(context, name).map { it.bukkitEntity }

        @JvmStatic
        fun getOptionalBukkitPlayers(context: CommandContext<CommandSourceStack>, name: String): Collection<Player> =
            EntityArgument.getOptionalPlayers(context, name).map { it.bukkitEntity }
}