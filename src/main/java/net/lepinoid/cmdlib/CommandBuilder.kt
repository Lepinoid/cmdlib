package net.lepinoid.cmdlib

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource as Source
import com.mojang.authlib.GameProfile
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.datafixers.util.Either
import java.util.EnumSet
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.function.Predicate
import java.util.function.Supplier
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.reflect
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.lepinoid.cmdlib.argument.ArgumentGetter
import net.lepinoid.cmdlib.argument.ScoreHoldersArgument
import net.lepinoid.cmdlib.argument.paper.AdventureComponentArgumentType
import net.lepinoid.cmdlib.argument.paper.AdventureTextColorArgumentType
import net.lepinoid.cmdlib.argument.paper.AdventureTextComponentArgumentType
import net.lepinoid.cmdlib.argument.paper.BukkitEntityArgumentType
import net.lepinoid.cmdlib.argument.paper.BukkitItemStackArgumentType
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandFunction
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.AngleArgument
import net.minecraft.commands.arguments.CompoundTagArgument
import net.minecraft.commands.arguments.DimensionArgument
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.GameProfileArgument
import net.minecraft.commands.arguments.NbtPathArgument
import net.minecraft.commands.arguments.NbtTagArgument
import net.minecraft.commands.arguments.ObjectiveArgument
import net.minecraft.commands.arguments.ObjectiveCriteriaArgument
import net.minecraft.commands.arguments.OperationArgument
import net.minecraft.commands.arguments.ParticleArgument
import net.minecraft.commands.arguments.ResourceArgument
import net.minecraft.commands.arguments.ResourceLocationArgument
import net.minecraft.commands.arguments.ScoreHolderArgument
import net.minecraft.commands.arguments.ScoreboardSlotArgument
import net.minecraft.commands.arguments.SlotArgument
import net.minecraft.commands.arguments.TeamArgument
import net.minecraft.commands.arguments.UuidArgument
import net.minecraft.commands.arguments.blocks.BlockInput
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument
import net.minecraft.commands.arguments.blocks.BlockStateArgument
import net.minecraft.commands.arguments.coordinates.BlockPosArgument
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument
import net.minecraft.commands.arguments.coordinates.Coordinates
import net.minecraft.commands.arguments.coordinates.RotationArgument
import net.minecraft.commands.arguments.coordinates.SwizzleArgument
import net.minecraft.commands.arguments.coordinates.Vec2Argument
import net.minecraft.commands.arguments.coordinates.Vec3Argument
import net.minecraft.commands.arguments.item.FunctionArgument
import net.minecraft.commands.arguments.item.ItemPredicateArgument
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ColumnPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.block.state.pattern.BlockInWorld
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import net.minecraft.world.scores.Objective
import net.minecraft.world.scores.PlayerTeam
import net.minecraft.world.scores.criteria.ObjectiveCriteria
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

@Suppress("UNUSED")
class CommandBuilder(private val builder: ArgumentBuilder<CommandSourceStack, *>, private val registryAccess: CommandBuildContext) {
    internal var filter: ((CommandSourceStack) -> Boolean)? = null
        private set
    internal var aliases: List<String>? = null
        private set
    internal var executes: (CommandContext.() -> Unit)? = null
        private set

    fun requires(permission: String) {
        val requirement: (Source) -> Boolean = { it.bukkitSender.hasPermission(permission) }
        builder.requires { requirement(it) }
        filter = requirement
    }

    fun requires(opLevel: Int) {
        val requirement: (CommandSourceStack) -> Boolean = { it.hasPermission(opLevel) }
        builder.requires { requirement(it) }
        filter = requirement
    }

    fun requires(filter: (CommandSender) -> Boolean) {
        val requirement: (CommandSourceStack) -> Boolean = { filter(it.bukkitSender) }
        builder.requires { requirement(it) }
        this.filter = requirement
    }

    fun aliases(vararg aliases: String) {
        this.aliases = listOf(*aliases)
    }

    fun literal(literal: String, child: Child) {
        val arg = literal<CommandSourceStack>(literal)
        child(CommandBuilder(arg, registryAccess))
        builder.then(arg)
    }

    fun angle(child: CommandBuilder.(angle: ArgumentGetter<Float>) -> Unit) =
        next(child, AngleArgument::angle, AngleArgument::getAngle)

    fun blockPos(child: CommandBuilder.(blockPos: ArgumentGetter<BlockPos>) -> Unit) =
        next(child, BlockPosArgument::blockPos, BlockPosArgument::getLoadedBlockPos)

