package org.dimdev.dimdoors.shared.tileentities;

import lombok.Setter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.dimdoors.shared.blocks.BlockFloatingRift;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import java.util.List;
import java.util.Random;

import org.dimdev.dimdoors.shared.rifts.TileEntityRift;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

@NBTSerializable public class TileEntityFloatingRift extends TileEntityRift implements ITickable {

    private static final int ENDERMAN_SPAWNING_CHANCE = 1;
    private static final int MAX_ENDERMAN_SPAWNING_CHANCE = 32;
    private static final int HOSTILE_ENDERMAN_CHANCE = 1;
    private static final int MAX_HOSTILE_ENDERMAN_CHANCE = 3;
    private static final int UPDATE_PERIOD = 200; //10 seconds

    private static final Random random = new Random();

    //Need to be saved:
    @Saved /*package-private*/ int updateTimer;
    @Saved public boolean shouldClose = false; // TODO
    @Saved public int spawnedEndermenID = 0;
    @Saved public float growth = 0;

    @Setter private boolean unregisterDisabled = false;

    @SideOnly(Side.CLIENT)
    public double renderAngle; // This is @SideOnly(Side.CLIENT), don't initialize the field ( = 0), or class initialization won't work on the server!

    public TileEntityFloatingRift() {
        updateTimer = random.nextInt(UPDATE_PERIOD);
    }

    @Override
    public void update() {
        if (world.getBlockState(pos).getBlock() != ModBlocks.RIFT) {
            invalidate();
            return;
        }

        // Check if this rift should render white closing particles and
        // spread the closing effect to other rifts nearby.
        if (shouldClose) {
            closeRift();
            return;
        }

        if (updateTimer >= UPDATE_PERIOD) {
            spawnEndermen();
            updateTimer = 0;
        } else if (updateTimer == UPDATE_PERIOD / 2) {
            updateNearestRift();
        }
        growth += 1F / (growth + 1);
        updateTimer++;
    }

    private void spawnEndermen() {
        if (world.isRemote) {
            return;
        }

        // Ensure that this rift is only spawning one Enderman at a time, to prevent hordes of Endermen
        Entity entity = world.getEntityByID(spawnedEndermenID);
        if (entity instanceof EntityEnderman) {
            return;
        }

        if (random.nextInt(MAX_ENDERMAN_SPAWNING_CHANCE) < ENDERMAN_SPAWNING_CHANCE) {
            // Endermen will only spawn from groups of rifts
            if (updateNearestRift()) {
                List<EntityEnderman> list = world.getEntitiesWithinAABB(EntityEnderman.class,
                        new AxisAlignedBB(pos.getX() - 9, pos.getY() - 3, pos.getZ() - 9, pos.getX() + 9, pos.getY() + 3, pos.getZ() + 9));

                if (list.isEmpty()) {
                    EntityEnderman enderman = new EntityEnderman(world);
                    enderman.setLocationAndAngles(pos.getX() + 0.5, pos.getY() - 1, pos.getZ() + 0.5, 5, 6);
                    world.spawnEntity(enderman);

                    if (random.nextInt(MAX_HOSTILE_ENDERMAN_CHANCE) < HOSTILE_ENDERMAN_CHANCE) {
                        EntityPlayer player = world.getClosestPlayerToEntity(enderman, 50);
                        if (player != null) {
                            enderman.setAttackTarget(player);
                        }
                    }
                }
            }
        }
    }

    private void closeRift() {
        world.setBlockToAir(pos);
        growth--; //@todo?
    }

    public boolean updateNearestRift() {
        return false;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        // newState is not accurate if we change the state during onBlockBreak
        newSate = world.getBlockState(pos);
        return oldState.getBlock() != newSate.getBlock() &&
               !(world.getTileEntity(pos) instanceof TileEntityRift && // This is to prevent setBlockState clearing the tile entity right after we set it in breakBlock
                 newSate.getBlock() instanceof BlockFloatingRift);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return NBTUtils.writeToNBT(this, nbt); }

    @Override
    public boolean isFloating() {
        return true;
    }

    @Override
    public void unregister() {
        if (!unregisterDisabled) super.unregister();
    }
}
