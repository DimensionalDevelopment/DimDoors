package com.zixiken.dimdoors.schematic;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import java.util.ArrayList;

public class CompoundFilter extends SchematicFilter {

	private ArrayList<SchematicFilter> filters;
	
	public CompoundFilter() {
		super("CompoundFilter");
		filters = new ArrayList<SchematicFilter>();
	}
	
	public void addFilter(SchematicFilter filter)
	{
		filters.add(filter);
	}
	
	@Override
	protected boolean initialize(Schematic schematic, IBlockState[] metadata) {
		for (SchematicFilter filter : filters) {
			if (!filter.initialize(schematic, metadata)) {
				return false;
			}
		}
		return !filters.isEmpty();
	}
	
	@Override
	protected boolean finish() {
		for (SchematicFilter filter : filters) {
			if (!filter.finish()) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	protected boolean applyToBlock(int index, IBlockState[] state) {
		for (SchematicFilter filter : filters) {
			if (filter.applyToBlock(index, state)) {
				return filter.terminates();
			}
		}
		return false;
	}
}
