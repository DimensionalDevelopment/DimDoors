package StevenDimDoors.mod_pocketDim.blocks;

import java.util.Random;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDoorGold extends BlockDoor
{
	public BlockDoorGold(int par1, Material par2Material)
	{
		super(par1, par2Material);
	}

    @SideOnly(Side.CLIENT)
    protected String getTextureName()
    {
        return mod_pocketDim.modid + ":" + this.getUnlocalizedName();
    }
	
	
	@Override
	public int idDropped(int par1, Random par2Random, int par3)
    {
        return (par1 & 8) != 0 ? 0 : mod_pocketDim.itemGoldenDoor.itemID;
    }
}
