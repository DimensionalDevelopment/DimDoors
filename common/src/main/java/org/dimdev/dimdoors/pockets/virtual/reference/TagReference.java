package org.dimdev.dimdoors.pockets.virtual.reference;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dimdev.dimdoors.api.util.WeightedList;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;

import java.util.ArrayList;
import java.util.List;

public class TagReference extends PocketGeneratorReference {
	public static MapCodec<TagReference> CODEC = RecordCodecBuilder.mapCodec(instance -> commonRefereceFields(instance)
			.and(Codec.STRING.listOf().optionalFieldOf("required", new ArrayList<>()).forGetter(a -> a.required))
			.and(Codec.STRING.listOf().optionalFieldOf("blackList", new ArrayList<>()).forGetter(a -> a.blackList))
			.and(Codec.BOOL.optionalFieldOf("required", false).forGetter(a -> a.exact))
			.apply(instance, TagReference::new));


	public static final String KEY = "tag";

	private final List<String> required;
	private final List<String> blackList;
	private Boolean exact;

	private WeightedList<PocketGenerator, PocketGenerationContext> pockets;

	public TagReference(String resouceKey, String weight, Boolean setupLoot, List<Modifier> modifierList, List<CompoundTag> addons, List<String> required, List<String> blackList, boolean exact) {
		super(resouceKey, weight, setupLoot, modifierList, addons);
		this.required = required;
		this.blackList = blackList;
		this.exact = exact;
	}

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
	protected CompoundTag toNbtInternal(CompoundTag nbt) {
		super.toNbtInternal(nbt);

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
		return VirtualPocketType.TAG_REFERENCE.get();
	}

	@Override
	public String getKey() {
		return KEY;
	}
	// TODO: this will break if pockets change in between (which they could if we add a tool for creating pocket json config stuff ingame)
	@Override
	public PocketGenerator peekReferencedPocketGenerator(PocketGenerationContext parameters) {
		return selectPocket(parameters);
	}

	@Override
	public PocketGenerator getReferencedPocketGenerator(PocketGenerationContext parameters) {
		return selectPocket(parameters);
	}

	private PocketGenerator selectPocket(PocketGenerationContext parameters) {
		if (pockets == null) pockets = PocketLoader.getInstance().getPocketsMatchingTags(required, blackList, exact != null && exact);
		return pockets.peekNextRandomWeighted(parameters);
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
