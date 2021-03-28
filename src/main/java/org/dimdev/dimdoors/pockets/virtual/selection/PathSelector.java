package org.dimdev.dimdoors.pockets.virtual.selection;

import net.minecraft.nbt.CompoundTag;
import org.dimdev.dimdoors.api.util.Path;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.virtual.AbstractVirtualPocket;

// TODO: Override equals
public class PathSelector extends AbstractVirtualPocketList {
	public static final String KEY = "path";

	private String path;

	@Override
	public AbstractVirtualPocket fromTag(CompoundTag tag) {
		this.path = tag.getString("path");

		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		tag.putString("path", path);

		return tag;
	}

	@Override
	public VirtualPocketType<? extends AbstractVirtualPocket> getType() {
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