    fun blockPredicate(child: CommandBuilder.(blockPredicate: ArgumentGetter<Predicate<BlockInWorld>>) -> Unit) =
        next(child, { BlockPredicateArgument.blockPredicate(registryAccess) }, BlockPredicateArgument::getBlockPredicate)

    fun blockStateArg(child: CommandBuilder.(blockState: ArgumentGetter<BlockInput>) -> Unit) =
        next(child, { BlockStateArgument.block(registryAccess) }, BlockStateArgument::getBlock)

    fun boolean(child: CommandBuilder.(angle: ArgumentGetter<Boolean>) -> Unit) =
        next(child, BoolArgumentType::bool, BoolArgumentType::getBool)

    fun color(child: CommandBuilder.(color: ArgumentGetter<TextColor>) -> Unit) =
        next(child, AdventureTextColorArgumentType::color, AdventureTextColorArgumentType::getColor)

    fun columnPos(child: CommandBuilder.(columnPos: ArgumentGetter<ColumnPos>) -> Unit) =
        next(child, ColumnPosArgument::columnPos, ColumnPosArgument::getColumnPos)

    fun nbtCompound(child: CommandBuilder.(nbtCompound: ArgumentGetter<CompoundTag>) -> Unit) =
        next(child, CompoundTagArgument::compoundTag, CompoundTagArgument::getCompoundTag)

    fun scoreboardCriteria(child: CommandBuilder.(scoreboardCriteria: ArgumentGetter<ObjectiveCriteria>) -> Unit) =
        next(child, ObjectiveCriteriaArgument::criteria, ObjectiveCriteriaArgument::getCriteria)

    fun dimension(child: CommandBuilder.(world: ArgumentGetter<ServerLevel>) -> Unit) =
        next(child, DimensionArgument::dimension, DimensionArgument::getDimension)

    fun double(
        min: Double = -1.7976931348623157E308,
        max: Double = 1.7976931348623157E308,
        child: CommandBuilder.(double: ArgumentGetter<Double>) -> Unit,
    ) = next(child, { DoubleArgumentType.doubleArg(min, max) }, DoubleArgumentType::getDouble)

    fun enchantment(child: CommandBuilder.(enchantment: ArgumentGetter<Enchantment>) -> Unit) =
        next(child, { ResourceArgument.resource(registryAccess, Registries.ENCHANTMENT) }) { ctx, name ->
            ResourceArgument.getEnchantment(ctx, name).value()
        }

    fun entities(child: CommandBuilder.(entities: ArgumentGetter<Collection<net.minecraft.world.entity.Entity>>) -> Unit) =
        next(child, EntityArgument::entities, EntityArgument::getEntities)

    fun entity(child: CommandBuilder.(entity: ArgumentGetter<net.minecraft.world.entity.Entity>) -> Unit) =
        next(child, EntityArgument::entity, EntityArgument::getEntity)

    fun entityAnchor(child: CommandBuilder.(entityAnchor: ArgumentGetter<EntityAnchorArgument.Anchor>) -> Unit) =
        next(child, EntityAnchorArgument::anchor, EntityAnchorArgument::getAnchor)

    fun entitySummon(child: CommandBuilder.(entityId: ArgumentGetter<EntityType>) -> Unit) =
        next(child, { ResourceArgument.resource(registryAccess, Registries.ENTITY_TYPE) }) { ctx, name ->
            ResourceArgument.getEntityType(ctx, name).value().let { EntityType.fromName(it.descriptionId)!! }
        }

    fun float(min: Float = -3.4028235E38f, max: Float = 3.4028235E38f, child: CommandBuilder.(float: ArgumentGetter<Float>) -> Unit) =
        next(child, { FloatArgumentType.floatArg(min, max) }, FloatArgumentType::getFloat)

    fun functionOrTag(child: CommandBuilder.(pair: ArgumentGetter<com.mojang.datafixers.util.Pair<ResourceLocation, Either<CommandFunction, Collection<CommandFunction>>>>) -> Unit) =
        next(child, FunctionArgument::functions, FunctionArgument::getFunctionOrTag)

    fun functions(child: CommandBuilder.(functions: ArgumentGetter<Collection<CommandFunction>>) -> Unit) =
        next(child, FunctionArgument::functions, FunctionArgument::getFunctions)

    fun gameProfile(child: CommandBuilder.(gameProfile: ArgumentGetter<Collection<GameProfile>>) -> Unit) =
        next(child, GameProfileArgument::gameProfile, GameProfileArgument::getGameProfiles)

    fun greedyString(child: CommandBuilder.(greedyString: ArgumentGetter<String>) -> Unit) =
        next(child, StringArgumentType::greedyString, StringArgumentType::getString)

