//package org.dimdev.dimdoors.compat.create;
//
//import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
//import net.minecraft.core.BlockPos;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.sounds.SoundSource;
//import net.minecraft.util.Mth;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.DoorBlock;
//import net.minecraft.world.level.block.entity.BlockEntityType;
//import net.minecraft.world.level.block.state.BlockState;
//import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
//import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
//
//public class SlidingEntranceRiftBlockEntity extends EntranceRiftBlockEntity {
//    private static final float ANIMATION_STEP = .15f;
//
//    private float animation;
//    private float previousAnimation;
//    private BlockState renderBlockState;
//    int bridgeTicks;
//    boolean deferUpdate;
//
//    public SlidingEntranceRiftBlockEntity(BlockPos pos, BlockState state) {
//        this(CreateCompatBlockEntityTypes.SLIDING_ENTRANCE_RIFT, pos, state);
//    }
//
//    protected SlidingEntranceRiftBlockEntity(BlockEntityType<? extends SlidingEntranceRiftBlockEntity> type, BlockPos pos, BlockState state) {
//        super(type, pos, state);
//        this.animation = isOpen(state) ? 1 : 0;
//        this.previousAnimation = this.animation;
//        this.renderBlockState = createRenderBlockState(state);
//    }
//
//    @Override
//    public void tick(Level level, BlockPos pos, BlockState blockState) {
//        if (deferUpdate && !level.isClientSide()) {
//            deferUpdate = false;
//            BlockState currentState = getBlockState();
//            currentState.handleNeighborChanged(level, worldPosition, Blocks.AIR, worldPosition, false);
//        }
//
//        super.tick(level, pos, blockState);
//
//        boolean open = isOpen(getBlockState());
//        float target = open ? 1 : 0;
//        boolean wasSettled = isAnimationSettled(target);
//        previousAnimation = animation;
//        animation = chase(animation, target);
//
//        if (level.isClientSide()) {
//            if (bridgeTicks < 2 && open) {
//                bridgeTicks++;
//            } else if (bridgeTicks > 0 && !open && isVisible(getBlockState())) {
//                bridgeTicks--;
//            }
//            return;
//        }
//
//        if (!open && !wasSettled && isAnimationSettled(target) && !isVisible(getBlockState())) {
//            showBlockModel();
//        }
//    }
//
//    @Override
//    public void setBlockState(BlockState state) {
//        super.setBlockState(state);
//        this.renderBlockState = createRenderBlockState(state);
//    }
//
//    @Override
//    public BlockState getRenderBlockState() {
//        return renderBlockState == null ? super.getRenderBlockState() : renderBlockState;
//    }
//
//    public float getAnimation(float partialTicks) {
//        return Mth.lerp(partialTicks, previousAnimation, animation);
//    }
//
//    public boolean shouldRenderSliding(BlockState state) {
//        return !isVisible(state) || bridgeTicks != 0;
//    }
//
//    private BlockState createRenderBlockState(BlockState state) {
//        if (state.getBlock() instanceof DimensionalDoorBlockRegistrar.AutoGenDimensionalDoorBlock autoDimensionalDoorBlock) {
//            return autoDimensionalDoorBlock.getEffectiveBlockState(state);
//        }
//        return state;
//    }
//
//    private boolean isVisible(BlockState state) {
//        return state.getOptionalValue(SlidingDoorBlock.VISIBLE)
//                .orElse(true);
//    }
//
//    private void showBlockModel() {
//        BlockState state = getBlockState();
//        if (!state.hasProperty(SlidingDoorBlock.VISIBLE)) {
//            return;
//        }
//
//        level.setBlock(worldPosition, state.setValue(SlidingDoorBlock.VISIBLE, true), Block.UPDATE_ALL);
//        level.playSound(null, worldPosition, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, .5f, 1);
//    }
//
//    private static boolean isOpen(BlockState state) {
//        return state.getOptionalValue(DoorBlock.OPEN)
//                .orElse(false);
//    }
//
//    private static float chase(float value, float target) {
//        if (value < target) {
//            return Math.min(target, value + ANIMATION_STEP);
//        }
//        if (value > target) {
//            return Math.max(target, value - ANIMATION_STEP);
//        }
//        return value;
//    }
//
//    private boolean isAnimationSettled(float target) {
//        return Math.abs(animation - target) < 1.0E-4f;
//    }
//}
