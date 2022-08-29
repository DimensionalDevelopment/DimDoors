package org.dimdev.dimdoors.entity;

import dev.onyxstudios.cca.api.v3.util.NbtSerializable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.dimdoors.entity.ai.MaskPatrolMove;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class MaskEntity extends PathAwareEntity implements IAnimatable { // TODO
    private AnimationFactory factory = new AnimationFactory(this);
	private PatrolData patrolData;

    protected MaskEntity(EntityType<? extends MaskEntity> entityType, World world) {
        super(entityType, world);
		patrolData = new PatrolData(this.getBlockPos(), List.of(new BlockPos(0, 0, 0), new BlockPos(0, 0, 10)));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mask.hover", true));
        return PlayState.CONTINUE;
    }

	@Override
	protected void initGoals() {
		goalSelector.add(0, new MaskPatrolMove(this));
	}

	@Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

	public PatrolData getPatrolData() {
		return patrolData;
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		patrolData.fromTag(nbt.getCompound("patrolData"));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.put("patrolData", patrolData.toTag(new NbtCompound()));
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return ModSoundEvents.MASK_CRACK;
	}

	public static final class PatrolData implements NbtSerializable {
		private List<BlockPos> points;
		private BlockPos origin;
		private int index;

		public PatrolData(BlockPos origin, List<BlockPos> points) {
			this(origin, points, 0);
		}

		public PatrolData(BlockPos origin, List<BlockPos> points, int index) {
			this.points = points;
			this.origin = origin;
			this.index = index;
		}

		public PatrolData(BlockPos origin) {
			this(origin, new ArrayList<>());
		}

		public boolean isEmpty() {
			return points.isEmpty();
		}

		public BlockPos getNextTarget() {
			return origin.add(points.get(((index = (index + 1) % (points.size())))));
		}

		public BlockPos getCurrentTarget() {
			return origin.add(points.get(index));
		}

		@Override
		public void fromTag(NbtCompound nbtCompound) {
			origin = BlockPos.fromLong(nbtCompound.getLong("origin"));
			points = LongStream.of(nbtCompound.getLongArray("points")).mapToObj(BlockPos::fromLong).collect(Collectors.toList());
			index = nbtCompound.getInt("index") % points.size();
		}

		@Override
		public NbtCompound toTag(NbtCompound nbtCompound) {
			nbtCompound.putLong("origin", origin.asLong());
			nbtCompound.putLongArray("points", points.stream().map(BlockPos::asLong).collect(Collectors.toList()));
			nbtCompound.putInt("index", index);
			return nbtCompound;
		}
	}
}
