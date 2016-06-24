package com.zixiken.dimdoors.dungeon;

import net.minecraft.block.Block;
import com.zixiken.dimdoors.schematic.SchematicFilter;
import net.minecraft.block.state.IBlockState;

import java.util.List;

public class ModBlockFilter extends SchematicFilter {

	private List<Block> exceptions;
	private IBlockState replacementState;
	
	public ModBlockFilter(List<Block> exceptions, IBlockState state)
	{
		super("ModBlockFilter");
		this.exceptions = exceptions;
		this.replacementState = state;
	}
	
	@Override
	protected boolean applyToBlock(int index, IBlockState[] state) {
		int k;
		Block current = state[index].getBlock();
		if (!Block.blockRegistry.getNameForObject(current).getResourcePath().startsWith("minecraft:")) {
			//This might be a mod block. Check if an exception exists.
			for (k = 0; k < exceptions.size(); k++) {
				if (current == exceptions.get(k)) {
					//Exception found, not considered a mod block
					return false;
				}
			}
			//No matching exception found. Replace the block.
			state[index] = replacementState;
			return true;
		}
		return false;
	}
	
	@Override
	protected boolean terminates()
	{
		return false;
	}
}
