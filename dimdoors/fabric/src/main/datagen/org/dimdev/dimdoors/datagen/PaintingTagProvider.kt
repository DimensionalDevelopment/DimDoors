package org.dimdev.dimdoors.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.tags.PaintingVariantTags
import net.minecraft.world.entity.decoration.PaintingVariant
import org.dimdev.dimdoors.painting.ModPaintings
import java.util.concurrent.CompletableFuture

class PaintingTagProvider(output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) : DimDoorsTagsProvider<PaintingVariant>(output, Registries.PAINTING_VARIANT, registriesFuture) {
    override fun addTags(wrapperLookup: HolderLookup.Provider) {
        tag(PaintingVariantTags.PLACEABLE).add(
            ModPaintings.LIMBO,
            ModPaintings.EYES,
            ModPaintings.FREEDOM,
            ModPaintings.PORTAL,
            ModPaintings.GATEWAY_AT_NIGHT
        )
    }
}
