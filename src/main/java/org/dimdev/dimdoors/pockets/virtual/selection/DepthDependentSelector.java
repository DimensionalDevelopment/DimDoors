package org.dimdev.dimdoors.pockets.virtual.selection;

import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.dimdev.dimdoors.pockets.PocketGroup;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.VirtualSingularPocket;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.world.pocket.Pocket;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class DepthDependentSelector extends VirtualSingularPocket {
	public static final String KEY = "depth_dependent";
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



	private String name;
	private LinkedHashMap<String, VirtualPocket> pocketList;

	public DepthDependentSelector() {

	}
	public DepthDependentSelector(String name, LinkedHashMap<String, VirtualPocket> pocketList) {
		this.name = name;
		this.pocketList = pocketList;
	}

	public String getName() {
		return name;
	}

	public LinkedHashMap<String, VirtualPocket> getPocketList() {
		return pocketList;
	}

	@Override
	public VirtualSingularPocket fromTag(CompoundTag tag) {
		this.name = tag.getString("id");
		ListTag regexPockets = tag.getList("pockets", 10);
		pocketList = Maps.newLinkedHashMap();
		for (int i = 0; i < regexPockets.size(); i++) {
			CompoundTag pocket = regexPockets.getCompound(i);
			String regex = pocket.getString("regex");
			if (pocketList.containsKey(regex)) continue;
			pocketList.put(pocket.getString("regex"), VirtualPocket.deserialize(pocket.get("pocket")));
		}
		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		tag.putString("id", this.name);

		ListTag regexPockets = new ListTag();
		pocketList.forEach((regex, pocket) -> {
			CompoundTag compound = new CompoundTag();
			compound.putString("regex", regex);
			compound.put("pocket", VirtualPocket.serialize(pocket));
			regexPockets.add(compound);
		});
		tag.put("pockets", regexPockets);
		return tag;
	}

	@Override
	public void init(PocketGroup group) {
		pocketList.forEach((regex, pocket) -> pocket.init(group));
	}

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationParameters parameters) {
		return getNextPocket(parameters).prepareAndPlacePocket(parameters);
	}

	// TODO: write method
	@Override
	public String toString() {
		return null;
	}

	@Override
	public VirtualSingularPocketType<? extends VirtualSingularPocket> getType() {
		return VirtualSingularPocketType.DEPTH_DEPENDENT;
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
		for (Map.Entry<String, VirtualPocket> entry : pocketList.entrySet()) {
			if (Pattern.compile(entry.getKey()).matcher(String.valueOf(parameters.getSourceVirtualLocation().getDepth())).matches()) {
				return entry.getValue();
			}
		}
		return pocketList.values().stream().findFirst().get(); // TODO: orElse() with some NONE VirtualPocket
	}
}
