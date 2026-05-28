package org.dimdev.dimdoors.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.entity.mask.MaskType;
import org.dimdev.dimdoors.world.ModDimensions;

public final class MaskWearerEffects {
    private static final int MAX_STACKS = 5;
    private static final int WITHER_REFRESH_INTERVAL = 40;
    private static final int WITHER_DURATION = 80;
    private static final int SUBTYPE_EFFECT_REFRESH = 40;
    private static final int CYCLOP_TELEPORT_INTERVAL = 180;
    private static final int CYCLOP_TELEPORT_MAX_BLOCKS = 24;
    private static final double ENLIGHTENED_NOTICE_RANGE = 32.0;

    private MaskWearerEffects() {
    }

    static int maxStacks() {
        return MAX_STACKS;
    }

    static ItemStack createMaskStack(ServerPlayer player, MaskType type, int stacks) {
        ItemStack stack = createMaskStack(type);
        stack.set(ModDataComponentTypes.MASK_STACKS, Mth.clamp(stacks, 1, MAX_STACKS));
        stack.enchant(player.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.BINDING_CURSE), 1);
        return stack;
    }

    public static ItemStack createMaskStack(MaskType type) {
        ItemStack stack = new ItemStack(ModItems.MASK);
        stack.set(ModDataComponentTypes.MASK_TYPE, type == MaskType.RANDOM ? MaskType.CYCLOP : type);
        return stack;
    }

    static void tickWornMask(ServerPlayer player, ItemStack mask) {
        if (ModDimensions.isLimboDimension(player.level())) {
            destroyMask(player, true);
            return;
        }

        boolean inPocket = ModDimensions.isPocketDimension(player.level());
        if (!inPocket) {
            if (player.isSleeping()) {
                destroyMask(player, false);
            }

            calmNearbyEndermen(player);
            return;
        }

        int stacks = MaskItem.getMaskStacks(mask);

        if (player.tickCount % WITHER_REFRESH_INTERVAL == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.WITHER, WITHER_DURATION, witherAmplifier(stacks)));
        }

        if (stacks >= MAX_STACKS && player.tickCount % SUBTYPE_EFFECT_REFRESH == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 0));
        }

        applySubtypeEffect(player, MaskItem.getMaskType(mask));
        calmNearbyEndermen(player);
    }

    private static int witherAmplifier(int stacks) {
        return stacks >= MAX_STACKS ? 2 : stacks >= 3 ? 1 : 0;
    }

    private static void applySubtypeEffect(ServerPlayer player, MaskType type) {
        if (type == MaskType.CYCLOP && player.tickCount % CYCLOP_TELEPORT_INTERVAL == 0 && player.getRandom().nextInt(3) == 0) {
            cyclopTeleport(player);
        }

        if (player.tickCount % SUBTYPE_EFFECT_REFRESH != 0) {
            return;
        }

        switch (type) {
            case ECHO -> player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 80, 1));
            case ENLIGHTENED -> {
                player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 80, 0));
                drawHostileAttention(player);
            }
            case SCULKING -> player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 0));
            case CYCLOP, FORESIGHT, RANDOM, BLACK -> {
            }
        }
    }

    private static void cyclopTeleport(ServerPlayer player) {
        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(player.getRandom());
        BlockPos origin = player.blockPosition();
        BlockPos destination = origin;

        for (int i = 1; i <= CYCLOP_TELEPORT_MAX_BLOCKS; i++) {
            BlockPos candidate = origin.relative(direction, i);

            if (canStandAt(player, candidate)) {
                destination = candidate;
                continue;
            }

            if (player.level().getBlockState(candidate).getBlock() instanceof DoorBlock) {
                continue;
            }

            break;
        }

        if (!destination.equals(origin)) {
            player.connection.teleport(
                    destination.getX() + 0.5,
                    player.getY(),
                    destination.getZ() + 0.5,
                    player.getYRot(),
                    player.getXRot()
            );

            player.level().playSound(
                    null,
                    destination,
                    SoundEvents.CHORUS_FRUIT_TELEPORT,
                    SoundSource.PLAYERS,
                    0.8F,
                    0.55F
            );
        }
    }

    private static boolean canStandAt(ServerPlayer player, BlockPos pos) {
        Vec3 target = new Vec3(pos.getX() + 0.5, player.getY(), pos.getZ() + 0.5);
        AABB moved = player.getBoundingBox().move(target.subtract(player.position()));
        return player.level().noCollision(player, moved);
    }

    private static void drawHostileAttention(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        AABB area = player.getBoundingBox().inflate(ENLIGHTENED_NOTICE_RANGE);

        for (Mob mob : serverLevel.getEntitiesOfClass(
                Mob.class,
                area,
                mob -> mob instanceof Enemy && mob.getTarget() == null && mob.hasLineOfSight(player)
        )) {
            mob.setTarget(player);
        }
    }

    private static void calmNearbyEndermen(ServerPlayer player) {
        if (player.tickCount % 20 != 0 || !(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        AABB area = player.getBoundingBox().inflate(64.0);

        for (EnderMan enderMan : serverLevel.getEntitiesOfClass(EnderMan.class, area)) {
            if (enderMan.getTarget() == player) {
                enderMan.setTarget(null);
            }
        }
    }

    private static void destroyMask(ServerPlayer player, boolean burns) {
        player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);

        player.level().playSound(
                null,
                player.blockPosition(),
                burns ? SoundEvents.FIRE_EXTINGUISH : SoundEvents.ITEM_BREAK,
                SoundSource.PLAYERS,
                0.9F,
                burns ? 0.6F : 1.2F
        );
    }
}