package org.dimdev.dimdoors.world

import com.mojang.serialization.Codec
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.util.Unit
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimcore.util.DataValue
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.entity.mask.PendingReaper

object DataValues : PlatformRegistry.DataValuePlatformRegistry(DimensionalDoors.getSided()) {
    @JvmField val FRAY_VALUE: DataValue<Int> = create("fray", { 0 }, Codec.INT, ByteBufCodecs.INT)
    @JvmField val POCKET_GENERATED: DataValue<Boolean> = create("pocket_generated", { false }, Codec.BOOL)
    @JvmField val TRANSCENDENT_PROJECTILE: DataValue<Unit> = create("transcendent_projectile", { Unit.INSTANCE }, Unit.CODEC)
    @JvmField val PLACED_CONTENT: DataValue<Int2ObjectOpenHashMap<LongArray>> = create("placed_content", { Int2ObjectOpenHashMap() }, ChunkPlacedContentTracker.CODEC)
    @JvmField val MASK_BODY_STACKS: DataValue<Int> = create("mask_body_stacks", { 0 }, Codec.INT, ByteBufCodecs.VAR_INT)
    @JvmField val MASK_CYCLOPS_TIMER: DataValue<Int> = create("mask_cyclops_timer", { 0 }, Codec.INT)
    @JvmField val PENDING_REAPER: DataValue<PendingReaper> = create("pending_reaper", { PendingReaper(BlockPos.ZERO, 0) }, PendingReaper.CODEC)
}
