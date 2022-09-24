package org.dimdev.dimdoors.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.dimdev.dimdoors.block.ModBlocks;

public class FakeBlockEntity extends BlockEntity {

	private Block MIRROR;

	FakeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public FakeBlockEntity(BlockPos blockPos, BlockState blockState) {
		this(ModBlockEntityTypes.FAKE_BLOCK, blockPos, blockState);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		if(nbt.contains("block")) MIRROR = Registry.BLOCK.get(new Identifier(nbt.getString("block")));
		else MIRROR = ModBlocks.BLACK_FABRIC;
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		nbt.putString("block",Registry.BLOCK.getId(MIRROR).toString());
	}

	public Block getMirror() {
		return this.MIRROR;
	}
}
