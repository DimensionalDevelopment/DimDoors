package org.dimdev.dimdoors.mixin.accessor;

import java.util.List;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ListTag.class)
public interface ListTagAccessor {
	@Invoker("<init>")
	static ListTag createListTag(List<Tag> list, byte type) {
		throw new UnsupportedOperationException();
	}
}