    fun identifier(child: CommandBuilder.(identifier: ArgumentGetter<ResourceLocation>) -> Unit) =
        next(child, ResourceLocationArgument::id, ResourceLocationArgument::getId)

    fun integer(min: Int = -2147483648, max: Int = 2147483647, child: CommandBuilder.(integer: ArgumentGetter<Int>) -> Unit) =
        next(child, { IntegerArgumentType.integer(min, max) }, IntegerArgumentType::getInteger)

    fun itemPredicate(child: CommandBuilder.(itemPredicate: ArgumentGetter<Predicate<ItemStack>>) -> Unit) =
        next(child, { ItemPredicateArgument.itemPredicate(registryAccess) }, ItemPredicateArgument::getItemPredicate)

    fun itemSlot(child: CommandBuilder.(itemSlot: ArgumentGetter<Int>) -> Unit) =
        next(child, SlotArgument::slot, SlotArgument::getSlot)

    fun itemStack(child: CommandBuilder.(itemStack: ArgumentGetter<org.bukkit.inventory.ItemStack>) -> Unit) =
        next(child, { BukkitItemStackArgumentType.bukkitItem(registryAccess) }, BukkitItemStackArgumentType::getBukkitItem)

    fun long(
        min: Long = -9223372036854775807L,
        max: Long = 9223372036854775807L,
        child: CommandBuilder.(long: ArgumentGetter<Long>) -> Unit,
    ) = next(child, { LongArgumentType.longArg(min, max) }, LongArgumentType::getLong)

    fun message(child: CommandBuilder.(message: ArgumentGetter<Component>) -> Unit) =
        next(child, AdventureComponentArgumentType::component, AdventureComponentArgumentType::getComponent)

    fun statusEffect(child: CommandBuilder.(statusEffect: ArgumentGetter<MobEffect>) -> Unit) =
        next(child, { ResourceArgument.resource(registryAccess, Registries.MOB_EFFECT) }) { ctx, name ->
            ResourceArgument.getMobEffect(ctx, name).value()
        }

    fun nbtPath(child: CommandBuilder.(nbtPath: ArgumentGetter<NbtPathArgument.NbtPath>) -> Unit) =
        next(child, NbtPathArgument::nbtPath, NbtPathArgument::getPath)

    fun scoreboardObjective(child: CommandBuilder.(scoreboardObjective: ArgumentGetter<Objective>) -> Unit) =
        next(child, ObjectiveArgument::objective, ObjectiveArgument::getObjective)

    fun scoreboardWritableObjective(child: CommandBuilder.(scoreboardWritableObjective: ArgumentGetter<Objective>) -> Unit) =
        next(child, ObjectiveArgument::objective, ObjectiveArgument::getWritableObjective)

    fun operation(child: CommandBuilder.(operation: ArgumentGetter<OperationArgument.Operation>) -> Unit) =
        next(child, OperationArgument::operation, OperationArgument::getOperation)

    fun particleEffect(child: CommandBuilder.(particleEffect: ArgumentGetter<ParticleOptions>) -> Unit) =
        next(child, { ParticleArgument.particle(registryAccess) }, ParticleArgument::getParticle)

    fun player(child: CommandBuilder.(player: ArgumentGetter<Player>) -> Unit) =
        next(child, BukkitEntityArgumentType::bukkitPlayer, BukkitEntityArgumentType::getBukkitPlayer)

    fun players(child: CommandBuilder.(players: ArgumentGetter<Collection<Player>>) -> Unit) =
        next(child, BukkitEntityArgumentType::bukkitPlayers, BukkitEntityArgumentType::getBukkitPlayers)

    fun optionalPlayers(child: CommandBuilder.(players: ArgumentGetter<Collection<Player>>) -> Unit) =
        next(child, BukkitEntityArgumentType::bukkitPlayers, BukkitEntityArgumentType::getOptionalBukkitPlayers)

    fun rotation(child: CommandBuilder.(rotation: ArgumentGetter<Coordinates>) -> Unit) =
        next(child, RotationArgument::rotation, RotationArgument::getRotation)

    fun scoreHolder(child: CommandBuilder.(scoreHolder: ArgumentGetter<String>) -> Unit) =
        next(child, ScoreHolderArgument::scoreHolder, ScoreHolderArgument::getName)

    fun scoreHolders(child: CommandBuilder.(scoreHolders: ScoreHoldersArgument) -> Unit) =
        nextScoreHolders(child, ScoreHolderArgument::scoreHolders, ScoreHolderArgument::getNames)

