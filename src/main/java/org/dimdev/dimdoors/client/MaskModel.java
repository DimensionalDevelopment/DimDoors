package org.dimdev.dimdoors.client;

import net.minecraft.util.Identifier;

import org.dimdev.dimdoors.entity.AbstractMaskEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MaskModel extends AnimatedGeoModel<AbstractMaskEntity> {
	@Override
	public Identifier getModelResource(AbstractMaskEntity abstractMaskEntity) {
		return new Identifier("dimdoors:geo/mob/mask/mask.geo.json");
	}

	@Override
	public Identifier getTextureResource(AbstractMaskEntity abstractMaskEntity) {
		return new Identifier("dimdoors:textures/mob/mask/mask.png");
	}

	@Override
	public Identifier getAnimationResource(AbstractMaskEntity abstractMaskEntity) {
		return new Identifier("dimdoors:animations/mob/mask/mask.animation.json");
	}
}
