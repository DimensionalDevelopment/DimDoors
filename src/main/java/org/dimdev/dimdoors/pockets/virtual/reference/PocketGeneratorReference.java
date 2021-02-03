package org.dimdev.dimdoors.pockets.virtual.reference;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.pockets.SchematicV2Handler;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.virtual.VirtualSingularPocket;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.math.StringEquationParser;
import org.dimdev.dimdoors.world.pocket.Pocket;

import java.util.List;

public abstract class PocketGeneratorReference extends VirtualSingularPocket {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String defaultWeightEquation = "5"; // TODO: make config
	private static final int fallbackWeight = 5; // TODO: make config

	private String weight;
	private StringEquationParser.Equation weightEquation;
	private Boolean setupLoot;
	private final List<Modifier> modifierList = Lists.newArrayList();


	private void parseWeight() {
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
	}

	@Override
	public VirtualSingularPocket fromTag(CompoundTag tag) {
		if (tag.contains("weight")) { // override referenced pockets weight
			this.weight = tag.getString("weight");
			parseWeight();
		}

		if (tag.contains("setup_loot")) setupLoot = tag.getBoolean("setup_loot");

		if (tag.contains("modifiers")) {
			ListTag modifiersTag = tag.getList("modifiers", 10);
			for (int i = 0; i < modifiersTag.size(); i++) {
				modifierList.add(Modifier.deserialize(modifiersTag.getCompound(i)));
			}
		}

		return null;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		if (weight != null) tag.putString("weight", weight);

		if (setupLoot != null) tag.putBoolean("setup_loot", setupLoot);

		ListTag modifiersTag = new ListTag();
		for (Modifier modifier : modifierList) {
			modifiersTag.add(modifier.toTag(new CompoundTag()));
		}
		if (modifiersTag.size() > 0) tag.put("modifiers", modifiersTag);

		return tag;
	}

	@Override
	public double getWeight(PocketGenerationParameters parameters) {
		return weightEquation != null ? this.weightEquation.apply(parameters.toVariableMap(Maps.newHashMap())) : peekReferencedPocket(parameters).getWeight(parameters);
	}

	public void applyModifiers(Pocket pocket, PocketGenerationParameters parameters) {
		for (Modifier modifier : modifierList) {
			modifier.apply(pocket, parameters);
		}
	}

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationParameters parameters) {
		PocketGenerator generator = getReferencedPocket(parameters);
		Pocket pocket = generator.prepareAndPlacePocket(parameters);
		generator.applyModifiers(pocket, parameters);
		this.applyModifiers(pocket, parameters);
		generator.setup(pocket, parameters, setupLoot != null ? setupLoot : true);
		return pocket;
	}

	public abstract PocketGenerator peekReferencedPocket(PocketGenerationParameters parameters);

	public abstract PocketGenerator getReferencedPocket(PocketGenerationParameters parameters);
}
