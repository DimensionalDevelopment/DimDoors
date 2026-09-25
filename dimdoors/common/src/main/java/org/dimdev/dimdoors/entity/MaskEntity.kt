package org.dimdev.dimdoors.entity

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.level.Level

open class MaskEntity(entityType: EntityType<out MaskEntity>, world: Level) : PathfinderMob(entityType, world) // TODO
