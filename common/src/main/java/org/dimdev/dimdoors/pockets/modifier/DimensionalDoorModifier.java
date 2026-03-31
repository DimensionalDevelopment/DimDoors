package org.dimdev.dimdoors.pockets.modifier;

import com.bedrockk.molang.Expression;
import com.google.common.base.MoreObjects;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.rift.targets.IdMarker;
import org.dimdev.dimdoors.util.MolangUtils;

import java.util.HashMap;

public class DimensionalDoorModifier implements Modifier {
    public static final MapCodec<DimensionalDoorModifier> CODEC = RecordCodecBuilder.<DimensionalDoorModifier>mapCodec(instance -> instance.group(
            Direction.CODEC.fieldOf("facing").flatXmap(
                            direction -> direction.getAxis().isHorizontal() ? DataResult.success(direction) : DataResult.error(() -> "Direction:" + direction.name() + "is not horizontal."),
                            DataResult::success
                    ).forGetter(a -> a.facing),
            BuiltInRegistries.BLOCK.holderByNameCodec().flatXmap(blockHolder -> {
                if (blockHolder.unwrap().right().filter(DimensionalDoorBlock.class::isInstance).isPresent()) {
                    return DataResult.success(blockHolder);
                } else {
                    return DataResult.<Holder<Block>>error(() -> blockHolder.getRegisteredName() + " is not an instance of DimensionalDoorBlock.");
                }
            }, DataResult::success).fieldOf("doorType").forGetter(a -> a.doorType),
            RiftData.CODEC.fieldOf("rift_data").forGetter(a -> a.doorData),
            MolangUtils.CODEC.fieldOf("x").forGetter(a -> a.x),
            MolangUtils.CODEC.fieldOf("y").forGetter(a -> a.y),
            MolangUtils.CODEC.fieldOf("z").forGetter(a -> a.z)
    ).apply(instance, DimensionalDoorModifier::new));
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "door";

	private Direction facing;
	private String doorTypeString;
	private Holder<Block> doorType;
	private Holder<RiftData> doorData;

	private Expression x;
	private Expression y;
	private Expression z;

    public DimensionalDoorModifier(Direction facing, Holder<Block> doorType, Holder<RiftData> doorData, Expression x, Expression y, Expression z) {
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
				.add("doorTypeString", doorTypeString)
				.add("doorType", doorType)
				.add("doorData", doorData)
				.add("x", x)
				.add("y", y)
				.add("z", z)
				.toString();
	}

	@Override
	public ModifierType<? extends Modifier> getType() {
		return ModifierType.DIMENSIONAL_DOOR_MODIFIER_TYPE.get();
	}

    @Override
	public void apply(PocketGenerationContext parameters, RiftManager manager) {
        var variableMap = manager.getPocket().toVariableMap(new HashMap<>());
        var pos = new BlockPos((int) MolangUtils.evaulateDouble(x, variableMap), (int) MolangUtils.evaulateDouble(y, variableMap), (int) MolangUtils.evaulateDouble(z, variableMap));

        BlockState lower = doorType.value().defaultBlockState().setValue(DimensionalDoorBlock.HALF, DoubleBlockHalf.LOWER).setValue(DimensionalDoorBlock.FACING, facing);
        BlockState upper = doorType.value().defaultBlockState().setValue(DimensionalDoorBlock.HALF, DoubleBlockHalf.UPPER).setValue(DimensionalDoorBlock.FACING, facing);
        EntranceRiftBlockEntity rift = ModBlockEntityTypes.ENTRANCE_RIFT.get().create(pos, lower);
        rift.setLevel(parameters.world());

        if (doorData == null) {
            rift.setDestination(new IdMarker(manager.nextId()));
        } else {
            rift.setData(doorData);
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