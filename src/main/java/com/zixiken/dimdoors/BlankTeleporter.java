package com.zixiken.dimdoors;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class BlankTeleporter extends Teleporter {
	public BlankTeleporter(WorldServer worldIn) {super(worldIn);}

	/**
	 * Create a new portal near an entity.
	 */
	 @Override
	 public void placeInPortal(Entity entityIn, float rotationYaw) {}

	 public void setEntityPosition(Entity entity, double x, double y, double z) {
		 y = y+entity.getYOffset();
		 entity.lastTickPosX = entity.prevPosX = x;
		 entity.lastTickPosY = entity.prevPosY = y;
		 entity.lastTickPosZ = entity.prevPosZ = z;
		 entity.setPosition(x, y, z);
	 }
	  
	 @Override
	 public void removeStalePortalLocations(long par1) {}
}
