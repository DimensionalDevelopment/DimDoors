package org.dimdev.dimdoors.client;

import net.minecraft.client.renderer.RenderType;

import java.util.function.Consumer;

public interface ShaderPackDetector {
    void wrap(Consumer<RenderType> type);
}
