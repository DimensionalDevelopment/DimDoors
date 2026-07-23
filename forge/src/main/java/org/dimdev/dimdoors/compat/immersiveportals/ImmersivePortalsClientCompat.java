package org.dimdev.dimdoors.compat.immersiveportals;

import net.minecraftforge.common.MinecraftForge;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalLike;
import qouteall.imm_ptl.core.render.PortalRenderer;
import qouteall.imm_ptl.core.render.context_management.PortalRendering;

final class ImmersivePortalsClientCompat {
	private static boolean registered;

	private ImmersivePortalsClientCompat() {
	}

	static void init() {
		if (!registered) {
			MinecraftForge.EVENT_BUS.addListener(ImmersivePortalsClientCompat::onDoRenderPortal);
			registered = true;
		}
	}

	private static void onDoRenderPortal(PortalRenderer.DoRenderPortalEvent event) {
		if (!PortalRendering.isRendering()) {
			return;
		}

		PortalLike renderingPortalLike = PortalRendering.getRenderingPortal();
		if (!(renderingPortalLike instanceof Portal renderingPortal)) {
			return;
		}

		if (ImmersivePortalsDoorBridge.isSameDimDoorsPortalSet(renderingPortal, event.portal)) {
			event.setCanceled(true);
		}
	}
}
