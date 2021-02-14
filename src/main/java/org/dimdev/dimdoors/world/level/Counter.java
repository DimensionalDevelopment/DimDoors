package org.dimdev.dimdoors.world.level;

import net.minecraft.item.ItemStack;

import dev.onyxstudios.cca.api.v3.item.ItemComponent;

public class Counter extends ItemComponent {
	public Counter(ItemStack stack) {
		super(stack, DimensionalDoorsComponents.COUNTER_COMPONENT_KEY);
		if (!this.hasTag("counter"))
			this.putInt("counter", 0);
	}

	public int count() {
		return getInt("counter");
	}

	public void set(int value) {
		this.putInt("counter", omitZero(value));
	}

	public int increment() {
		int count = count() + 1;
		this.putInt("counter", count);
		return count;
	}

	public void set(int value) {
		this.counter = value;
	}

	public static <T> Counter get(T provider) {
		return DimensionalDoorsComponents.COUNTER_COMPONENT_KEY.get(provider);
	}

	@Override
	protected void putInt(String key, int value) {
		super.putInt(key, omitZero(value));
	}


	@Override
	protected int getInt(String key) {
		return withZero(super.getInt(key));
	}

	private int omitZero(int value) {
		return value >= 0 ? value + 1 : value;
	}

	private int withZero(int value) {
		return value > 0 ? value - 1 : value;
	}
}
