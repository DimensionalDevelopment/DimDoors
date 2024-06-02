package org.dimdev.dimdoors.forge.item.component;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.ItemStack;

public interface CounterComponent {

	int increment();

	int count();

	void reset();

	@ExpectPlatform
	static CounterComponent get(ItemStack provider) {
		throw new RuntimeException();
	}
}
