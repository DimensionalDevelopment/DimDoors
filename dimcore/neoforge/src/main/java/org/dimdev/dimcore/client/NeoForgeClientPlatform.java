package org.dimdev.dimcore.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.NeoForge;
import org.dimdev.dimcore.api.client.ClientPlatform;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NeoForgeClientPlatform implements ClientPlatform {
    private final List<Runnable> loginRunnables = new ArrayList<>();

    public NeoForgeClientPlatform() {
        NeoForge.EVENT_BUS.<ClientPlayerNetworkEvent.LoggingIn>addListener(event -> loginRunnables.forEach(Runnable::run));
    }

    @Override
    public void register(RenderType type, Block... blocks) {
        for (Block block : blocks) {
            ItemBlockRenderTypes.setRenderLayer(block, type);
        }
    }

    @Override
    public void onClientPlayerJoin(Runnable listener) {
        loginRunnables.add(listener);
    }

    @Override
    public TextureAtlasSprite fluidSprite(Fluid fluid) {
        ResourceLocation id = IClientFluidTypeExtensions.of(fluid).getStillTexture();
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(id);
    }

    @Override
    public int fluidTint(Fluid fluid, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid);
        return level != null && pos != null ? extensions.getTintColor(fluid.defaultFluidState(), level, pos) : extensions.getTintColor();
    }
}
