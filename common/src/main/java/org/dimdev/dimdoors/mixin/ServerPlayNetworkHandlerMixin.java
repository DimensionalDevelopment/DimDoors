package org.dimdev.dimdoors.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.dimdev.dimdoors.api.block.AfterMoveCollidableBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {
	@Shadow
	public ServerPlayer player;
	@Shadow
	private double firstGoodX;
	@Shadow
	private double firstGoodY;
	@Shadow
	private double firstGoodZ;

	@Inject(method = "handleMovePlayer", at = @At("TAIL"))
	protected void checkBlockCollision(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
		// stolen from Entity#checkBlockCollision
		AABB box = player.getBoundingBox();
		BlockPos blockPos = new BlockPos((int) (box.minX + 0.001D), (int) (box.minY + 0.001D), (int) (box.minZ + 0.001D));
		BlockPos blockPos2 = new BlockPos((int) (box.maxX - 0.001D), (int) (box.maxY - 0.001D), (int) (box.maxZ - 0.001D));
		if (player.level.hasChunksAt(blockPos, blockPos2)) {
			BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

			boolean done = false;
			for(int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
				for(int j = blockPos.getY(); j <= blockPos2.getY(); ++j) {
					for(int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
						mutable.set(i, j, k);
						BlockState blockState = player.level.getBlockState(mutable);
						Block block = blockState.getBlock();
						if (block instanceof AfterMoveCollidableBlock && ((AfterMoveCollidableBlock) block).onAfterMovePlayerCollision(blockState, player.getLevel(), mutable, player, player.position().subtract(firstGoodX, firstGoodY, firstGoodZ)).consumesAction()) {
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
