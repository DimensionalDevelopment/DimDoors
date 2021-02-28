package org.dimdev.dimdoors.util;

import com.qouteall.immersive_portals.portal.Portal;

public class ImmersivePortalsUtil {
	public static void linkPortals(Portal p1, Portal p2) {
		p1.setDestinationDimension(p2.getOriginDim());
		p1.setDestination(p2.getOriginPos());

		p2.setDestinationDimension(p1.getOriginDim());
		p2.setDestination(p1.getOriginPos());
	}
}
