package StevenDimDoors.mod_pocketDim.helpers;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class BlockRotationHelper
{
	public HashMap<Integer,HashMap<Block,HashMap<Integer,Integer>>> rotationMappings = new HashMap<Integer,HashMap<Block,HashMap<Integer,Integer>>>();
	
	public BlockRotationHelper()
	{
		this.InitializeRotationMap();
	}
	
	public void InitializeRotationMap()
	{
		HashMap<Block,HashMap<Integer, Integer>> orientation0 = new HashMap<Block,HashMap<Integer, Integer>>();
		
		HashMap<Integer,Integer> stairs0 = new HashMap<Integer,Integer>();
		
		stairs0.put(0, 2);
		stairs0.put(1, 3);
		stairs0.put(2, 1);
		stairs0.put(3, 0);
		stairs0.put(7, 4);
		stairs0.put(6, 5);
		stairs0.put(5, 7);
		stairs0.put(4, 6);

		HashMap<Integer,Integer> chestsLadders0 = new HashMap<Integer,Integer>();
		
		chestsLadders0.put(2, 5);
		chestsLadders0.put(3, 4);
		chestsLadders0.put(4, 2);
		chestsLadders0.put(5, 3);

		HashMap<Integer,Integer> vine0 = new HashMap<Integer,Integer>();
		
		vine0.put(1, 2);
		vine0.put(2, 4);
		vine0.put(4, 8);
		vine0.put(8, 1);
		
		HashMap<Integer,Integer> leverButtonTorch0 = new HashMap<Integer,Integer>();
		
		leverButtonTorch0.put(12, 9);
		leverButtonTorch0.put(11, 10);
		leverButtonTorch0.put(10, 12);
		leverButtonTorch0.put(9, 11);
		leverButtonTorch0.put(2, 4);
		leverButtonTorch0.put(3, 2);
		leverButtonTorch0.put(1, 3);
		leverButtonTorch0.put(4, 1);
		
		HashMap<Integer,Integer> pistonDropperDispenser0 = new HashMap<Integer,Integer>();
		
		pistonDropperDispenser0.put(4, 2);
		pistonDropperDispenser0.put(5, 3);
		pistonDropperDispenser0.put(13, 11);
		pistonDropperDispenser0.put(3, 4);
		pistonDropperDispenser0.put(2, 5);
		pistonDropperDispenser0.put(11, 12);
		pistonDropperDispenser0.put(10, 13);
		pistonDropperDispenser0.put(12, 10);

		HashMap<Integer,Integer> repeaterComparatorDoorTripwire0 = new HashMap<Integer,Integer>();
		
		repeaterComparatorDoorTripwire0.put(0, 1);
		repeaterComparatorDoorTripwire0.put(1, 2);
		repeaterComparatorDoorTripwire0.put(2, 3);
		repeaterComparatorDoorTripwire0.put(3, 0);
		repeaterComparatorDoorTripwire0.put(4, 5);
		repeaterComparatorDoorTripwire0.put(5, 6);
		repeaterComparatorDoorTripwire0.put(6, 7);
		repeaterComparatorDoorTripwire0.put(7, 4);	
		repeaterComparatorDoorTripwire0.put(8, 9);
		repeaterComparatorDoorTripwire0.put(9, 10);
		repeaterComparatorDoorTripwire0.put(10, 11);
		repeaterComparatorDoorTripwire0.put(11, 8);
		repeaterComparatorDoorTripwire0.put(12, 13);
		repeaterComparatorDoorTripwire0.put(13, 14);
		repeaterComparatorDoorTripwire0.put(14, 15);
		repeaterComparatorDoorTripwire0.put(15, 12);
		
		HashMap<Integer,Integer> rails0 = new HashMap<Integer,Integer>();
		rails0.put(0, 1);
		rails0.put(1, 0);
		rails0.put(8, 9);
		rails0.put(9, 6);
		rails0.put(6, 7);
		rails0.put(7, 8);
		
		
		HashMap<Integer,Integer> railsSpecial0 = new HashMap<Integer,Integer>();
		railsSpecial0.put(0, 1);
		railsSpecial0.put(1, 0);
		railsSpecial0.put(8, 9);
		railsSpecial0.put(9, 8);


		HashMap<Block,HashMap<Integer, Integer>> orientation1 = new HashMap<Block,HashMap<Integer, Integer>>();
		
		HashMap<Integer,Integer> stairs1 = new HashMap<Integer,Integer>();
		
		stairs1.put(0, 1);
		stairs1.put(1, 0);
		stairs1.put(2, 3);
		stairs1.put(3, 2);
		stairs1.put(7, 6);
		stairs1.put(6, 7);
		stairs1.put(5, 4);
		stairs1.put(4, 5);
		
		HashMap<Integer,Integer> chestsLadders1 = new HashMap<Integer,Integer>();
		
		chestsLadders1.put(2, 3);
		chestsLadders1.put(3, 2);
		chestsLadders1.put(4, 5);
		chestsLadders1.put(5, 4);
		
		HashMap<Integer,Integer> vine1 = new HashMap<Integer,Integer>();
		
		vine1.put(1, 4);
		vine1.put(2, 8);
		vine1.put(4, 1);
		vine1.put(8, 2);
		
		HashMap<Integer,Integer> leverButtonTorch1 = new HashMap<Integer,Integer>();
		
		leverButtonTorch1.put(12, 9);
		leverButtonTorch1.put(11, 10);
		leverButtonTorch1.put(10, 12);
		leverButtonTorch1.put(9, 11);
		leverButtonTorch1.put(2, 4);
		leverButtonTorch1.put(3, 2);
		leverButtonTorch1.put(1, 3);
		leverButtonTorch1.put(4, 1);
		
		HashMap<Integer,Integer> pistonDropperDispenser1 = new HashMap<Integer,Integer>();
		
		pistonDropperDispenser1.put(12, 11);
		pistonDropperDispenser1.put(11, 12);
		pistonDropperDispenser1.put(10, 9);
		pistonDropperDispenser1.put(9, 10);
		pistonDropperDispenser1.put(2, 1);
		pistonDropperDispenser1.put(3, 4);
		pistonDropperDispenser1.put(1, 2);
		pistonDropperDispenser1.put(4,3);
		
		
		HashMap<Integer,Integer> repeaterComparatorDoorTripwire1 = new HashMap<Integer,Integer>();
		
		repeaterComparatorDoorTripwire1.put(0, 2);
		repeaterComparatorDoorTripwire1.put(1, 3);
		repeaterComparatorDoorTripwire1.put(2, 0);
		repeaterComparatorDoorTripwire1.put(3, 1);
		repeaterComparatorDoorTripwire1.put(4, 6);
		repeaterComparatorDoorTripwire1.put(5, 7);
		repeaterComparatorDoorTripwire1.put(6, 4);
		repeaterComparatorDoorTripwire1.put(7, 5);	
		repeaterComparatorDoorTripwire1.put(8, 10);
		repeaterComparatorDoorTripwire1.put(9, 11);
		repeaterComparatorDoorTripwire1.put(10, 8);
		repeaterComparatorDoorTripwire1.put(11, 9);
		repeaterComparatorDoorTripwire1.put(12, 14);
		repeaterComparatorDoorTripwire1.put(13, 15);
		repeaterComparatorDoorTripwire1.put(14, 12);
		repeaterComparatorDoorTripwire1.put(15, 13);
		
		HashMap<Integer,Integer> rails1 = new HashMap<Integer,Integer>();
		rails1.put(0, 0);
		rails1.put(1, 1);
		rails1.put(8, 6);
		rails1.put(9, 7);
		rails1.put(6, 8);
		rails1.put(7, 9);
		
		
		HashMap<Integer,Integer> railsSpecial1 = new HashMap<Integer,Integer>();
		railsSpecial1.put(1, 1);
		railsSpecial1.put(0, 0);
		railsSpecial1.put(8, 8);
		railsSpecial1.put(9, 9);
		
		HashMap<Block,HashMap<Integer, Integer>> orientation2 = new HashMap<Block,HashMap<Integer, Integer>>();
		
		HashMap<Integer,Integer> stairs2 = new HashMap<Integer,Integer>();
		
		stairs2.put(2, 0);
		stairs2.put(3, 1);
		stairs2.put(1, 2);
		stairs2.put(0, 3);
		stairs2.put(4, 7);
		stairs2.put(5, 6);
		stairs2.put(7, 5);
		stairs2.put(6, 4);
		
		HashMap<Integer,Integer> chestsLadders2 = new HashMap<Integer,Integer>();
		
		chestsLadders2.put(2, 4);
		chestsLadders2.put(3, 5);
		chestsLadders2.put(4, 3);
		chestsLadders2.put(5, 2);
		
		HashMap<Integer,Integer> vine2 = new HashMap<Integer,Integer>();
		
		vine2.put(1, 8);
		vine2.put(2, 1);
		vine2.put(4, 2);
		vine2.put(8, 4);
		
		HashMap<Integer,Integer> leverButtonTorch2 = new HashMap<Integer,Integer>();
		
		leverButtonTorch2.put(9, 12);
		leverButtonTorch2.put(10, 11);
		leverButtonTorch2.put(12, 10);
		leverButtonTorch2.put(11, 9);
		leverButtonTorch2.put(4, 2);
		leverButtonTorch2.put(2, 3);
		leverButtonTorch2.put(3, 1);
		leverButtonTorch2.put(1, 4);
		
		HashMap<Integer,Integer> pistonDropperDispenser2 = new HashMap<Integer,Integer>();
		
		pistonDropperDispenser2.put(2, 4);
		pistonDropperDispenser2.put(3, 5);
		pistonDropperDispenser2.put(11, 13);
		pistonDropperDispenser2.put(10, 12);
		pistonDropperDispenser2.put(4, 3);
		pistonDropperDispenser2.put(5, 2);
		pistonDropperDispenser2.put(12, 11);
		pistonDropperDispenser2.put(13,10);
		
		
		HashMap<Integer,Integer> repeaterComparatorDoorTripwire2 = new HashMap<Integer,Integer>();
		
		repeaterComparatorDoorTripwire2.put(1, 0);
		repeaterComparatorDoorTripwire2.put(2, 1);
		repeaterComparatorDoorTripwire2.put(3, 2);
		repeaterComparatorDoorTripwire2.put(0, 3);
		repeaterComparatorDoorTripwire2.put(5, 4);
		repeaterComparatorDoorTripwire2.put(6, 5);
		repeaterComparatorDoorTripwire2.put(7, 6);
		repeaterComparatorDoorTripwire2.put(4, 7);	
		repeaterComparatorDoorTripwire2.put(9, 8);
		repeaterComparatorDoorTripwire2.put(10, 9);
		repeaterComparatorDoorTripwire2.put(11, 10);
		repeaterComparatorDoorTripwire2.put(8, 11);
		repeaterComparatorDoorTripwire2.put(13, 12);
		repeaterComparatorDoorTripwire2.put(14, 13);
		repeaterComparatorDoorTripwire2.put(15, 14);
		repeaterComparatorDoorTripwire2.put(12, 15);
		
		HashMap<Integer,Integer> rails2 = new HashMap<Integer,Integer>();
		rails2.put(0, 1);
		rails2.put(1, 0);
		rails2.put(8, 7);
		rails2.put(9, 8);
		rails2.put(6, 9);
		rails2.put(7, 6);
		
		
		HashMap<Integer,Integer> railsSpecial2 = new HashMap<Integer,Integer>();
		railsSpecial2.put(0, 1);
		railsSpecial2.put(1, 0);
		railsSpecial2.put(8, 9);
		railsSpecial2.put(9, 8);

	

		
		orientation0.put(Blocks.brick_stairs, stairs0);
		orientation0.put(Blocks.stone_stairs, stairs0);
		orientation0.put(Blocks.nether_brick_stairs, stairs0);
		orientation0.put(Blocks.quartz_stairs, stairs0);
		orientation0.put(Blocks.sandstone_stairs, stairs0);
		orientation0.put(Blocks.stone_brick_stairs, stairs0);
		orientation0.put(Blocks.birch_stairs, stairs0);
		orientation0.put(Blocks.jungle_stairs, stairs0);
		orientation0.put(Blocks.oak_stairs, stairs0);
		orientation0.put(Blocks.spruce_stairs, stairs0);
		orientation0.put(Blocks.brick_stairs, stairs0);
		orientation0.put(Blocks.vine, vine0);
		orientation0.put(Blocks.chest, chestsLadders0);
		orientation0.put(Blocks.trapped_chest, chestsLadders0);
		orientation0.put(Blocks.ladder, chestsLadders0);
		orientation0.put(Blocks.lever, leverButtonTorch0);
		orientation0.put(Blocks.stone_button, leverButtonTorch0);
		orientation0.put(Blocks.wooden_button, leverButtonTorch0);
		orientation0.put(Blocks.redstone_torch, leverButtonTorch0);
		orientation0.put(Blocks.unlit_redstone_torch, leverButtonTorch0);
		orientation0.put(Blocks.torch, leverButtonTorch0);
		orientation0.put(Blocks.piston,pistonDropperDispenser0);
		orientation0.put(Blocks.piston_head,pistonDropperDispenser0);
		orientation0.put(Blocks.piston_extension,pistonDropperDispenser0);
		orientation0.put(Blocks.sticky_piston,pistonDropperDispenser0);
		orientation0.put(Blocks.dropper,pistonDropperDispenser0);
		orientation0.put(Blocks.dispenser,pistonDropperDispenser0);
		orientation0.put(Blocks.powered_comparator,pistonDropperDispenser0);
		orientation0.put(Blocks.unpowered_comparator,pistonDropperDispenser0);
		orientation0.put(Blocks.powered_repeater,pistonDropperDispenser0);
		orientation0.put(Blocks.unpowered_repeater,pistonDropperDispenser0);
		orientation0.put(Blocks.wooden_door,pistonDropperDispenser0);
		orientation0.put(Blocks.iron_door,pistonDropperDispenser0);
		orientation0.put(Blocks.tripwire_hook,pistonDropperDispenser0);
		orientation0.put(Blocks.detector_rail,railsSpecial0);
		orientation0.put(Blocks.activator_rail,railsSpecial0);
		orientation0.put(Blocks.golden_rail,railsSpecial0);
		orientation0.put(Blocks.rail,rails0);
		
		orientation1.put(Blocks.brick_stairs, stairs1);
		orientation1.put(Blocks.stone_stairs, stairs1);
		orientation1.put(Blocks.nether_brick_stairs, stairs1);
		orientation1.put(Blocks.quartz_stairs, stairs1);
		orientation1.put(Blocks.sandstone_stairs, stairs1);
		orientation1.put(Blocks.stone_brick_stairs, stairs1);
		orientation1.put(Blocks.birch_stairs, stairs1);
		orientation1.put(Blocks.jungle_stairs, stairs1);
		orientation1.put(Blocks.oak_stairs, stairs1);
		orientation1.put(Blocks.spruce_stairs, stairs1);
		orientation1.put(Blocks.vine, vine1);
		orientation1.put(Blocks.chest, chestsLadders1);
		orientation1.put(Blocks.trapped_chest, chestsLadders1);
		orientation1.put(Blocks.ladder, chestsLadders1);
		orientation1.put(Blocks.lever, leverButtonTorch1);
		orientation1.put(Blocks.stone_button, leverButtonTorch1);
		orientation1.put(Blocks.wooden_button, leverButtonTorch1);
		orientation1.put(Blocks.redstone_torch, leverButtonTorch1);
		orientation1.put(Blocks.unlit_redstone_torch, leverButtonTorch1);
		orientation1.put(Blocks.torch, leverButtonTorch1);
		orientation1.put(Blocks.piston,pistonDropperDispenser1);
		orientation1.put(Blocks.piston_head,pistonDropperDispenser1);
		orientation1.put(Blocks.piston_extension,pistonDropperDispenser1);
		orientation1.put(Blocks.sticky_piston,pistonDropperDispenser1);
		orientation1.put(Blocks.dropper,pistonDropperDispenser1);
		orientation1.put(Blocks.dispenser,pistonDropperDispenser1);
		orientation1.put(Blocks.powered_comparator,pistonDropperDispenser1);
		orientation1.put(Blocks.unpowered_comparator,pistonDropperDispenser1);
		orientation1.put(Blocks.powered_repeater,pistonDropperDispenser1);
		orientation1.put(Blocks.unpowered_repeater,pistonDropperDispenser1);
		orientation1.put(Blocks.wooden_door,pistonDropperDispenser1);
		orientation1.put(Blocks.iron_door,pistonDropperDispenser1);
		orientation1.put(Blocks.tripwire_hook,pistonDropperDispenser1);
		orientation1.put(Blocks.detector_rail,railsSpecial1);
		orientation1.put(Blocks.activator_rail,railsSpecial1);
		orientation1.put(Blocks.golden_rail,railsSpecial1);
		orientation1.put(Blocks.rail,rails1);

		orientation2.put(Blocks.brick_stairs, stairs2);
		orientation2.put(Blocks.stone_stairs, stairs2);
		orientation2.put(Blocks.nether_brick_stairs, stairs2);
		orientation2.put(Blocks.quartz_stairs, stairs2);
		orientation2.put(Blocks.sandstone_stairs, stairs2);
		orientation2.put(Blocks.stone_brick_stairs, stairs2);
		orientation2.put(Blocks.birch_stairs, stairs2);
		orientation2.put(Blocks.jungle_stairs, stairs2);
		orientation2.put(Blocks.oak_stairs, stairs2);
		orientation2.put(Blocks.spruce_stairs, stairs2);
		orientation2.put(Blocks.vine, vine2);
		orientation2.put(Blocks.trapped_chest, chestsLadders2);
		orientation2.put(Blocks.ladder, chestsLadders2);
		orientation2.put(Blocks.lever, leverButtonTorch2);
		orientation2.put(Blocks.stone_button, leverButtonTorch2);
		orientation2.put(Blocks.wooden_button, leverButtonTorch2);
		orientation2.put(Blocks.redstone_torch, leverButtonTorch2);
		orientation2.put(Blocks.unlit_redstone_torch, leverButtonTorch2);
		orientation2.put(Blocks.torch, leverButtonTorch2);
		orientation2.put(Blocks.piston,pistonDropperDispenser2);
		orientation2.put(Blocks.piston_head,pistonDropperDispenser2);
		orientation2.put(Blocks.piston_extension,pistonDropperDispenser2);
		orientation2.put(Blocks.sticky_piston,pistonDropperDispenser2);
		orientation2.put(Blocks.dropper,pistonDropperDispenser2);
		orientation2.put(Blocks.dispenser,pistonDropperDispenser2);
		orientation2.put(Blocks.powered_comparator,pistonDropperDispenser2);
		orientation2.put(Blocks.unpowered_comparator,pistonDropperDispenser2);
		orientation2.put(Blocks.powered_repeater,pistonDropperDispenser2);
		orientation2.put(Blocks.unpowered_repeater,pistonDropperDispenser2);
		orientation2.put(Blocks.wooden_door,pistonDropperDispenser2);
		orientation2.put(Blocks.iron_door,pistonDropperDispenser2);
		orientation2.put(Blocks.tripwire_hook,pistonDropperDispenser2);
		orientation2.put(Blocks.detector_rail,railsSpecial2);
		orientation2.put(Blocks.activator_rail,railsSpecial2);
		orientation2.put(Blocks.golden_rail,railsSpecial2);
		orientation2.put(Blocks.rail,rails2);
		
		this.rotationMappings.put(2, orientation2);
		this.rotationMappings.put(1, orientation1);
		this.rotationMappings.put(0, orientation0);
	}
	
	public int getRotatedBlock(int metaData, int desiredOrientation, int blockID)
	{
		if(this.rotationMappings.containsKey(desiredOrientation))
		{
			if(this.rotationMappings.get(desiredOrientation).containsKey(blockID))
			{
				if(this.rotationMappings.get(desiredOrientation).get(blockID).containsKey(metaData))
				{
					return this.rotationMappings.get(desiredOrientation).get(blockID).get(metaData);
				}
			}
		}
		return metaData ;	
	}
	
	
	
}