package org.dimdev.dimdoors.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.dimdev.dimdoors.api.block.AfterMoveCollidableBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	@Shadow
	public ServerPlayerEntity player;
	@Shadow
	private double lastTickX;
	@Shadow
	private double lastTickY;
	@Shadow
	private double lastTickZ;

	@Inject(method = "onPlayerMove", at = @At("TAIL"))
	protected void checkBlockCollision(PlayerMoveC2SPacket packet, CallbackInfo ci) {
		// stolen from Entity#checkBlockCollision
		Box box = player.getBoundingBox();
		BlockPos blockPos = new BlockPos(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D);
		BlockPos blockPos2 = new BlockPos(box.maxX - 0.001D, box.maxY - 0.001D, box.maxZ - 0.001D);
		if (player.world.isRegionLoaded(blockPos, blockPos2)) {
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			boolean done = false;
			for(int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
				for(int j = blockPos.getY(); j <= blockPos2.getY(); ++j) {
					for(int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
						mutable.set(i, j, k);
						BlockState blockState = player.world.getBlockState(mutable);
						Block block = blockState.getBlock();
						if (block instanceof AfterMoveCollidableBlock && ((AfterMoveCollidableBlock) block).onAfterMovePlayerCollision(blockState, player.getWorld(), mutable, player, player.getPos().subtract(lastTickX, lastTickY, lastTickZ)).isAccepted()) {
							done = true;
						}
						if (done) {
							break;
						}
					}
					if (done) {
						break;
					}
				}
				if (done) {
					break;
				}
			}
		}

	}
}
