package com.zixiken.dimdoors.schematic;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class SchematicFilter {

	private String name;
	
	protected SchematicFilter(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean apply(Schematic schematic, IBlockState[] state)
	{
		if (!initialize(schematic, state))
			return false;
		
		for (int index = 0; index < state.length; index++) {
			if (applyToBlock(index, state) && terminates())
				return false;
		}
		
		return finish();
	}
	
	protected boolean initialize(Schematic schematic, IBlockState[] state) {
		return true;
	}
	
	protected boolean applyToBlock(int index, IBlockState[] state) {
		return true;
	}
	
	protected boolean finish() {
		return true;
	}
	
	protected boolean terminates() {
		return true;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
