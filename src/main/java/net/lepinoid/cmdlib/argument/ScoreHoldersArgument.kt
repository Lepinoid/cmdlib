package net.lepinoid.cmdlib.argument

import com.mojang.brigadier.context.CommandContext
import java.util.function.Supplier
import net.minecraft.commands.CommandSourceStack

class ScoreHoldersArgument(
    internal val factory: (CommandContext<CommandSourceStack>, Supplier<Collection<String>>) -> Collection<String>,
)
