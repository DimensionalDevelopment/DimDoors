package org.dimdev.dimdoors.block

import net.minecraft.world.level.block.state.properties.BlockSetType

interface DoorSoundProvider {
    val setType: BlockSetType get() = BlockSetType.IRON
}
