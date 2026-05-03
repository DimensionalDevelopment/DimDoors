package org.dimdev.dimdoors.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.block.AfterMoveCollidableBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayer player;

    @Unique
    private Vec3 dimdoors$positionBeforeMove = Vec3.ZERO;

    @Inject(
            method = "handleMovePlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void dimdoors$capturePositionBeforeMove(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
        this.dimdoors$positionBeforeMove = this.player.position();
    }

    @Inject(method = "handleMovePlayer", at = @At("TAIL"))
    protected void checkBlockCollision(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
        AABB box = this.player.getBoundingBox();
        Vec3 previousPos = this.dimdoors$positionBeforeMove;
        Vec3 currentPos = this.player.position();

        this.dimdoors$checkAfterMoveCollision(box, previousPos, currentPos);
    }

    @Unique
    private void dimdoors$checkAfterMoveCollision(AABB box, Vec3 previousPos, Vec3 currentPos) {
        BlockPos min = BlockPos.containing(box.minX + 1.0E-7D, box.minY + 1.0E-7D, box.minZ + 1.0E-7D);
        BlockPos max = BlockPos.containing(box.maxX - 1.0E-7D, box.maxY - 1.0E-7D, box.maxZ - 1.0E-7D);

        if (!this.player.level().hasChunksAt(min, max)) {
            return;
        }

        ServerLevel level = this.player.serverLevel();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    mutable.set(x, y, z);
                    BlockState blockState = this.player.level().getBlockState(mutable);
                    Block block = blockState.getBlock();

                    if (block instanceof AfterMoveCollidableBlock collidable) {
                        collidable.onAfterMovePlayerCollision(blockState, level, mutable, this.player, previousPos, currentPos);
                    }
                }
            }
        }
    }
}
