package org.dimdev.dimdoors.world.pocket.type

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import org.dimdev.dimdoors.rift.registry.PocketRegistry.Companion.instance
import org.dimdev.dimdoors.world.pocket.PocketDirectory
import java.util.function.Function

class IdReferencePocket : AbstractPocket<IdReferencePocket, IdReferencePocket.IdReferencePocketBuilder> {
    var referencedId: Int = Int.MIN_VALUE

    constructor(id: Int, world: ResourceKey<Level>, referencedId: Int) : super(id, world) {
        this.referencedId = referencedId
    }

    constructor() : super()

    override val type get() = Pockets.ID_REFERENCE

    override val referencedPocket: Pocket<*, *>? get() = getReferencedPocket(instance.getPocketDirectory(world))

    override fun getReferencedPocket(directory: PocketDirectory): Pocket<*, *>? {
        return directory.getPocket(referencedId)
    }

    class IdReferencePocketBuilder : AbstractPocketBuilder<IdReferencePocket, IdReferencePocketBuilder> {
        private var referencedId = Int.MIN_VALUE

        constructor(referenceId: Int) {
            this.referencedId = referenceId
        }

        constructor() : super()

        override fun build(): IdReferencePocket {
            val pocket = super.build()
            pocket.referencedId = referencedId
            return pocket
        }

        override fun pocket(): IdReferencePocket {
            return IdReferencePocket()
        }

        override val type get() = Pockets.ID_REFERENCE

        public override fun copy(): IdReferencePocketBuilder {
            val copy = super.copy()
            copy.referencedId = referencedId
            return copy
        }

        public override fun instance(): IdReferencePocketBuilder {
            return IdReferencePocketBuilder()
        }

        fun referencedId(referencedId: Int): IdReferencePocketBuilder {
            this.referencedId = referencedId
            return this
        }

        companion object {
            val CODEC = RecordCodecBuilder.mapCodec<IdReferencePocketBuilder>(
                Function { instance -> instance.group(
                        Codec.INT.optionalFieldOf("referenced_id", Int.MIN_VALUE).forGetter(IdReferencePocketBuilder::referencedId)
                    ).apply(instance, ::IdReferencePocketBuilder)
                })
        }
    }

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec { instance -> commonFields(instance).and(
                    Codec.INT.fieldOf("referenced_id").forGetter(IdReferencePocket::referencedId)
                ).apply(instance, ::IdReferencePocket)
            }

        var KEY: String = "id_reference"

        fun builder(): IdReferencePocketBuilder {
            return IdReferencePocketBuilder()
        }
    }
}
