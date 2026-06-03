//package org.dimdev.dimdoors.compat.create.mixin;
//
//import com.simibubi.create.content.contraptions.Contraption;
//import com.simibubi.create.content.contraptions.StructureTransform;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.HolderLookup;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.ListTag;
//import net.minecraft.nbt.Tag;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
//import org.apache.commons.lang3.tuple.Pair;
//import org.dimdev.dimdoors.api.util.Location;
//import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
//import org.dimdev.dimdoors.compat.create.CreateRiftMovement;
//import org.dimdev.dimdoors.compat.create.DimDoorsCreateContraption;
//import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Mixin(value = Contraption.class, remap = false)
//public abstract class ContraptionMixin implements DimDoorsCreateContraption {
//    @Unique
//    private static final String DIMDOORS_RIFTS_KEY = "DimDoorsRifts";
//    @Unique
//    private static final String DIMDOORS_POS_KEY = "Pos";
//    @Unique
//    private static final String DIMDOORS_LOCATION_KEY = "Location";
//
//    @Unique
//    private final Map<BlockPos, Location> dimdoors$trackedRifts = new HashMap<>();
//
//    @Shadow
//    public BlockPos anchor;
//
//    @Override
//    public Map<BlockPos, Location> dimdoors$getTrackedRifts() {
//        return this.dimdoors$trackedRifts;
//    }
//
//    @Inject(method = "addBlock", at = @At("TAIL"))
//    private void dimdoors$trackRift(Level level, BlockPos pos, Pair<StructureBlockInfo, BlockEntity> pair, CallbackInfo ci) {
//        if (!(level instanceof ServerLevel serverLevel) || !(pair.getValue() instanceof RiftBlockEntity)) {
//            return;
//        }
//
//        Location source = Location.ofWorld(serverLevel, pos);
//        if (DimensionalRegistry.getRiftRegistry().isRiftAt(source)) {
//            this.dimdoors$trackedRifts.put(pos.subtract(this.anchor), source);
//        }
//    }
//
//    @Inject(method = "removeBlocksFromWorld", at = @At("HEAD"))
//    private void dimdoors$prepareRiftsForRemoval(Level world, BlockPos offset, CallbackInfo ci) {
//        if (!(world instanceof ServerLevel serverLevel)) {
//            return;
//        }
//
//        for (Map.Entry<BlockPos, Location> entry : Map.copyOf(this.dimdoors$trackedRifts).entrySet()) {
//            BlockPos sourcePos = entry.getKey().offset(this.anchor).offset(offset);
//            Location actualSource = Location.ofWorld(serverLevel, sourcePos);
//            if (!entry.getValue().equals(actualSource) && DimensionalRegistry.getRiftRegistry().isRiftAt(actualSource)) {
//                this.dimdoors$trackedRifts.put(entry.getKey(), actualSource);
//            }
//
//            BlockEntity blockEntity = world.getBlockEntity(sourcePos);
//            if (blockEntity instanceof RiftBlockEntity rift) {
//                rift.setDeleteRift(false);
//            }
//        }
//    }
//
//    @Inject(method = "addBlocksToWorld", at = @At("TAIL"))
//    private void dimdoors$moveRiftsToDisassembledPositions(Level world, StructureTransform transform, CallbackInfo ci) {
//        if (world instanceof ServerLevel serverLevel) {
//            CreateRiftMovement.moveTrackedRifts(serverLevel, this.dimdoors$trackedRifts, transform);
//        }
//    }
//
//    @Inject(method = "writeNBT", at = @At("RETURN"))
//    private void dimdoors$writeTrackedRifts(HolderLookup.Provider registries, boolean spawnPacket, CallbackInfoReturnable<CompoundTag> cir) {
//        if (this.dimdoors$trackedRifts.isEmpty()) {
//            return;
//        }
//
//        ListTag rifts = new ListTag();
//        for (Map.Entry<BlockPos, Location> entry : this.dimdoors$trackedRifts.entrySet()) {
//            CompoundTag tag = new CompoundTag();
//            tag.putIntArray(DIMDOORS_POS_KEY, new int[]{entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getZ()});
//            tag.put(DIMDOORS_LOCATION_KEY, Location.toNbt(entry.getValue()));
//            rifts.add(tag);
//        }
//
//        cir.getReturnValue().put(DIMDOORS_RIFTS_KEY, rifts);
//    }
//
//    @Inject(method = "readNBT", at = @At("TAIL"))
//    private void dimdoors$readTrackedRifts(Level world, CompoundTag nbt, boolean spawnData, CallbackInfo ci) {
//        this.dimdoors$trackedRifts.clear();
//
//        if (!nbt.contains(DIMDOORS_RIFTS_KEY, Tag.TAG_LIST)) {
//            return;
//        }
//
//        ListTag rifts = nbt.getList(DIMDOORS_RIFTS_KEY, Tag.TAG_COMPOUND);
//        for (Tag riftTag : rifts) {
//            CompoundTag tag = (CompoundTag) riftTag;
//            int[] pos = tag.getIntArray(DIMDOORS_POS_KEY);
//            if (pos.length != 3 || !tag.contains(DIMDOORS_LOCATION_KEY, Tag.TAG_COMPOUND)) {
//                continue;
//            }
//
//            this.dimdoors$trackedRifts.put(new BlockPos(pos[0], pos[1], pos[2]), Location.fromNbt(tag.getCompound(DIMDOORS_LOCATION_KEY)));
//        }
//    }
//}
