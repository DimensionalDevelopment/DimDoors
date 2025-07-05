//package org.dimdev.dimdoors.pockets.modifier;
//
//import com.mojang.serialization.MapCodec;
//import org.dimdev.dimdoors.pockets.PocketGenerationContext;
//import org.dimdev.dimdoors.world.pocket.type.Pocket;
//
//public class NoneModifer implements Modifier {
//    public static final NoneModifer INSTANCE = new NoneModifer();
//    public static final MapCodec<NoneModifer> CODEC = MapCodec.unit(INSTANCE);
//
//    public static final String KEY = "none";
//
//    @Override
//    public ModifierType<? extends Modifier> getType() {
//        return ModifierType.NONE_MODIFIER_TYPE.get();
//    }
//
//    @Override
//    public void apply(PocketGenerationContext parameters, RiftManager manager) {
//
//    }
//
//    @Override
//    public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
//
//    }
//}
