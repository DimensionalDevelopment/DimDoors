package org.dimdev.dimdoors.compat.lootr

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.blockentity.ChestRenderer
import noobanidus.mods.lootr.common.block.entity.LootrChestBlockEntity
import org.dimdev.dimcore.DimCore.clientPlatform
import org.dimdev.dimcore.api.client.CustomBlockEntityItemRenderer
import org.dimdev.dimcore.api.client.EntityRendererRegister
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.client.ModSheets

object LootrClientCompat {
    fun register() {
        val rift_wood_lootr = ModSheets.LootrChestMaterial.create(DimensionalDoors.MOD_ID, "driftwood")
        ModSheets.registerMaterial(BlockEntities.DRIFTWOOD_LOOTR_CHEST) { blockEntity, type, _ ->
            blockEntity.cast<LootrChestBlockEntity>()?.hasClientOpened(Minecraft.getInstance().player)?.let { rift_wood_lootr.choose(it) }
        }

        clientPlatform.registerCustomItemRenderer(CustomBlockEntityItemRenderer(Blocks.DRIFTWOOD_LOOTR_CHEST,::DriftwoodLootrChestBlockEntity), Blocks.DRIFTWOOD_LOOTR_CHEST)
    }

    fun registerEntities(register: EntityRendererRegister) {
        register.registerBlockEntity(BlockEntities.DRIFTWOOD_LOOTR_CHEST, ::ChestRenderer)
    }


}