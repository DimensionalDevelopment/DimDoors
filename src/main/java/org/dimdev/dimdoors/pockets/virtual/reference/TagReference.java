package org.dimdev.dimdoors.pockets.virtual.reference;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

import com.google.common.base.MoreObjects;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.api.util.WeightedList;

import java.util.ArrayList;
import java.util.List;

public class TagReference extends PocketGeneratorReference{
	public static final String KEY = "tag";

	private final List<String> required = new ArrayList<>();
	private final List<String> blackList = new ArrayList<>();
	private Boolean exact;

	private WeightedList<PocketGenerator, PocketGenerationContext> pockets;

	@Override
	public ImplementedVirtualPocket fromTag(CompoundTag tag) {
		super.fromTag(tag);

		if (tag.contains("required")) {
			ListTag listTag = tag.getList("required", NbtType.STRING);
			for (int i = 0; i < listTag.size(); i++) {
				required.add(listTag.getString(i));
			}
		}

		if (tag.contains("blackList")) {
			ListTag listTag = tag.getList("blackList", NbtType.STRING);
			for (int i = 0; i < listTag.size(); i++) {
				blackList.add(listTag.getString(i));
			}
		}

		if (tag.contains("exact")) exact = tag.getBoolean("exact");

		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		if (required.size() > 0) {
			ListTag listTag = new ListTag();
			for (String tagString : required) {
				listTag.add(StringTag.of(tagString));
			}
			tag.put("required", listTag);
		}

		if (blackList.size() > 0) {
			ListTag listTag = new ListTag();
			for (String tagString : blackList) {
				listTag.add(StringTag.of(tagString));
			}
			tag.put("blackList", listTag);
		}

		if (exact != null) {
			tag.putBoolean("exact", exact);
		}

		return tag;
	}


	@Override
	public VirtualPocketType<? extends ImplementedVirtualPocket> getType() {
		return VirtualPocketType.TAG_REFERENCE;
	}

	@Override
	public String getKey() {
		return KEY;
	}
	// TODO: this will break if pockets change in between (which they could if we add a tool for creating pocket json config stuff ingame)
	@Override
	public PocketGenerator peekReferencedPocketGenerator(PocketGenerationContext parameters) {
		if (pockets == null) pockets = PocketLoader.getInstance().getPocketsMatchingTags(required, blackList, exact);
		return pockets.peekNextRandomWeighted(parameters);
	}

	@Override
	public PocketGenerator getReferencedPocketGenerator(PocketGenerationContext parameters) {
		if (pockets == null) pockets = PocketLoader.getInstance().getPocketsMatchingTags(required, blackList, exact);
		return pockets.getNextRandomWeighted(parameters);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("weight", weight)
				.add("weightEquation", weightEquation)
				.add("setupLoot", setupLoot)
				.add("modifierList", modifierList)
				.add("required", required)
				.add("blackList", blackList)
				.add("exact", exact)
				.add("pockets", pockets)
				.toString();
	}
}
