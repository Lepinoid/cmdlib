package net.lepinoid.cmdlib

import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.context.CommandContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.minecraft.commands.CommandListenerWrapper
import net.minecraft.commands.arguments.ArgumentChat
import net.minecraft.commands.arguments.ArgumentEntity
import net.minecraft.commands.arguments.ArgumentUUID
import net.minecraft.commands.arguments.coordinates.ArgumentPosition
import net.minecraft.commands.arguments.coordinates.ArgumentVec3
import net.minecraft.commands.arguments.item.ArgumentItemStack
import net.minecraft.network.chat.IChatBaseComponent
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*

class Context internal constructor(private val ctx: CommandContext<CommandListenerWrapper>) {

    private val source = ctx.source
    val sender: CommandSender get() = source.bukkitSender
    val player: Player get() = source.h().bukkitEntity

    fun getBoolean(name: String): Boolean = BoolArgumentType.getBool(ctx, name)
    fun getDouble(name: String): Double = DoubleArgumentType.getDouble(ctx, name)
    fun getFloat(name: String): Float = FloatArgumentType.getFloat(ctx, name)
    fun getInteger(name: String): Int = IntegerArgumentType.getInteger(ctx, name)
    fun getLong(name: String): Long = LongArgumentType.getLong(ctx, name)
    fun getString(name: String): String = StringArgumentType.getString(ctx, name)
    fun getBlockPos(name: String): Vector = ArgumentPosition.a(ctx, name).let { Vector(it.x, it.y, it.z) }
    fun getEntity(name: String): Entity = ArgumentEntity.a(ctx, name).bukkitEntity
    fun getEntities(name: String): List<Entity> = ArgumentEntity.b(ctx, name).map { it.bukkitEntity }
    fun getPlayer(name: String): Player = ArgumentEntity.e(ctx, name).bukkitEntity
    fun getPlayers(name: String): List<Player> = ArgumentEntity.f(ctx, name).map { it.bukkitEntity }
    fun getItemStack(name: String): ItemStack = CraftItemStack.asBukkitCopy(ArgumentItemStack.a(ctx, name).a(1, false))
    fun getUUID(name: String): UUID = ArgumentUUID.a(ctx, name)
    fun getVector(name: String): Vector = ArgumentVec3.a(ctx, name).let { Vector(it.x, it.y, it.z) }
    fun getMessage(name: String): Component = GsonComponentSerializer.gson().deserialize(
        IChatBaseComponent.ChatSerializer.a(
            ArgumentChat.a(ctx, name)
        )
    )
}