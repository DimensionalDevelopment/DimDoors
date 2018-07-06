package org.dimdev.dimdoors.shared.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.client.TileEntityFloatingRiftRenderer;
import org.dimdev.dimdoors.shared.ModConfig;

import java.util.List;
import net.minecraft.util.math.BlockPos;
import org.dimdev.dimdoors.client.DDGUIHandler;

public class ItemRiftConfigurationTool extends Item {

    public static final String ID = "rift_configuration_tool";

    ItemRiftConfigurationTool() {
        setMaxStackSize(1);
        setMaxDamage(16);
        setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        RayTraceResult hit = RayTraceHelper.rayTraceForRiftTools(world, player);

        if (world.isRemote) {
            if (!RayTraceHelper.isRift(hit, world)) {
                player.sendStatusMessage(new TextComponentTranslation("tools.rift_miss"), true);
                TileEntityFloatingRiftRenderer.showRiftCoreUntil = System.currentTimeMillis() + ModConfig.graphics.highlightRiftCoreFor;
            } else {
                BlockPos hitPos = hit.getBlockPos();
                player.openGui(DimDoors.instance, DDGUIHandler.RIFT, world, hitPos.getX(), hitPos.getY(), hitPos.getZ());
            }
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        if (RayTraceHelper.isRift(hit, world)) {
            //TODO: implement this tool's functionality
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (I18n.hasKey(getUnlocalizedName() + ".info")) {
            tooltip.add(I18n.format(getUnlocalizedName() + ".info"));
        }
    }
}
