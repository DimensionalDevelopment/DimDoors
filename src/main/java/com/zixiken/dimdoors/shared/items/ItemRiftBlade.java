package com.zixiken.dimdoors.shared.items;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.tileentities.TileEntityFloatingRift;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.RayTraceHelper;
import com.zixiken.dimdoors.shared.util.TeleportUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by Jared Johnson on 1/20/2017.
 */
public class ItemRiftBlade extends ItemSword {

    public static final String ID = "rift_blade";

    public ItemRiftBlade() {
        super(ToolMaterial.DIAMOND);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return ModItems.STABLE_FABRIC == repair.getItem();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        if (worldIn.isRemote) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        //SchematicHandler.Instance.getPersonalPocketTemplate().place(0, 20, 0, 20, 0, 0, 1, EnumPocketType.DUNGEON); //this line can be activated for testing purposes
        RayTraceResult hit = rayTrace(worldIn, playerIn, true);
        if (RayTraceHelper.isRift(hit, worldIn)) {
            TileEntityFloatingRift rift = (TileEntityFloatingRift) worldIn.getTileEntity(hit.getBlockPos());
            rift.teleport(playerIn);

            stack.damageItem(1, playerIn);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);

        } else if (RayTraceHelper.isLivingEntity(hit)) {
            TeleportUtils.teleport(playerIn, new Location(worldIn, hit.getBlockPos()), playerIn.rotationYaw, playerIn.rotationPitch); //@todo teleport to a location 1 or 2 blocks distance from the entity
            stack.damageItem(1, playerIn); // TODO: check if successful
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        DimDoors.translateAndAdd("info.rift_blade", tooltip);
    }
}
