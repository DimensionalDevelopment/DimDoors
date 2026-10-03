package org.dimdev.dimdoors.world.structure.processors

import com.mojang.serialization.Codec
import com.mojang.serialization.Dynamic
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import org.dimdev.dimdoors.api.util.toNbt
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import org.dimdev.dimdoors.util.CodecUtils
import org.dimdev.dimdoors.api.util.immutable
import org.dimdev.dimdoors.world.ModStructureProccessors

class DestinationDataModifier(val destinations: Map<Int, Tag>) :
    StructureProcessor() {

    override fun processBlock(
        level: LevelReader,
        offset: BlockPos,
        pos: BlockPos,
        blockInfo: StructureTemplate.StructureBlockInfo,
        relativeBlockInfo: StructureTemplate.StructureBlockInfo,
        settings: StructurePlaceSettings
    ): StructureTemplate.StructureBlockInfo {
        val data = apply(relativeBlockInfo.nbt())

        return StructureTemplate.StructureBlockInfo(relativeBlockInfo.pos(), relativeBlockInfo.state(), data)
    }

    fun apply(tag: CompoundTag?): CompoundTag? {
        if (tag != null && tag.contains("data")) {
            val data = tag.getCompound("data")

            val id = getMarkerId(data)
            if (id >= 0) {
                destinations[id]?.also { data.put("destination", it) }
            }
        }

        return tag
    }

    public override fun getType(): StructureProcessorType<*> = ModStructureProccessors.DESTINATION_DATA

    private fun getMarkerId(data: CompoundTag): Int {
        val destination = data.getCompound("destination")
        if (destination.getString("type") == "dimdoors:id_marker") {
            return destination.getInt("id")
        }

        return -1
    }

    companion object {
        fun of(destinations: Map<Int, VirtualTarget<*>>): DestinationDataModifier =
            destinations.mapValues { VirtualTarget.CODEC.toNbt(it.value) }.let(::DestinationDataModifier)

        fun of(id: Int, data: VirtualTarget<*>): DestinationDataModifier = of(mapOf(id to data))

        fun of(data: VirtualTarget<*>): DestinationDataModifier {
            return of(0, data)
        }

        @JvmField
        val CODEC: MapCodec<DestinationDataModifier> = RecordCodecBuilder.mapCodec { instance -> instance.group(
                Codec.unboundedMap(
                    CodecUtils.STRING_INT, Codec.PASSTHROUGH.xmap(
                        { a -> a.convert(NbtOps.INSTANCE).getValue() },
                        { a -> Dynamic(NbtOps.INSTANCE, a) })
                ).immutable().fieldOf("destinations").forGetter(DestinationDataModifier::destinations)
            ).apply(
                instance, ::DestinationDataModifier
            )
        }
    }
}


