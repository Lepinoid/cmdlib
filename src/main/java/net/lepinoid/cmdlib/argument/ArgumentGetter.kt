package net.lepinoid.cmdlib.argument

import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack

class ArgumentGetter<T>(internal val factory: (CommandContext<CommandSourceStack>) -> T)
