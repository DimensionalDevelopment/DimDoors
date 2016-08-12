package com.zixiken.dimdoors.schematic;


import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class ReplacementFilter extends SchematicFilter {

	private IBlockState targetState;
	private boolean matchState;
	private IBlockState replacementState;
	private boolean changeState;
	
	public ReplacementFilter(IBlockState targetState, byte targetMetadata, IBlockState replacementState) {
		super("ReplacementFilter");
		this.targetState = targetState;
		this.matchState = true;
		this.replacementState = replacementState;
		this.changeState = true;
	}
	
	/*public ReplacementFilter(IBlockState targetState, IBlockState replacementState)
	{
		super("ReplacementFilter");
		this.targetState = targetState;
		this.matchState = false;
		this.replacementState = replacementState;
		this.changeState = true;
	}
	
	public ReplacementFilter(Block targetBlock, byte targetMetadata, Block replacementBlock)
	{
		super("ReplacementFilter");
		this.targetBlock = targetBlock;
		this.targetMetadata = targetMetadata;
		this.matchState = true;
		this.replacementBlock = replacementBlock;
		this.changeState = false;
	}
	
	public ReplacementFilter(IBlockState targetState, IBlockState replacementState) {
		super("ReplacementFilter");
		this.targetState = targetState;
		this.matchState = false;
		this.replacementState = replacementState;
		this.changeState = false;
	}*/

	@Override
	protected boolean applyToBlock(int index, IBlockState[] states) {
		if (states[index] == targetState) {
			if ((matchState && states[index] == targetState) || !matchState) {
				states[index] = replacementState;
				if (changeState) {
					states[index] = replacementState;
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected boolean terminates()
	{
		return false;
	}
}
