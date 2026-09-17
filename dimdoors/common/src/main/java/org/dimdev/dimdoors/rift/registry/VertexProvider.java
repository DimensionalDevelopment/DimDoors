package org.dimdev.dimdoors.rift.registry;

import java.util.List;

public interface VertexProvider {
    List<? extends RegistryVertex> collectVertices();
}
