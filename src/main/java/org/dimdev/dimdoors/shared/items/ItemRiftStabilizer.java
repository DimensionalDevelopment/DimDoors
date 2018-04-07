package org.dimdev.dimdoors.shared.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.client.TileEntityFloatingRiftRenderer;
import org.dimdev.dimdoors.shared.ModConfig;
import org.dimdev.dimdoors.shared.sound.ModSounds;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;

import java.util.List;

public class ItemRiftStabilizer extends Item {
    public static final String ID = "rift_stabilizer";

    public ItemRiftStabilizer() {
        setMaxStackSize(1);
        setMaxDamage(6); // TODO: Add more uses and make it reduce rift growth speed instead?
        setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        RayTraceResult hit = rayTrace(world, player, true);

        if (world.isRemote) {
            if (RayTraceHelper.isFloatingRift(hit, world)) {
                // TODO: not necessarily success, fix this and all other similar cases to make arm swing correct
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            } else {
                player.sendStatusMessage(new TextComponentTranslation("tools.rift_miss"), true);
                TileEntityFloatingRiftRenderer.showRiftCoreUntil = System.currentTimeMillis() + ModConfig.graphics.highlightRiftCoreFor;
                return new ActionResult<>(EnumActionResult.FAIL, stack);
            }
        }

        if (RayTraceHelper.isFloatingRift(hit, world)) {
            TileEntityFloatingRift rift = (TileEntityFloatingRift) world.getTileEntity(hit.getBlockPos());
            if (!rift.stabilized && !rift.closing) {
                rift.setStabilized(true);
                world.playSound(null, player.getPosition(), ModSounds.RIFT_CLOSE, SoundCategory.BLOCKS, 0.6f, 1); // TODO: different sound
                stack.damageItem(1, player);
                player.sendStatusMessage(new TextComponentTranslation(getUnlocalizedName() + ".stabilized"), true);
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            } else {
                player.sendStatusMessage(new TextComponentTranslation(getUnlocalizedName() + ".already_stabilized"), true);
            }
        }
        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(I18n.format(I18n.format(getUnlocalizedName() + ".info")));
    }
}
