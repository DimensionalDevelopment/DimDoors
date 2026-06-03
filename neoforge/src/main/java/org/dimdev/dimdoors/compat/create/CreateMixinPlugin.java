//package org.dimdev.dimdoors.compat.create;
//
//import org.objectweb.asm.tree.ClassNode;
//import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
//import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
//import org.spongepowered.asm.service.MixinService;
//
//import java.util.List;
//import java.util.Set;
//
//public class CreateMixinPlugin implements IMixinConfigPlugin {
//    private final boolean createLoaded;
//
//    public CreateMixinPlugin() {
//        boolean loaded;
//        try {
//            loaded = MixinService.getService().getBytecodeProvider().getClassNode("com.simibubi.create.Create") != null;
//        } catch (Exception e) {
//            loaded = false;
//        }
//        this.createLoaded = loaded;
//    }
//
//    @Override
//    public void onLoad(String mixinPackage) {
//    }
//
//    @Override
//    public String getRefMapperConfig() {
//        return "";
//    }
//
//    @Override
//    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
//        return this.createLoaded;
//    }
//
//    @Override
//    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
//    }
//
//    @Override
//    public List<String> getMixins() {
//        return List.of();
//    }
//
//    @Override
//    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
//    }
//
//    @Override
//    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
//    }
//}
