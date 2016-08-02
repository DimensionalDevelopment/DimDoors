package com.zixiken.dimdoors.items;

import java.util.List;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.blocks.IDimDoor;
import com.zixiken.dimdoors.core.DDLock;
import com.zixiken.dimdoors.core.DimLink;
import com.zixiken.dimdoors.core.PocketManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import com.zixiken.dimdoors.watcher.ClientLinkData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDDKey extends Item {
	public static final int TIME_TO_UNLOCK = 30;
    public static final String ID = "itemDDKey";

	public ItemDDKey() {
		super();
		setCreativeTab(DimDoors.dimDoorsCreativeTab);
		setMaxStackSize(1);
        setUnlocalizedName(ID);
	}

	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		if (DDLock.hasCreatedLock(stack)) tooltip.add(StatCollector.translateToLocal("info.riftkey.bound"));
		else tooltip.add(StatCollector.translateToLocal("info.riftkey.unbound"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {return !DDLock.hasCreatedLock(stack);}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, 
            EnumFacing side, float hitX, float hitY, float hitZ) {
		playerIn.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		return false;
	}

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
            EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) return false;
		if (player.inventory.getCurrentItem() != null) return true;
        
		// make sure we are dealing with a door
		if (!(world.getBlockState(pos).getBlock() instanceof IDimDoor)) return false;

		DimLink link = PocketManager.getLink(pos, world);
		// dont do anything to doors without links
		if (link == null) return false;

		// what to do if the door has a lock already
		if (link.hasLock()) {
			if (link.doesKeyUnlock(stack)) {
				if (link.getLockState()) world.playSoundAtEntity(player, DimDoors.MODID + ":keyUnlock", 1F, 1F);
				else world.playSoundAtEntity(player, DimDoors.MODID + ":keyLock", 1F, 1F);
				PocketManager.getDimensionData(world).lock(link, !link.getLockState());
				PocketManager.getLinkWatcher().update(new ClientLinkData(link));
			} else world.playSoundAtEntity(player, DimDoors.MODID + ":doorLocked", 1F, 1F);
		} else {
			if (!DDLock.hasCreatedLock(stack)) {
				world.playSoundAtEntity(player, DimDoors.MODID + ":keyLock", 1F, 1F);
				PocketManager.getDimensionData(world).createLock(link, stack, world.rand.nextInt(Integer.MAX_VALUE));
				PocketManager.getLinkWatcher().update(new ClientLinkData(link));
			}
		}
		return false;
	}

	/**
	 * Handle removal of locks here
	 */
	@Override
	public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer player, int heldTime) {
        if(world.isRemote) return;
		int j = this.getMaxItemUseDuration(itemStack) - heldTime;
		if (j >= TIME_TO_UNLOCK) {
			//Raytrace to make sure we are still looking at a door
			MovingObjectPosition pos = getMovingObjectPositionFromPlayer(player.worldObj, player, true);
			if (pos != null && pos.typeOfHit ==  MovingObjectPosition.MovingObjectType.BLOCK) {
				//make sure we have a link, it has a lock, and the given key is able to access the lock
				DimLink link = PocketManager.getLink(pos.getBlockPos(), player.worldObj);
				if (link != null && link.hasLock() && link.doesKeyUnlock(itemStack)) {
                    PocketManager.getDimensionData(world).removeLock(link, itemStack);
                    world.playSoundAtEntity(player, DimDoors.MODID + ":doorLockRemoved", 1F, 1F);
				}
			}
		}
		player.clearItemInUse();
	}

	/**
	 * Raytrace to make sure we are still looking at the right block while preparing to remove the lock
	 */
	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
		// no need to check every tick, twice a second instead
		if (count % 10 == 0) {
			MovingObjectPosition pos = getMovingObjectPositionFromPlayer(player.worldObj, player, true);
			if (pos != null && pos.typeOfHit ==  MovingObjectPosition.MovingObjectType.BLOCK) {
				DimLink link = PocketManager.getLink(pos.getBlockPos(), player.worldObj);
				if (link != null && link.hasLock() && link.doesKeyUnlock(stack)) return;
			}
			player.clearItemInUse();
		}
	}

	public EnumAction getItemUseAction(ItemStack par1ItemStack) {return EnumAction.BOW;}

	public int getMaxItemUseDuration(ItemStack par1ItemStack) {return 72000;}

	public String getItemStackDisplayName(ItemStack stack) {
		return StatCollector.translateToLocal(this.getUnlocalizedName(stack));
	}
}
