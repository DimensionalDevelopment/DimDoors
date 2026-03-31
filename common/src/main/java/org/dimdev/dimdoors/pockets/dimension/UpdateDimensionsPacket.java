package org.dimdev.dimdoors.pockets.dimension;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;

/**
 * @param keys Keys to add or remove in the client's dimension list
 * @param add If true, keys are to be added; if false, keys are to be removed
 */
public record UpdateDimensionsPacket(Set<ResourceKey<Level>> keys, boolean add) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<UpdateDimensionsPacket> TYPE = new CustomPacketPayload.Type<>(DimensionalDoors.id( "update_dimensions"));
	
	public static final StreamCodec<ByteBuf, UpdateDimensionsPacket> STREAM_CODEC = StreamCodec.composite(
		ResourceKey.streamCodec(Registries.DIMENSION).apply(ByteBufCodecs.list()).map(Set::copyOf, List::copyOf), UpdateDimensionsPacket::keys,
		ByteBufCodecs.BOOL, UpdateDimensionsPacket::add,
		UpdateDimensionsPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
}