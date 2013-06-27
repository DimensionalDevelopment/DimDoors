package StevenDimDoors.mod_pocketDim.helpers;

import java.util.HashMap;

import net.minecraft.block.Block;

public class BlockRotationHelper
{
	public HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> rotationMappings = new HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>>();
	
	public BlockRotationHelper()
	{
		this.InitializeRotationMap();
	}
	
	
	
	public void InitializeRotationMap()
	{
		HashMap<Integer,HashMap<Integer, Integer>> orientation0 = new HashMap<Integer,HashMap<Integer, Integer>>();
		
		HashMap<Integer,Integer> stairs0 = new HashMap();
		
		stairs0.put(0, 2);
		stairs0.put(1, 3);
		stairs0.put(2, 1);
		stairs0.put(3, 0);
		stairs0.put(7, 4);
		stairs0.put(6, 5);
		stairs0.put(5, 7);
		stairs0.put(4, 6);
		
		

		
		HashMap chestsLadders0 = new HashMap();
		
		chestsLadders0.put(2, 5);
		chestsLadders0.put(3, 4);
		chestsLadders0.put(4, 2);
		chestsLadders0.put(5, 3);
		
		
		
		HashMap vine0 = new HashMap();
		
		vine0.put(1, 2);
		vine0.put(2, 4);
		vine0.put(4, 8);
		vine0.put(8, 1);
		
		orientation0.put(Block.vine.blockID, vine0);

		HashMap leverButtonTorch0 = new HashMap();
		
		leverButtonTorch0.put(12, 9);
		leverButtonTorch0.put(11, 10);
		leverButtonTorch0.put(10, 12);
		leverButtonTorch0.put(9, 11);
		leverButtonTorch0.put(2, 4);
		leverButtonTorch0.put(3, 2);
		leverButtonTorch0.put(1, 3);
		leverButtonTorch0.put(4, 1);
		
		
		
		HashMap pistonDropperDispenser0 = new HashMap();
		
		pistonDropperDispenser0.put(4, 2);
		pistonDropperDispenser0.put(5, 3);
		pistonDropperDispenser0.put(13, 11);
		pistonDropperDispenser0.put(3, 4);
		pistonDropperDispenser0.put(2, 5);
		pistonDropperDispenser0.put(11, 12);
		pistonDropperDispenser0.put(10, 13);
		pistonDropperDispenser0.put(12, 10);
		
		
		
		
		HashMap repeaterComparatorDoorTripwire0 = new HashMap();
		
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
		

		
		HashMap<Integer,HashMap<Integer, Integer>> orientation1 = new HashMap<Integer,HashMap<Integer, Integer>>();
		
		HashMap stairs1 = new HashMap();
		
		stairs1.put(0, 1);
		stairs1.put(1, 0);
		stairs1.put(2, 3);
		stairs1.put(3, 2);
		stairs1.put(7, 6);
		stairs1.put(6, 7);
		stairs1.put(5, 4);
		stairs1.put(4, 5);
		
		HashMap chestsLadders1 = new HashMap();
		
		chestsLadders1.put(2, 3);
		chestsLadders1.put(3, 2);
		chestsLadders1.put(4, 5);
		chestsLadders1.put(5, 4);
		
		HashMap vine1 = new HashMap();
		
		vine1.put(1, 4);
		vine1.put(2, 8);
		vine1.put(4, 1);
		vine1.put(8, 2);
		
		HashMap leverButtonTorch1 = new HashMap();
		
		leverButtonTorch1.put(12, 9);
		leverButtonTorch1.put(11, 10);
		leverButtonTorch1.put(10, 12);
		leverButtonTorch1.put(9, 11);
		leverButtonTorch1.put(2, 4);
		leverButtonTorch1.put(3, 2);
		leverButtonTorch1.put(1, 3);
		leverButtonTorch1.put(4, 1);
		
		HashMap pistonDropperDispenser1 = new HashMap();
		
		pistonDropperDispenser1.put(12, 11);
		pistonDropperDispenser1.put(11, 12);
		pistonDropperDispenser1.put(10, 9);
		pistonDropperDispenser1.put(9, 10);
		pistonDropperDispenser1.put(2, 1);
		pistonDropperDispenser1.put(3, 4);
		pistonDropperDispenser1.put(1, 2);
		pistonDropperDispenser1.put(4,3);
		
		
		HashMap repeaterComparatorDoorTripwire1 = new HashMap();
		
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
		
		HashMap<Integer,HashMap<Integer, Integer>> orientation2 = new HashMap<Integer,HashMap<Integer, Integer>>();
		
		HashMap stairs2 = new HashMap();
		
		stairs2.put(2, 0);
		stairs2.put(3, 1);
		stairs2.put(1, 2);
		stairs2.put(0, 3);
		stairs2.put(4, 7);
		stairs2.put(5, 6);
		stairs2.put(7, 5);
		stairs2.put(6, 4);
		
		HashMap chestsLadders2 = new HashMap();
		
		chestsLadders2.put(2, 4);
		chestsLadders2.put(3, 5);
		chestsLadders2.put(4, 3);
		chestsLadders2.put(5, 2);
		
		HashMap vine2 = new HashMap();
		
		vine2.put(1, 8);
		vine2.put(2, 1);
		vine2.put(4, 2);
		vine2.put(8, 4);
		
		HashMap leverButtonTorch2 = new HashMap();
		
		leverButtonTorch2.put(9, 12);
		leverButtonTorch2.put(10, 11);
		leverButtonTorch2.put(12, 10);
		leverButtonTorch2.put(11, 9);
		leverButtonTorch2.put(4, 2);
		leverButtonTorch2.put(2, 3);
		leverButtonTorch2.put(3, 1);
		leverButtonTorch2.put(1, 4);
		
		HashMap pistonDropperDispenser2 = new HashMap();
		
		pistonDropperDispenser2.put(2, 4);
		pistonDropperDispenser2.put(3, 5);
		pistonDropperDispenser2.put(11, 13);
		pistonDropperDispenser2.put(10, 12);
		pistonDropperDispenser2.put(4, 3);
		pistonDropperDispenser2.put(5, 2);
		pistonDropperDispenser2.put(12, 11);
		pistonDropperDispenser2.put(13,10);
		
		
		HashMap repeaterComparatorDoorTripwire2 = new HashMap();
		
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

	

		
		orientation0.put(Block.stairsBrick.blockID, stairs0);
		orientation0.put(Block.stairsCobblestone.blockID, stairs0);
		orientation0.put(Block.stairsNetherBrick.blockID, stairs0);
		orientation0.put(Block.stairsNetherQuartz.blockID, stairs0);
		orientation0.put(Block.stairsSandStone.blockID, stairs0);
		orientation0.put(Block.stairsStoneBrick.blockID, stairs0);
		orientation0.put(Block.stairsWoodBirch.blockID, stairs0);
		orientation0.put(Block.stairsWoodJungle.blockID, stairs0);
		orientation0.put(Block.stairsWoodOak.blockID, stairs0);
		orientation0.put(Block.stairsWoodSpruce.blockID, stairs0);
		orientation0.put(Block.stairsBrick.blockID, stairs0);
		
		orientation0.put(Block.chest.blockID, chestsLadders0);
		orientation0.put(Block.chestTrapped.blockID, chestsLadders0);
		orientation0.put(Block.ladder.blockID, chestsLadders0);
		
		orientation0.put(Block.lever.blockID, leverButtonTorch0);
		orientation0.put(Block.stoneButton.blockID, leverButtonTorch0);
		orientation0.put(Block.woodenButton.blockID, leverButtonTorch0);
		orientation0.put(Block.torchRedstoneActive.blockID, leverButtonTorch0);
		orientation0.put(Block.torchRedstoneIdle.blockID, leverButtonTorch0);
		orientation0.put(Block.torchWood.blockID, leverButtonTorch0);
		
		orientation0.put(Block.pistonBase.blockID,pistonDropperDispenser0);
		orientation0.put(Block.pistonExtension.blockID,pistonDropperDispenser0);
		orientation0.put(Block.pistonMoving.blockID,pistonDropperDispenser0);
		orientation0.put(Block.pistonStickyBase.blockID,pistonDropperDispenser0);
		orientation0.put(Block.dropper.blockID,pistonDropperDispenser0);
		orientation0.put(Block.dispenser.blockID,pistonDropperDispenser0);
		
		orientation0.put(Block.redstoneComparatorActive.blockID,pistonDropperDispenser0);
		orientation0.put(Block.redstoneComparatorIdle.blockID,pistonDropperDispenser0);
		orientation0.put(Block.redstoneRepeaterActive.blockID,pistonDropperDispenser0);
		orientation0.put(Block.redstoneRepeaterIdle.blockID,pistonDropperDispenser0);
		orientation0.put(Block.doorWood.blockID,pistonDropperDispenser0);
		orientation0.put(Block.doorIron.blockID,pistonDropperDispenser0);
		orientation0.put(Block.tripWireSource.blockID,pistonDropperDispenser0);
		
		
		
		
		
		orientation1.put(Block.stairsBrick.blockID, stairs1);
		orientation1.put(Block.stairsCobblestone.blockID, stairs1);
		orientation1.put(Block.stairsNetherBrick.blockID, stairs1);
		orientation1.put(Block.stairsNetherQuartz.blockID, stairs1);
		orientation1.put(Block.stairsSandStone.blockID, stairs1);
		orientation1.put(Block.stairsStoneBrick.blockID, stairs1);
		orientation1.put(Block.stairsWoodBirch.blockID, stairs1);
		orientation1.put(Block.stairsWoodJungle.blockID, stairs1);
		orientation1.put(Block.stairsWoodOak.blockID, stairs1);
		orientation1.put(Block.stairsWoodSpruce.blockID, stairs1);
		orientation1.put(Block.stairsBrick.blockID, stairs1);
		
		orientation1.put(Block.chest.blockID, chestsLadders1);
		orientation1.put(Block.chestTrapped.blockID, chestsLadders1);
		orientation1.put(Block.ladder.blockID, chestsLadders1);
		
		orientation1.put(Block.lever.blockID, leverButtonTorch1);
		orientation1.put(Block.stoneButton.blockID, leverButtonTorch1);
		orientation1.put(Block.woodenButton.blockID, leverButtonTorch1);
		orientation1.put(Block.torchRedstoneActive.blockID, leverButtonTorch1);
		orientation1.put(Block.torchRedstoneIdle.blockID, leverButtonTorch1);
		orientation1.put(Block.torchWood.blockID, leverButtonTorch1);
		
		orientation1.put(Block.pistonBase.blockID,pistonDropperDispenser1);
		orientation1.put(Block.pistonExtension.blockID,pistonDropperDispenser1);
		orientation1.put(Block.pistonMoving.blockID,pistonDropperDispenser1);
		orientation1.put(Block.pistonStickyBase.blockID,pistonDropperDispenser1);
		orientation1.put(Block.dropper.blockID,pistonDropperDispenser1);
		orientation1.put(Block.dispenser.blockID,pistonDropperDispenser1);
		
		orientation1.put(Block.redstoneComparatorActive.blockID,pistonDropperDispenser1);
		orientation1.put(Block.redstoneComparatorIdle.blockID,pistonDropperDispenser1);
		orientation1.put(Block.redstoneRepeaterActive.blockID,pistonDropperDispenser1);
		orientation1.put(Block.redstoneRepeaterIdle.blockID,pistonDropperDispenser1);
		orientation1.put(Block.doorWood.blockID,pistonDropperDispenser1);
		orientation1.put(Block.doorIron.blockID,pistonDropperDispenser1);
		orientation1.put(Block.tripWireSource.blockID,pistonDropperDispenser1);
		
		
		
		
		
		
		orientation2.put(Block.stairsBrick.blockID, stairs2);
		orientation2.put(Block.stairsCobblestone.blockID, stairs2);
		orientation2.put(Block.stairsNetherBrick.blockID, stairs2);
		orientation2.put(Block.stairsNetherQuartz.blockID, stairs2);
		orientation2.put(Block.stairsSandStone.blockID, stairs2);
		orientation2.put(Block.stairsStoneBrick.blockID, stairs2);
		orientation2.put(Block.stairsWoodBirch.blockID, stairs2);
		orientation2.put(Block.stairsWoodJungle.blockID, stairs2);
		orientation2.put(Block.stairsWoodOak.blockID, stairs2);
		orientation2.put(Block.stairsWoodSpruce.blockID, stairs2);
		orientation2.put(Block.stairsBrick.blockID, stairs2);
		
		orientation2.put(Block.chest.blockID, chestsLadders2);
		orientation2.put(Block.chestTrapped.blockID, chestsLadders2);
		orientation2.put(Block.ladder.blockID, chestsLadders2);
		
		orientation2.put(Block.lever.blockID, leverButtonTorch2);
		orientation2.put(Block.stoneButton.blockID, leverButtonTorch2);
		orientation2.put(Block.woodenButton.blockID, leverButtonTorch2);
		orientation2.put(Block.torchRedstoneActive.blockID, leverButtonTorch2);
		orientation2.put(Block.torchRedstoneIdle.blockID, leverButtonTorch2);
		orientation2.put(Block.torchWood.blockID, leverButtonTorch2);
		
		orientation2.put(Block.pistonBase.blockID,pistonDropperDispenser2);
		orientation2.put(Block.pistonExtension.blockID,pistonDropperDispenser2);
		orientation2.put(Block.pistonMoving.blockID,pistonDropperDispenser2);
		orientation2.put(Block.pistonStickyBase.blockID,pistonDropperDispenser2);
		orientation2.put(Block.dropper.blockID,pistonDropperDispenser2);
		orientation2.put(Block.dispenser.blockID,pistonDropperDispenser2);
		
		orientation2.put(Block.redstoneComparatorActive.blockID,pistonDropperDispenser2);
		orientation2.put(Block.redstoneComparatorIdle.blockID,pistonDropperDispenser2);
		orientation2.put(Block.redstoneRepeaterActive.blockID,pistonDropperDispenser2);
		orientation2.put(Block.redstoneRepeaterIdle.blockID,pistonDropperDispenser2);
		orientation2.put(Block.doorWood.blockID,pistonDropperDispenser2);
		orientation2.put(Block.doorIron.blockID,pistonDropperDispenser2);
		orientation2.put(Block.tripWireSource.blockID,pistonDropperDispenser2);
		
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
		
		return 0 ;
	}
	
	
	
}