package org.dimdev.dimdoors.pockets.virtual.selection;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.ResourceManager;
import org.dimdev.dimdoors.api.util.Path;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;

// TODO: Override equals
public class PathSelector extends AbstractVirtualPocketList {
	public static final String KEY = "path";

	private String path;

	@Override
	public ImplementedVirtualPocket fromNbt(NbtCompound nbt, ResourceManager manager) {
		this.path = nbt.getString("path");

		return this;
	}

	@Override
	public NbtCompound toNbtInternal(NbtCompound nbt, boolean allowReference) {
		super.toNbtInternal(nbt, allowReference);

		nbt.putString("path", path);

		return nbt;
	}

	@Override
	public VirtualPocketType<? extends ImplementedVirtualPocket> getType() {
		return VirtualPocketType.PATH_SELECTOR;
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
