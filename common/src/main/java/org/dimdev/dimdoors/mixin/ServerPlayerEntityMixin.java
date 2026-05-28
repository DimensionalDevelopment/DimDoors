package org.dimdev.dimdoors.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.stats.Stat;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.block.AfterBlockRecord;
import org.dimdev.dimdoors.block.UnravelledFabricBlock;
import org.dimdev.dimdoors.block.door.ServerPlayerExt;
import org.dimdev.dimdoors.criteria.ModCriteria;
import org.dimdev.dimdoors.entity.limbo.LimboEntranceSource;
import org.dimdev.dimdoors.entity.stat.ModStats;
import org.dimdev.dimdoors.item.MaskItem;
import org.dimdev.dimdoors.world.ModDimensions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mixin(value = ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntityMixin implements ServerPlayerExt {
    @Shadow
    @Final
    private ServerRecipeBook recipeBook;

    @Shadow
    public abstract void readAdditionalSaveData(CompoundTag nbt);

    @Shadow
    public abstract void awardStat(Stat<?> arg, int i);

    @Shadow
    @Final
    public ServerPlayerGameMode gameMode;

    @Unique private List<AfterBlockRecord> afterBlockMoves = new ArrayList<>();
    
    private static final float RANDOM_ACTION_CHANCE = 0.1F;
    private static final float CHANCE_TO_MAKE_LIMBO_LIKE_OTHER_DIMENSIONS = 0.1F;
    private static final int CHUNK_SIZES = 25;
    private static final int POSITION_AWAY = 50;
    private static final float RANDOM_LIQUID_CHANCE = 0.7F;
    @Unique
    RandomSource dimdoors_random = RandomSource.create();

    @Inject(method = "tick", at = @At("HEAD"))
    public void playerTickMixin(CallbackInfo ci) {
        if (dimdoors_random.nextFloat() <= RANDOM_ACTION_CHANCE) {
            if (ModDimensions.isLimboDimension(((Player) (Object) (this)).level())) {
                tryMakingLimboLikeOtherDimensions((Player) (Object) this);
            }

        }

    }

    private boolean isValidBlockToReplace(Level world, BlockPos pos) {
        return world.getBlockState(pos.above()).isAir() && world.getBlockState(pos).getBlock() instanceof UnravelledFabricBlock;
    }

    private void makeLimboLikeOverworld(Player player) {
    /*
    World world = player.getEntityWorld();
    BlockPos pos = player.getBlockPos().add(random.nextInt(random.nextInt(POSITION_AWAY)), 0, random.nextInt(POSITION_AWAY));
    if(random.nextFloat() <= RANDOM_LIQUID_CHANCE) {
        makeSpotOfLiquid(world, pos, Blocks.WATER.getDefaultState(), 3);
    }
    BlockPos.iterateOutwards(pos, CHUNK_SIZES, 15, CHUNK_SIZES).forEach( (blockPos -> {
        if(isValidBlockToReplace(world, blockPos)) {
        world.setBlockState(blockPos, Blocks.GRASS_BLOCK.getDefaultState());
        }

    }));

     */

    }

    private void makeLimboLikeEnd(Player player) {
    /*
    World world = player.getEntityWorld();
    BlockPos pos = player.getBlockPos().add(random.nextInt(POSITION_AWAY), 0, random.nextInt(POSITION_AWAY));
    BlockPos.iterateOutwards(pos, CHUNK_SIZES, 15, CHUNK_SIZES).forEach( (blockPos -> {
        if(isValidBlockToReplace(world, blockPos)) {
        world.setBlockState(blockPos, Blocks.END_STONE.getDefaultState());
        }

    }));

     */
    }

    private void makeSpotOfLiquid(Level world, BlockPos pos, BlockState state, int range) {
        BlockPos.withinManhattan(pos, dimdoors_random.nextInt(range), dimdoors_random.nextInt(range), dimdoors_random.nextInt(range)).forEach((blockPos -> {
            if (isValidBlockToReplace(world, blockPos)) {
                world.setBlockAndUpdate(blockPos, state);
            }
        }));
    }

    private void makeLimboLikeNether(Player player) {
    /*
    World world = player.getEntityWorld();
    BlockPos pos = player.getBlockPos().add(random.nextInt(POSITION_AWAY), 0, random.nextInt(POSITION_AWAY));
    if(random.nextFloat() <= RANDOM_LIQUID_CHANCE) {
        makeSpotOfLiquid(world, pos, Blocks.LAVA.getDefaultState(), 10);
    }
    BlockPos.iterateOutwards(pos, CHUNK_SIZES, 15, CHUNK_SIZES).forEach( (blockPos -> {
        if(isValidBlockToReplace(world, blockPos)) {
        world.setBlockState(blockPos, Blocks.NETHERRACK.getDefaultState());
        }

    }));

     */
    }

    private void tryMakingLimboLikeOtherDimensions(Player player) {
        if (dimdoors_random.nextFloat() > CHANCE_TO_MAKE_LIMBO_LIKE_OTHER_DIMENSIONS) {
            return;
        }
        switch (dimdoors_random.nextInt(3)) {
            case 0 -> makeLimboLikeOverworld(player);
            case 1 -> makeLimboLikeNether(player);
            case 2 -> makeLimboLikeEnd(player);
        }
    }

    @Inject(method = "teleportTo(DDD)V", at = @At("HEAD"), cancellable = true)
    private void dimdoors$redirectMaskedSameDimensionTeleport(double x, double y, double z, CallbackInfo ci) {
        if (dimdoors$shouldRedirectMaskedTeleport(((ServerPlayer) (Object) this).level())) {
            dimdoors$sendMaskedTeleportToLimbo();
            ci.cancel();
        }
    }

    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V", at = @At("HEAD"), cancellable = true)
    private void dimdoors$redirectMaskedLevelTeleport(ServerLevel level, double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
        if (dimdoors$shouldRedirectMaskedTeleport(level)) {
            dimdoors$sendMaskedTeleportToLimbo();
            ci.cancel();
        }
    }

    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z", at = @At("HEAD"), cancellable = true)
    private void dimdoors$redirectMaskedRelativeTeleport(ServerLevel level, double x, double y, double z, Set<RelativeMovement> movements, float yaw, float pitch, CallbackInfoReturnable<Boolean> cir) {
        if (dimdoors$shouldRedirectMaskedTeleport(level)) {
            dimdoors$sendMaskedTeleportToLimbo();
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "changeDimension", at = @At("HEAD"), cancellable = true)
    private void dimdoors$redirectMaskedDimensionChange(DimensionTransition transition, CallbackInfoReturnable<Entity> cir) {
        if (dimdoors$shouldRedirectMaskedTeleport(transition.newLevel())) {
            cir.setReturnValue(dimdoors$sendMaskedTeleportToLimbo());
        }
    }

    @Unique
    private boolean dimdoors$shouldRedirectMaskedTeleport(Level targetLevel) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        return MaskItem.isWearingMask(player) && ModDimensions.LIMBO_DIMENSION != null && !ModDimensions.isLimboDimension(targetLevel);
    }

    @Unique
    private Entity dimdoors$sendMaskedTeleportToLimbo() {
        return TeleportUtil.teleportUntargeted((ServerPlayer) (Object) this, ModDimensions.LIMBO_DIMENSION);
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    public void checkDeathServer(DamageSource source, CallbackInfo ci) {
        this.doOnDeathStuff(source, ci);
        if (ci.isCancelled()) {
            if (ModDimensions.isPocketDimension(this.level())) {
                this.awardStat(ModStats.DEATHS_IN_POCKETS);
            }
            this.awardStat(ModStats.TIMES_SENT_TO_LIMBO);
            TeleportUtil.teleportRandom((Entity) (Object) this, ModDimensions.LIMBO_DIMENSION, 512);
            LimboEntranceSource.ofDamageSource(source).broadcast((Player) (Object) this, this.getServer());
        }
    }

    @Inject(method = "setRespawnPosition", at = @At("TAIL"))
    public void onSpawnPointSet(ResourceKey<Level> dimension, BlockPos pos, float angle, boolean spawnPointSet, boolean bl, CallbackInfo ci) {
        if (ModDimensions.isPocketDimension(dimension)) {
            ModCriteria.POCKET_SPAWN_POINT_SET.trigger((ServerPlayer) (Object) this);
        }
    }

    @Override
    public void recordAfterBlockMove(BlockState state, Level world, BlockPos pos) {
        afterBlockMoves.add(new AfterBlockRecord(state, world, pos));
    }

    @Override
    public void playerBackAfterBlockMove() {

    }
}
