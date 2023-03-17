package org.dimdev.dimdoors.pockets.virtual.reference;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.packs.resources.ResourceManager;

import org.dimdev.dimdoors.api.util.WeightedList;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;

public class TagReference extends PocketGeneratorReference {
	public static final String KEY = "tag";

	private final List<String> required = new ArrayList<>();
	private final List<String> blackList = new ArrayList<>();
	private Boolean exact;

	private WeightedList<PocketGenerator, PocketGenerationContext> pockets;

	@Override
	public ImplementedVirtualPocket fromNbt(CompoundTag nbt, ResourceManager manager) {
		super.fromNbt(nbt, manager);

		if (nbt.contains("required")) {
			ListTag listNbt = nbt.getList("required", Tag.TAG_STRING);
			for (int i = 0; i < listNbt.size(); i++) {
				required.add(listNbt.getString(i));
			}
		}

		if (nbt.contains("blackList")) {
			ListTag listNbt = nbt.getList("blackList", Tag.TAG_STRING);
			for (int i = 0; i < listNbt.size(); i++) {
				blackList.add(listNbt.getString(i));
			}
		}

		if (nbt.contains("exact")) exact = nbt.getBoolean("exact");

		return this;
	}

	@Override
	protected CompoundTag toNbtInternal(CompoundTag nbt, boolean allowReference) {
		super.toNbtInternal(nbt, allowReference);

		if (required.size() > 0) {
			ListTag listNbt = new ListTag();
			for (String nbtStr : required) {
				listNbt.add(StringTag.valueOf(nbtStr));
			}
			nbt.put("required", listNbt);
		}

		if (blackList.size() > 0) {
			ListTag list = new ListTag();
			for (String nbtStr : blackList) {
				list.add(StringTag.valueOf(nbtStr));
			}
			nbt.put("blackList", list);
		}

		if (exact != null) {
			nbt.putBoolean("exact", exact);
		}

		return nbt;
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
