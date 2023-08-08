package net.lepinoid.cmdlib.argument.paper

import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.item.ItemArgument
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack

object BukkitItemStackArgumentType {
    @JvmStatic
    fun bukkitItem(registryAccess: CommandBuildContext): ItemArgument = ItemArgument.item(registryAccess)

    @JvmStatic
    fun getBukkitItem(context: CommandContext<CommandSourceStack>, name: String): ItemStack {
        val vanillaItem = ItemArgument.getItem(context, name).createItemStack(1, true)
        return CraftItemStack.asCraftMirror(vanillaItem)
    }
}
