//package org.dimdev.dimdoors.compat.create.mixin;
//
//import com.simibubi.create.content.contraptions.Contraption;
//import com.simibubi.create.content.contraptions.StructureTransform;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.level.Level;
//import org.dimdev.dimdoors.compat.create.CreateRiftMovement;
//import org.dimdev.dimdoors.compat.create.DimDoorsCreateContraption;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(targets = "com.simibubi.create.content.contraptions.AbstractContraptionEntity", remap = false)
//public abstract class AbstractContraptionEntityMixin extends Entity {
//    protected AbstractContraptionEntityMixin(EntityType<?> entityType, Level level) {
//        super(entityType, level);
//    }
//
//    @Shadow
//    protected Contraption contraption;
//
//    @Shadow
//    protected abstract StructureTransform makeStructureTransform();
//
//    @Shadow
//    protected abstract void onContraptionStalled();
//
//    @Inject(method = "disassemble", at = @At("HEAD"), cancellable = true)
//    private void dimdoors$blockDisassemblyOverRifts(CallbackInfo ci) {
//        if (!(this.level() instanceof ServerLevel level) || this.contraption == null) {
//            return;
//        }
//
//        if (!(this.contraption instanceof DimDoorsCreateContraption dimdoorsContraption)) {
//            return;
//        }
//
//        StructureTransform transform = this.makeStructureTransform();
//        if (!CreateRiftMovement.hasBlockingRiftTarget(level, dimdoorsContraption.dimdoors$getTrackedRifts(), transform)) {
//            return;
//        }
//
//        this.contraption.stalled = true;
//        this.onContraptionStalled();
//        ci.cancel();
//    }
//}
