package org.dimdev.dimdoors.pockets.virtual;

import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.pockets.TemplateUtils;
import org.dimdev.dimdoors.pockets.virtual.modifier.Modifier;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.math.StringEquationParser;
import org.dimdev.dimdoors.world.pocket.Pocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public abstract class VirtualGeneratorPocket extends VirtualSingularPocket {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String defaultWeightEquation = "5"; // TODO: make config
	private static final int fallbackWeight = 5; // TODO: make config
	private final List<Modifier> modifierList = new ArrayList<>();

	private String weight;
	private StringEquationParser.Equation weightEquation;

	public VirtualGeneratorPocket() { }

	public VirtualGeneratorPocket(String weight) {
		this.weight = weight;
		try {
			this.weightEquation = StringEquationParser.parse(weight);
		} catch (StringEquationParser.EquationParseException e) {
			LOGGER.error("Could not parse weight equation \"" + weight + "\", defaulting to weight \"5\"");
			this.weightEquation = (stringDoubleMap -> 5d);
		}
	}

	public VirtualGeneratorPocket fromTag(CompoundTag tag) {
		this.weight = tag.contains("weight") ? tag.getString("weight") : defaultWeightEquation;
		try {
			this.weightEquation = StringEquationParser.parse(weight);
		} catch (StringEquationParser.EquationParseException e) {
			LOGGER.error("Could not parse weight equation \"" + weight + "\", defaulting to default weight equation \"" + defaultWeightEquation + "\"", e);
			try {
				this.weightEquation = StringEquationParser.parse(defaultWeightEquation);
			} catch (StringEquationParser.EquationParseException equationParseException) {
				LOGGER.error("Could not parse default weight equation \"" + defaultWeightEquation + "\", defaulting to fallback weight \"" + fallbackWeight + "\"", equationParseException);
				this.weightEquation = stringDoubleMap -> fallbackWeight;
			}
		}

		if (tag.contains("modifiers")) {
			ListTag modifiersTag = tag.getList("modifiers", 10);
			for (int i = 0; i < modifiersTag.size(); i++) {
				modifierList.add(Modifier.deserialize(modifiersTag.getCompound(i)));
			}
		}
		return this;
	}

	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		if (!weight.equals("5")) tag.putString("weight", weight);

		ListTag modifiersTag = new ListTag();
		for (Modifier modifier : modifierList) {
			modifiersTag.add(modifier.toTag(new CompoundTag()));
		}
		if (modifiersTag.size() > 0) tag.put("modifiers", modifiersTag);
		return tag;
	}

	public void applyModifiers(Pocket pocket, PocketGenerationParameters parameters) {
		for (Modifier modifier : modifierList) {
			modifier.apply(pocket, parameters);
		}
	}

	public void setup(Pocket pocket, PocketGenerationParameters parameters, boolean setupLootTables) {
		ServerWorld world = DimensionalDoorsInitializer.getWorld(pocket.world);
		List<RiftBlockEntity> rifts = new ArrayList<>();

		pocket.getBlockEntities().forEach((blockPos, blockEntity) -> {
			if (blockEntity instanceof RiftBlockEntity) {
				LOGGER.debug("Rift found in pocket at " + blockPos);
				RiftBlockEntity rift = (RiftBlockEntity) blockEntity;
				rift.getDestination().setLocation(new Location((ServerWorld) Objects.requireNonNull(rift.getWorld()), rift.getPos()));
				rifts.add(rift);
			} else if (setupLootTables && blockEntity instanceof Inventory) {
				Inventory inventory = (Inventory) blockEntity;
				if (inventory.isEmpty()) {
					if (blockEntity instanceof ChestBlockEntity || blockEntity instanceof DispenserBlockEntity) {
						TemplateUtils.setupLootTable(world, blockEntity, inventory, LOGGER);
						if (inventory.isEmpty()) {
							LOGGER.error(", however Inventory is: empty!");
						}
					}
				}
			}
		});
		TemplateUtils.registerRifts(rifts, parameters.getLinkTo(), parameters.getLinkProperties(), pocket);
		pocket.virtualLocation = parameters.getSourceVirtualLocation(); //TODO: this makes very little sense
	}

	@Override
	public double getWeight(PocketGenerationParameters parameters){
		return this.weightEquation.apply(parameters.toVariableMap(new HashMap<>()));
	}
}
