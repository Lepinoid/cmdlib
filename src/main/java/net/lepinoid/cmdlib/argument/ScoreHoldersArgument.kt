package net.lepinoid.cmdlib.argument

import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import java.util.function.Supplier

class ScoreHoldersArgument(
    internal val factory: (CommandContext<CommandSourceStack>, Supplier<Collection<String>>) -> Collection<String>,
)