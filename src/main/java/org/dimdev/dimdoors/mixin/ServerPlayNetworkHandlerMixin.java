package org.dimdev.dimdoors.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.dimdev.dimdoors.api.block.AfterMoveCollidableBlock;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {
	@Shadow
	public ServerPlayer player;
	@Shadow
	private double lastTickX;
	@Shadow
	private double lastTickY;
	@Shadow
	private double lastTickZ;

	@Inject(method = "onPlayerMove", at = @At("TAIL"))
	protected void checkBlockCollision(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
		// stolen from Entity#checkBlockCollision
		AABB box = player.getBoundingBox();
		BlockPos blockPos = new BlockPos(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D);
		BlockPos blockPos2 = new BlockPos(box.maxX - 0.001D, box.maxY - 0.001D, box.maxZ - 0.001D);
		if (player.level.hasChunksAt(blockPos, blockPos2)) {
			BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

			boolean done = false;
			for(int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
				for(int j = blockPos.getY(); j <= blockPos2.getY(); ++j) {
					for(int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
						mutable.set(i, j, k);
						BlockState blockState = player.level.getBlockState(mutable);
						Block block = blockState.getBlock();
						if (block instanceof AfterMoveCollidableBlock && ((AfterMoveCollidableBlock) block).onAfterMovePlayerCollision(blockState, player.getLevel(), mutable, player, player.position().subtract(lastTickX, lastTickY, lastTickZ)).consumesAction()) {
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
