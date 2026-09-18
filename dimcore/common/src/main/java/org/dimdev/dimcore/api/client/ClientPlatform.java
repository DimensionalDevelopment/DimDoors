package org.dimdev.dimcore.api.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public interface ClientPlatform {
    void register(RenderType type, Block... blocks);

    void onClientPlayerJoin(Runnable listener);

    default TextureAtlasSprite fluidSprite(Fluid fluid) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper()
                .getParticleIcon(fluid.defaultFluidState().createLegacyBlock());
    }

    default int fluidTint(Fluid fluid, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
        return Minecraft.getInstance().getBlockColors()
                .getColor(fluid.defaultFluidState().createLegacyBlock(), level, pos, 0);
    }
}
