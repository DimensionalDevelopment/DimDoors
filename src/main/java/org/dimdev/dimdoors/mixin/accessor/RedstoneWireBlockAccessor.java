package org.dimdev.dimdoors.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

@Mixin(RedstoneWireBlock.class)
public interface RedstoneWireBlockAccessor {
	@Invoker
	WireConnection invokeGetRenderConnectionType(BlockView blockView, BlockPos blockPos, Direction direction);
}
