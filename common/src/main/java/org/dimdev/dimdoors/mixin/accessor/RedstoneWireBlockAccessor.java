package org.dimdev.dimdoors.mixin.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RedStoneWireBlock.class)
public interface RedstoneWireBlockAccessor {
	@Invoker
	RedstoneSide invokeGetConnectingSide(BlockGetter blockGetter, BlockPos blockPos, Direction direction);
}
