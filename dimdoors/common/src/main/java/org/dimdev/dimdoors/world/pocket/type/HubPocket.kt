package org.dimdev.dimdoors.world.pocket.type

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.structure.BoundingBox
import org.dimdev.dimdoors.pockets.generator.PocketGenerator
import org.dimdev.dimdoors.world.pocket.VirtualLocation
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon
import java.util.*

class HubPocket  : Pocket<HubPocket, HubPocket.Builder> {
    constructor(
        id: Int,
        world: ResourceKey<Level>,
        range: Int,
        box: BoundingBox,
        virtualLocation: VirtualLocation,
        addons: MutableList<PocketAddon>,
        generator: Optional<ResourceKey<PocketGenerator<*>>>,
    ) : super(id, world, range, box, virtualLocation, addons, generator) {
    }

    constructor()

    class Builder : PocketBuilder<HubPocket, Builder> {

        private constructor(addons: MutableList<PocketAddon.PocketBuilderAddon<*, *>>) : super(addons)

        constructor() : super()

        override fun instance(): Builder {
            return builderHubPocket()
        }

        override fun build(): HubPocket {
            val pocket = super.build()

            return pocket
        }

        override val type get() = Pockets.HUB

        override fun pocket() = HubPocket()

        companion object {
            val CODEC: MapCodec<Builder> = RecordCodecBuilder.mapCodec<Builder> { instance ->
                commonFields<Builder>(instance).apply(instance, ::Builder)
            }
        }
    }

    override val type get() = Pockets.HUB

    companion object {
        val CODEC: MapCodec<HubPocket> = RecordCodecBuilder.mapCodec<HubPocket> { instance -> commonPocketFields<HubPocket>(instance)
            .apply(instance, ::HubPocket)
        }

        fun builderHubPocket(): Builder {
            return Builder()
        }
    }
}