package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import org.dimdev.dimdoors.util.EntityUtils;

import net.minecraft.entity.Entity;
import net.minecraft.text.TranslatableText;

public class PocketExitMarker extends VirtualTarget implements EntityTarget {
	public static final Codec<PocketExitMarker> CODEC = Codec.unit(PocketExitMarker::new);

	public PocketExitMarker() {
	}

	@Override
	public boolean receiveEntity(Entity entity, float yawOffset) {
		EntityUtils.chat(entity, new TranslatableText("The exit of this dungeon has not been linked. If this is a normally generated pocket, please report this bug."));
		return false;
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.POCKET_EXIT;
	}
}
