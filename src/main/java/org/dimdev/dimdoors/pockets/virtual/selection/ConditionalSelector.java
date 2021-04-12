package org.dimdev.dimdoors.pockets.virtual.selection;

import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class ConditionalSelector implements ImplementedVirtualPocket {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "conditional";

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
	public ImplementedVirtualPocket fromNbt(NbtCompound nbt) {
		NbtList conditionalPockets = nbt.getList("pockets", 10);
		for (int i = 0; i < conditionalPockets.size(); i++) {
			NbtCompound pocket = conditionalPockets.getCompound(i);
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
	public NbtCompound toNbt(NbtCompound nbt) {
		ImplementedVirtualPocket.super.toNbt(nbt);

		NbtList conditionalPockets = new NbtList();
		pocketMap.forEach((condition, pocket) -> {
			NbtCompound compound = new NbtCompound();
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
		pocketMap.values().forEach(VirtualPocket::init);
	}

	@Override
	public VirtualPocketType<? extends ImplementedVirtualPocket> getType() {
		return VirtualPocketType.CONDITIONAL_SELECTOR;
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
		for (Map.Entry<String, VirtualPocket> entry : pocketMap.entrySet()) {
			if (equationMap.get(entry.getKey()).asBoolean(parameters.toVariableMap(new HashMap<>()))) {
				return entry.getValue();
			}
		}
		return pocketMap.values().stream().findFirst().orElse(NoneVirtualPocket.NONE);
	}
}
