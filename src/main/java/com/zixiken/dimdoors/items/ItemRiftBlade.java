package com.zixiken.dimdoors.items;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.blocks.ModBlocks;
import com.zixiken.dimdoors.tileentities.TileEntityRift;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.tileentity.TileEntity;
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
        super(ToolMaterial.IRON);
        setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack par1ItemStack) {
        return true;
    }

    private boolean teleportToEntity(ItemStack item, Entity par1Entity, EntityPlayer holder) {
        Vec3d var2 = new Vec3d(holder.posX - par1Entity.posX, holder.getEntityBoundingBox().minY + holder.height / 2.0F - par1Entity.posY + par1Entity.getEyeHeight(), holder.posZ - par1Entity.posZ);

        double cooef =( var2.lengthVector()-2.5)/var2.lengthVector();
        var2.scale(cooef);
        double var5 = holder.posX  - var2.xCoord;
        double var9 = holder.posZ - var2.zCoord;


        double var7 = MathHelper.floor(holder.posY  - var2.yCoord) ;

        int var14 = MathHelper.floor(var5);
        int var15 = MathHelper.floor(var7);
        int var16 = MathHelper.floor(var9);
        while(!holder.world.isAirBlock(new BlockPos(var14, var15, var16))) {
            var15++;
        }

        var7=var15;


        holder.setPositionAndUpdate(var5, var7, var9);

        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            @SuppressWarnings("unchecked")
            List<EntityLiving> list =  world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(player.posX-10,player.posY-10, player.posZ-10, player.posX+10,player.posY+10, player.posZ+10));
            list.remove(player);

            for (EntityLiving ent : list) {
                Vec3d var3 = player.getLook(1.0F).normalize();
                Vec3d var4 =  new Vec3d(ent.posX -  player.posX, ent.getEntityBoundingBox().minY + (ent.height) / 2.0F - ( player.posY + player.getEyeHeight()), ent.posZ -  player.posZ);
                double var5 = var4.lengthVector();
                var4 = var4.normalize();
                double var7 = var3.dotProduct(var4);

                if( (var7+.1) > 1.0D - 0.025D / var5 ?  player.canEntityBeSeen(ent) : false) {
                    teleportToEntity(stack, ent, player);
                    stack.damageItem(3, player);
                    return ActionResult.newResult(EnumActionResult.PASS, stack);
                }
            }

            RayTraceResult hit = this.rayTrace(world, player, false);
            if (hit != null) {
                BlockPos pos = hit.getBlockPos();

                TileEntity tile = world.getTileEntity(pos);

                if (tile != null && tile instanceof TileEntityRift) {
                    if (((TileEntityRift) tile).isPaired()) {
                        if (player.canPlayerEdit(pos, hit.sideHit, stack) && player.canPlayerEdit(pos.offset(EnumFacing.UP), hit.sideHit, stack))
                        {
                            EnumFacing orientation = EnumFacing.fromAngle(player.rotationYaw).getOpposite();

                            if (ItemDoorBase.canPlace(world, pos) && ItemDoorBase.canPlace(world, pos.offset(EnumFacing.DOWN))) {
                                ItemDimDoor.placeDoor(world, pos.offset(EnumFacing.DOWN), orientation, ModBlocks.blockDimDoorTransient, true);
                                stack.damageItem(3, player);
                                return ActionResult.newResult(EnumActionResult.PASS, stack);
                            }
                        }
                    }
                }
            }
        }
        return ActionResult.newResult(EnumActionResult.PASS, stack);
    }


    /**
     * Return whether this item is repairable in an anvil.
     */
    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        //Don't include a call to super.getIsRepairable()!
        //That would cause this sword to accept diamonds as a repair material (since we set material = Diamond).
        return ModItems.itemStableFabric == par2ItemStack.getItem() ? true : false;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        DimDoors.translateAndAdd("info.riftblade", list);
    }
}
