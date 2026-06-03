//package org.dimdev.dimdoors.compat.create;
//
//import com.simibubi.create.content.contraptions.StructureTransform;
//import net.minecraft.core.BlockPos;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//import org.dimdev.dimdoors.api.util.Location;
//import org.dimdev.dimdoors.block.RiftProvider;
//import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
//import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//public final class CreateRiftMovement {
//    private CreateRiftMovement() {
//    }
//
//    public static boolean hasBlockingRiftTarget(ServerLevel level, Map<BlockPos, Location> trackedRifts, StructureTransform transform) {
//        if (trackedRifts.isEmpty()) {
//            return false;
//        }
//
//        Set<Location> movingSources = new HashSet<>(trackedRifts.values());
//        var registry = DimensionalRegistry.getRiftRegistry();
//
//        for (Map.Entry<BlockPos, Location> entry : trackedRifts.entrySet()) {
//            Location source = entry.getValue();
//            Location target = Location.ofWorld(level, transform.apply(entry.getKey()));
//
//            if (source.equals(target)) {
//                continue;
//            }
//
//            if (movingSources.contains(target)) {
//                continue;
//            }
//
//            BlockState targetState = level.getBlockState(target.getBlockPos());
//            if (stateContainsRift(targetState) || registry.isRiftAt(target)) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    public static void moveTrackedRifts(ServerLevel level, Map<BlockPos, Location> trackedRifts, StructureTransform transform) {
//        if (trackedRifts.isEmpty()) {
//            return;
//        }
//
//        var registry = DimensionalRegistry.getRiftRegistry();
//        Map<Location, Location> registryMoves = new HashMap<>();
//
//        for (Map.Entry<BlockPos, Location> entry : trackedRifts.entrySet()) {
//            Location source = entry.getValue();
//            BlockPos targetPos = transform.apply(entry.getKey());
//            Location target = Location.ofWorld(level, targetPos);
//
//            if (registry.isRiftAt(source)) {
//                registryMoves.put(source, target);
//            }
//        }
//
//        registry.moveRifts(registryMoves);
//
//        for (Map.Entry<BlockPos, Location> entry : trackedRifts.entrySet()) {
//            BlockPos targetPos = transform.apply(entry.getKey());
//            BlockEntity blockEntity = level.getBlockEntity(targetPos);
//            if (blockEntity instanceof RiftBlockEntity rift) {
//                if (!registry.isRiftAt(Location.ofWorld(level, targetPos))) {
//                    rift.register();
//                }
//                rift.updateType();
//                rift.updateProperties();
//            }
//        }
//    }
//
//    public static boolean stateContainsRift(BlockState state) {
//        return state.getBlock() instanceof RiftProvider<?> riftProvider && riftProvider.stateContainsRift(state);
//    }
//}
