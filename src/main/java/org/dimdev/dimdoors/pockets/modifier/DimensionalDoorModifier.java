package org.dimdev.dimdoors.pockets.modifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.block.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.pockets.SchematicV2Handler;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.IdMarker;
import org.dimdev.dimdoors.rift.targets.PocketEntranceMarker;
import org.dimdev.dimdoors.rift.targets.PocketExitMarker;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.TagEquations;
import org.dimdev.dimdoors.util.math.Equation;
import org.dimdev.dimdoors.util.math.Equation.EquationParseException;
import org.dimdev.dimdoors.world.pocket.Pocket;

public class DimensionalDoorModifier implements Modifier {
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
			LOGGER.error("Could not interpret facing direction \"" + facingString + "\"");
			facing = Direction.NORTH;
		}

		doorTypeString = tag.getString("door_type");
		Block doorBlock = Registry.BLOCK.get(Identifier.tryParse(doorTypeString));
		if (!(doorBlock instanceof DimensionalDoorBlock)) {
			LOGGER.error("Could not interpret door type \"" + doorTypeString + "\"");
			doorBlock = ModBlocks.IRON_DIMENSIONAL_DOOR;
		}
		doorType = (DimensionalDoorBlock) doorBlock;

		if (tag.getType("door_data") == NbtType.STRING) {
			doorDataReference = tag.getString("door_data");
			doorData = (CompoundTag) SchematicV2Handler.getInstance().readNbtFromJson(doorDataReference);
		}
		else if (tag.getType("door_data") == NbtType.COMPOUND) doorData = tag.getCompound("door_data");

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
		Modifier.super.toTag(tag);

		tag.putString("facing", facing.asString());
		tag.putString("door_type", doorTypeString);
		if (doorDataReference != null) tag.putString("door_data", doorDataReference);
		else if (doorData != null) tag.put("door_data", doorData);
		tag.putString("x", x);
		tag.putString("y", y);
		tag.putString("z", z);

		return tag;
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
	public void apply(PocketGenerationParameters parameters, RiftManager manager) {
		Map<String, Double> variableMap = manager.getPocket().toVariableMap(new HashMap<>());
		BlockPos pocketOrigin = manager.getPocket().getOrigin();
		BlockPos pos = new BlockPos(xEquation.apply(variableMap) + pocketOrigin.getX(), yEquation.apply(variableMap) + pocketOrigin.getY(), zEquation.apply(variableMap) + pocketOrigin.getZ());

		ServerWorld world = parameters.getWorld();
		BlockState lower = doorType.getDefaultState().with(DimensionalDoorBlock.HALF, DoubleBlockHalf.LOWER).with(DimensionalDoorBlock.FACING, facing);
		world.setBlockState(pos, lower);
		world.setBlockState(pos.up(), doorType.getDefaultState().with(DimensionalDoorBlock.HALF, DoubleBlockHalf.UPPER).with(DimensionalDoorBlock.FACING, facing));

		EntranceRiftBlockEntity rift = ModBlockEntityTypes.ENTRANCE_RIFT.instantiate();

		if (doorData == null) {
			rift.setDestination(new IdMarker(manager.nextId()));
		} else {
			CompoundTag solvedDoorData = TagEquations.solveCompoundTagEquations(doorData, variableMap);
			rift.setData(RiftData.fromTag(solvedDoorData));
		}

		manager.add(rift);

		world.setBlockEntity(pos, rift);
	}
}
