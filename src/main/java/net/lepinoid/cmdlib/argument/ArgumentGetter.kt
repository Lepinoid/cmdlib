package net.lepinoid.cmdlib.argument

import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSource
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.SharedSuggestionProvider

class ArgumentGetter<T>(internal val factory: (CommandContext<CommandSourceStack>) -> T)