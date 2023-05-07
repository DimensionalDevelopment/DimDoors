package org.dimdev.dimdoors.api.item;

import dev.architectury.event.CompoundEventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface ExtendedItem {

	// TODO: add javadocs
	// true -> send packet to server
	// false -> don't send packet to server
	// boolean value currently does nothing server-side
	default CompoundEventResult<Boolean> onAttackBlock(Level world, Player player, InteractionHand hand, BlockPos pos, Direction direction) {
		return CompoundEventResult.interruptFalse(false);
	}
}
