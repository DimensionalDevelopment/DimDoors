package org.dimdev.dimdoors.world.pocket;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.annotatednbt.AnnotatedNbt;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.world.ModDimensions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import static net.minecraft.world.World.OVERWORLD;

public class VirtualLocation {
    public static Codec<VirtualLocation> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    World.CODEC.fieldOf("world").forGetter(virtualLocation -> virtualLocation.world),
                    Codec.INT.fieldOf("x").forGetter(virtualLocation -> virtualLocation.x),
                    Codec.INT.fieldOf("z").forGetter(virtualLocation -> virtualLocation.z),
                    Codec.INT.fieldOf("depth").forGetter(virtualLocation -> virtualLocation.depth)
            ).apply(instance, VirtualLocation::new)
    );


    public final RegistryKey<World> world;

    public final int x;

    public final int z;

    public final int depth;

    public VirtualLocation(RegistryKey<World> world, int x, int z, int depth) {
        this.world = world;
        this.x = x;
        this.z = z;
        this.depth = depth;
    }

    public void fromTag(CompoundTag nbt) {
        AnnotatedNbt.load(this, nbt);
    }

    public CompoundTag toTag(CompoundTag nbt) {
        AnnotatedNbt.save(this, nbt);
        return nbt;
    }

    public static VirtualLocation fromLocation(Location location) {
        VirtualLocation virtualLocation = null;

        if (ModDimensions.isDimDoorsPocketDimension(location.world)) {
            Pocket pocket = PocketRegistry.instance(location.world).getPocketAt(location.pos);
            if (pocket != null) {
                virtualLocation = pocket.virtualLocation; // TODO: pockets-relative coordinates
            } else {
                virtualLocation = null; // TODO: door was placed in a pockets dim but outside of a pockets...
            }
        } else if (ModDimensions.isLimboDimension(location.getWorld())) { // TODO: convert to interface on worldprovider
            virtualLocation = new VirtualLocation(location.world, location.getX(), location.getZ(), ModConfig.INSTANCE.getDungeonsConfig().maxDungeonDepth);
        } // TODO: nether coordinate transform

        if (virtualLocation == null) {
            return new VirtualLocation(OVERWORLD, location.getX(), location.getZ(), 5);
        }

        return virtualLocation;
    }

    public Location projectToWorld(boolean acceptLimbo) {
        ServerWorld world = DimensionalDoorsInitializer.getServer().getWorld(this.world);

        if (!acceptLimbo && ModDimensions.isLimboDimension(world)) {
            world = world.getServer().getWorld(OVERWORLD);
        }

        float spread = ModConfig.INSTANCE.getGeneralConfig().depthSpreadFactor * this.depth;
        int newX = (int) (this.x + spread * 2 * (Math.random() - 0.5));
        int newZ = (int) (this.z + spread * 2 * (Math.random() - 0.5));
        BlockPos pos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, new BlockPos(newX, 0, newZ));
        return new Location(world, pos);
    }
}
