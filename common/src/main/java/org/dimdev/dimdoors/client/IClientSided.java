package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IClientSided {
    Supplier<RecipeBookCategories> getRecipBookCategories(String name, Supplier<ItemStack> itemStack);

    void onClientPlayerJoin(Runnable listener);

    void registerCoreShader(Identifier id, VertexFormat vertexFormat, Consumer<ShaderInstance> loadCallback);

    <T extends CustomPacketPayload> void sendPacket(T packet);
}
