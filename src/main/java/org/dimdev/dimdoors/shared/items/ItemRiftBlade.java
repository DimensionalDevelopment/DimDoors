package org.dimdev.dimdoors.shared.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.client.TileEntityFloatingRiftRenderer;
import org.dimdev.dimdoors.shared.ModConfig;
import org.dimdev.ddutils.Location;
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
import org.dimdev.dimdoors.shared.tileentities.TileEntityRift;

import java.util.List;

public class ItemRiftBlade extends ItemSword {

    public static final String ID = "rift_blade";

    public ItemRiftBlade() {
        super(ToolMaterial.IRON);
        setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return ModItems.STABLE_FABRIC == repair.getItem();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        RayTraceResult hit = rayTrace(world, player, true);

        if (world.isRemote) {
            if (RayTraceHelper.isRift(hit, world) || RayTraceHelper.isLivingEntity(hit)) {
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            } else {
                player.sendStatusMessage(new TextComponentTranslation(getUnlocalizedName() + ".rift_miss"), true);
                TileEntityFloatingRiftRenderer.showRiftCoreUntil = System.currentTimeMillis() + ModConfig.graphics.highlightRiftCoreFor;
                return new ActionResult<>(EnumActionResult.FAIL, stack);
            }
        }

        if (RayTraceHelper.isRift(hit, world)) {
            TileEntityRift rift = (TileEntityRift) world.getTileEntity(hit.getBlockPos());
            rift.teleport(player);

            stack.damageItem(1, player);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);

        } else if (RayTraceHelper.isLivingEntity(hit)) {
            BlockPos hitPos = hit.getBlockPos();
            // TODO: gaussian, and depend on rift blade's wear
            int xDiff = (int) (5 * (Math.random() - 0.5));
            int zDiff = (int) (5 * (Math.random() - 0.5));
            BlockPos tpPos = new BlockPos(hitPos.getX() + xDiff, hitPos.getY(), hitPos.getZ() + zDiff);
            while (world.getBlockState(tpPos).getMaterial().blocksMovement()) tpPos = tpPos.up(); // TODO: move to ddutils
            TeleportUtils.teleport(player, new Location(world, tpPos), player.rotationYaw, player.rotationPitch);
            stack.damageItem(1, player);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(I18n.format(getUnlocalizedName() + ".info"));
    }
}
