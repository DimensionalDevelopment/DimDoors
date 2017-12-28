package org.dimdev.dimdoors.shared.items;

import lombok.ToString;
import lombok.Value;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.ddutils.Location;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.ddutils.I18nUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.dimdev.dimdoors.shared.blocks.IRiftProvider;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.rifts.GlobalDestination;
import org.dimdev.dimdoors.shared.rifts.RiftDestination;
import org.dimdev.dimdoors.shared.rifts.TileEntityRift;
import org.dimdev.dimdoors.shared.sound.ModSounds;

import java.util.List;

import static org.dimdev.ddutils.I18nUtils.translateAndAdd;

public class ItemRiftSignature extends Item {
    public static final String ID = "rift_signature";

    public ItemRiftSignature() {
        super();
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.hasSubtypes = true;
        this.setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        this.setUnlocalizedName(ID);
        this.setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.getItemDamage() != 0;
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        // We must use onItemUseFirst() instead of onItemUse() because Minecraft checks
        // whether the user is in creative mode after calling onItemUse() and undoes any
        // damage we might set to indicate the rift sig has been activated. Otherwise,
        // we would need to rely on checking NBT tags for hasEffect() and that function
        // gets called constantly. Avoiding NBT lookups reduces our performance impact.

        // Return false on the client side to pass this request to the server
        if (world.isRemote) {
            return EnumActionResult.FAIL;
        }

        ItemStack stack = player.getHeldItem(hand);

        //Increase y by 2 to place the rift at head level
        BlockPos adjustedPos = adjustYForSpecialBlocks(world, pos.up(2));
        if (!player.canPlayerEdit(adjustedPos, side, stack)) {
            return EnumActionResult.PASS;
        }

        Transform source = getSource(stack);
        int orientation = MathHelper.floor(((player.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D);

        if (source != null) {

            // Try placing a rift at the destination point
            world.setBlockState(adjustedPos, ModBlocks.RIFT.getDefaultState());
            TileEntityRift rift1 = (TileEntityRift) source.getLocation().getTileEntity();
            rift1.setSingleDestination(new GlobalDestination(source.getLocation()));
            rift1.setRotation(source.getYaw(), 0);
            rift1.register();

            // Try placing a rift at the source point
            // We don't need to check if sourceWorld is null - that's already handled.
            World sourceWorld = source.getLocation().getWorld();
            sourceWorld.setBlockState(source.location.getPos(), ModBlocks.RIFT.getDefaultState());
            TileEntityRift rift2 = (TileEntityRift) source.getLocation().getTileEntity();
            rift2.setSingleDestination(new GlobalDestination(source.getLocation()));
            rift2.setRotation(source.getYaw(), 0);
            rift2.register();


            if (!player.capabilities.isCreativeMode) {
                stack.setCount(stack.getCount() - 1);
            }

            clearSource(stack);
            DimDoors.chat(player, "Rift Created");
            world.playSound(player, player.getPosition(), ModSounds.RIFT_END, SoundCategory.BLOCKS, 0.6f, 1);
        } else {
            //The link signature has not been used. Store its current target as the first location.
            setSource(stack, new Transform(new Location(world, adjustedPos), orientation));
            DimDoors.chat(player, "Location Stored in Rift Signature");
            world.playSound(player, player.getPosition(), ModSounds.RIFT_START, SoundCategory.BLOCKS, 0.6f, 1);
        }

        return EnumActionResult.SUCCESS;
    }

    public static void setSource(ItemStack itemStack, Transform destination) {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setTag("destination", Transform.writeToNBT(destination));

        itemStack.setTagCompound(tag);
        itemStack.setItemDamage(1);
    }

    public static void clearSource(ItemStack itemStack) {
        //Don't just set the tag to null since there may be other data there (e.g. for renamed items)
        NBTTagCompound tag = itemStack.getTagCompound();
        tag.removeTag("destination");
        itemStack.setItemDamage(0);
    }

    public static Transform getSource(ItemStack itemStack) {
        if (itemStack.getItemDamage() != 0) {
            if (itemStack.hasTagCompound()) {
                NBTTagCompound tag = itemStack.getTagCompound();

                Transform transform = Transform.readFromNBT(tag.getCompoundTag("destination"));

                return transform;
            }

            // Mark the item as uninitialized if its source couldn't be read
            itemStack.setItemDamage(0);
        }

        return null;
    }

    public static BlockPos adjustYForSpecialBlocks(World world, BlockPos pos) {
        BlockPos target = pos.down(2); // Get the block the player actually clicked on
        Block block = world.getBlockState(target).getBlock();
        if (block == null) {
            return target.up(2);
        }
        if (block.isReplaceable(world, pos)) {
            return target.up(); // Move block placement down (-2+1) one so its directly over things like snow
        }
        if (block instanceof IRiftProvider) {
            if (((IRiftProvider) block).hasTileEntity(world.getBlockState(target))) {
                return target; // Move rift placement down two so its in the right place on the door.
            }
            // Move rift placement down one so its in the right place on the door.
            return target.up();
        }
        return target.up(2);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        Transform transform = getSource(stack);

        if (transform != null)
            tooltip.add(I18n.translateToLocalFormatted("info.rift_signature.bound", transform.getLocation().getX(), transform.getLocation().getY(), transform.getLocation().getZ(), transform.getLocation().getDim()));
        else translateAndAdd("info.rift_signature.unbound", tooltip);
    }

    @ToString
    @Value
    public static class Transform {
        private Location location;
        private int yaw;

        public Transform(Location location, int yaw) {
            this.location = location;
            this.yaw = yaw;
        }

        public Location getLocation() {
            return location;
        }

        public int getYaw() {
            return yaw;
        }

        public static NBTTagCompound writeToNBT(Transform transform) {
            NBTTagCompound transformNBT = new NBTTagCompound();

            transformNBT.setTag("location", Location.writeToNBT(transform.getLocation()));
            transformNBT.setInteger("yaw", transform.getYaw());
            return transformNBT;
        }

        public static Transform readFromNBT(NBTTagCompound transformNBT) {
            Location location = Location.readFromNBT(transformNBT);
            int yaw = transformNBT.getInteger("yaw");

            return new Transform(location, yaw);
        }

    }
}
