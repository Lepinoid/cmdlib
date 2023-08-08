package net.lepinoid.cmdlib.argument.paper

import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.Component
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.ComponentArgument

object AdventureTextComponentArgumentType {
    @JvmStatic
    fun adventureText(): ComponentArgument = ComponentArgument.textComponent()

    @JvmStatic
    fun getAdventureTextArgument(context: CommandContext<CommandSourceStack>, name: String): Component {
        val vanillaComponent = ComponentArgument.getComponent(context, name)
        return PaperAdventure.asAdventure(vanillaComponent)
    }
}
