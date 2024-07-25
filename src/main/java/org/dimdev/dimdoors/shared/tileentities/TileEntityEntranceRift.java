package org.dimdev.dimdoors.shared.tileentities;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.ddutils.WorldUtils;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.ddutils.RGBA;
import org.dimdev.ddutils.TeleportUtils;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import org.dimdev.dimdoors.shared.blocks.BlockDimensionalDoor;

import java.util.Objects;
import java.util.Random;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static org.dimdev.dimdoors.DimDoors.proxy;
import static org.dimdev.dimdoors.shared.ModConfig.general;

@NBTSerializable public class TileEntityEntranceRift extends TileEntityRift {
    
    @Saved @Getter protected boolean leaveRiftOnBreak = false;
    @Saved @Getter protected boolean closeAfterPassThrough = false; // TODO: doesn't make sense lore-wise for doors, split into separate tile entity

    // Set by the block on tile entity creation, can't get from the block, it's not necessarily a door
    @Saved public EnumFacing orientation;

    // Render info, use += to change these on block tile entity creation
    @SideOnly(CLIENT) public double extendUp;
    @SideOnly(CLIENT) public double extendDown;
    @SideOnly(CLIENT) public double extendLeft;
    @SideOnly(CLIENT) public double extendRight;
    @SideOnly(CLIENT) public double pushIn;
    @SideOnly(CLIENT) public byte lockStatus; // TODO

    public TileEntityEntranceRift() {
        if(proxy.isClient()) {
            this.extendUp = this.extendDown = this.extendLeft = this.extendRight = 0.5d;
            this.pushIn = 0.01d;
            this.lockStatus = 0;
        }
    }

    @Override
    public void copyFrom(TileEntityRift oldRift) {
        super.copyFrom(oldRift);
        if(oldRift instanceof TileEntityEntranceRift) {
            TileEntityEntranceRift oldEntrance = (TileEntityEntranceRift) oldRift;
            this.closeAfterPassThrough = oldEntrance.closeAfterPassThrough;
        }
        this.leaveRiftOnBreak = true;
    }

    @Override public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTUtils.readFromNBT(this, nbt);
    }
    
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);
        return NBTUtils.writeToNBT(this, nbt);
    }

    public void setLeaveRiftOnBreak(boolean leaveRiftOnBreak) {
        this.leaveRiftOnBreak = leaveRiftOnBreak;
        markDirty();
    }
    
    public void setLockStatus(byte lockStatus) {
        this.lockStatus = lockStatus;
        markDirty();
    }
    
    public void setCloseAfterPassThrough(boolean closeAfterPassThrough) {
        this.closeAfterPassThrough = closeAfterPassThrough;
        markDirty();
    }

    @Override
    public boolean teleport(Entity entity) {
        boolean status = super.teleport(entity);
        if(this.riftStateChanged && !this.alwaysDelete) {
            this.leaveRiftOnBreak = true;
            markDirty();
        }
        return status;
    }

    @Override
    public boolean receiveEntity(Entity entity, float relativeYaw, float relativePitch) { // TODO: teleportOffset for all rifts instead?
        Vec3d targetPos = new Vec3d(this.pos).add(0.5,0,0.5)
                .add(new Vec3d(this.orientation.getDirectionVec()).scale(general.teleportOffset+0.5d));
        int dimension = WorldUtils.getDim(this.world);
        Entity teleported;
        if(this.relativeRotation) {
            float yaw = getDestinationYaw(entity.rotationYaw) + entity.rotationYaw - relativeYaw;
            float pitch = entity instanceof EntityLiving ? entity.rotationPitch :
                    getDestinationPitch(entity.rotationPitch)+entity.rotationPitch-relativePitch;
            teleported = TeleportUtils.teleport(entity,dimension,targetPos.x,targetPos.y,targetPos.z,yaw,pitch);
            // TODO: velocity
        } else teleported = TeleportUtils.teleport(entity,dimension,targetPos.x,targetPos.y,targetPos.z,
                                      this.orientation.getHorizontalAngle(),0);
        return Objects.isNull(teleported) || entity!=teleported || teleported.dimension==dimension;
    }

    // Use vanilla behavior of refreshing only when block changes, not state (otherwise, opening the door would destroy the tile entity)
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        // newState is not accurate if we change the state during onBlockBreak
        newSate = world.getBlockState(pos);
        return oldState.getBlock()!=newSate.getBlock();
    }

    @SideOnly(CLIENT)
    public RGBA[] getColors(int count) { // TODO: cache this
        Random rand = new Random(31100L);
        RGBA[] colors = new RGBA[count];
        for(int i=0; i<count; i++) colors[i] = getEntranceRenderColor(rand);
        return colors;
    }

    @SideOnly(CLIENT)
    protected RGBA getEntranceRenderColor(Random rand) {
        float red,green,blue;
        if(this.world.provider.getDimension()==-1) { // Nether
            red = rand.nextFloat()*0.5f+0.4f;
            green = rand.nextFloat()*0.05f;
            blue = rand.nextFloat()*0.05f;
        } else {
            red = rand.nextFloat()*0.5f+0.1f;
            green = rand.nextFloat()*0.4f+0.4f;
            blue = rand.nextFloat()*0.6f+0.5f;
        }
        return new RGBA(red,green,blue,1);
    }

    @Override
    public boolean isFloating() {
        return false;
    }

    @Override
    public float getSourceYaw(float entityYaw) {
        return this.orientation.getOpposite().getHorizontalAngle();
    }

    @Override
    public float getSourcePitch(float entityPitch) {
        return this.orientation.getOpposite().getYOffset()*90;
    }

    @Override
    public float getDestinationYaw(float entityYaw) {
        return this.orientation.getHorizontalAngle();
    }

    @Override
    public float getDestinationPitch(float entityPitch) {
        return 0;
    }

    @SideOnly(CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        Block block = getBlockType();
        if(block instanceof BlockDimensionalDoor) return new AxisAlignedBB(this.pos,this.pos.add(1, 2, 1));
        return super.getRenderBoundingBox();
    }
}
