package org.dimdev.dimdoors.pockets.virtual.selection;

import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.VirtualSingularPocket;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.math.Equation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConditionalSelector extends VirtualSingularPocket {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "conditional";
	/*
	private static final Codec<Pair<String, VirtualPocket>> PAIR_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("regex").forGetter(Pair::getLeft),
			VirtualPocket.CODEC.fieldOf("pocket").forGetter(Pair::getRight)
	).apply(instance, Pair::new));

	public static final Codec<DepthDependentSelector> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("id").forGetter(DepthDependentSelector::getName),
			PAIR_CODEC.listOf().fieldOf("pockets").forGetter(DepthDependentSelector::getPocketList)
	).apply(instance, DepthDependentSelector::new));
	 */



	private LinkedHashMap<String, VirtualPocket> pocketMap = Maps.newLinkedHashMap();
	private LinkedHashMap<String, Equation> equationMap = Maps.newLinkedHashMap();

	public ConditionalSelector() {
	}

	public ConditionalSelector(LinkedHashMap<String, VirtualPocket> pocketMap) {
		this.pocketMap = pocketMap;
	}

	public LinkedHashMap<String, VirtualPocket> getPocketMap() {
		return pocketMap;
	}

	@Override
	public VirtualSingularPocket fromTag(CompoundTag tag) {
		ListTag conditionalPockets = tag.getList("pockets", 10);
		for (int i = 0; i < conditionalPockets.size(); i++) {
			CompoundTag pocket = conditionalPockets.getCompound(i);
			String condition = pocket.getString("condition");
			if (pocketMap.containsKey(condition)) continue;
			try {
				equationMap.put(condition, Equation.parse(condition));
				pocketMap.put(condition, VirtualPocket.deserialize(pocket.get("pocket")));
			} catch (Equation.EquationParseException e) {
				LOGGER.error("Could not parse pocket condition equation!", e);
			}
		}
		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		ListTag conditionalPockets = new ListTag();
		pocketMap.forEach((condition, pocket) -> {
			CompoundTag compound = new CompoundTag();
			compound.putString("condition", condition);
			compound.put("pocket", VirtualPocket.serialize(pocket));
			conditionalPockets.add(compound);
		});
		tag.put("pockets", conditionalPockets);
		return tag;
	}

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationParameters parameters) {
		return getNextPocket(parameters).prepareAndPlacePocket(parameters);
	}

	@Override
	public PocketGeneratorReference getNextPocketGeneratorReference(PocketGenerationParameters parameters) {
		return getNextPocket(parameters).getNextPocketGeneratorReference(parameters);
	}

	@Override
	public PocketGeneratorReference peekNextPocketGeneratorReference(PocketGenerationParameters parameters) {
		return getNextPocket(parameters).peekNextPocketGeneratorReference(parameters);
	}

	@Override
	public VirtualSingularPocketType<? extends VirtualSingularPocket> getType() {
		return VirtualSingularPocketType.CONDITIONAL_SELECTOR;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public double getWeight(PocketGenerationParameters parameters) {
		return getNextPocket(parameters).getWeight(parameters);
	}

	private VirtualPocket getNextPocket(PocketGenerationParameters parameters) {
		for (Map.Entry<String, VirtualPocket> entry : pocketMap.entrySet()) {
			if (equationMap.get(entry.getKey()).asBoolean(parameters.toVariableMap(new HashMap<>()))) {
				return entry.getValue();
			}
		}
		return pocketMap.values().stream().findFirst().orElse(NoneVirtualPocket.NONE);
	}
}
