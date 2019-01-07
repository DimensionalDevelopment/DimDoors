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
        setTranslationKey(ID);
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
        RayTraceResult hit = RayTraceHelper.rayTraceEntity(world, player, 16, 1.0F); //TODO: make the range of the Rift Blade configurable
        if (hit == null) {
            hit = RayTraceHelper.rayTraceForRiftTools(world, player);
        }

        if (world.isRemote) {
            if (RayTraceHelper.isLivingEntity(hit) || RayTraceHelper.isRift(hit, world)) {
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            } else {
                player.sendStatusMessage(new TextComponentTranslation(getRegistryName() + ".rift_miss"), true);
                TileEntityFloatingRiftRenderer.showRiftCoreUntil = System.currentTimeMillis() + ModConfig.graphics.highlightRiftCoreFor;
                return new ActionResult<>(EnumActionResult.FAIL, stack);
            }
        }

        if (RayTraceHelper.isLivingEntity(hit)) {
            double damageMultiplier = (double) stack.getItemDamage() /  (double) stack.getMaxDamage();
            // TODO: gaussian, instead or random
            double offsetDistance = Math.random() * damageMultiplier * 7 + 2; //TODO: make these offset distances configurable
            double offsetRotationYaw = (Math.random() - 0.5) * damageMultiplier * 360;            
            
            Vec3d playerVec = player.getPositionVector();
            Vec3d entityVec = hit.hitVec;
            Vec3d offsetDirection = playerVec.subtract(entityVec).normalize();
            offsetDirection = offsetDirection.rotateYaw((float) (offsetRotationYaw * Math.PI) / 180);

            BlockPos tpPos = new BlockPos(entityVec.add(offsetDirection.scale(offsetDistance)));
            while (world.getBlockState(tpPos).getMaterial().blocksMovement()) tpPos = tpPos.up(); // TODO: move to ddutils
            TeleportUtils.teleport(player, new Location(world, tpPos), (player.rotationYaw - (float) offsetRotationYaw) % 360, player.rotationPitch);
            
            stack.damageItem(1, player);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        } else if (RayTraceHelper.isRift(hit, world)) {
            TileEntityRift rift = (TileEntityRift) world.getTileEntity(hit.getBlockPos());
            rift.teleport(player);

            stack.damageItem(1, player);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(I18n.format(getRegistryName() + ".info"));
    }
}
