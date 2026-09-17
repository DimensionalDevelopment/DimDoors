package org.dimdev.dimdoors.block;

import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class ModButtonBlock extends ButtonBlock {
    public ModButtonBlock(BlockSetType type, int ticksToStayPressed, BlockBehaviour.Properties properties) {
        super(type, ticksToStayPressed, properties);
    }
}
