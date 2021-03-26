package org.dimdev.dimdoors.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tag.TagRegistry;

public class FabricBlock extends Block {
	public static final Tag<Block> BLOCK_TAG = TagRegistry.block(new Identifier("dimdoors", "fabric"));

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
