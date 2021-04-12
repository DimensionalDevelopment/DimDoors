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
	public ImplementedVirtualPocket fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);

		if (nbt.contains("required")) {
			NbtList listNbt = nbt.getList("required", NbtType.STRING);
			for (int i = 0; i < listNbt.size(); i++) {
				required.add(listNbt.getString(i));
			}
		}

		if (nbt.contains("blackList")) {
			NbtList listNbt = nbt.getList("blackList", NbtType.STRING);
			for (int i = 0; i < listNbt.size(); i++) {
				blackList.add(listNbt.getString(i));
			}
		}

		if (nbt.contains("exact")) exact = nbt.getBoolean("exact");

		return this;
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);

		if (required.size() > 0) {
			NbtList listNbt = new NbtList();
			for (String nbtStr : required) {
				listNbt.add(NbtString.of(nbtStr));
			}
			nbt.put("required", listNbt);
		}

		if (blackList.size() > 0) {
			NbtList list = new NbtList();
			for (String nbtStr : blackList) {
				list.add(NbtString.of(nbtStr));
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
