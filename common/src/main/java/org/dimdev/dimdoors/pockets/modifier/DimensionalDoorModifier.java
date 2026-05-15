package org.dimdev.dimdoors.pockets.modifier;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.rift.targets.IdMarker;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.HashMap;
import java.util.Map;

public class DimensionalDoorModifier implements Modifier {
    public static final MapCodec<DimensionalDoorModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Direction.CODEC.fieldOf("facing").flatXmap(
                    direction -> direction.getAxis().isHorizontal() ? DataResult.success(direction) : DataResult.error(() -> "Direction:" + direction.name() + "is not horizontal."),
                    DataResult::success
            ).forGetter(a -> a.facing),
            BuiltInRegistries.BLOCK.holderByNameCodec().flatXmap(blockHolder -> {
                if (blockHolder.unwrap().right().filter(DimensionalDoorBlock.class::isInstance).isPresent()) {
                    return DataResult.success(blockHolder);
                } else {
                    return DataResult.error(() -> blockHolder.getRegisteredName() + " is not an instance of DimensionalDoorBlock.");
                }
            }, DataResult::success).fieldOf("doorType").forGetter(a -> a.doorType),
            RiftData.HOLDER_CODEC.fieldOf("rift_data").forGetter(a -> a.doorData),
            Equation.CODEC.fieldOf("x").forGetter(a -> a.x),
            Equation.CODEC.fieldOf("y").forGetter(a -> a.y),
            Equation.CODEC.fieldOf("z").forGetter(a -> a.z)
    ).apply(instance, DimensionalDoorModifier::new));

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String KEY = "door";

    private Direction facing;
    private Holder<Block> doorType;
    private Holder<RiftData> doorData;

    private Equation x;
    private Equation y;
    private Equation z;

    public DimensionalDoorModifier(Direction facing, Holder<Block> doorType, Holder<RiftData> doorData, Equation x, Equation y, Equation z) {
        this.facing = facing;
        this.doorType = doorType;
        this.doorData = doorData;
        this.x = x;
        this.y = y;
        this.z = z;

    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("facing", facing)
                .add("doorType", doorType)
                .add("doorData", doorData)
                .add("x", x)
                .add("y", y)
                .add("z", z)
                .toString();
    }

    @Override
    public ModifierType<? extends Modifier> getType() {
        return ModifierType.DIMENSIONAL_DOOR_MODIFIER_TYPE;
    }

    @Override
    public void apply(PocketGenerationContext parameters, RiftManager manager) {
        Map<String, Double> variableMap = manager.getPocket().toVariableMap(new HashMap<>());
        BlockPos pocketOrigin = manager.getPocket().getOrigin();
        BlockPos pos = new BlockPos((int) (x.apply(variableMap) + pocketOrigin.getX()), (int) (y.apply(variableMap) + pocketOrigin.getY()), (int) (z.apply(variableMap) + pocketOrigin.getZ()));

        var state = doorType.value().defaultBlockState();

        BlockState lower = state.setValue(DimensionalDoorBlock.HALF, DoubleBlockHalf.LOWER).setValue(DimensionalDoorBlock.FACING, facing);
        BlockState upper = state.setValue(DimensionalDoorBlock.HALF, DoubleBlockHalf.UPPER).setValue(DimensionalDoorBlock.FACING, facing);
        EntranceRiftBlockEntity rift = ModBlockEntityTypes.ENTRANCE_RIFT.create(pos, lower);
        rift.setLevel(parameters.world());

        if (doorData == null) {
            rift.setDestination(new IdMarker(manager.nextId()));
        } else {
            rift.setData(doorData.value());
        }

        manager.add(rift);

        ServerLevel world = parameters.world();

        world.setBlockAndUpdate(pos, lower);
        world.setBlockAndUpdate(pos.above(), upper);

        world.setBlockEntity(rift);
    }

    @Override
    public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {

    }
}