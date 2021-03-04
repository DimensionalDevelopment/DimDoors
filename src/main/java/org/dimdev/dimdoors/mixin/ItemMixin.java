package org.dimdev.dimdoors.mixin;

import org.dimdev.dimdoors.item.ItemExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.item.Item;

@Mixin(Item.class)
public class ItemMixin implements ItemExtensions {
	@Unique
	private Item.Settings settings;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void cacheSettings(Item.Settings settings, CallbackInfo ci) {
		this.settings = settings;
	}

	@Override
	public Item.Settings dimdoors_getSettings() {
		return settings;
	}

	@Mixin(Item.Settings.class)
	public static class SettingsMixin implements ItemExtensions.SettingsExtensions {
		@Override
		public Item.Settings clone() {
			try {
				return (Item.Settings) super.clone();
			} catch (CloneNotSupportedException e) {
				throw new AssertionError(e); // Cant happen, we are Cloneable
			}
		}
	}
}
