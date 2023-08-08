package net.lepinoid.cmdlib

import com.mojang.brigadier.context.CommandContext
import java.util.function.Supplier
import net.lepinoid.cmdlib.argument.ArgumentGetter
import net.lepinoid.cmdlib.argument.ScoreHoldersArgument
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import org.bukkit.World
import org.bukkit.entity.Player

@Suppress("MemberVisibilityCanBePrivate", "unused")
class CommandContext(private val context: CommandContext<CommandSourceStack>) {

    val source: CommandSourceStack = context.source
    val world: World get() = source.bukkitWorld!!
    val player: Player get() = source.playerOrException.bukkitEntity

    fun sendFeedback(message: Component, broadcastToOps: Boolean = false) = source.sendSuccess({ message }, broadcastToOps)

    fun sendError(message: Component) = source.sendFailure(message)

    operator fun <T> ArgumentGetter<T>.invoke(): T = factory(context)

    operator fun ScoreHoldersArgument.invoke(
        supplier: Supplier<Collection<String>> = Supplier { emptyList() },
    ): Collection<String> = factory(context, supplier)
}
