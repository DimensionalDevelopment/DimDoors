package org.dimdev.dimdoors.block;

import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.DimensionalDoors;

import static net.minecraft.world.level.block.Blocks.STONE;

public class FabricBlock extends Block {
	public static final TagKey<Block> BLOCK_TAG = TagKey.create(Registry.BLOCK_REGISTRY, DimensionalDoors.id("fabric"));

	FabricBlock(DyeColor color) {
		super(Properties.copy(STONE).color(color.getMaterialColor()).strength(1.2F).lightLevel(state -> 15));
	}

	@Override
	public boolean canBeReplaced(BlockState blockState, BlockPlaceContext context) {
		if (context.getPlayer().isShiftKeyDown()) return false;
		Block heldBlock = Block.byItem(context.getPlayer().getItemInHand(context.getHand()).getItem());
		if (!heldBlock.defaultBlockState().isCollisionShapeFullBlock(context.getLevel(), context.getClickedPos())) return false;
		return !(heldBlock instanceof EntityBlock) && !(heldBlock instanceof FabricBlock);
	}
}
