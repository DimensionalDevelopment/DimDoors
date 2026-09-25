package org.dimdev.dimdoors.block

import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks

class AncientFabricBlock(color: DyeColor) : Block(
    Properties.ofFullCopy(Blocks.STONE)
        .mapColor(color)
        .strength(-1.0f, 3600000.0f)
        .dropsLike(Blocks.AIR)
        .lightLevel { 15 }
)