    fun scoreboardScoreHolders(child: CommandBuilder.(scoredHolders: ArgumentGetter<Collection<String>>) -> Unit) =
        next(child, ScoreHolderArgument::scoreHolders, ScoreHolderArgument::getNamesWithDefaultWildcard)

    fun scoreboardSlot(child: CommandBuilder.(scoreboardSlot: ArgumentGetter<Int>) -> Unit) =
        next(child, ScoreboardSlotArgument::displaySlot, ScoreboardSlotArgument::getDisplaySlot)

    fun string(child: CommandBuilder.(string: ArgumentGetter<String>) -> Unit) =
        next(child, StringArgumentType::string, StringArgumentType::getString)

    fun swizzle(child: CommandBuilder.(swizzle: ArgumentGetter<EnumSet<Direction.Axis>>) -> Unit) =
        next(child, SwizzleArgument::swizzle, SwizzleArgument::getSwizzle)

    fun nbtElement(child: CommandBuilder.(nbtElement: ArgumentGetter<Tag>) -> Unit) =
        next(child, NbtTagArgument::nbtTag, NbtTagArgument::getNbtTag)

    fun team(child: CommandBuilder.(team: ArgumentGetter<PlayerTeam>) -> Unit) =
        next(child, TeamArgument::team, TeamArgument::getTeam)

    fun text(child: CommandBuilder.(text: ArgumentGetter<Component>) -> Unit) =
        next(child, AdventureTextComponentArgumentType::adventureText, AdventureTextComponentArgumentType::getAdventureTextArgument)

    fun uuid(child: CommandBuilder.(uuid: ArgumentGetter<UUID>) -> Unit) =
        next(child, UuidArgument::uuid, UuidArgument::getUuid)

    fun vec2(child: CommandBuilder.(vec2: ArgumentGetter<Vec2>) -> Unit) =
        next(child, Vec2Argument::vec2, Vec2Argument::getVec2)

    fun vec3(child: CommandBuilder.(vec3: ArgumentGetter<Vec3>) -> Unit) =
        next(child, Vec3Argument::vec3, Vec3Argument::getVec3)

    fun world(child: CommandBuilder.(world: ArgumentGetter<String>) -> Unit) =
        next(child, StringArgumentType::word, StringArgumentType::getString)

    fun executes(process: CommandContext.() -> Unit) {
        builder.executes {
            process(CommandContext(it))
            0
        }
        this.executes = executes
    }

    @Suppress("UNCHECKED_CAST")
    fun suggests(
        suggests: (com.mojang.brigadier.context.CommandContext<CommandSourceStack>, SuggestionsBuilder) -> CompletableFuture<Suggestions>,
    ) {
        if (builder is RequiredArgumentBuilder<*, *>) {
            builder.suggests { context, builder ->
                suggests(
                    context as com.mojang.brigadier.context.CommandContext<CommandSourceStack>,
                    builder,
                )
            }
        }
    }

    @OptIn(ExperimentalReflectionOnLambdas::class)
    private fun <T1, T2> next(
        child: CommandBuilder.(ArgumentGetter<T1>) -> Unit,
        argumentProvider: () -> ArgumentType<T2>,
        factory: (com.mojang.brigadier.context.CommandContext<CommandSourceStack>, String) -> T1,
    ) {
        val name = getterNameToParamName(child.reflect()?.parameters?.get(1)?.name.toString())
        val arg = RequiredArgumentBuilder.argument<CommandSourceStack, T2>(name, argumentProvider())
        child(CommandBuilder(arg, registryAccess), ArgumentGetter { factory(it, name) })
        builder.then(arg)
    }

    @OptIn(ExperimentalReflectionOnLambdas::class)
    private fun <T> nextScoreHolders(
        child: CommandBuilder.(ScoreHoldersArgument) -> Unit,
        argumentProvider: () -> ArgumentType<T>,
        factory: (
            com.mojang.brigadier.context.CommandContext<CommandSourceStack>,
            String,
            Supplier<Collection<String>>,
        ) -> Collection<String>,
    ) {
        val getterName = child.reflect()?.parameters?.get(1)?.name.toString()
        val name = getterNameToParamName(getterName)
        val arg = RequiredArgumentBuilder.argument<CommandSourceStack, T>(name, argumentProvider())
        child(CommandBuilder(arg, registryAccess), ScoreHoldersArgument { ctx, supplier -> factory(ctx, name, supplier) })
    }

    private fun getterNameToParamName(getterName: String): String {
        val name = StringBuilder(getterName)
        if (name.startsWith("get") && 3 < name.length) {
            name.delete(0, 3)
            name[0] = name[0].lowercaseChar()
        }
        return name.toString()
    }
}
