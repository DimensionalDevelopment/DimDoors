package org.dimdev.dimdoors.item.component;

import com.mojang.serialization.Codec;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;
// TODO: Fix the Fabric Implementation of idCounter
public class IdCounter {
	public static final Codec<IdCounter> CODEC = Codec.INT.xmap(IdCounter::new, IdCounter::count);
	private int counter;
	public IdCounter(int value) {
		this.counter = value;
	}

	public int increment() {
		counter++;
		return counter;
	}

	public int count() {
		return counter;
	}

	public void reset() {
		counter = 0;
	}

	@ExpectPlatform
	public static IdCounter get(ItemStack provider) {
		throw new RuntimeException();
	}
	@ExpectPlatform
	public static void register () {
		throw new RuntimeException("This should not happen: Register() in IdCounter for Architectury");
	}
}
