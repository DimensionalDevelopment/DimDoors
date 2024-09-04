package org.dimdev.dimdoors.pockets.virtual.selection;

import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.AbstractVirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.forge.world.pocket.type.Pocket;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConditionalSelector extends AbstractVirtualPocket {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "conditional";

	// TODO: redo this weird map part, Equations now have Equation.asString()
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
				LOGGER.error("Could not parse pocket conditions equation!", e);
			}
		}
		return this;
	}

	@Override
	public CompoundTag toNbtInternal(CompoundTag nbt, boolean allowReference) {
		super.toNbtInternal(nbt, allowReference);

		ListTag conditionalPockets = new ListTag();
		pocketMap.forEach((condition, pocket) -> {
			CompoundTag compound = new CompoundTag();
			compound.putString("condition", condition);
			compound.put("pocket", VirtualPocket.serialize(pocket, allowReference));
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
		for (Map.Entry<String, VirtualPocket> entry : pocketMap.entrySet()) {
			if (equationMap.get(entry.getKey()).asBoolean(parameters.toVariableMap(new HashMap<>()))) {
				return entry.getValue();
			}
		}
		return pocketMap.values().stream().findFirst().orElse(NoneVirtualPocket.NONE);
	}
}
