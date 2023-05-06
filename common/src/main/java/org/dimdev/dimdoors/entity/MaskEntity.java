package org.dimdev.dimdoors.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

//import software.bernie.geckolib3.core.IAnimatable;
//import software.bernie.geckolib3.core.PlayState;
//import software.bernie.geckolib3.core.builder.AnimationBuilder;
//import software.bernie.geckolib3.core.controller.AnimationController;
//import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
//import software.bernie.geckolib3.core.manager.AnimationData;
//import software.bernie.geckolib3.core.manager.AnimationFactory;

public class MaskEntity extends PathfinderMob /*implements IAnimatable*/ { // TODO
//    private AnimationFactory factory = new AnimationFactory(this);

    protected MaskEntity(EntityType<? extends MaskEntity> entityType, Level world) {
        super(entityType, world);
    }

//    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
//        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mask.hover", true));
//        return PlayState.CONTINUE;
//    }

//    @Override
//    public void registerControllers(AnimationData data) {
//        data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
//    }

//    @Override
//    public AnimationFactory getFactory() {
//        return this.factory;
//    }
}
