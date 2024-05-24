package org.dimdev.dimdoors.pockets.virtual.selection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.AbstractVirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.HashMap;
import java.util.List;

public class ConditionalSelector extends AbstractVirtualPocket {
	public static MapCodec<ConditionalSelector> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
			.and(Codec.list(Condition.CODEC).optionalFieldOf("conditions", Lists.newArrayList()).forGetter(a -> a.conditions))
			.apply(instance, ConditionalSelector::new));

	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "conditional";

	private List<Condition> conditions;

	public ConditionalSelector(String resourceString, List<Condition> conditions) {
		super(resourceString);
		this.conditions = conditions;
	}


	public List<Condition> getConditions() {
		return conditions;
	}

	@Override
	public ImplementedVirtualPocket fromNbt(CompoundTag nbt, ResourceManager manager) {
		ListTag conditionalPockets = nbt.getList("pockets", 10);
		for (int i = 0; i < conditionalPockets.size(); i++) {
			CompoundTag pocket = conditionalPockets.getCompound(i);
			String condition = pocket.getString("condition");
			if (pocketMap.containsKey(condition)) continue;
			try {
				equationMap.put(condition, Equation.parse(condition));
				pocketMap.put(condition, VirtualPocket.deserialize(pocket.get("pocket"), manager));
			} catch (Equation.EquationParseException e) {
				LOGGER.error("Could not parse pocket condition equation!", e);
			}
		}
		return this;
	}

	@Override
	public CompoundTag toNbtInternal(CompoundTag nbt) {
		super.toNbtInternal(nbt);

		ListTag conditionalPockets = new ListTag();
		pocketMap.forEach((condition, pocket) -> {
			CompoundTag compound = new CompoundTag();
			compound.putString("condition", condition);
			compound.put("pocket", VirtualPocket.serialize(pocket));
			conditionalPockets.add(compound);
		});
		nbt.put("pockets", conditionalPockets);
		return nbt;
	}

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
		conditions.values().stream().map(Condition::pocket).forEach(VirtualPocket::init);
	}

	@Override
	public VirtualPocketType<? extends ImplementedVirtualPocket> getType() {
		return VirtualPocketType.CONDITIONAL_SELECTOR.get();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public double getWeight(PocketGenerationContext parameters) {
		return getNextPocket(parameters).getWeight(parameters);
	}

	private VirtualPocket getNextPocket(PocketGenerationContext parameters) {
		for (var entry : conditions) {
			if (entry.equation.asBoolean(parameters.toVariableMap(new HashMap<>()))) {
				return entry.pocket();
			}
		}
		return conditions.stream().map(Condition::pocket).findFirst().orElse(NoneVirtualPocket.NONE);
	}

	public record Condition(Equation equation, VirtualPocket pocket) {
		public static Codec<Condition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Equation.CODEC.fieldOf("equation").forGetter(Condition::equation),
				VirtualPocket.CODEC.fieldOf("pocket").forGetter(Condition::pocket))
				.apply(instance, Condition::new));

	}
}
