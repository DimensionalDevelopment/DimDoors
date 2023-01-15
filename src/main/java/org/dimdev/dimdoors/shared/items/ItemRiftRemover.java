package org.dimdev.dimdoors.shared.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.client.TileEntityFloatingRiftRenderer;
import org.dimdev.dimdoors.shared.ModConfig;
import org.dimdev.dimdoors.shared.sound.ModSounds;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.List;

public class ItemRiftRemover extends Item {
    public static final String ID = "rift_remover";

    public ItemRiftRemover() {
        setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        setRegistryName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        setMaxStackSize(1);
        setMaxDamage(100);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (I18n.hasKey(getRegistryName() + ".info")) {
            tooltip.add(I18n.format(getRegistryName() + ".info"));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        RayTraceResult hit = RayTraceHelper.rayTraceForRiftTools(world, player);

        if (world.isRemote) {
            if (!RayTraceHelper.isFloatingRift(hit, world)) {
                player.sendStatusMessage(new TextComponentTranslation("tools.rift_miss"), true);
                TileEntityFloatingRiftRenderer.showRiftCoreUntil = System.currentTimeMillis() + ModConfig.graphics.highlightRiftCoreFor;
            }
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        if (RayTraceHelper.isFloatingRift(hit, world)) {
            TileEntityFloatingRift rift = (TileEntityFloatingRift) world.getTileEntity(hit.getBlockPos());
            if (!rift.closing) {
                rift.setClosing(true);
                world.playSound(null, player.getPosition(), ModSounds.RIFT_CLOSE, SoundCategory.BLOCKS, 0.6f, 1);
                stack.damageItem(10, player);
                player.sendStatusMessage(new TextComponentTranslation(getRegistryName() + ".closing"), true);
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            } else {
                player.sendStatusMessage(new TextComponentTranslation(getRegistryName() + ".already_closing"), true);
            }
        }
        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }
}
