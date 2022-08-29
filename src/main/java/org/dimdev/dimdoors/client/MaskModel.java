package org.dimdev.dimdoors.client;

import net.minecraft.util.Identifier;

import org.dimdev.dimdoors.entity.MaskEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MaskModel extends AnimatedGeoModel<MaskEntity> {
	@Override
	public Identifier getModelResource(MaskEntity maskEntity) {
		return new Identifier("dimdoors:geo/mob/mask/mask.geo.json");
	}

	@Override
	public Identifier getTextureResource(MaskEntity maskEntity) {
		return new Identifier("dimdoors:textures/mob/mask/mask.png");
	}

	@Override
	public Identifier getAnimationResource(MaskEntity maskEntity) {
		return new Identifier("dimdoors:animations/mob/mask/mask.animation.json");
	}
}
