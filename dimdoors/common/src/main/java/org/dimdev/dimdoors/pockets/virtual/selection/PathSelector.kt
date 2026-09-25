package org.dimdev.dimdoors.pockets.virtual.selection

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket
import org.dimdev.dimdoors.pockets.virtual.VirtualPockets
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference

// TODO: Override equals
class PathSelector(private val path: ResourceLocation) : AbstractVirtualPocketList<PathSelector>() {
    private var initalized = false

    override val type get() = VirtualPockets.PATH_SELECTOR

    override fun getWeight(parameters: PocketGenerationContext): Double {
        initialize(parameters)

        return super.getWeight(parameters)
    }

    override fun getNextPocketGeneratorReference(parameters: PocketGenerationContext): PocketGeneratorReference<*> {
        initialize(parameters)

        return super.getNextPocketGeneratorReference(parameters)
    }

    override fun peekNextPocketGeneratorReference(parameters: PocketGenerationContext): PocketGeneratorReference<*> {
        initialize(parameters)

        return super.peekNextPocketGeneratorReference(parameters)
    }

    private fun initialize(context: PocketGenerationContext) {
        if (!initalized) {
            context.provider.lookup(ModRegistryKeys.VIRTUAL_POCKET).stream()
                .flatMap { it.listElements() }
                .filter(::checkKey)
                .map(Holder.Reference<VirtualPocket>::value)
                .forEach(::add)
            this.initalized = true
        }
    }

    private fun checkKey(virtualPocketReference: Holder.Reference<VirtualPocket>): Boolean {
        val key = virtualPocketReference.key().location()
        return (key.namespace == "minecraft" || key.namespace == path.namespace) && key.path.startsWith(path.path)
    }

    companion object {
        val CODEC: MapCodec<PathSelector> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(ResourceLocation.CODEC.fieldOf("path").forGetter(PathSelector::path)).apply(instance, ::PathSelector)
        }

        const val KEY: String = "path"
    }
}
