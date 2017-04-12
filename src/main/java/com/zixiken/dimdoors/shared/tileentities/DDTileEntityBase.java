package com.zixiken.dimdoors.shared.tileentities;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.EnumPocketType;
import com.zixiken.dimdoors.shared.Pocket;
import com.zixiken.dimdoors.shared.PocketRegistry;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.RiftRegistry;
import com.zixiken.dimdoors.shared.blocks.IDimDoor;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class DDTileEntityBase extends TileEntity implements ITickable {

    //Short life fields
    public boolean isTeleporting = false;
    public Entity teleportingEntity;

    //Class specific value fields
    protected boolean canRiftBePaired = true;

    //Need to be saved:
    protected boolean isPaired = false;
    protected int riftID = -1; //should not start at 0
    protected int pairedRiftID = -1;
    protected boolean isInPocket = false;
    protected int pocketID = -1;
    protected EnumPocketType pocketType;
    protected int depth = 0; //depth of the pocket it is in (not in a pocket -> 0)

    /**
     *
     * @param rand
     * @return an array of floats representing RGBA color where 1.0 = 255.
     */
    public abstract float[] getRenderColor(Random rand);

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    public boolean pair(int otherRiftID) { //should only ever be called from the RiftRegistry.pair method
        if (isPaired) {
            if (otherRiftID == pairedRiftID) {
                return true;
            } else {
                RiftRegistry.INSTANCE.unpair(pairedRiftID);
            }
        }
        pairedRiftID = otherRiftID;
        isPaired = true;
        RiftRegistry.INSTANCE.pair(pairedRiftID, riftID); //make sure it gets paired the other way around
        this.markDirty();
        return false;
    }

    public boolean unpair() { //should only ever be called from the RiftRegistry.unpair method
        if (!isPaired) {
            return true;
        } else {
            isPaired = false;
            RiftRegistry.INSTANCE.unpair(pairedRiftID);
            this.markDirty();
        }
        return false;
    }

    public void register(int depth) {
        if (riftID == -1) {
            riftID = RiftRegistry.INSTANCE.registerNewRift(this, depth);
            DimDoors.log(this.getClass(), "Finished registering rift as ID: " + riftID);

            this.markDirty();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        try {
            isPaired = nbt.getBoolean("isPaired");
            riftID = nbt.getInteger("riftID");
            pairedRiftID = nbt.getInteger("pairedRiftID");
            isInPocket = nbt.getBoolean("isInPocket");
            pocketID = nbt.getInteger("pocketID");
            if (nbt.hasKey("pocketType")) {
                pocketType = EnumPocketType.valueOf(nbt.getString("pocketType"));
            }
            depth = nbt.getInteger("depth");
        } catch (Exception e) {
            //reading these values should only fail on loading old saves, or loading old schematics, in which case the default values will do 
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("isPaired", this.isPaired);
        nbt.setInteger("riftID", this.riftID);
        nbt.setInteger("pairedRiftID", this.pairedRiftID);
        nbt.setBoolean("isInPocket", this.isInPocket);
        nbt.setInteger("pocketID", this.pocketID);
        if (pocketType != null) {
            nbt.setString("pocketType", this.pocketType.name());
        }
        nbt.setInteger("depth", this.depth);
        return nbt;
    }

    public void loadDataFrom(DDTileEntityBase rift2) {
        if (rift2 != null && rift2.riftID != -1) {
            isPaired = rift2.isPaired;
            riftID = rift2.riftID; //should not start at 0
            pairedRiftID = rift2.pairedRiftID;

            isInPocket = rift2.isInPocket;
            pocketID = rift2.pocketID;
            pocketType = rift2.pocketType;
            depth = rift2.depth;

            this.markDirty();
        }
    }

    public int getRiftID() {
        return riftID;
    }

    public int getPairedRiftID() {
        return pairedRiftID;
    }

    public boolean isPaired() {
        return isPaired;
    }

    public int getDepth() {
        return depth;
    }

    public Location getTeleportTargetLocation() {
        return new Location(this.getWorld().provider.getDimension(), this.getPos());
    }

    public abstract boolean tryTeleport(Entity entity);

    public void setPocket(int ID, EnumPocketType type) {
        pocketID = ID;
        pocketType = type;
        isInPocket = true;
        this.markDirty();
    }

    public void setIsInPocket() {
        isInPocket = true;
        this.markDirty();
    }

    protected EnumPocketType getPocketType() {
        return pocketType;
    }

    public void validatePlayerPocketEntry(EntityPlayer player) {
        if (!isInPocket || pocketType == EnumPocketType.PRIVATE) {
            return;
        } else {
            Pocket pocket = PocketRegistry.INSTANCE.getPocket(pocketID, pocketType);
            pocket.validatePlayerEntry(player);
        }
    }

    @Override
    public void update() {
        if (isTeleporting && teleportingEntity != null) {
            IDimDoor door = (IDimDoor) this.world.getBlockState(this.pos).getBlock();
            if (tryTeleport(teleportingEntity)) {
                //player is succesfully teleported
            } else {
                //probably should only happen on personal dimdoors?
                if (teleportingEntity instanceof EntityPlayer) {
                    EntityPlayer entityPlayer = (EntityPlayer) teleportingEntity;
                    DimDoors.chat(entityPlayer, "Teleporting failed, but since mod is still in alpha, stuff like that might simply happen.");
                }
            }
            isTeleporting = false;
            teleportingEntity = null;
        }
    }

    public int getPocketID() {
        return isInPocket ? pocketID : -1;
    }
}
