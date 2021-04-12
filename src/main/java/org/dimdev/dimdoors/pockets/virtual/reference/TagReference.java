package org.dimdev.dimdoors.pockets.virtual.reference;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
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
	public ImplementedVirtualPocket fromTag(NbtCompound tag) {
		super.fromTag(tag);

		if (tag.contains("required")) {
			NbtList listTag = tag.getList("required", NbtType.STRING);
			for (int i = 0; i < listTag.size(); i++) {
				required.add(listTag.getString(i));
			}
		}

		if (tag.contains("blackList")) {
			NbtList listTag = tag.getList("blackList", NbtType.STRING);
			for (int i = 0; i < listTag.size(); i++) {
				blackList.add(listTag.getString(i));
			}
		}

		if (tag.contains("exact")) exact = tag.getBoolean("exact");

		return this;
	}

	@Override
	public NbtCompound toTag(NbtCompound tag) {
		super.toTag(tag);

		if (required.size() > 0) {
			NbtList listTag = new NbtList();
			for (String tagString : required) {
				listTag.add(NbtString.of(tagString));
			}
			tag.put("required", listTag);
		}

		if (blackList.size() > 0) {
			NbtList listTag = new NbtList();
			for (String tagString : blackList) {
				listTag.add(NbtString.of(tagString));
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
