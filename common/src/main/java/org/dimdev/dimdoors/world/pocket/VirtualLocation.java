package org.dimdev.dimdoors.world.pocket;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;

import static net.minecraft.world.level.Level.OVERWORLD;

public record VirtualLocation(ResourceKey<Level> world, int x, int z, int depth) {
    public static Codec<VirtualLocation> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(VirtualLocation::world),
                    Codec.INT.fieldOf("x").forGetter(VirtualLocation::x),
                    Codec.INT.fieldOf("z").forGetter(VirtualLocation::z),
                    Codec.INT.fieldOf("depth").forGetter(VirtualLocation::depth)
            ).apply(instance, VirtualLocation::new)
    );

    public static CompoundTag toNbt(VirtualLocation virtualLocation) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("world", virtualLocation.world.location().toString());
        nbt.putInt("x", virtualLocation.x);
        nbt.putInt("z", virtualLocation.z);
        nbt.putInt("depth", virtualLocation.depth);
        return nbt;
    }

    public static VirtualLocation fromNbt(CompoundTag nbt) {
        return new VirtualLocation(
                ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(nbt.getString("world"))),
                nbt.getInt("x"),
                nbt.getInt("z"),
                nbt.getInt("depth")
        );
    }

    public static VirtualLocation fromLocation(Location location) {
        VirtualLocation virtualLocation = null;

        if (ModDimensions.isPocketDimension(location.world)) {
            Pocket pocket = DimensionalRegistry.getPocketDirectory().getPocketFromWorld(location.getWorldId());
            if (pocket != null) {
                virtualLocation = pocket.virtualLocation; // TODO: pockets-relative coordinates
            }
        } else if (ModDimensions.isLimboDimension(location.getWorld())) {
            virtualLocation = new VirtualLocation(location.world, location.getX(), location.getZ(), DimensionalDoors.getConfig().getDungeonsConfig().maxDungeonDepth);
        } else if (location.getWorld() != null) {
            virtualLocation = new VirtualLocation(location.world, location.getX(), location.getY(), 5);
        }

        if (virtualLocation == null) {
            return new VirtualLocation(OVERWORLD, location.getX(), location.getZ(), 5);
        }
        return new VirtualLocation(location.getWorldId(), location.getX(), location.getZ(), virtualLocation.depth());
    }

    public Location projectToWorld(boolean acceptLimbo) {
        ServerLevel world = DimensionalDoors.getServer().getLevel(this.world);

        if (!acceptLimbo && ModDimensions.isLimboDimension(world)) {
            world = world.getServer().overworld();
        }

        float spread = DimensionalDoors.getConfig().getGeneralConfig().depthSpreadFactor * this.depth;
        int newX = (int) (this.x + spread * 2 * (Math.random() - 0.5));
        int newZ = (int) (this.z + spread * 2 * (Math.random() - 0.5));
        //BlockPos pos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, new BlockPos(newX, 1, newZ));
        BlockPos pos = getTopPos(world, newX, newZ).above();
        return new Location(world, pos);
    }

    public static BlockPos getTopPos(Level world, int x, int z) {
        int topHeight = world.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z)) // guarantees WorldChunk
                .getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
        return new BlockPos(x, topHeight, z);
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("world", this.world)
                .add("x", this.x)
                .add("z", this.z)
                .add("depth", this.depth)
                .toString();
    }
}
