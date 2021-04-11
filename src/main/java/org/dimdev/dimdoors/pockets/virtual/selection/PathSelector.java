package org.dimdev.dimdoors.pockets.virtual.selection;

import net.minecraft.nbt.NbtCompound;
import org.dimdev.dimdoors.api.util.Path;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;

// TODO: Override equals
public class PathSelector extends AbstractVirtualPocketList {
	public static final String KEY = "path";

	private String path;

	@Override
	public ImplementedVirtualPocket fromTag(NbtCompound tag) {
		this.path = tag.getString("path");

		return this;
	}

	@Override
	public NbtCompound toTag(NbtCompound tag) {
		super.toTag(tag);

		tag.putString("path", path);

		return tag;
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
