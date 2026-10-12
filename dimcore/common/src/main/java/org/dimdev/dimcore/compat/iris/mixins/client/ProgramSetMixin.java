package org.dimdev.dimcore.compat.iris.mixins.client;

import net.irisshaders.iris.shaderpack.ShaderPack;
import net.irisshaders.iris.shaderpack.include.AbsolutePackPath;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.shaderpack.programs.ProgramSource;
import net.irisshaders.iris.shaderpack.properties.ShaderProperties;
import org.dimdev.dimcore.compat.iris.node.ProgramSetExt;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(value = net.irisshaders.iris.shaderpack.programs.ProgramSet.class, remap = false)
public abstract class ProgramSetMixin implements ProgramSetExt {
    @Unique
    private AbsolutePackPath dimensionalDoors$directory;
    @Unique
    private Function<AbsolutePackPath, String> dimensionalDoors$sourceProvider;
    @Unique
    private ShaderProperties dimensionalDoors$properties;

    @Shadow
    private static ProgramSource readProgramSource(AbsolutePackPath directory, Function<AbsolutePackPath, String> sourceProvider, String program, ProgramSet programSet, ShaderProperties properties, boolean readTesselation) {
        throw new AssertionError();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void dimensionalDoors$captureSources(AbsolutePackPath directory, Function<AbsolutePackPath, String> sourceProvider, ShaderProperties properties, ShaderPack pack, CallbackInfo ci) {
        dimensionalDoors$directory = directory;
        dimensionalDoors$sourceProvider = sourceProvider;
        dimensionalDoors$properties = properties;
    }

    @Override
    public @Nullable ProgramSource dimensionalDoors$readProgram(String name) {
        ProgramSource source = readProgramSource(dimensionalDoors$directory, dimensionalDoors$sourceProvider, name, (ProgramSet) (Object) this, dimensionalDoors$properties, false);
        return source.isValid() ? source : null;
    }
}
