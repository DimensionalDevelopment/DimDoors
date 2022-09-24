package org.dimdev.dimdoors.entity;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.goal.WanderAroundPointOfInterestGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.dimdev.dimdoors.entity.ai.mask.MaskWanderGoal;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class AbstractMaskEntity extends PathAwareEntity implements IAnimatable { // TODO

	private static final TrackedData<Integer> AI_MODE = DataTracker.registerData(AbstractMaskEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private final AnimationFactory factory = new AnimationFactory(this);
	private Pocket pocket;
	private BlockPos pocketOrigin;
	private BlockBox pocketBounds;

	private int

    protected AbstractMaskEntity(EntityType<? extends AbstractMaskEntity> entityType, World world) {
        super(entityType, world);
    }

	public static DefaultAttributeContainer.Builder createMobAttributes() {
		return LivingEntity.createLivingAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 50.0);
	}

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mask.hover", true));
        return PlayState.CONTINUE;
    }

	public void initializeInPocket(Pocket pocket, double xOffset, double yOffset, double zOffset) {
		this.pocketOrigin = pocket.getOrigin();
		this.pocketBounds = pocket.getBox();
		this.setPosition(this.pocketOrigin.getX()+xOffset,this.pocketOrigin.getY()+yOffset,this.pocketOrigin.getZ()+zOffset);
	}

	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
		setNoGravity(true);
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	@Override
	protected void initGoals() {
		goalSelector.add(0, new WanderAroundGoal(this,0.5d));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(AI_MODE,0);
	}

	@Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

	public BlockPos getPocketOrigin() {
		return this.pocketOrigin;
	}

	public BlockBox getPocketBounds() {
		return this.pocketBounds;
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
	}

	/*

	@SuppressWarnings("UnstableApiUsage")
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
		public @NotNull NbtCompound toTag(NbtCompound nbtCompound) {
			nbtCompound.putLong("origin", origin.asLong());
			nbtCompound.putLongArray("points", points.stream().map(BlockPos::asLong).collect(Collectors.toList()));
			nbtCompound.putInt("index", index);
			return nbtCompound;
		}
	}
	 */
}
