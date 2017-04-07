package com.zixiken.dimdoors.shared.items;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.RayTraceHelper;
import com.zixiken.dimdoors.shared.TeleporterDimDoors;
import com.zixiken.dimdoors.shared.tileentities.TileEntityRift;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
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
        if (world.isRemote) {
            return new ActionResult(EnumActionResult.FAIL, stack);
        }
        //SchematicHandler.Instance.getPersonalPocketTemplate().place(0, 20, 0, 20, 0, 0, 1, EnumPocketType.DUNGEON); //this line can be activated for testing purposes
        RayTraceResult hit = rayTrace(world, player, true);
        if (RayTraceHelper.isRift(hit, world)) {
            TileEntityRift rift = (TileEntityRift) world.getTileEntity(hit.getBlockPos());
            rift.isTeleporting = true;
            rift.teleportingEntity = player;

            stack.damageItem(1, player);
            return new ActionResult(EnumActionResult.SUCCESS, stack);

        } else if (RayTraceHelper.isLivingEntity(hit)) {
            EnumActionResult teleportResult = TeleporterDimDoors.instance().teleport(player, new Location(world, hit.getBlockPos())) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL; //@todo teleport to a location 1 or 2 blocks distance from the entity
            if (teleportResult == EnumActionResult.SUCCESS) {
                stack.damageItem(1, player);
            }
            return new ActionResult(teleportResult, stack);
        }

        return new ActionResult(EnumActionResult.FAIL, stack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        DimDoors.translateAndAdd("info.riftblade", list);
    }
}
