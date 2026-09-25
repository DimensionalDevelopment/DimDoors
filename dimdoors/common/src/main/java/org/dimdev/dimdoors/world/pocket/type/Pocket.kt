package org.dimdev.dimdoors.world.pocket.type

import com.mojang.datafixers.Products.P1
import com.mojang.datafixers.Products.P6
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.Vec3i
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.levelgen.structure.BoundingBox
import org.dimdev.dimcore.api.cast
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.rift.registry.PocketRegistry.Companion.instance
import org.dimdev.dimdoors.world.pocket.VirtualLocation
import org.dimdev.dimdoors.world.pocket.type.addon.AddonProvider
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddonType
import java.util.function.Consumer
import kotlin.streams.asSequence

abstract class Pocket<T : Pocket<T, V>, V : Pocket.PocketBuilder<T, V>> : AbstractPocket<T, V>, AddonProvider {
    protected val addons = mutableMapOf<Holder<out PocketAddonType>, PocketAddon>()
    private var range = -1
    lateinit var box: BoundingBox
        protected set

    lateinit var virtualLocation: VirtualLocation

    constructor(
        id: Int,
        world: ResourceKey<Level>,
        range: Int,
        box: BoundingBox,
        virtualLocation: VirtualLocation,
        addons: MutableList<PocketAddon>
    ) : super(id, world) {
        this.range = range
        this.box = box
        this.virtualLocation = virtualLocation

        addons.forEach { addon -> this.addons[addon.type] = addon }
    }

    constructor(id: Int, world: ResourceKey<Level>, x: Int, z: Int) : super(id, world) {
        val gridSize = instance.getPocketDirectory(world).gridSize * 16
        this.box = BoundingBox.fromCorners(
            Vec3i(x * gridSize, 0, z * gridSize),
            Vec3i((x + 1) * gridSize, 0, (z + 1) * gridSize)
        )
        this.virtualLocation = VirtualLocation(world, x, z, 0)
    }

    protected constructor()

    override fun hasAddon(id: Holder<out PocketAddonType>): Boolean {
        return addons.containsKey(id)
    }

    override fun <C : PocketAddon> addAddon(addon: C): Boolean {
        if (addon.applicable(this)) {
            addon.addAddon(addons)
            return true
        }
        return false
    }

    fun removeAddon(type: Holder<out PocketAddonType>): Boolean {
        return addons.remove(type) != null
    }

    fun getAddons(predicate: (PocketAddon) -> Boolean): List<PocketAddon> = streamAddon().filter(predicate)

    fun streamAddon(): MutableCollection<PocketAddon> = addons.values

    fun <T : PocketAddon> getAddon(type: Holder<out PocketAddonType>): T? = addons[type]?.cast()

    fun isInBounds(pos: BlockPos): Boolean = this.box.isInside(pos)

    val origin: BlockPos
        get() = BlockPos(this.box.minX(), this.box.minY(), this.box.minZ())

    fun offsetOrigin(vec: Vec3i) {
        this.box.move(vec)
    }

    fun offsetOrigin(x: Int, y: Int, z: Int) {
        this.box.move(x, y, z)
    }

    fun setSize(x: Int, y: Int, z: Int) {
        this.box = BoundingBox.fromCorners(
            Vec3i(this.box.minX(), this.box.minY(), this.box.minZ()),
            Vec3i(this.box.minX() + x - 1, this.box.minY() + y - 1, this.box.minZ() + z - 1)
        )
    }

    fun setRange(range: Int) {
        if (this.range > 0) throw UnsupportedOperationException("Cannot set range of Pocket that has already been initialized.")
        this.range = range
    }

    fun getRange(): Int {
        if (range < 1) throw UnsupportedOperationException("Range of pocket has not been initialized yet.")
        return range
    }

    var size: Vec3i
        get() = this.box.length
        set(size) {
            setSize(size.x, size.y, size.z)
        }

    val blockEntities: Map<BlockPos, BlockEntity>
        get() {
            val serverWorld: Level = DimensionalDoors.getWorld(this.world)!!

            val minChunk = ChunkPos(box.minX() shr 4, box.minZ() shr 4)
            val maxChunk = ChunkPos(box.maxX() shr 4, box.maxZ() shr 4)

            return ChunkPos.rangeClosed(minChunk, maxChunk).asSequence()
                .map { pos ->
                    serverWorld.getChunk(
                        pos.x,
                        pos.z
                    )
                }
                .map { obj -> obj.getBlockEntities() }
                .map { obj -> obj.entries }
                .flatMap { it }
                .filter { pair ->
                    this.box.isInside(
                        pair.key
                    )
                }.associate { it.key to it.value }
        }

    override fun toVariableMap(variableMap: MutableMap<String, Double>): MutableMap<String, Double> {
        var variableMap = variableMap
        variableMap = super.toVariableMap(variableMap)
        variableMap["originX"] = this.box.minX().toDouble()
        variableMap["originY"] = this.box.minY().toDouble()
        variableMap["originZ"] = this.box.minZ().toDouble()
        variableMap["width"] = this.box.length.x.toDouble()
        variableMap["height"] = this.box.length.y.toDouble()
        variableMap["length"] = this.box.length.z.toDouble()
        variableMap["depth"] = this.virtualLocation.depth.toDouble()
        return variableMap
    }

