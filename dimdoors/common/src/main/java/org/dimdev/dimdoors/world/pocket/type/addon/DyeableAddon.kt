package org.dimdev.dimdoors.world.pocket.type.addon

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.DyeColor
import org.dimdev.dimcore.api.util.EntityUtils
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.PortalColors
import org.dimdev.dimdoors.block.AncientFabricBlock
import org.dimdev.dimdoors.block.FabricBlock
import org.dimdev.dimdoors.block.ModBlocks.ancientFabricFromDye
import org.dimdev.dimdoors.block.ModBlocks.fabricFromDye
import org.dimdev.dimdoors.world.pocket.type.Pocket
import org.dimdev.dimdoors.world.pocket.type.PocketColor
import org.dimdev.dimdoors.world.pocket.type.PocketColor.Companion.pocketColor
import org.dimdev.dimdoors.world.pocket.type.PrivatePocket
import kotlin.math.max
import kotlin.math.min

class DyeableAddon(
    private var dyeColor: PocketColor = PocketColor.WHITE,
    private var nextDyeColor: PocketColor = PocketColor.NONE,
    private var count: Int = 0
) : PocketAddon, PortalColorProvider {

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
            setColor(entity, pocket, dyeColor)
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

    fun setColor(entity: Entity, pocket: Pocket<*, *>, dyeColor: DyeColor) {
        val innerWall = fabricFromDye(dyeColor)!!.defaultBlockState()
        val outerWall = ancientFabricFromDye(dyeColor)!!.defaultBlockState()

        pocket.modify {
            return@modify when (it.block) {
                is AncientFabricBlock -> outerWall
                is FabricBlock -> innerWall
                else -> null
            }
        }


        this.dyeColor = dyeColor.pocketColor
        this.nextDyeColor = PocketColor.NONE
        this.count = 0

        EntityUtils.chat(
            entity,
            Component.translatable(
                "advancement.pocket.pocketHasBeenDyed",
                dyeColor.serializedName
            )
        )
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
        override fun apply(pocket: Pocket<*, *>): DyeableAddon {
            val addon = DyeableAddon(dyeColor)
                addon.dyeColor = dyeColor
                pocket.addAddon(addon)

            return addon
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
