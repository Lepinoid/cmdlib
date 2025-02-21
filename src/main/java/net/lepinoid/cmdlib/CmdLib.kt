package net.lepinoid.cmdlib

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.data.registries.VanillaRegistries
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_20_R1.CraftServer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.Plugin

data class CmdLib(val plugin: Plugin) {

    init {
        plugin.server.pluginManager.registerEvents(PluginDisableListener(plugin), plugin)
    }

    fun register(name: String, child: CommandBuilder.() -> Unit) {
        val commandbuildcontext = Commands.createValidationContext(VanillaRegistries.createLookup())
        val builder = LiteralArgumentBuilder.literal<CommandSourceStack>(name)
        child(CommandBuilder(builder, commandbuildcontext))
        val node = dispatcher.register(builder as LiteralArgumentBuilder<CommandSourceStack>)
        registeredNodes.add(node)
    }

    companion object {
        internal val dispatcher = (Bukkit.getServer() as CraftServer).server.commands.dispatcher
        internal val registeredNodes = HashSet<LiteralCommandNode<*>>()
    }

    internal class PluginDisableListener(private val plugin: Plugin) : Listener {
        @EventHandler
        fun onReload(event: PluginDisableEvent) {
            if (event.plugin == plugin) {
                registeredNodes.forEach {
                    // FIXME
//                    dispatcher.root.removeCommand(it.literal)
                }
            }
        }
    }
}