    override val referencedPocket: Pocket<*, *>
        get() = this

    fun expand(amount: Int) {
        this.box = this.box.inflatedBy(amount)
    }


    // TODO: flesh this out a bit more, stuff like box() makes little sense in how it is implemented atm
    abstract class PocketBuilder<T : Pocket<T, P>, P : PocketBuilder<T, P>> : AbstractPocketBuilder<T, P> {
        protected var addons = mutableMapOf<Holder<out PocketAddonType>, PocketAddon.PocketBuilderAddon<*, *>>()

        protected var origin: Vec3i = Vec3i(0, 0, 0)
        protected var size: Vec3i = Vec3i(0, 0, 0)
        protected var expected: Vec3i = Vec3i(0, 0, 0)
        protected lateinit var virtualLocation: VirtualLocation
        protected var range: Int = -1

        constructor(addons: MutableList<PocketAddon.PocketBuilderAddon<*, *>>) {
            this.addons = Util.make(mutableMapOf()) { map ->
                addons.forEach { pocketBuilderAddon ->
                    map[pocketBuilderAddon.type] = pocketBuilderAddon
                }
            }
        }

        protected constructor() : super() {
            initAddons()
        }


        open fun initAddons() {
        }

        fun hasAddon(id: Holder<out PocketAddonType>): Boolean {
            return addons.containsKey(id)
        }

        protected fun <C : PocketAddon.PocketBuilderAddon<*, *>> addAddon(addon: C) {
            if (addon.applicable(this)) {
                addon.addAddon(addons)
            }
        }

        fun <C : PocketAddon.PocketBuilderAddon<*, *>> getAddon(id: Holder<out PocketAddonType>): C? = addons[id]?.cast()

        override val expectedSize: Vec3i
            get() = expected

        override fun build(): T {
            if (range < 1) throw RuntimeException("Cannot create pocket with range < 1")

            val instance = super.build()

            instance.setRange(range)
            instance.box = BoundingBox.fromCorners(
                Vec3i(origin.getX(), origin.getY(), origin.getZ()),
                Vec3i(
                    origin.getX() + size.getX() - 1,
                    origin.getY() + size.getY() - 1,
                    origin.getZ() + size.getZ() - 1
                )
            )
            instance.virtualLocation = virtualLocation

            addons.values.forEach(Consumer { addon: PocketAddon.PocketBuilderAddon<*, *>? -> addon!!.apply(instance) })

            return instance
        }

        override fun copy(): P {
            val copy = super.copy()
            copy.range = range
            copy.origin = origin
            copy.size = size
            copy.expected = expected
            copy.virtualLocation = virtualLocation
            copy.addons = addons.toMutableMap()
            return copy
        }

        fun offsetOrigin(offset: Vec3i): P {
            this.origin =
                Vec3i(origin.x + offset.x, origin.getY() + offset.getY(), origin.getZ() + offset.getZ())
            return self
        }

        fun expand(expander: Vec3i): P {
            this.size =
                Vec3i(size.x + expander.x, size.y + expander.y, size.z + expander.z)
            this.expected = Vec3i(
                expected.getX() + expander.getX(),
                expected.getY() + expander.getY(),
                expected.getZ() + expander.getZ()
            )
            return self
        }

        fun expandExpected(expander: Vec3i): P {
            this.expected = Vec3i(
                expected.getX() + expander.getX(),
                expected.getY() + expander.getY(),
                expected.getZ() + expander.getZ()
            )
            return self
        }

        fun virtualLocation(virtualLocation: VirtualLocation): P {
            this.virtualLocation = virtualLocation
            return self
        }

        fun range(range: Int): P {
            this.range = range
            return self
        }

        companion object {
            protected fun <T : PocketBuilder<*, *>> commonFields(instance: RecordCodecBuilder.Instance<T>): P1<RecordCodecBuilder.Mu<T>, MutableList<PocketAddon.PocketBuilderAddon<*, *>>> {
                return instance.group(
                    PocketAddon.LIST_BUILDER_CODEC.optionalFieldOf("addons", mutableListOf()).forGetter<T> { t -> t.addons.values.toMutableList() }
                )
            }
        }
    }

    companion object {
        @JvmField
        var KEY: String = "pocket"

        fun <T : Pocket<*, *>> commonPocketFields(instance: RecordCodecBuilder.Instance<T>): P6<RecordCodecBuilder.Mu<T>, Int, ResourceKey<Level>, Int, BoundingBox, VirtualLocation, MutableList<PocketAddon>> {
            return commonFields(instance)
                .and(Codec.INT.fieldOf("range").forGetter(Pocket<*, *>::getRange))
                .and(BoundingBox.CODEC.fieldOf("box").forGetter(Pocket<*, *>::box))
                .and(VirtualLocation.CODEC.fieldOf("virtualLocation").forGetter(Pocket<*, *>::virtualLocation))
                .and(PocketAddon.LIST_CODEC.fieldOf("addons").forGetter { it.addons.values.toMutableList() })
        }
    }
}
