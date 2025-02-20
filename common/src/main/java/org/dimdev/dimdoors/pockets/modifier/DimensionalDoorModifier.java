package org.dimdev.dimdoors.pockets.modifier;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.GeneralUtil;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.rift.targets.IdMarker;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.HashMap;
import java.util.Map;

public class DimensionalDoorModifier extends AbstractLazyCompatibleModifier {
	public static final MapCodec<DimensionalDoorModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			GeneralUtil.HORIZONTAL_DIRECTION_CODEC.fieldOf("facing").forGetter(a -> a.facing),
			GeneralUtil.DIMENSIONAL_DOOR_BLOCK_CODEC.fieldOf("door_type").forGetter(a -> a.doorType),
			RiftData.CODEC.fieldOf("rift_data").forGetter(a -> a.doorData),
			Equation.CODEC.fieldOf("x").forGetter(a -> a.x),
			Equation.CODEC.fieldOf("y").forGetter(a -> a.y),
			Equation.CODEC.fieldOf("z").forGetter(a -> a.z)
			).apply(instance, DimensionalDoorModifier::new)
	);

	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "door";

	private final Direction facing;
    private final DimensionalDoorBlock doorType;
	private final RiftData doorData;

	private final Equation x;
	private final Equation y;
	private final Equation z;

	public DimensionalDoorModifier(Direction facing, DimensionalDoorBlock block, RiftData doorData, Equation x, Equation y, Equation z) {
        this.facing = facing;
        this.doorType = block;
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
		return ModifierType.DIMENSIONAL_DOOR_MODIFIER_TYPE.get();
	}

	@Override
	public void apply(PocketGenerationContext parameters, RiftManager manager) {
		Map<String, Double> variableMap = manager.getPocket().toVariableMap(new HashMap<>());
		BlockPos pocketOrigin = manager.getPocket().getOrigin();
		BlockPos pos = new BlockPos((int) (x.apply(variableMap) + pocketOrigin.getX()), (int) (y.apply(variableMap) + pocketOrigin.getY()), (int) (z.apply(variableMap) + pocketOrigin.getZ()));

		BlockState lower = doorType.defaultBlockState().setValue(DimensionalDoorBlock.HALF, DoubleBlockHalf.LOWER).setValue(DimensionalDoorBlock.FACING, facing);
		BlockState upper = doorType.defaultBlockState().setValue(DimensionalDoorBlock.HALF, DoubleBlockHalf.UPPER).setValue(DimensionalDoorBlock.FACING, facing);
		EntranceRiftBlockEntity rift = ModBlockEntityTypes.ENTRANCE_RIFT.get().create(pos, lower);
		rift.setLevel(parameters.world());

		if (doorData == null) {
			rift.setDestination(new IdMarker(manager.nextId()));
		} else {
			RiftData solvedDoorData = doorData; //NbtEquations.solveNbtCompoundEquations(doorData, variableMap);
			rift.setData(solvedDoorData);//RiftData.fromNbt(solvedDoorData));
		}

		manager.add(rift);

		if (manager.getPocket() instanceof LazyGenerationPocket) {

			// queue two separate tasks, Cubic Chunks may cause the positions to be in different chunks.
			queueChunkModificationTask(new ChunkPos(pos), chunk -> {
				chunk.setBlockState(pos, lower, false);
				chunk.setBlockEntity(rift);
			});
			queueChunkModificationTask(new ChunkPos(pos.above()), chunk -> {
				chunk.setBlockState(pos.above(), upper, false);
			});
		} else {
			ServerLevel world = parameters.world();

			world.setBlockAndUpdate(pos, lower);
			world.setBlockAndUpdate(pos.above(), upper);

			world.setBlockEntity(rift);
		}
	}

	@Override
	public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {

	}
}
