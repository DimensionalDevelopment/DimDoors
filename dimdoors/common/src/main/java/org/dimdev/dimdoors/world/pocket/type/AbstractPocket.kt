package org.dimdev.dimdoors.world.pocket.type

import com.mojang.datafixers.Products.P2
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Vec3i
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import org.dimdev.dimcore.api.BuilderTypeHasHolder
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimdoors.world.pocket.PocketDirectory

abstract class AbstractPocket<V : AbstractPocket<V, T>, T : AbstractPocket.AbstractPocketBuilder<V, T>> : BuilderTypeHasHolder<AbstractPocket<*, *>, AbstractPocket.AbstractPocketBuilder<*, *>> {
    var id: Int = 0
        protected set

    lateinit var world: ResourceKey<Level>
        protected set

    constructor(id: Int, world: ResourceKey<Level>) {
        this.id = id
        this.world = world
    }

    protected constructor()

    open fun toVariableMap(variableMap: MutableMap<String, Double> = mutableMapOf()): MutableMap<String, Double> {
        variableMap["id"] = this.id.toDouble()
        return variableMap
    }

    abstract val referencedPocket: Pocket<*, *>?

    open fun getReferencedPocket(directory: PocketDirectory): Pocket<*, *>? {
        return this.referencedPocket
    }

    abstract class AbstractPocketBuilder<T : AbstractPocket<T, P>, P : AbstractPocketBuilder<T, P>> : BuilderTypeHasHolder<AbstractPocket<*, *>, AbstractPocketBuilder<*, *>> {
        protected constructor()
        protected var id: Int = 0
        protected var world: ResourceKey<Level>? = null

        open val expectedSize: Vec3i
            get() = Vec3i(1, 1, 1)

        open fun build(): T {
            val instance = pocket()

            instance.id = id
            instance.world = world!!

            return instance
        }

        fun id(id: Int): P {
            this.id = id
            return this.self
        }

        fun world(world: ResourceKey<Level>): P {
            this.world = world
            return this.self
        }

        open val self: P
            get() = this.cast()

        abstract fun pocket(): T

        open fun copy(): P {
            val copy = instance()
            copy.id = this.id
            copy.world = this.world
            return copy
        }

        abstract fun instance(): P
    }

    companion object {
        fun <T : AbstractPocket<*, *>> commonFields(instance: RecordCodecBuilder.Instance<T>): P2<RecordCodecBuilder.Mu<T>, Int, ResourceKey<Level>> = instance.group(
                Codec.INT.fieldOf("id").forGetter(AbstractPocket<*, *>::id),
                Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(AbstractPocket<*, *>::world)
            )

        val CODEC = Pockets.codec
        val BUILDER_CODEC = Pockets.builderCodec
    }
}
