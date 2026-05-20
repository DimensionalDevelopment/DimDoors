package org.dimdev.dimdoors.compat.create;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.BiConsumer;

public final class CreateClientCompat {
    private CreateClientCompat() {
    }

    public static void initBlockEntityRenderers(BiConsumer<BlockEntityType, BlockEntityRendererProvider> blockEntityRenderers) {
        blockEntityRenderers.accept(CreateCompatBlockEntityTypes.SLIDING_ENTRANCE_RIFT, SlidingEntranceRiftBlockEntityRenderer::new);
    }
}
