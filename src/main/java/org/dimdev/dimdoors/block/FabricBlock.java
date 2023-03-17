package org.dimdev.dimdoors.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import org.dimdev.dimdoors.DimensionalDoors;

public class FabricBlock extends Block {
	public static final TagKey<Block> BLOCK_TAG = TagKey.create(Registries.BLOCK, DimensionalDoors.resource("fabric"));

	FabricBlock(DyeColor color) {
		super(BlockBehaviour.Properties.of(Material.STONE, color).strength(1.2F).lightLevel(state -> 15));
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		if (context.getPlayer().isShiftKeyDown()) return false;
		Block heldBlock = Block.byItem(context.getPlayer().getItemInHand(context.getHand()).getItem());
		if (!heldBlock.defaultBlockState().isCollisionShapeFullBlock(context.getLevel(), context.getClickedPos())) return false;
		if (heldBlock instanceof EntityBlock || heldBlock instanceof FabricBlock) return false;

		return true;
	}
}
