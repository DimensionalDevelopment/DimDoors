package org.dimdev.dimdoors.world.pocket;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.world.ModDimensions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
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

    private RegistryKey<World> world;
    private int x;
    private int z;
    private int depth;

    public VirtualLocation(RegistryKey<World> world, int x, int z, int depth) {
        this.world = world;
        this.x = x;
        this.z = z;
        this.depth = depth;
    }

    public void fromTag(CompoundTag tag) {
        VirtualLocation location = CODEC.decode(NbtOps.INSTANCE, tag).getOrThrow(false, System.err::println).getFirst();
        this.x = location.x;
        this.z = location.z;
        this.depth = location.depth;
        this.world = location.world;
    }

    public CompoundTag toTag(CompoundTag tag) {
        CompoundTag encodedTag = (CompoundTag) CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow(false, System.err::println);
        for (String key : encodedTag.getKeys()) {
            tag.put(key, encodedTag.get(key));
        }
        return tag;
    }

    public static VirtualLocation fromLocation(Location location) {
        VirtualLocation virtualLocation = null;

        if (ModDimensions.isDimDoorsPocketDimension(location.world)) {
            Pocket pocket = PocketRegistry.getInstance(location.world).getPocketAt(location.pos);
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

    public RegistryKey<World> getWorld() {
        return this.world;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public int getDepth() {
        return this.depth;
    }
}
