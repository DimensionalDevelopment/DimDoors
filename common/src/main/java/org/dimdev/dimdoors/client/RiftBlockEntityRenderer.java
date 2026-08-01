package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.dimdev.dimdoors.block.entity.Rift;
import org.dimdev.dimdoors.item.ModItems;

import java.util.ArrayList;

public abstract class RiftBlockEntityRenderer<T extends BlockEntity & Rift> implements BlockEntityRenderer<T> {
    private final BlockEntityRendererProvider.Context context;

    public RiftBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(T rift, float f, PoseStack matrices, MultiBufferSource multiBufferSource, int i, int j) {
        var minecraft = Minecraft.getInstance();

        if(minecraft.player != null && minecraft.player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.RIFT_CONFIGURATION_TOOL)) {
            matrices.pushPose();

            matrices.translate(0.5D, 1.25D, 0.5D);
            matrices.mulPose(minecraft.getBlockEntityRenderDispatcher().camera.rotation());
            matrices.scale(0.025F, -0.025F, 0.025F);

            RenderUtils.renderTextLines(Util.make(new ArrayList<>(), list -> rift.gatherDebug(list::add)), matrices, multiBufferSource, context.getFont(), LightTexture.FULL_BRIGHT);

            matrices.popPose();
        }
    }

}
