package org.dimdev.dimdoors.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.tags.TagsProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EntityType
import org.dimdev.dimdoors.api.util.key
import org.dimdev.dimdoors.tag.ModEntityTypeTags
import java.util.concurrent.CompletableFuture

class EntityTagProvider(output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) : TagsProvider<EntityType<*>>(output, Registries.ENTITY_TYPE, registriesFuture) {
    override fun addTags(provider: HolderLookup.Provider) {
        tag(ModEntityTypeTags.IMMUNE_TO_FARSHOT_SWAP).add(key("monolith"))
    }

    companion object {
        fun key(name: String): ResourceKey<EntityType<*>> = Registries.ENTITY_TYPE.key(name)
    }
}
