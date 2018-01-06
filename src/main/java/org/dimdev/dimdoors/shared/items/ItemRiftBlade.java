package org.dimdev.dimdoors.shared.items;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;
import org.dimdev.ddutils.I18nUtils;
import org.dimdev.ddutils.Location;
import org.dimdev.dimdoors.shared.RayTraceHelper;
import org.dimdev.ddutils.TeleportUtils;
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
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (world.isRemote) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        //SchematicHandler.Instance.getPersonalPocketTemplate().place(0, 20, 0, 20, 0, 0, 1, EnumPocketType.DUNGEON); //this line can be activated for testing purposes
        RayTraceResult hit = rayTrace(world, player, true);
        if (RayTraceHelper.isFloatingRift(hit, world)) {
            TileEntityFloatingRift rift = (TileEntityFloatingRift) world.getTileEntity(hit.getBlockPos());
            rift.teleport(player);

            stack.damageItem(1, player);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);

        } else if (RayTraceHelper.isLivingEntity(hit)) {
            TeleportUtils.teleport(player, new Location(world, hit.getBlockPos()), player.rotationYaw, player.rotationPitch); //@todo teleport to a location 1 or 2 blocks distance from the entity
            stack.damageItem(1, player); // TODO: check if successful
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.addAll(I18nUtils.translateMultiline("info.rift_blade"));
    }
}
