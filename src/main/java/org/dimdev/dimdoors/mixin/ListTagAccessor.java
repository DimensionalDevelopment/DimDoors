package org.dimdev.dimdoors.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

@Mixin(ListTag.class)
public interface ListTagAccessor {
	@Invoker
	static ListTag createListTag(List<Tag> list, byte type) {
		throw new UnsupportedOperationException();
	}
}
