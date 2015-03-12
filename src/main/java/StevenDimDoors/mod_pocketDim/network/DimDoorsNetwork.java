package StevenDimDoors.mod_pocketDim.network;

import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;

import java.util.EnumMap;

@ChannelHandler.Sharable
public class DimDoorsNetwork extends FMLIndexedMessageToMessageCodec<DimDoorsPacket> {

    private static final DimDoorsNetwork INSTANCE = new DimDoorsNetwork();
    private static final EnumMap<Side, FMLEmbeddedChannel> channels = Maps.newEnumMap(Side.class);

    public static void init() {
        if (!channels.isEmpty())
            return;

        INSTANCE.addDiscriminator(0, ClientJoinPacket.class);
        INSTANCE.addDiscriminator(1, CreateDimensionPacket.class);
        INSTANCE.addDiscriminator(2, DeleteDimensionPacket.class);
        INSTANCE.addDiscriminator(3, CreateLinkPacket.class);
        INSTANCE.addDiscriminator(4, DeleteLinkPacket.class);
        INSTANCE.addDiscriminator(5, UpdateLinkPacket.class);

        channels.putAll(NetworkRegistry.INSTANCE.newChannel("DimDoors", INSTANCE));
    }

    public void encodeInto(ChannelHandlerContext ctx, DimDoorsPacket msg, ByteBuf target) throws Exception {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        msg.write(out);
        target.writeBytes(out.toByteArray());
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, DimDoorsPacket msg) {
        ByteArrayDataInput in = ByteStreams.newDataInput(source.array());

        in.skipBytes(1);
        msg.read(in);

        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            handleClient(msg);
        else
            handleServer(ctx, msg);
    }

    @SideOnly(Side.CLIENT)
    private void handleClient(DimDoorsPacket msg) {
        msg.handleClient(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer);
    }

    private void handleServer(ChannelHandlerContext ctx, DimDoorsPacket msg) {
        EntityPlayerMP player = ((NetHandlerPlayServer)ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).playerEntity;
        msg.handleServer(player.worldObj, player);
    }

    public static void sendToAllPlayers(DimDoorsPacket packet) {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        channels.get(Side.SERVER).writeAndFlush(packet);
    }

    public static void sendToServer(DimDoorsPacket packet) {
        channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channels.get(Side.CLIENT).writeAndFlush(packet);
    }

    public static void sendToPlayer(DimDoorsPacket packet, EntityPlayer player) {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        channels.get(Side.SERVER).writeAndFlush(packet);
    }

    public static void sendToVicinity(DimDoorsPacket packet, TileEntity entity, double distance) {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);

        NetworkRegistry.TargetPoint vicinity = new NetworkRegistry.TargetPoint(entity.getWorldObj().provider.dimensionId, entity.xCoord + 0.5, entity.yCoord + 0.5, entity.zCoord + 0.5, distance);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(vicinity);
        channels.get(Side.SERVER).writeAndFlush(packet);
    }
}