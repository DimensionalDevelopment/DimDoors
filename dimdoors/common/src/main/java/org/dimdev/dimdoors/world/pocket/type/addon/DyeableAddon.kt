package org.dimdev.dimdoors.world.pocket.type.addon

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.Level
import org.dimdev.dimcore.api.util.EntityUtils
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.PortalColors
import org.dimdev.dimdoors.block.AncientFabricBlock
import org.dimdev.dimdoors.block.FabricBlock
import org.dimdev.dimdoors.block.ModBlocks.ancientFabricFromDye
import org.dimdev.dimdoors.block.ModBlocks.fabricFromDye
import org.dimdev.dimdoors.world.pocket.type.Pocket
import org.dimdev.dimdoors.world.pocket.type.PocketColor
import org.dimdev.dimdoors.world.pocket.type.PrivatePocket
import kotlin.math.max
import kotlin.math.min

class DyeableAddon(
    private var dyeColor: PocketColor = PocketColor.WHITE,
    private var nextDyeColor: PocketColor = PocketColor.NONE,
    private var count: Int = 0
) : PocketAddon, PortalColorProvider {

    private fun repaint(pocket: Pocket<*, *>, dyeColor: DyeColor) {
        val serverWorld: Level = DimensionalDoors.getWorld(pocket.world)!!

        val innerWall = fabricFromDye(dyeColor)!!.defaultBlockState()
        val outerWall = ancientFabricFromDye(dyeColor)!!.defaultBlockState()

        val box = pocket.box
        val minX = box.minX()
        val minChunkX = minX shr 4
        val minZ = box.minZ()
        val minChunkZ = minZ shr 4
        val minY = box.minY()
        val minChunkY = minY shr 4


        val xSpan = box.xSpan
        val xChunkSpan = xSpan shr 4
        val ySpan = box.xSpan
        val yChunkSpan = ySpan shr 4
        val zSpan = box.xSpan
        val zChunkSpan = zSpan shr 4

        for (chunkX in 0..xChunkSpan) {
            for (chunkZ in 0..zChunkSpan) {
                val chunk = serverWorld.getChunk(minChunkX + chunkX, minChunkZ + chunkZ)
                var changed = false

                for (sectionY in 0..yChunkSpan) {
                    val sectionIndex = chunk.getSectionIndexFromSectionY(minChunkY + sectionY)
                    val section = chunk.getSection(sectionIndex)

                    for (x in 0..15) {
                        for (y in 0..15) {
                            for (z in 0..15) {
                                val state = section.getBlockState(x, y, z)
                                val block = state.block

                                val replacement = when (block) {
                                    is AncientFabricBlock -> outerWall
                                    is FabricBlock -> innerWall
                                    else -> null
                                }

                                if (replacement != null) {
                                    section.setBlockState(x, y, z, replacement)
                                    changed = true
                                }
                            }
                        }
                    }
                }

                if (changed) {
                    chunk.isUnsaved = true
                }
            }
        }
    }

    fun addDye(pocket: Pocket<*, *>, entity: Entity, dyeColor: DyeColor, count: Int): Int {
        val color = PocketColor.from(dyeColor)

        val maxDye: Int = amountOfDyeRequiredToColor(pocket)

        if (count <= 0) {
            return count
        }

        if (this.dyeColor == color) {
            EntityUtils.chat(entity, Component.translatable("advancement.pocket.dyeAlreadyAbsorbed"))
            return count
        }

        if (this.nextDyeColor != color) {
            this.nextDyeColor = color
            this.count = 0
        }

        val remainingNeeded = maxDye - this.count
        val absorbed = min(count, remainingNeeded)
        val remainingInStack = count - absorbed

        this.count += absorbed

        if (this.count >= maxDye) {
            repaint(pocket, dyeColor)

            this.dyeColor = color
            this.nextDyeColor = PocketColor.NONE
            this.count = 0

            EntityUtils.chat(
                entity,
                Component.translatable(
                    "advancement.pocket.pocketHasBeenDyed",
                    dyeColor.serializedName
                )
            )
        } else {
            EntityUtils.chat(
                entity,
                Component.translatable(
                    "advancement.pocket.remainingNeededDyes",
                    this.count,
                    maxDye,
                    color.serializedName
                )
            )
        }

        return remainingInStack
    }

    override fun applicable(pocket: Pocket<*, *>): Boolean = pocket is PrivatePocket

    override val type get() = PocketAddons.DYEABLE_ADDON

    override val colors: IntArray? get() = PortalColors.dye(dyeColor.color)


    interface DyeablePocketBuilder<T : Pocket<T, P>, P : Pocket.PocketBuilder<T, P>> :
        PocketAddon.PocketBuilderExtension<T, P> {
        fun dyeColor(dyeColor: PocketColor): P {
            this.getAddon<DyeableBuilderAddon>(PocketAddons.DYEABLE_ADDON)?.dyeColor = dyeColor

            return self
        }
    }

    class DyeableBuilderAddon @JvmOverloads constructor(internal var dyeColor: PocketColor = PocketColor.NONE) : PocketAddon.PocketBuilderAddon<DyeableAddon, DyeableBuilderAddon> {
        // TODO: add some Pocket#init so that we can have boolean shouldRepaintOnInit
        override fun apply(pocket: Pocket<*, *>) {
            val addon = DyeableAddon(dyeColor)
            addon.dyeColor = dyeColor
            pocket.addAddon(addon)
        }

        override val type get() = PocketAddons.DYEABLE_ADDON
    }

    interface DyeablePocket : AddonProvider

    companion object {
        val CODEC: MapCodec<DyeableAddon> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                PocketColor.CODEC.fieldOf("dyeColor").forGetter(DyeableAddon::dyeColor),
                PocketColor.CODEC.fieldOf("nextDyeColor").forGetter(DyeableAddon::nextDyeColor),
                Codec.INT.fieldOf("count").forGetter(DyeableAddon::count)
            ).apply(
                instance, ::DyeableAddon
            )
        }

        var BUILDER_CODEC: MapCodec<DyeableBuilderAddon> = RecordCodecBuilder.mapCodec { instance -> instance.group(
            PocketColor.CODEC.lenientOptionalFieldOf("dye_color", PocketColor.NONE).forGetter(DyeableBuilderAddon::dyeColor)
        ).apply(instance, ::DyeableBuilderAddon)

        }

        private fun amountOfDyeRequiredToColor(pocket: Pocket<*, *>): Int {
            val outerVolume = pocket.box.ySpan * pocket.box.zSpan * pocket.box.xSpan

            return max(outerVolume / DimensionalDoors.config.pocketsConfig.blocksColoredPerDye, 1)
        }
    }
}
