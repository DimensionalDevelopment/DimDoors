package org.dimdev.dimdoors.mixin;

import net.minecraft.util.math.BlockBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

// TODO: DELETE WHEN MOJANK BUG FIXED
@Mixin(BlockBox.class)
public class BlockBoxMixin {
	@Shadow
	private int minX;
	@Shadow
	private int minY;
	@Shadow
	private int minZ;
	@Shadow
	private int maxX;
	@Shadow
	private int maxY;
	@Shadow
	private int maxZ;


	/**
	 * @author CreepyCre
	 */
	@Overwrite
	public BlockBox intersection(BlockBox box) {
		this.minX = Math.max(this.minX, box.getMinX());
		this.minY = Math.max(this.minY, box.getMinY());
		this.minZ = Math.max(this.minZ, box.getMinZ());
		this.maxX = Math.min(this.maxX, box.getMaxX());
		this.maxY = Math.min(this.maxY, box.getMaxY());
		this.maxZ = Math.min(this.maxZ, box.getMaxZ());
		return (BlockBox) (Object) this;
	}
}
