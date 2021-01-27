package org.dimdev.dimdoors.pockets.selection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Pair;
import org.dimdev.dimdoors.pockets.VirtualPocket;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.world.pocket.Pocket;

import java.util.List;
import java.util.regex.Pattern;

public class DepthDependentSelector extends VirtualPocket {
	public static final String KEY = "depth_dependent";

	private static final Codec<Pair<String, VirtualPocket>> PAIR_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("regex").forGetter(Pair::getLeft),
			VirtualPocket.CODEC.fieldOf("pocket").forGetter(Pair::getRight)
	).apply(instance, Pair::new));

	public static final Codec<DepthDependentSelector> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("id").forGetter(DepthDependentSelector::getName),
			PAIR_CODEC.listOf().fieldOf("pockets").forGetter(DepthDependentSelector::getPocketList)
	).apply(instance, DepthDependentSelector::new));



	private final String name;
	private final List<Pair<String, VirtualPocket>> pocketList;

	public DepthDependentSelector(String name, List<Pair<String, VirtualPocket>> pocketList) {
		this.name = name;
		this.pocketList = pocketList;
	}

	public String getName() {
		return name;
	}

	public List<Pair<String, VirtualPocket>> getPocketList() {
		return pocketList;
	}

	@Override
	public void init(String group) {
		pocketList.forEach(pair -> pair.getRight().init(group));
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
	public VirtualPocketType<? extends VirtualPocket> getType() {
		return VirtualPocketType.DEPTH_DEPENDENT;
	}

	@Override
	public String getKey() {
		return null;
	}

	@Override
	public int getWeight(PocketGenerationParameters parameters) {
		return getNextPocket(parameters).getWeight(parameters);
	}

	private VirtualPocket getNextPocket(PocketGenerationParameters parameters) {
		for (Pair<String, VirtualPocket> pair : pocketList) {
			if (Pattern.compile(pair.getLeft()).matcher(String.valueOf(parameters.getSourceVirtualLocation().getDepth())).matches()) {
				return pair.getRight();
			}
		}
		return pocketList.get(0).getRight();
	}
}
