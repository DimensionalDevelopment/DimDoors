package com.zixiken.dimdoors.items;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.blocks.BlockDimDoor;
import com.zixiken.dimdoors.blocks.BlockDimDoorTransient;
import com.zixiken.dimdoors.blocks.ModBlocks;
import com.zixiken.dimdoors.shared.Location;
import com.zixiken.dimdoors.shared.RayTraceHelper;
import com.zixiken.dimdoors.shared.RiftRegistry;
import com.zixiken.dimdoors.shared.TeleportHelper;
import com.zixiken.dimdoors.tileentities.DDTileEntityBase;
import com.zixiken.dimdoors.tileentities.TileEntityDimDoor;
import com.zixiken.dimdoors.tileentities.TileEntityRift;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by Jared Johnson on 1/20/2017.
 */
public class ItemRiftBlade extends ItemSword {
    public static final String ID = "itemRiftBlade";

    public ItemRiftBlade() {
        super(ToolMaterial.DIAMOND);
        setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack par1ItemStack) {
        return true;
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return ModItems.itemStableFabric == repair.getItem();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        RayTraceResult hit = rayTrace(world, player, true);
        if (RayTraceHelper.isRift(hit, world)) {
            TileEntityRift rift = (TileEntityRift) world.getTileEntity(hit.getBlockPos());

            ItemDoorBase.placeDoor(world, hit.getBlockPos().down(2), EnumFacing.fromAngle((double) player.rotationYaw), ModBlocks.blockDimDoorTransient, false);

            DDTileEntityBase newTileEntityDimDoor = (DDTileEntityBase) world.getTileEntity(hit.getBlockPos());
            if (rift instanceof DDTileEntityBase) { //
                DDTileEntityBase oldRift = (DDTileEntityBase) rift;
                newTileEntityDimDoor.loadDataFrom(oldRift);
            } else {
                newTileEntityDimDoor.register();
            }
            if (newTileEntityDimDoor instanceof TileEntityDimDoor) {
                TileEntityDimDoor tileEntityDimDoor = (TileEntityDimDoor) newTileEntityDimDoor;
                tileEntityDimDoor.orientation
                        = newTileEntityDimDoor.getWorld().getBlockState(newTileEntityDimDoor.getPos()).getValue(BlockDimDoor.FACING).getOpposite();
                //storing the orientation inside the tile-entity, because that thing can actually save the orientation in the worldsave, unlike the block itself, which fucks up somehow
            }

            return  new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
        } if(RayTraceHelper.isLivingEntity(hit)) {
            TeleportHelper.teleport(player, new Location(world, hit.getBlockPos()));
            return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
        }

        return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        DimDoors.translateAndAdd("info.riftblade", list);
    }
}
