package org.dimdev.dimdoors.client

import net.minecraft.client.Minecraft
import net.minecraft.client.RecipeBookCategories
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.common.asm.enumextension.EnumProxy
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent
import net.neoforged.neoforge.client.event.RenderFrameEvent
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.common.NeoForge
import org.dimdev.dimcore.client.NeoForgeClientSided
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.client.IDimDoorsClientSided.PreRender
import org.dimdev.dimdoors.client.config.ConfigScreen
import org.dimdev.dimdoors.client.effect.DimensionEffect
import org.dimdev.dimdoors.item.ModItems

@Mod(dist = [Dist.CLIENT], value = DimensionalDoors.MOD_ID)
class DimensionalDoorsForgeClient(bus: IEventBus, container: ModContainer) :
    NeoForgeClientSided<DimensionalDoorsForgeClient, DimensionalDoorsClient>(
        bus,
        DimensionalDoorsClient.INSTANCE
    ), IDimDoorsClientSided<DimensionalDoorsForgeClient> {
    override fun getRecipBookCategories(name: String, itemStack: () -> ItemStack) = fun() = when (name) {
        "TESSELATING_GENERAL" -> {
            TESSELLATING_GENERAL.getValue()
        }

        "TESSELATING_SEARCH" -> {
            TESSELLATING_SEARCH.getValue()
        }

        else -> throw IllegalArgumentException("Unknown recipe book category: $name")
    }

    init {
        container.registerExtensionPoint(IConfigScreenFactory::class.java, IConfigScreenFactory { _, screen -> ConfigScreen.createScreen(screen) })

        bus.addListener(::registerRecipeBookCategories)

        GeneratedDoorModelEvents.init(bus)
    }

    override fun createVoidEffect(effect: DimensionEffect) = NfVoidDimensionEffects(effect)

    override fun onPreRender(onPrerender: PreRender) {
        NeoForge.EVENT_BUS.addListener<RenderFrameEvent.Pre> { event ->
            val level = Minecraft.getInstance().level ?: return@addListener

            onPrerender.preRender(level.gameTime, event.partialTick.getGameTimeDeltaPartialTick(false))
        }
    }

    companion object {
        val TESSELLATING_GENERAL = EnumProxy(RecipeBookCategories::class.java, { listOf(ModItems.WORLD_THREAD.value().defaultInstance) })
        val TESSELLATING_SEARCH = EnumProxy(RecipeBookCategories::class.java, { listOf(Items.COMPASS.defaultInstance) })

        fun registerRecipeBookCategories(event: RegisterRecipeBookCategoriesEvent) {
            ModRecipeBookGroups.init()
            org.dimdev.dimdoors.api.util.RegisterRecipeBookCategoriesEvent.EVENT.invoker().accept(
                org.dimdev.dimdoors.api.util.RegisterRecipeBookCategoriesEvent(
                    event::registerAggregateCategory,
                    event::registerBookCategories,
                    { type, lookup -> event.registerRecipeCategoryFinder(type, lookup) }
                )
            )
        }
    }
}
