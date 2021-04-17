package org.dimdev.dimdoors.item.component;

import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import org.dimdev.dimdoors.DimensionalDoorsComponents;

import net.minecraft.item.ItemStack;

public class CounterComponent extends ItemComponent {

	public CounterComponent(ItemStack stack) {
		super(stack);
		if (!this.hasTag("counter"))
			this.putInt("counter", 0);
	}

	public int increment() {
		int counter = count();
		putInt("counter", counter + 1);
		return counter;
	}

	public int count() {
		return getInt("counter");
	}

	public void reset() {
		putInt("counter", 0);
	}

	public static <T> CounterComponent get(T provider) {
		return DimensionalDoorsComponents.COUNTER_COMPONENT_KEY.get(provider);
	}
}
