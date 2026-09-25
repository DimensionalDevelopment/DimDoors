package org.dimdev.dimdoors

import com.chocohead.mm.api.ClassTinkerers
import com.google.common.base.Suppliers
import com.mojang.datafixers.util.Pair
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry
import net.fabricmc.fabric.api.item.v1.EnchantingContext
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.fabricmc.fabric.impl.content.registry.util.ImmutableCollectionUtils
import net.minecraft.core.Holder
import net.minecraft.server.MinecraftServer
import net.minecraft.world.inventory.RecipeBookType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.GameRules
import org.dimdev.dimcore.FabricSided
import org.dimdev.dimdoors.api.event.ChunkServedCallback
import org.dimdev.dimdoors.item.FarShotItem
import org.dimdev.dimdoors.mixin.RecipeBookSettingsAccessor

class DimensionalDoorsFabric : FabricSided<DimensionalDoorsFabric, DimensionalDoors>(DimensionalDoors.INSTANCE), IDimensionalDoorsSided<DimensionalDoorsFabric> {
    private val TESSELLATING = Suppliers.memoize<RecipeBookType> {
        val type = ClassTinkerers.getEnum(RecipeBookType::class.java, "TESSELLATING")

        ImmutableCollectionUtils.getAsMutableMap(
            RecipeBookSettingsAccessor::getTagFields,
                RecipeBookSettingsAccessor::setTagFields)
            .putIfAbsent(type, Pair.of("isTessellatingGui", "isTessellatingFilteringCraftable"))
            type
        }

    override fun onInitialize() {
        super.onInitialize()
        this.tesselatingRecipeBookType

        PlayerBlockBreakEvents.AFTER.register(DimensionalDoors::afterBlockBreak)
        ServerChunkEvents.CHUNK_LOAD.register(ChunkServedCallback.EVENT.invoker()::onChunkServed)
    }

    override val tesselatingRecipeBookType: RecipeBookType get() = TESSELLATING.get()

    override fun onServerStopped(server: (MinecraftServer) -> Unit) = ServerLifecycleEvents.SERVER_STOPPED.register(server)

    override fun onServerStopping(server: (MinecraftServer) -> Unit) = ServerLifecycleEvents.SERVER_STOPPING.register(server)

    override fun registerGameRule(
        name: String,
        category: GameRules.Category,
        value: Boolean
    ): GameRules.Key<GameRules.BooleanValue> {
        val type = GameRuleFactory.createBooleanRule(value)
        return GameRuleRegistry.register(name, category, type)
    }

    override fun registerGameRule(
        name: String,
        category: GameRules.Category,
        value: Int
    ): GameRules.Key<GameRules.IntegerValue> {
        val type = GameRuleFactory.createIntRule(value)
        return GameRuleRegistry.register(name, category, type)
    }

    override fun createFarShot(properties: Item.Properties): FarShotItem {
        return object : FarShotItem(properties) {
            override fun canBeEnchantedWith(
                stack: ItemStack?,
                enchantment: Holder<Enchantment>,
                context: EnchantingContext?
            ): Boolean {
                return super.canBeEnchantedWith(stack, enchantment, context) && allowsEnchantment(enchantment)
            }
        }
    }

    override val enderPearlsTag get() = ConventionalItemTags.ENDER_PEARLS
}
