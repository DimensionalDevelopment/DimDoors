package StevenDimDoors.mod_pocketDim.network;

import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.network.handlers.*;
import StevenDimDoors.mod_pocketDim.network.packets.*;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
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
public class DimDoorsNetwork extends SimpleNetworkWrapper {

    private static final DimDoorsNetwork INSTANCE = new DimDoorsNetwork(mod_pocketDim.modid);

    public DimDoorsNetwork(String channelName) {
        super(channelName);
    }

    public static void init() {
        INSTANCE.registerMessage(ClientJoinHandler.class, ClientJoinPacket.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(CreateDimensionHandler.class, CreateDimensionPacket.class, 1, Side.CLIENT);
        INSTANCE.registerMessage(DeleteDimensionHandler.class, DeleteDimensionPacket.class, 2, Side.CLIENT);
        INSTANCE.registerMessage(CreateLinkHandler.class, CreateLinkPacket.class, 3, Side.CLIENT);
        INSTANCE.registerMessage(DeleteLinkHandler.class, DeleteLinkPacket.class, 4, Side.CLIENT);
        INSTANCE.registerMessage(UpdateLinkHandler.class, UpdateLinkPacket.class, 5, Side.CLIENT);
    }

    public static void sendToAllPlayers(IMessage message) {
        INSTANCE.sendToAll(message);
    }

    public static void sendToPlayer(IMessage message, EntityPlayer player) {
        if (player instanceof EntityPlayerMP)
            INSTANCE.sendTo(message, (EntityPlayerMP)player);
    }
}