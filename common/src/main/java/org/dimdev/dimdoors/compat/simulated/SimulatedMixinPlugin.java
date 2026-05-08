package org.dimdev.dimdoors.compat.simulated;

import dev.simulated_team.simulated.Simulated;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.util.List;
import java.util.Set;

public class SimulatedMixinPlugin implements IMixinConfigPlugin {
    private boolean isSimulatedLoaded = false;

    public SimulatedMixinPlugin() {
        try {
            isSimulatedLoaded = MixinService.getService().getBytecodeProvider().getClassNode("dev.simulated_team.simulated.Simulated") != null;
        } catch (Exception e) {
            isSimulatedLoaded = false;
        }
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
        return isSimulatedLoaded;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}

