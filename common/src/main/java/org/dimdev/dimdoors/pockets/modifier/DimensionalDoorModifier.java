package org.dimdev.dimdoors.pockets.modifier;

import com.google.common.base.MoreObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.NbtEquations;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.api.util.math.Equation.EquationParseException;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.rift.targets.IdMarker;
import org.dimdev.dimdoors.forge.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.forge.world.pocket.type.Pocket;

import java.util.HashMap;
import java.util.Map;

public class DimensionalDoorModifier extends AbstractLazyCompatibleModifier {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "door";

	private Direction facing;
	private String doorTypeString;
	private DimensionalDoorBlock doorType;
	private CompoundTag doorData;
	private String doorDataReference;

	private String x;
	private String y;
	private String z;
	private Equation xEquation;
	private Equation yEquation;
	private Equation zEquation;

	@Override
	public Modifier fromNbt(CompoundTag nbt, ResourceManager manager) {
		String facingString = nbt.getString("facing");
		facing = Direction.byName(nbt.getString("facing"));
		if (facing == null || facing.getAxis().isVertical()) {
			throw new RuntimeException("Could not interpret facing direction \"" + facingString + "\"");
		}

		doorTypeString = nbt.getString("door_type");
		Block doorBlock = Registry.BLOCK.get(ResourceLocation.tryParse(doorTypeString));
		if (!(doorBlock instanceof DimensionalDoorBlock)) {
			throw new RuntimeException("Could not interpret door type \"" + doorTypeString + "\"");
		}
		doorType = (DimensionalDoorBlock) doorBlock;

		// TODO: rift data via ResourceManager
		if (nbt.getTagType("rift_data") == Tag.TAG_STRING) {
			doorDataReference = nbt.getString("rift_data");
			doorData = PocketLoader.getInstance().getDataNbtCompound(doorDataReference);
		}

		else if (nbt.getTagType("rift_data") == Tag.TAG_COMPOUND) doorData = nbt.getCompound("rift_data");

		try {
			x = nbt.getString("x");
			y = nbt.getString("y");
			z = nbt.getString("z");

			xEquation = Equation.parse(x);
			yEquation = Equation.parse(y);
			zEquation = Equation.parse(z);
		} catch (EquationParseException e) {
			LOGGER.error(e);
		}
		return this;
	}

	@Override
	public CompoundTag toNbtInternal(CompoundTag nbt, boolean allowReference) {
		super.toNbtInternal(nbt, allowReference);

		nbt.putString("facing", facing.getSerializedName());
		nbt.putString("door_type", doorTypeString);
		if (doorDataReference != null) nbt.putString("rift_data", doorDataReference);
		else if (doorData != null) nbt.put("rift_data", doorData);
		nbt.putString("x", x);
		nbt.putString("y", y);
		nbt.putString("z", z);

		return nbt;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("facing", facing)
				.add("doorTypeString", doorTypeString)
				.add("doorType", doorType)
				.add("doorData", doorData)
				.add("doorDataReference", doorDataReference)
				.add("x", x)
				.add("y", y)
				.add("z", z)
				.add("xEquation", xEquation)
				.add("yEquation", yEquation)
				.add("zEquation", zEquation)
				.toString();
	}

	@Override
	public ModifierType<? extends Modifier> getType() {
		return ModifierType.DIMENSIONAL_DOOR_MODIFIER_TYPE.get();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public void apply(PocketGenerationContext parameters, RiftManager manager) {
		Map<String, Double> variableMap = manager.getPocket().toVariableMap(new HashMap<>());
		BlockPos pocketOrigin = manager.getPocket().getOrigin();
		BlockPos pos = new BlockPos((int) (xEquation.apply(variableMap) + pocketOrigin.getX()), (int) (yEquation.apply(variableMap) + pocketOrigin.getY()), (int) (zEquation.apply(variableMap) + pocketOrigin.getZ()));

		BlockState lower = doorType.defaultBlockState().setValue(DimensionalDoorBlock.HALF, DoubleBlockHalf.LOWER).setValue(DimensionalDoorBlock.FACING, facing);
		BlockState upper = doorType.defaultBlockState().setValue(DimensionalDoorBlock.HALF, DoubleBlockHalf.UPPER).setValue(DimensionalDoorBlock.FACING, facing);
		EntranceRiftBlockEntity rift = ModBlockEntityTypes.ENTRANCE_RIFT.get().create(pos, lower);
		rift.setLevel(parameters.world());

		if (doorData == null) {
			rift.setDestination(new IdMarker(manager.nextId()));
		} else {
			CompoundTag solvedDoorData = NbtEquations.solveNbtCompoundEquations(doorData, variableMap);
			rift.setData(RiftData.fromNbt(solvedDoorData));
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
