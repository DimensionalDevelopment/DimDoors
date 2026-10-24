package org.dimdev.dimdoors.compat.lootr

import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import noobanidus.mods.lootr.common.api.replacement.ILootrBlockReplacementProvider
import org.dimdev.dimdoors.api.util.tag

class DriftWoodChestReplacmentProvider : ILootrBlockReplacementProvider {
    override fun getPriority(): Int = 10

    override fun getApplicableTag(): TagKey<Block> = CONVERTS_RIFTWOOD

    override fun getBlock(): Block = Blocks.DRIFTWOOD_LOOTR_CHEST

    companion object {
        @JvmField
        val CONVERTS_RIFTWOOD: TagKey<Block> = Registries.BLOCK.tag("converts_riftwood")
    }
}
