package org.dimdev.dimdoors.world.pocket.type

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.structure.BoundingBox
import org.dimdev.dimdoors.pockets.generator.PocketGenerator
import org.dimdev.dimdoors.rift.registry.DialingAddress
import org.dimdev.dimdoors.world.pocket.VirtualLocation
import org.dimdev.dimdoors.world.pocket.type.addon.DyeableAddon
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon
import java.util.*

class DialingPocket : Pocket<DialingPocket, DialingPocket.Builder>, DyeableAddon.DyeablePocket {
    var address: DialingAddress = DialingAddress.DEFAULT

    constructor(
        id: Int,
        world: ResourceKey<Level>,
        range: Int,
        box: BoundingBox,
        virtualLocation: VirtualLocation,
        addons: MutableList<PocketAddon>,
        generator: Optional<ResourceKey<PocketGenerator<*>>>,
        address: DialingAddress
    ) : super(id, world, range, box, virtualLocation, addons, generator) {
        this.address = address
    }

    constructor()

    class Builder : PocketBuilder<DialingPocket, Builder> {
        private var address: DialingAddress? = null

        private constructor(addons: MutableList<PocketAddon.PocketBuilderAddon<*, *>>) : super(addons)

        constructor() : super()

        override fun instance(): Builder {
            return builderDialingPocket()
        }

        fun address(address: DialingAddress?): Builder {
            requireNotNull(address) { "address" }
            this.address = address
            return this
        }

        override fun build(): DialingPocket {
            val pocket = super.build()
            address?.let { pocket.address = it }

            return pocket
        }

        override val type get() = Pockets.DIALING

        override fun pocket() = DialingPocket()

        companion object {
            val CODEC: MapCodec<Builder> = RecordCodecBuilder.mapCodec<Builder> { instance ->
                commonFields<Builder>(instance).apply(instance, ::Builder)
            }
        }
    }

    override val type get() = Pockets.DIALING

    companion object {
        val CODEC: MapCodec<DialingPocket> = RecordCodecBuilder.mapCodec<DialingPocket> { instance -> commonPocketFields<DialingPocket>(instance)
            .and(DialingAddress.MAP_CODEC.forGetter(DialingPocket::address))
            .apply(instance, ::DialingPocket)
        }

        fun builderDialingPocket(): Builder {
            return Builder()
        }
    }
}