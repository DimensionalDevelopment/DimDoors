package org.dimdev.dimdoors.compat.simulated.mixin;

import com.simibubi.create.content.contraptions.AssemblyException;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import dev.simulated_team.simulated.util.SimAssemblyHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.block.RiftProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = dev.simulated_team.simulated.content.blocks.physics_assembler.PhysicsAssemblerBlockEntity.class, remap = false)
public class PhysicsAssemblerBlockEntityMixin {
    @Shadow
    private int disassemblyAngle;

    @Inject(method = "throwDisassemblyExceptions", at = @At("TAIL"))
    private void dimdoors$preventDisassemblyOverRifts(ServerSubLevel subLevel, CallbackInfo ci) throws AssemblyException {
        BlockEntity assembler = (BlockEntity) (Object) this;
        Level level = assembler.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos subLevelAnchor = assembler.getBlockPos();
        BlockPos disassemblyGoal = BlockPos.containing(subLevel.logicalPose().transformPosition(Vec3.atCenterOf(subLevelAnchor)));
        Rotation rotation = SimAssemblyHelper.rotationFrom90DegRots(this.disassemblyAngle);
        SubLevelAssemblyHelper.AssemblyTransform transform = new SubLevelAssemblyHelper.AssemblyTransform(
                subLevelAnchor,
                disassemblyGoal,
                rotation == Rotation.NONE ? 0 : (4 - rotation.ordinal()),
                rotation,
                serverLevel
        );

        for (PlotChunkHolder chunk : subLevel.getPlot().getLoadedChunks()) {
            BoundingBox3ic localChunkBounds = chunk.getBoundingBox();

            if (localChunkBounds == null || localChunkBounds == BoundingBox3i.EMPTY) continue;

            for (int x = localChunkBounds.minX(); x <= localChunkBounds.maxX(); x++) {
                for (int y = localChunkBounds.minY(); y <= localChunkBounds.maxY(); y++) {
                    for (int z = localChunkBounds.minZ(); z <= localChunkBounds.maxZ(); z++) {
                        BlockPos sourcePos = new BlockPos(
                                x + chunk.getPos().getMinBlockX(),
                                y,
                                z + chunk.getPos().getMinBlockZ()
                        );

                        if (serverLevel.getBlockState(sourcePos).isAir()) {
                            continue;
                        }

                        BlockPos targetPos = transform.apply(sourcePos);
                        BlockState targetState = serverLevel.getBlockState(targetPos);

                        if (targetState.getBlock() instanceof RiftProvider<?> riftProvider && riftProvider.stateContainsRift(targetState)) {
                            throw AssemblyException.unmovableBlock(targetPos, targetState);
                        }
                    }
                }
            }
        }
    }
}
