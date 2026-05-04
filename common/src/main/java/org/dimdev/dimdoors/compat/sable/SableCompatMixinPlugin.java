package org.dimdev.dimdoors.compat.sable;

import dev.ryanhcode.sable.companion.SableCompanion;
import org.dimdev.dimdoors.item.RaycastHelper;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class SableCompatMixinPlugin implements IMixinConfigPlugin {
    private final boolean sableLoaded;

    public SableCompatMixinPlugin() {
        boolean loaded = false;
        try {
            Class.forName("dev.ryanhcode.sable.Sable"); // or any Sable class that always exists
            loaded = true;
            RaycastHelper.transformFunction = SableCompanion.INSTANCE::projectOutOfSubLevel;
        } catch (ClassNotFoundException ignored) {
        }

        sableLoaded = loaded;
    }

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return sableLoaded;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
