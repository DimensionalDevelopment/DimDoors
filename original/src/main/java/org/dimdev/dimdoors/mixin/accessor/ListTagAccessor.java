package org.dimdev.dimdoors.mixin.accessor;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

@Mixin(NbtList.class)
public interface ListTagAccessor {
	@Invoker("<init>")
	static NbtList createListTag(List<NbtElement> list, byte type) {
		throw new UnsupportedOperationException();
	}
}
