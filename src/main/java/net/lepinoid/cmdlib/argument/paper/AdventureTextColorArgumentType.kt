package net.lepinoid.cmdlib.argument.paper

import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.format.TextColor
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.ColorArgument

object AdventureTextColorArgumentType {
    @JvmStatic
    fun color(): ColorArgument = ColorArgument.color()

    @JvmStatic
    fun getColor(context: CommandContext<CommandSourceStack>, name: String): TextColor {
        val vanillaFormatting = ColorArgument.getColor(context, name)
        return PaperAdventure.asAdventure(vanillaFormatting)
    }
}
