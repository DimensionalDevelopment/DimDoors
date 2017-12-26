package ddutils;

import java.io.Serializable;

import lombok.ToString;
import lombok.Value;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

/**
 * @author Robijnvogel
 */
@ToString @Value
public class Location implements Serializable {

    int dim;
    private BlockPos pos;

    public Location(World world, BlockPos pos) {
        this(world.provider.getDimension(), pos);
    }

    public Location(World world, int x, int y, int z) {
        this(world, new BlockPos(x, y, z));
    }

    public Location(int dim, int x, int y, int z) {
        this(dim, new BlockPos(x, y, z));
    }

    public Location(int dim, BlockPos pos) {
        this.dim = dim;
        this.pos = pos;
    }

    public int getX() { return pos.getX(); }
    public int getY() { return pos.getY(); }
    public int getZ() { return pos.getZ(); }

    public TileEntity getTileEntity() {
        return getWorld().getTileEntity(pos);
    }

    public IBlockState getBlockState() {
        return getWorld().getBlockState(this.pos);
    }

    public WorldServer getWorld() {
        return DimensionManager.getWorld(dim);
    }

    public static Location getLocation(TileEntity tileEntity) {
        World world = tileEntity.getWorld();
        BlockPos blockPos = tileEntity.getPos();
        return new Location(world, blockPos);
    }

    public static Location getLocation(Entity entity) {
        World world = entity.world;
        BlockPos blockPos = entity.getPosition();
        return new Location(world, blockPos);
    }

    public static NBTTagCompound writeToNBT(Location location) {
        NBTTagCompound locationNBT = new NBTTagCompound();
        locationNBT.setInteger("worldID", location.dim);
        locationNBT.setInteger("x", location.pos.getX());
        locationNBT.setInteger("y", location.pos.getY());
        locationNBT.setInteger("z", location.pos.getZ());
        return locationNBT;
    }

    public static Location readFromNBT(NBTTagCompound locationNBT) {
        int worldID = locationNBT.getInteger("worldID");
        int x = locationNBT.getInteger("x");
        int y = locationNBT.getInteger("y");
        int z = locationNBT.getInteger("z");
        BlockPos blockPos = new BlockPos(x, y, z);
        return new Location(worldID, blockPos);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Location)) {
            return false;
        }
        Location other = (Location) obj;
        return other.dim == dim && other.pos.equals(pos);
    }

    @Override
    public int hashCode() {
        return pos.hashCode() * 31 + dim; // TODO
    }
}
