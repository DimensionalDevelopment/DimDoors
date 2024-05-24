package org.dimdev.dimdoors.pockets.virtual.selection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dimdev.dimdoors.api.util.Path;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;

// TODO: Override equals
public class PathSelector extends AbstractVirtualPocketList {
	public static final String KEY = "path";

	public static MapCodec<PathSelector> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).and(Codec.STRING.optionalFieldOf("path", null).forGetter(a -> a.path)).apply(instance, PathSelector::new));

	private String path;

	public PathSelector(String resourceKey, String path) {
		super(resourceKey);
		this.path = path;
	}

	@Override
	public ImplementedVirtualPocket fromNbt(CompoundTag nbt, ResourceManager manager) {
		this.path = nbt.getString("path");

		return this;
	}

	@Override
	public CompoundTag toNbtInternal(CompoundTag nbt) {
		super.toNbtInternal(nbt);

		nbt.putString("path", path);

		return nbt;
	}

	@Override
	public VirtualPocketType<? extends ImplementedVirtualPocket> getType() {
		return VirtualPocketType.PATH_SELECTOR.get();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public void init() {
		this.addAll(PocketLoader.getInstance().getVirtualPockets().getNode(Path.stringPath(path)).values());
	}
}
