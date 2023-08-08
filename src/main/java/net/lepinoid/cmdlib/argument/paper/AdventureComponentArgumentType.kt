package net.lepinoid.cmdlib.argument.paper

import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.Component
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.MessageArgument

object AdventureComponentArgumentType {
    @JvmStatic
    fun component(): MessageArgument = MessageArgument.message()

    @JvmStatic
    fun getComponent(context: CommandContext<CommandSourceStack>, name: String): Component {
        val vanillaComponent = MessageArgument.getMessage(context, name)
        return PaperAdventure.asAdventure(vanillaComponent)
    }
}
