package ddutils;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public final class WorldUtils {
    public static WorldServer getWorld(int dim) {
        return DimensionManager.getWorld(0).getMinecraftServer().getWorld(dim);
    }

    public static int getDim(World world) {
        return world.provider.getDimension();
    }
}
