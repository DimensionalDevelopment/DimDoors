package org.dimdev.dimdoors.mixin;

import java.util.Objects;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;

@Mixin(RegistryKey.class)
public class RegistryKeyMixin {
	@Final
	@Shadow
	private Identifier value;

	@Final
	@Shadow
	private Identifier registry;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof RegistryKey)) return false;
		RegistryKey<?> that = (RegistryKey<?>) o;
		return Objects.equals(this.value, that.getValue());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.value, this.registry);
	}
}
