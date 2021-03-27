package org.dimdev.dimdoors.pockets.modifier;

import java.util.HashMap;
import java.util.Map;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.nbt.CompoundTag;
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
import org.dimdev.dimdoors.api.util.TagEquations;
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
	private CompoundTag doorData;
	private String doorDataReference;

	private String x;
	private String y;
	private String z;
	private Equation xEquation;
	private Equation yEquation;
	private Equation zEquation;

	@Override
	public Modifier fromTag(CompoundTag tag) {
		String facingString = tag.getString("facing");
		facing = Direction.byName(tag.getString("facing"));
		if (facing == null || facing.getAxis().isVertical()) {
			throw new RuntimeException("Could not interpret facing direction \"" + facingString + "\"");
		}

		doorTypeString = tag.getString("door_type");
		Block doorBlock = Registry.BLOCK.get(Identifier.tryParse(doorTypeString));
		if (!(doorBlock instanceof DimensionalDoorBlock)) {
			throw new RuntimeException("Could not interpret door type \"" + doorTypeString + "\"");
		}
		doorType = (DimensionalDoorBlock) doorBlock;

		if (tag.getType("rift_data") == NbtType.STRING) {
			doorDataReference = tag.getString("rift_data");
			doorData = PocketLoader.getInstance().getDataCompoundTag(doorDataReference);
		}
		else if (tag.getType("rift_data") == NbtType.COMPOUND) doorData = tag.getCompound("rift_data");

		try {
			x = tag.getString("x");
			y = tag.getString("y");
			z = tag.getString("z");

			xEquation = Equation.parse(x);
			yEquation = Equation.parse(y);
			zEquation = Equation.parse(z);
		} catch (EquationParseException e) {
			LOGGER.error(e);
		}
		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		LazyCompatibleModifier.super.toTag(tag);

		tag.putString("facing", facing.asString());
		tag.putString("door_type", doorTypeString);
		if (doorDataReference != null) tag.putString("rift_data", doorDataReference);
		else if (doorData != null) tag.put("rift_data", doorData);
		tag.putString("x", x);
		tag.putString("y", y);
		tag.putString("z", z);

		return tag;
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
			CompoundTag solvedDoorData = TagEquations.solveCompoundTagEquations(doorData, variableMap);
			rift.setData(RiftData.fromTag(solvedDoorData));
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
			ServerWorld world = parameters.getWorld();

			world.setBlockState(pos, lower);
			world.setBlockState(pos.up(), upper);

			world.addBlockEntity(rift);
		}
	}

	@Override
	public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {

	}
}
