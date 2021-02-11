package org.dimdev.dimdoors.pockets.virtual.reference;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import org.dimdev.dimdoors.pockets.SchematicV2Handler;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.virtual.VirtualSingularPocket;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.WeightedList;

import java.util.ArrayList;
import java.util.List;

public class TagReference extends PocketGeneratorReference{
	public static final String KEY = "tag";

	private final List<String> required = new ArrayList<>();
	private final List<String> blackList = new ArrayList<>();
	private Boolean exact;

	private WeightedList<PocketGenerator, PocketGenerationParameters> pockets;

	@Override
	public VirtualSingularPocket fromTag(CompoundTag tag) {
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
	public VirtualSingularPocketType<? extends VirtualSingularPocket> getType() {
		return VirtualSingularPocketType.TAG_REFERENCE;
	}

	@Override
	public String getKey() {
		return KEY;
	}
	// TODO: this will break if pockets change in between (which they could if we add a tool for creating pocket json config stuff ingame)
	@Override
	public PocketGenerator peekReferencedPocketGenerator(PocketGenerationParameters parameters) {
		if (pockets == null) pockets = SchematicV2Handler.getInstance().getPocketsMatchingTags(required, blackList, exact);
		return pockets.peekNextRandomWeighted(parameters);
	}

	@Override
	public PocketGenerator getReferencedPocketGenerator(PocketGenerationParameters parameters) {
		if (pockets == null) pockets = SchematicV2Handler.getInstance().getPocketsMatchingTags(required, blackList, exact);
		return pockets.getNextRandomWeighted(parameters);
	}
}
