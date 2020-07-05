package org.dimdev.dimdoors.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.limbo.LimboDecay;

import java.util.Random;

public class UnravelledFabricBlock extends Block {
    public static final String ID = "unravelled_fabric";

    public UnravelledFabricBlock(Settings settings) {
        super(settings);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (ModDimensions.isLimboDimension(world)) {
            LimboDecay.applySpreadDecay(world, pos);
        }
    }
}
