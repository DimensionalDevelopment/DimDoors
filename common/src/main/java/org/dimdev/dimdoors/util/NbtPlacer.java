package org.dimdev.dimdoors.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimcore.api.world.NbtPlacerUtil;

import java.util.Map;
import java.util.Optional;

public final class NbtPlacer {
    public static final Logger LOGGER = LogManager.getLogger();

    private NbtPlacer() {
    }

    public static void place(NbtPlacerUtil nbt, ServerLevel world, BlockPos origin) {
        placeBlocks(nbt, world, origin);
        placeEntities(nbt, world, origin);
    }

    public static void placeBlocks(NbtPlacerUtil nbt, ServerLevel world, BlockPos origin) {
        for (Map.Entry<BlockPos, Pair<BlockState, Optional<CompoundTag>>> entry : nbt.positions.entrySet()) {
            BlockPos pos = origin.offset(entry.getKey());
            BlockState state = entry.getValue().getFirst();

            world.setBlock(
                    pos,
                    state,
                    Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE
            );

            Optional<CompoundTag> blockEntityNbt = entry.getValue().getSecond();

            if (blockEntityNbt.isPresent()) {
                BlockEntity blockEntity = world.getBlockEntity(pos);

                if (blockEntity != null) {
                    CompoundTag tag = blockEntityNbt.get().copy();

                    tag.putInt("x", pos.getX());
                    tag.putInt("y", pos.getY());
                    tag.putInt("z", pos.getZ());

                    blockEntity.loadWithComponents(tag, world.registryAccess());
                    blockEntity.setChanged();

                    world.sendBlockUpdated(
                            pos,
                            state,
                            state,
                            Block.UPDATE_CLIENTS
                    );
                }
            }
        }
    }

    public static void placeEntities(NbtPlacerUtil structure, ServerLevel world, BlockPos origin) {
        for (Tag tag : structure.entities) {
            if (!(tag instanceof CompoundTag entityCompound)) {
                continue;
            }

            placeEntity(structure, entityCompound, world, origin);
        }
    }

    private static void placeEntity(
            NbtPlacerUtil structure,
            CompoundTag entityCompound,
            ServerLevel world,
            BlockPos origin
    ) {
        ListTag storedPos = entityCompound.getList("pos", Tag.TAG_DOUBLE);

        if (storedPos.size() < 3) {
            LOGGER.warn("Structure entity did not contain a valid position: {}", entityCompound);
            return;
        }

        Vec3 relativePosition = new Vec3(
                storedPos.getDouble(0),
                storedPos.getDouble(1),
                storedPos.getDouble(2)
        ).subtract(Vec3.atLowerCornerOf(structure.lowestPos));

        Vec3 realPosition = relativePosition.add(Vec3.atLowerCornerOf(origin));

        BlockPos relativeBlockPos = null;

        if (entityCompound.contains("blockPos", Tag.TAG_LIST)) {
            ListTag blockPosTag = entityCompound.getList("blockPos", Tag.TAG_INT);

            if (blockPosTag.size() >= 3) {
                relativeBlockPos = new BlockPos(
                        blockPosTag.getInt(0),
                        blockPosTag.getInt(1),
                        blockPosTag.getInt(2)
                ).subtract(structure.lowestPos);
            }
        }

        BlockPos realBlockPos = relativeBlockPos == null
                ? BlockPos.containing(realPosition)
                : origin.offset(relativeBlockPos);

        CompoundTag entityNbt = entityCompound.getCompound("nbt").copy();

        entityNbt.remove("Pos");
        entityNbt.remove("UUID");

        ListTag pos = new ListTag();
        pos.add(DoubleTag.valueOf(realPosition.x));
        pos.add(DoubleTag.valueOf(realPosition.y));
        pos.add(DoubleTag.valueOf(realPosition.z));
        entityNbt.put("Pos", pos);

        boolean hasTilePos =
                entityNbt.contains("TileX", Tag.TAG_INT)
                        && entityNbt.contains("TileY", Tag.TAG_INT)
                        && entityNbt.contains("TileZ", Tag.TAG_INT);

        if (hasTilePos) {
            entityNbt.putInt("TileX", realBlockPos.getX());
            entityNbt.putInt("TileY", realBlockPos.getY());
            entityNbt.putInt("TileZ", realBlockPos.getZ());
        }

        Optional<Entity> entityOptional = EntityType.create(entityNbt, world);

        if (entityOptional.isEmpty()) {
            LOGGER.warn("Failed to create structure entity: {}", entityNbt);
            return;
        }

        Entity entity = entityOptional.get();

        if (entity instanceof HangingEntity hangingEntity) {
            if (entity instanceof Painting painting) {
                realBlockPos = getPaintingAnchor(painting, realPosition);
            }

            hangingEntity.setPos(
                    realBlockPos.getX(),
                    realBlockPos.getY(),
                    realBlockPos.getZ()
            );
        } else {
            entity.moveTo(
                    realPosition.x,
                    realPosition.y,
                    realPosition.z,
                    entity.getYRot(),
                    entity.getXRot()
            );
        }

        world.addFreshEntityWithPassengers(entity);
    }

    private static BlockPos getPaintingAnchor(Painting painting, Vec3 center) {
        Direction direction = painting.getDirection();
        Direction counterClockWise = direction.getCounterClockWise();

        double widthOffset =
                painting.getVariant().value().width() % 2 == 0
                        ? 0.5D
                        : 0.0D;

        double heightOffset =
                painting.getVariant().value().height() % 2 == 0
                        ? 0.5D
                        : 0.0D;

        return BlockPos.containing(
                center.x()
                        + direction.getStepX() * 0.46875D
                        - counterClockWise.getStepX() * widthOffset
                        - 0.5D,
                center.y()
                        - heightOffset
                        - 0.5D,
                center.z()
                        + direction.getStepZ() * 0.46875D
                        - counterClockWise.getStepZ() * widthOffset
                        - 0.5D
        );
    }
}