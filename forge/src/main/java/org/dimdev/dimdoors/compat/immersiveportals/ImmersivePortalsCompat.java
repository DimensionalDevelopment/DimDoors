package org.dimdev.dimdoors.compat.immersiveportals;

import dev.architectury.platform.Platform;
import org.dimdev.dimdoors.compat.DoorPortalBridge;

/**
 * Entry point for the optional Immersive Portals integration. Keeps all
 * Immersive Portals classes out of this class so it is safe to load when
 * the mod is absent.
 */
public final class ImmersivePortalsCompat {
	public static final String IP_MOD_ID = "imm_ptl_core";

	private ImmersivePortalsCompat() {
	}

	public static void init() {
		if (Platform.isModLoaded(IP_MOD_ID)) {
			DoorPortalBridge.set(new ImmersivePortalsDoorBridge());
		}
	}

	public static void initClient() {
		if (Platform.isModLoaded(IP_MOD_ID)) {
			ImmersivePortalsClientCompat.init();
		}
	}
}
