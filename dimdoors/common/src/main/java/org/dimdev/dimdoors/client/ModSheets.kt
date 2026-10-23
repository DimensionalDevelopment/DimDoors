package org.dimdev.dimdoors.client

import net.minecraft.client.renderer.Sheets
import net.minecraft.client.resources.model.Material
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.properties.ChestType
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.block.entity.DriftwoodChestBlockEntity

object ModSheets {
    val DRIFTWOOD_CHEST = ChestMaterial.create(DimensionalDoors.MOD_ID, "driftwood")

    data class ChestMaterial(val normal: Material, val left: Material, val right: Material) {
        fun choose(type: ChestType, holiday: Boolean): Material = when(type) {
            ChestType.SINGLE -> normal
            ChestType.LEFT -> left
            ChestType.RIGHT -> right
        }

        companion object {
            fun create(modid: String, name: String): ChestMaterial {
                return ChestMaterial(
                    chestMaterial(modid, "${name}_normal"),
                    chestMaterial(modid, "${name}_normal_left"),
                    chestMaterial(modid, "${name}_normal_right")
                )
            }

            private fun chestMaterial(modid: String, chestName: String): Material {
                return Material(Sheets.CHEST_SHEET, ResourceLocation.fromNamespaceAndPath(modid, "entity/chest/$chestName"))
            }
        }
    }


    @JvmStatic
    fun chestMaterial(blockEntity: BlockEntity, chestType: ChestType, holiday: Boolean): Material? {
        return when(blockEntity) {
            is DriftwoodChestBlockEntity -> DRIFTWOOD_CHEST.choose(chestType, holiday)
            else -> null
        }
    }
}
