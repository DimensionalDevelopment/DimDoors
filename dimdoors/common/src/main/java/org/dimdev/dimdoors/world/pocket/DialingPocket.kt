package org.dimdev.dimdoors.world.pocket

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.structure.BoundingBox
import org.dimdev.dimdoors.rift.registry.DialingAddress
import org.dimdev.dimdoors.world.pocket.type.Pocket
import org.dimdev.dimdoors.world.pocket.type.Pockets
import org.dimdev.dimdoors.world.pocket.type.addon.DyeableAddon
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon
import java.util.*

class DialingPocket : Pocket<DialingPocket, DialingPocket.Builder>, DyeableAddon.DyeablePocket {
    lateinit var address: DialingAddress

    constructor(
        id: Int,
        world: ResourceKey<Level>,
        range: Int,
        box: BoundingBox,
        virtualLocation: VirtualLocation,
        addons: MutableList<PocketAddon>,
        address: DialingAddress
    ) : super(id, world, range, box, virtualLocation, addons) {
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
            if (address != null) {
                pocket.address = address
            }
            return pocket
        }

        override val type get() = Pockets.DIALING

        override fun pocket() = DialingPocket()

        companion object {
            val CODEC = RecordCodecBuilder.mapCodec { instance ->
                commonFields(instance).apply(instance, ::Builder)
            }
        }
    }

    override val type get() = Pockets.DIALING

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec { instance -> commonPocketFields(instance)
            .and(DialingAddress.MAP_CODEC.forGetter(DialingPocket::address))
            .apply(instance, ::DialingPocket)
        }

        fun builderDialingPocket(): Builder {
            return Builder()
        }
    }
}