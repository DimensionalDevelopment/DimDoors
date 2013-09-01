package StevenDimDoors.mod_pocketDim;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import StevenDimDoors.mod_pocketDim.core.IDimLink;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler 
{
	public static byte DIM_UPDATE_PACKET_ID = 1;
	public static byte REGISTER_DIM_PACKET_ID = 2;
	public static byte REGISTER_LINK_PACKET_ID = 3;
	public static byte REMOVE_LINK_PACKET_ID = 4;
	
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) 
	{
		if (packet.channel.equals("DimDoorPackets")) 
		{ 
			processPacket(packet, player);
		}
	}

	private void processPacket(Packet250CustomPayload packet, Player player) 
	{
		ByteArrayDataInput data = ByteStreams.newDataInput(packet.data);
		int id = data.readByte();
		if (id == REGISTER_DIM_PACKET_ID)
		{
			int dimId = data.readInt();
			try
			{
				NewDimData dimDataToAdd = new NewDimData(dimId, data.readBoolean(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt());

				if(!PocketManager.dimList.containsKey(dimId))
				{
					PocketManager.dimList.put(dimId, dimDataToAdd);
				}
				if (dimDataToAdd.isPocket)
				{
					DDProperties properties = DDProperties.instance();
					PocketManager.registerDimension(dimId, properties.PocketProviderID);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else if (id == REGISTER_LINK_PACKET_ID)
		{
			int dimId = data.readInt();
			try
			{
				NewDimData dimDataToAddLink= PocketManager.instance.getDimData(dimId);

				IDimLink linkToAdd = new IDimLink(dimId, data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readBoolean(),data.readInt());
				linkToAdd.hasGennedDoor=data.readBoolean();

				PocketManager.instance.createLink(linkToAdd);

			}
			catch (Exception e)
			{
				System.err.println("Tried to update client link data and failed!");
				e.printStackTrace();
			}
		}
		else if (id == REMOVE_LINK_PACKET_ID)
		{
			int dimId = data.readInt();
			try
			{
				NewDimData dimDataToRemoveFrom= PocketManager.instance.getDimData(dimId);

				IDimLink linkToAdd = new IDimLink(dimId, data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readBoolean(),data.readInt());
				dimDataToRemoveFrom.removeLinkAtCoords(linkToAdd.locDimID, linkToAdd.locXCoord,linkToAdd.locYCoord, linkToAdd.locZCoord);
			}
			catch (Exception e)
			{
				System.out.println("Tried to update client link data & failed!");
				e.printStackTrace();
			}
		}
	}
	
	private static void processRegisterDimPacket()
	{
		
	}
	
	private static void processUpdateDimPacket()
	{
		
	}
	
	private static void processRegisterLinkPacket()
	{
		
	}
	
	private static void processRemoveLinkPacket()
	{
		
	}
	
	public static void sendClientJoinPacket(INetworkManager manager)
	{
		Collection<Integer> dimIDs= dimList.keySet();
		Collection<NewDimData> dimDataSet= dimList.values();
		Collection<Packet250CustomPayload> packetsToSend = new HashSet();

		for(NewDimData data : dimDataSet)
		{
			manager.addToSendQueue(PacketHandler.onDimCreatedPacket(data));

			Collection <HashMap<Integer, HashMap<Integer,  IDimLink>>> linkList = data.linksInThisDim.values();

			for(HashMap map :  linkList )
			{
				Collection <HashMap<Integer,  IDimLink>> linkList2 = map.values();
				for(HashMap map2 : linkList2)
				{
					Collection <IDimLink> linkList3 = map2.values();

					for(IDimLink link : linkList3)
					{
						packetsToSend.add(( PacketHandler.onLinkCreatedPacket(link)));
					}
				}
			}
		}
		
		for (Packet250CustomPayload packet : packetsToSend)
		{
			manager.addToSendQueue(packet);
		}
	}

	public static void sendLinkCreatedPacket(IDimLink link)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream  dataOut = new DataOutputStream(bos);

		try
		{
			dataOut.writeByte(PacketHandler.registerLinkPacketID);
			dataOut.writeInt(link.locDimID);
			dataOut.writeInt(link.destDimID);
			dataOut.writeInt(link.locXCoord);
			dataOut.writeInt(link.locYCoord);
			dataOut.writeInt(link.locZCoord);
			dataOut.writeInt(link.destXCoord);
			dataOut.writeInt(link.destYCoord);
			dataOut.writeInt(link.destZCoord);
			dataOut.writeBoolean(link.isLocPocket);

			dataOut.writeInt(link.linkOrientation);
			dataOut.writeBoolean(link.hasGennedDoor);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		Packet250CustomPayload packet= new Packet250CustomPayload();
		packet.channel = "DimDoorPackets";
		packet.data = bos.toByteArray();
		packet.length = bos.size();;
		PacketDispatcher.sendPacketToAllPlayers(packet);
		return packet;
	}

	public static void sendLinkRemovedPacket(IDimLink link)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream  dataOut = new DataOutputStream(bos);

		try
		{
			dataOut.writeByte(PacketHandler.removeLinkPacketID);
			dataOut.writeInt(link.locDimID);
			dataOut.writeInt(link.destDimID);
			dataOut.writeInt(link.locXCoord);
			dataOut.writeInt(link.locYCoord);
			dataOut.writeInt(link.locZCoord);
			dataOut.writeInt(link.destXCoord);
			dataOut.writeInt(link.destYCoord);
			dataOut.writeInt(link.destZCoord);
			dataOut.writeBoolean(link.isLocPocket);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		Packet250CustomPayload packet= new Packet250CustomPayload();
		packet.channel="DimDoorPackets";
		packet.data = bos.toByteArray();
		packet.length = bos.size();;
		PacketDispatcher.sendPacketToAllPlayers(packet);
	}


	public static void sendDimCreatedPacket(NewDimData data)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(bos);

		try
		{
			dataOut.writeByte(PacketHandler.regsiterDimPacketID);
			dataOut.writeInt(data.dimID);
			dataOut.writeBoolean(data.isPocket);

			dataOut.writeInt(data.depth);
			dataOut.writeInt(data.exitDimLink.destDimID);
			dataOut.writeInt(data.exitDimLink.destXCoord);
			dataOut.writeInt(data.exitDimLink.destYCoord);
			dataOut.writeInt(data.exitDimLink.destZCoord);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		Packet250CustomPayload packet= new Packet250CustomPayload();
		packet.channel="DimDoorPackets";
		packet.data = bos.toByteArray();
		packet.length = bos.size();

		PacketDispatcher.sendPacketToAllPlayers(packet);
		return packet;
	}

	public static void sendDimObject(NewDimData dim)
	{
		try 
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream dataOut = new ObjectOutputStream(bos);
			dataOut.writeObject(dim);

			Packet250CustomPayload packet= new Packet250CustomPayload();
			packet.channel="DimDoorPackets";
			packet.data = bos.toByteArray();
			packet.length = bos.size();;
			PacketDispatcher.sendPacketToAllPlayers(packet);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}