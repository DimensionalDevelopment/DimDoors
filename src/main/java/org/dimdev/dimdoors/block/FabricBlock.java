package org.dimdev.dimdoors.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.DyeColor;
import org.dimdev.dimdoors.DimensionalDoors;

public class FabricBlock extends Block {
	public static final TagKey<Block> BLOCK_TAG = TagKey.of(RegistryKeys.BLOCK, DimensionalDoors.id("fabric"));

	FabricBlock(DyeColor color) {
		super(FabricBlockSettings.of(Material.STONE, color).strength(1.2F).luminance(15));
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		if (context.getPlayer().isSneaking()) return false;
		Block heldBlock = Block.getBlockFromItem(context.getPlayer().getStackInHand(context.getHand()).getItem());
		if (!heldBlock.getDefaultState().isFullCube(context.getWorld(), context.getBlockPos())) return false;
		if (heldBlock instanceof BlockEntityProvider || heldBlock instanceof FabricBlock) return false;

		return true;
	}
}
