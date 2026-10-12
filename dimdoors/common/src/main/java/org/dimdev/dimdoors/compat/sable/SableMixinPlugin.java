package org.dimdev.dimdoors.compat.sable;

import org.dimdev.dimcore.util.AbstractClassDependentMixinPlugin;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.util.List;
import java.util.Set;

//TODO Conver to kotlin once sure this won't explode if I do
public class SableMixinPlugin extends AbstractClassDependentMixinPlugin {
    public SableMixinPlugin() {
        super("dev.ryanhcode.sable.Sable");
    }
}