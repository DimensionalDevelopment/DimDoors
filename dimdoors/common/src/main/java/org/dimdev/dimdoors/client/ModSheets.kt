package org.dimdev.dimdoors.client

import net.minecraft.client.renderer.Sheets
import net.minecraft.client.resources.model.Material
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.properties.ChestType

object ModSheets {
    private val MATERIALS = mutableMapOf<BlockEntityType<*>, (BlockEntity, ChestType, Boolean) -> Material?>()

    fun registerMaterial(blockEntityType: BlockEntityType<*>, function: (BlockEntity, ChestType, Boolean) -> Material?) {
        MATERIALS[blockEntityType] = function
    }

    data class LootrChestMaterial(val normal: Material, val openned: Material) {
        fun choose(isOpen: Boolean): Material {
            return if(isOpen) openned else normal
        }

        companion object {
            fun create(modid: String, name: String): LootrChestMaterial {
                return LootrChestMaterial(
                    ChestMaterial.chestMaterial(modid, "${name}_lootr"),
                    ChestMaterial.chestMaterial(modid, "${name}_lootr_opened"),
                )
            }
        }
    }

    data class ChestMaterial(val normal: Material, val left: Material, val right: Material) {
        fun choose(type: ChestType): Material = when(type) {
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

            fun chestMaterial(modid: String, chestName: String): Material {
                return Material(Sheets.CHEST_SHEET, ResourceLocation.fromNamespaceAndPath(modid, "entity/chest/$chestName"))
            }
        }
    }


    @JvmStatic
    fun chestMaterial(blockEntity: BlockEntity, chestType: ChestType, holiday: Boolean): Material? {
        return MATERIALS[blockEntity.type]?.invoke(blockEntity, chestType, holiday)
    }
}
