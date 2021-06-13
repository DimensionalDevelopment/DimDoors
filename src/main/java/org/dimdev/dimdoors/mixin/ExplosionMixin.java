package org.dimdev.dimdoors.mixin;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;

import static org.dimdev.dimdoors.block.ModBlocks.DETACHED_RIFT;

@Mixin(Explosion.class)
public class ExplosionMixin {
	private static final Logger LOGGER = LogManager.getLogger();
	@Shadow
	private static final int field_30960 = 16;
	@Shadow
	private boolean createFire;
	@Shadow
	private Explosion.DestructionType destructionType;
	@Shadow
	private Random random;
	@Shadow
	private World world;
	@Shadow
	private double x;
	@Shadow
	private double y;
	@Shadow
	private double z;
	@Shadow
	@Nullable
	private Entity entity;
	@Shadow
	private float power;
	@Shadow
	private DamageSource damageSource;
	@Shadow
	private ExplosionBehavior behavior;
	@Shadow
	private List<BlockPos> affectedBlocks;
	@Shadow
	private Map<PlayerEntity, Vec3d> affectedPlayers;

	@Shadow
	private static void tryMergeStack(ObjectArrayList<Pair<ItemStack, BlockPos>> stacks, ItemStack stack, BlockPos pos) {

	}
	/**
	 * @author - MalekiRe
	 */
	@Overwrite
	public void affectWorld(boolean particles) {
		if (this.world.isClient) {
			this.world.playSound(this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F, false);
		}

		boolean bl = this.destructionType != Explosion.DestructionType.NONE;
		if (particles) {
			if (!(this.power < 2.0F) && bl) {
				this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
			} else {
				this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
			}
		}

		if (bl) {
			ObjectArrayList<Pair<ItemStack, BlockPos>> objectArrayList = new ObjectArrayList();
			Collections.shuffle(this.affectedBlocks, this.world.random);

			for (BlockPos blockPos : this.affectedBlocks) {
				BlockState blockState = this.world.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (!blockState.isAir()) {
					BlockPos blockPos2 = blockPos.toImmutable();
					this.world.getProfiler().push("explosion_blocks");
					if (block.shouldDropItemsOnExplosion((Explosion) (Object) this) && this.world instanceof ServerWorld) {
						//TODO: Change this to work with trapdoors as well, when we implement trapdoors.
						if (block instanceof DimensionalDoorBlock) {
							LOGGER.log(Level.INFO, "Creating Detached Rift From Explosion of Door");
							((DimensionalDoorBlock) block).createDetachedRift(this.world, blockPos2);
							continue;
						}
						else if(world.getBlockState(blockPos2).getBlock() == DETACHED_RIFT) {
							continue;
						}
						BlockEntity blockEntity = blockState.hasBlockEntity() ? this.world.getBlockEntity(blockPos) : null;
						LootContext.Builder builder = (new LootContext.Builder((ServerWorld) this.world)).random(this.world.random).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(blockPos)).parameter(LootContextParameters.TOOL, ItemStack.EMPTY).optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity).optionalParameter(LootContextParameters.THIS_ENTITY, this.entity);
						if (this.destructionType == Explosion.DestructionType.DESTROY) {
							builder.parameter(LootContextParameters.EXPLOSION_RADIUS, this.power);
						}

						blockState.getDroppedStacks(builder).forEach((stack) -> {
							tryMergeStack(objectArrayList, stack, blockPos2);
						});
					}

					this.world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
					block.onDestroyedByExplosion(this.world, blockPos, (Explosion) (Object) this);
					this.world.getProfiler().pop();
				}
			}

			ObjectListIterator var12 = objectArrayList.iterator();

			while(var12.hasNext()) {
				Pair<ItemStack, BlockPos> pair = (Pair)var12.next();
				Block.dropStack(this.world, (BlockPos)pair.getSecond(), (ItemStack)pair.getFirst());
			}
		}

		if (this.createFire) {
			Iterator var11 = this.affectedBlocks.iterator();

			while(var11.hasNext()) {
				BlockPos blockPos3 = (BlockPos)var11.next();
				if (this.random.nextInt(3) == 0 && this.world.getBlockState(blockPos3).isAir() && this.world.getBlockState(blockPos3.down()).isOpaqueFullCube(this.world, blockPos3.down())) {
					this.world.setBlockState(blockPos3, AbstractFireBlock.getState(this.world, blockPos3));
				}
			}
		}

	}
}
