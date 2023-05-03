package org.dimdev.dimdoors.api.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface ExtendedItem {

	// TODO: add javadocs
	// true -> send packet to server
	// false -> don't send packet to server
	// boolean value currently does nothing server-side
	default TypedActionResult<Boolean> onAttackBlock(World world, PlayerEntity player, Hand hand, BlockPos pos, Direction direction) {
		return TypedActionResult.pass(false);
	}
}
