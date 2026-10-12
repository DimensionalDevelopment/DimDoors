package org.dimdev.dimcore.compat.iris.node;

import net.irisshaders.iris.shaderpack.programs.ProgramSource;
import org.jetbrains.annotations.Nullable;

public interface ProgramSetExt {
    @Nullable ProgramSource dimensionalDoors$readProgram(String name);
}
