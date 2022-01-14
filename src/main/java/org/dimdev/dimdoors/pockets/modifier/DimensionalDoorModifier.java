package org.dimdev.dimdoors.pockets.modifier;

import java.util.HashMap;
import java.util.Map;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import com.google.common.base.MoreObjects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.rift.targets.IdMarker;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.api.util.NbtEquations;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.api.util.math.Equation.EquationParseException;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class DimensionalDoorModifier implements LazyCompatibleModifier {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "door";

	private Direction facing;
	private String doorTypeString;
	private DimensionalDoorBlock doorType;
	private NbtCompound doorData;
	private String doorDataReference;

	private String x;
	private String y;
	private String z;
	private Equation xEquation;
	private Equation yEquation;
	private Equation zEquation;

	@Override
	public Modifier fromNbt(NbtCompound nbt) {
		String facingString = nbt.getString("facing");
		facing = Direction.byName(nbt.getString("facing"));
		if (facing == null || facing.getAxis().isVertical()) {
			throw new RuntimeException("Could not interpret facing direction \"" + facingString + "\"");
		}

		doorTypeString = nbt.getString("door_type");
		Block doorBlock = Registry.BLOCK.get(Identifier.tryParse(doorTypeString));
		if (!(doorBlock instanceof DimensionalDoorBlock)) {
			throw new RuntimeException("Could not interpret door type \"" + doorTypeString + "\"");
		}
		doorType = (DimensionalDoorBlock) doorBlock;

		if (nbt.getType("rift_data") == NbtType.STRING) {
			doorDataReference = nbt.getString("rift_data");
			doorData = PocketLoader.getInstance().getDataNbtCompound(doorDataReference);
		}
		else if (nbt.getType("rift_data") == NbtType.COMPOUND) doorData = nbt.getCompound("rift_data");

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
	public NbtCompound toNbt(NbtCompound nbt) {
		LazyCompatibleModifier.super.toNbt(nbt);

		nbt.putString("facing", facing.asString());
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
		return ModifierType.DIMENSIONAL_DOOR_MODIFIER_TYPE;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public void apply(PocketGenerationContext parameters, RiftManager manager) {
		Map<String, Double> variableMap = manager.getPocket().toVariableMap(new HashMap<>());
		BlockPos pocketOrigin = manager.getPocket().getOrigin();
		BlockPos pos = new BlockPos(xEquation.apply(variableMap) + pocketOrigin.getX(), yEquation.apply(variableMap) + pocketOrigin.getY(), zEquation.apply(variableMap) + pocketOrigin.getZ());

		BlockState lower = doorType.getDefaultState().with(DimensionalDoorBlock.HALF, DoubleBlockHalf.LOWER).with(DimensionalDoorBlock.FACING, facing);
		BlockState upper = doorType.getDefaultState().with(DimensionalDoorBlock.HALF, DoubleBlockHalf.UPPER).with(DimensionalDoorBlock.FACING, facing);
		EntranceRiftBlockEntity rift = ModBlockEntityTypes.ENTRANCE_RIFT.instantiate(pos, lower);

		if (doorData == null) {
			rift.setDestination(new IdMarker(manager.nextId()));
		} else {
			NbtCompound solvedDoorData = NbtEquations.solveNbtCompoundEquations(doorData, variableMap);
			rift.setData(RiftData.fromNbt(solvedDoorData));
		}

		manager.add(rift);

		if (manager.getPocket() instanceof LazyGenerationPocket) {

			// queue two separate tasks, Cubic Chunks may cause the positions to be in different chunks.
			queueChunkModificationTask(new ChunkPos(pos), chunk -> {
				chunk.setBlockState(pos, lower, false);
				chunk.setBlockEntity(rift);
			});
			queueChunkModificationTask(new ChunkPos(pos.up()), chunk -> {
				chunk.setBlockState(pos.up(), upper, false);
			});
		} else {
			ServerWorld world = parameters.world();

			world.setBlockState(pos, lower);
			world.setBlockState(pos.up(), upper);

			world.addBlockEntity(rift);
		}
	}

	@Override
	public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {

	}
}
