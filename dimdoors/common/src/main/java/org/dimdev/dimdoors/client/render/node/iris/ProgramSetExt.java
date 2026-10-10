package org.dimdev.dimdoors.client.render.node.iris;

import net.irisshaders.iris.shaderpack.programs.ProgramSource;
import org.jetbrains.annotations.Nullable;

public interface ProgramSetExt {
    @Nullable ProgramSource dimensionalDoors$readProgram(String name);
}
