package org.dimdev.dimdoors.pockets.virtual.selection;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.*;

public class ConditionalSelector implements VirtualPocket {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "conditional";

	public static final MapCodec<ConditionalSelector> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ConditionPocket.CODEC.listOf().fieldOf("pockets").forGetter(a -> a.pocketMap)
	).apply(instance, ConditionalSelector::new));

	private List<ConditionPocket> pocketMap = Lists.newLinkedList();

	public ConditionalSelector() {
	}

	public ConditionalSelector(List<ConditionPocket> pocketMap) {
		this.pocketMap = pocketMap;
	}

//	public LinkedHashMap<String, VirtualPocket> getPocketMap() {
//		return pocketMap;
//	}

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationContext parameters) {
		return getNextPocket(parameters).prepareAndPlacePocket(parameters);
	}

	@Override
	public PocketGeneratorReference getNextPocketGeneratorReference(PocketGenerationContext parameters) {
		return getNextPocket(parameters).getNextPocketGeneratorReference(parameters);
	}

	@Override
	public PocketGeneratorReference peekNextPocketGeneratorReference(PocketGenerationContext parameters) {
		return getNextPocket(parameters).peekNextPocketGeneratorReference(parameters);
	}

	@Override
	public void init() {
		pocketMap.stream().map(ConditionPocket::pocket).forEach(VirtualPocket::init);
	}

	@Override
	public VirtualPocketType<? extends VirtualPocket> getType() {
		return VirtualPocketType.CONDITIONAL_SELECTOR.get();
	}

	@Override
	public double getWeight(PocketGenerationContext parameters) {
		return getNextPocket(parameters).getWeight(parameters);
	}

	private VirtualPocket getNextPocket(PocketGenerationContext parameters) {
		for (var entry : pocketMap) {
			if (entry.condition.asBoolean(parameters.toVariableMap(new HashMap<>()))) {
				return entry.pocket();
			}
		}
		return pocketMap.stream().findFirst().map(ConditionPocket::pocket).orElse(NoneVirtualPocket.NONE);
	}

	public record ConditionPocket(Equation condition, VirtualPocket pocket) {
		public static final Codec<ConditionPocket> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Equation.CODEC.fieldOf("condition").forGetter(a -> a.condition),
				VirtualPocket.CODEC_BASE.fieldOf("pocket").forGetter(a -> a.pocket)
		).apply(instance, ConditionPocket::new));
	}
}
