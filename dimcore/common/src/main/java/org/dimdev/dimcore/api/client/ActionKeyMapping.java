package org.dimdev.dimcore.api.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import com.mojang.blaze3d.platform.InputConstants;

import java.util.function.BooleanSupplier;

/**
 * A {@link KeyMapping} that runs its action itself on the press edge. No polling, no external state.
 *
 * <p>{@code setDown} is driven straight from the GLFW callback, so overriding it catches the press
 * without going through {@code clickCount} -- which fires on key repeat and would run the action
 * once per repeat event.
 */
public class ActionKeyMapping extends KeyMapping {
	private static final BooleanSupplier NONE = () -> true;

	private final BooleanSupplier gate;
	private final Runnable action;
	private boolean down;

	public ActionKeyMapping(String name, int keyCode, String category, Runnable action) {
		this(name, InputConstants.Type.KEYSYM, keyCode, category, NONE, action);
	}

	public ActionKeyMapping(String name, int keyCode, String category, BooleanSupplier gate, Runnable action) {
		this(name, InputConstants.Type.KEYSYM, keyCode, category, gate, action);
	}

	public ActionKeyMapping(String name, InputConstants.Type type, int keyCode, String category, BooleanSupplier gate, Runnable action) {
		super(name, type, keyCode, category);
		this.gate = gate;
		this.action = action;
	}

	public static ActionKeyMapping shift(String name, int keyCode, String category, Runnable action) {
		return new ActionKeyMapping(name, keyCode, category, Screen::hasShiftDown, action);
	}

	public static ActionKeyMapping control(String name, int keyCode, String category, Runnable action) {
		return new ActionKeyMapping(name, keyCode, category, Screen::hasControlDown, action);
	}

	public static ActionKeyMapping alt(String name, int keyCode, String category, Runnable action) {
		return new ActionKeyMapping(name, keyCode, category, Screen::hasAltDown, action);
	}

	@Override
	public void setDown(boolean value) {
		super.setDown(value);

		boolean was = this.down;
		this.down = value;

		if (value && !was && this.gate.getAsBoolean()) {
			this.action.run();
		}
	}

	@Override
	public boolean consumeClick() {
		super.consumeClick();
		return false;
	}
}
