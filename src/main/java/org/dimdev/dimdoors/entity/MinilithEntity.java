package org.dimdev.dimdoors.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class MinilithEntity extends MonolithEntity {

	public MinilithEntity(EntityType<? extends MonolithEntity> type, World world) {
		super(type,world);
	}
}
