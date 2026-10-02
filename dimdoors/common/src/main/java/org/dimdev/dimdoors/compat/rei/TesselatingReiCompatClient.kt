package org.dimdev.dimdoors.compat.rei

import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.display.Display
import me.shedaniel.rei.api.common.util.EntryStacks
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.client.screen.TesselatingLoomScreen
import org.dimdev.dimdoors.compat.rei.decay.DecayDisplayGenerator
import org.dimdev.dimdoors.compat.rei.decay.DecayPatternDisplay
import org.dimdev.dimdoors.compat.rei.decay.DefaultDecaysIntoCategory
import org.dimdev.dimdoors.compat.rei.tesselating.DefaultTesselatingCategory
import org.dimdev.dimdoors.compat.rei.tesselating.DefaultTesselatingDisplay
import org.dimdev.dimdoors.compat.rei.tesselating.DefaultTesselatingShapedDisplay
import org.dimdev.dimdoors.compat.rei.tesselating.DefaultTesselatingShapelessDisplay
import org.dimdev.dimdoors.recipe.ModRecipeTypes
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe
import org.dimdev.dimdoors.recipe.TesselatingShapelessRecipe
import org.dimdev.dimdoors.screen.TessellatingContainer

open class TesselatingReiCompatClient : REIClientPlugin {
    override fun registerCategories(registry: CategoryRegistry) {
        registry.add(
            DefaultTesselatingCategory()
        ) { configuration ->
            configuration.addWorkstations(EntryStacks.of(ModBlocks.TESSELATING_LOOM))
        }

        registry.add(
            DefaultDecaysIntoCategory()
        ) { configuration ->
            configuration.addWorkstations(EntryStacks.of(ModBlocks.UNRAVELLED_FABRIC))
        }
    }

    override fun registerDisplays(registry: DisplayRegistry) {
        registry.registerRecipeFiller(ShapedTesselatingRecipe::class.java, ModRecipeTypes.TESSELATING, ::DefaultTesselatingShapedDisplay)
        registry.registerRecipeFiller<TesselatingShapelessRecipe, Display>(TesselatingShapelessRecipe::class.java, ModRecipeTypes.TESSELATING, ::DefaultTesselatingShapelessDisplay)
        registry.registerDisplayGenerator(DECAYS_INTO, DECAY_DISPLAY_GENERATOR)
    }

    override fun registerScreens(registry: ScreenRegistry) {
        registry.registerContainerClickArea(
            Rectangle(90, 35, 22, 15),
            TesselatingLoomScreen::class.java,
            TESSELATING
        )
    }

    override fun registerTransferHandlers(registry: TransferHandlerRegistry) {
        registry.register(
            SimpleTransferHandler.create(
                TessellatingContainer::class.java,
                TESSELATING,
                SimpleTransferHandler.IntRange(1, 10)
            )
        )
    }

    companion object {
        val TESSELATING: CategoryIdentifier<out DefaultTesselatingDisplay<*>> = CategoryIdentifier.of("dimdoors", "tesselating")
        val DECAYS_INTO: CategoryIdentifier<DecayPatternDisplay> = CategoryIdentifier.of("dimdoors", "decays_into")
        private val DECAY_DISPLAY_GENERATOR = DecayDisplayGenerator()
    }
}
