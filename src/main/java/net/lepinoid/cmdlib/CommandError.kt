package net.lepinoid.cmdlib

import com.mojang.brigadier.Message
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType

class CommandError(override val message: String) : CommandSyntaxException(SimpleCommandExceptionType { message }, Message { message })
