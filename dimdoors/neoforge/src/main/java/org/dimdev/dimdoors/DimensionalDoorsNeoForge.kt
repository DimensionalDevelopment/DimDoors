package org.dimdev.dimdoors

import net.minecraft.core.Holder
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.world.inventory.RecipeBookType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.material.FlowingFluid
import net.minecraft.world.level.material.Fluid
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.fml.common.asm.enumextension.EnumProxy
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.event.level.ChunkEvent
import net.neoforged.neoforge.event.server.ServerStoppedEvent
import net.neoforged.neoforge.fluids.FluidType
import org.dimdev.dimcore.NeoForgeSided
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.api.event.ChunkServedCallback
import org.dimdev.dimdoors.fluid.EternalFluid
import org.dimdev.dimdoors.fluid.LeakFluid
import org.dimdev.dimdoors.fluid.ModFluidTypes
import org.dimdev.dimdoors.item.FarShotItem
import org.dimdev.dimdoors.world.ModBiomeModifiers

@Mod(DimensionalDoors.MOD_ID)
class DimensionalDoorsNeoForge(bus: IEventBus) : NeoForgeSided<DimensionalDoorsNeoForge, DimensionalDoors>(bus, DimensionalDoors.INSTANCE), IDimensionalDoorsSided<DimensionalDoorsNeoForge> {
    init {
        ModFluidTypes.register()
        ModBiomeModifiers.register()

        NeoForge.EVENT_BUS.addListener { load: ChunkEvent.Load ->
            if (load.isNewChunk) return@addListener
            val level = load.level.castOrNull<ServerLevel>() ?: return@addListener
            val chunk = load.chunk.castOrNull<LevelChunk>() ?: return@addListener
            ChunkServedCallback.EVENT.invoker().onChunkServed(level, chunk)
        }
    }

    override fun createFlowingEternalFluid(): Fluid {
        return object : EternalFluid.Flowing() {
            override fun getFluidType(): FluidType {
                return ModFluidTypes.ETERNAL
            }
        }
    }

    override fun createEternalFluid(): FlowingFluid {
        return object : EternalFluid.Still() {
            override fun getFluidType(): FluidType {
                return ModFluidTypes.ETERNAL
            }
        }
    }

    override fun createFlowingLeakFluid(): Fluid {
        return object : LeakFluid.Flowing() {
            override fun getFluidType(): FluidType = ModFluidTypes.LEAK
        }
    }

    override fun createLeakFluid(): FlowingFluid = object : LeakFluid.Still() {
        override fun getFluidType(): FluidType = ModFluidTypes.LEAK
    }

    override fun registerGameRule(
        name: String,
        category: GameRules.Category,
        value: Boolean
    ): GameRules.Key<GameRules.BooleanValue> = GameRules.register(name, category, GameRules.BooleanValue.create(value))

    override fun registerGameRule(
        name: String,
        category: GameRules.Category,
        value: Int
    ): GameRules.Key<GameRules.IntegerValue> = GameRules.register(name, category, GameRules.IntegerValue.create(value))

    override fun createFarShot(properties: Item.Properties): FarShotItem {
        return object : FarShotItem(properties) {
            override fun isPrimaryItemFor(stack: ItemStack, enchantment: Holder<Enchantment>) = allowsEnchantment(enchantment) && enchantment.value().isPrimaryItem(stack)

            override fun supportsEnchantment(stack: ItemStack, enchantment: Holder<Enchantment>) = allowsEnchantment(enchantment) && enchantment.value().isSupportedItem(stack)
        }
    }

    override val tesselatingRecipeBookType: RecipeBookType get() = TESSELLATING.getValue()

    public override fun onServerStopped(server: (MinecraftServer) -> Unit) =
        NeoForge.EVENT_BUS.addListener<ServerStoppedEvent> { event -> event.server.run(server) }

    override fun onServerStopping(server: (MinecraftServer) -> Unit) = NeoForge.EVENT_BUS.addListener<ServerStoppedEvent> { event -> event.server.run(server) }

    override val enderPearlsTag: TagKey<Item> get() = Tags.Items.ENDER_PEARLS

    companion object {
        @JvmField val TESSELLATING = EnumProxy(RecipeBookType::class.java)
    }
}
