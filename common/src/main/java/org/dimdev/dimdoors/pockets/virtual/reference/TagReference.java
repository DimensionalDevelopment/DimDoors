package org.dimdev.dimdoors.pockets.virtual.reference;

import com.google.common.base.MoreObjects;
import com.mojang.datafixers.util.Function7;
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
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.ArrayList;
import java.util.List;

public class TagReference extends PocketGeneratorReference {
	public static final String KEY = "tag";

	private List<String> required;
	private List<String> blackList;
	private Boolean exact;

	private WeightedList<PocketGenerator, PocketGenerationContext> pockets;

	public static final MapCodec<TagReference> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).and(
			Codec.list(Codec.STRING).optionalFieldOf("required", new ArrayList<>()).forGetter(a -> a.required)).and(
			Codec.list(Codec.STRING).optionalFieldOf("blackList", new ArrayList<>()).forGetter(a -> a.blackList)).and(
			Codec.BOOL.optionalFieldOf("exact", false).forGetter(a -> a.exact)).apply(instance, TagReference::new)
	);

	public TagReference(String weight, Boolean setupLoot, List<Modifier> modifierList, List<PocketAddon.PocketBuilderAddon<?,?>> addons, List<String> required, List<String> blackList, Boolean exact) {
		super(weight, setupLoot, modifierList, addons);
        this.required = required;
        this.blackList = blackList;
        this.exact = exact;
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
