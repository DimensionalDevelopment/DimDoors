package org.dimdev.dimdoors.pockets.modifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.PocketEntranceMarker;
import org.dimdev.dimdoors.rift.targets.PocketExitMarker;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.math.StringEquationParser;
import org.dimdev.dimdoors.world.pocket.Pocket;

public class DimensionalDoorModifier implements Modifier {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "door";

	private Direction facing;
	private String doorTypeString;
	private DimensionalDoorBlock doorType;

	private String x;
	private String y;
	private String z;
	private StringEquationParser.Equation xEquation;
	private StringEquationParser.Equation yEquation;
	private StringEquationParser.Equation zEquation;


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

		try {
			x = tag.getString("x");
			y = tag.getString("y");
			z = tag.getString("z");

			xEquation = StringEquationParser.parse(x);
			yEquation = StringEquationParser.parse(y);
			zEquation = StringEquationParser.parse(z);
		} catch (StringEquationParser.EquationParseException e) {
			LOGGER.error(e);
		}
		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		Modifier.super.toTag(tag);

		tag.putString("facing", facing.asString());
		tag.putString("door_type", doorTypeString);
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
	public void apply(Pocket pocket, PocketGenerationParameters parameters) {
		Map<String, Double> variableMap = pocket.toVariableMap(new HashMap<>());
		BlockPos pocketOrigin = pocket.getOrigin();
		BlockPos pos = new BlockPos(xEquation.apply(variableMap) + pocketOrigin.getX(), yEquation.apply(variableMap) + pocketOrigin.getY(), zEquation.apply(variableMap) + pocketOrigin.getZ());

		ServerWorld world = parameters.getWorld();
		BlockState lower = doorType.getDefaultState().with(DimensionalDoorBlock.HALF, DoubleBlockHalf.LOWER).with(DimensionalDoorBlock.FACING, facing);
		world.setBlockState(pos, lower);
		world.setBlockState(pos.up(), doorType.getDefaultState().with(DimensionalDoorBlock.HALF, DoubleBlockHalf.UPPER).with(DimensionalDoorBlock.FACING, facing));

		// TODO: make the rifts be built more dynamically
		EntranceRiftBlockEntity rift = ModBlockEntityTypes.ENTRANCE_RIFT.instantiate();
		rift.setDestination(PocketEntranceMarker.builder().ifDestination(new PocketExitMarker()).weight(1f).build());
		rift.setProperties(LinkProperties.builder().entranceWeight(1f).groups(Collections.singleton(1)).floatingWeight(1f).linksRemaining(1).oneWay(false).build());
		world.setBlockEntity(pos, rift);
	}
}
