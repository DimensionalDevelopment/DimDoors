package org.dimdev.dimdoors.mixin.client;

import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import org.dimdev.dimdoors.client.PostChainExt;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(PostChain.class)
public class PostChainMixin implements PostChainExt {

    @Shadow
    @Final
    private List<PostPass> passes;

    @Override
    public @NotNull List<@NotNull PostPass> getPasses() {
        return passes;
    }
}
