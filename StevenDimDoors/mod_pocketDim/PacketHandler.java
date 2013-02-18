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

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler 
{
	public static int regsiterDimPacketID = 3;
	public static int registerLinkPacketID = 4;
	public static int removeLinkPacketID = 5;
	public static int linkKeyPacketID = 7;
	public static int dimPacketID = 6;


	public static int dimUpdatePacketID = 1;

	

        @Override
        public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) 
        {
               
                if (packet.channel.equals("GenericRandom")) 
                { 
                	handleRandom(packet,player);      
                      
                }
                
        }
       
        private void handleRandom(Packet250CustomPayload packet, Player player) 
        {
                ByteArrayDataInput data =  ByteStreams.newDataInput(packet.data);
              
                int id=data.readByte();
              
                
                
              
                
                if(id==regsiterDimPacketID)
                {
                
                
                
                		int dimId = data.readInt();
                	//	System.out.println("regsitered dim ID" + dimId);
                		try
                		{
                			DimData dimDataToAdd = new DimData(dimId, data.readBoolean(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt());

                			if(!dimHelper.dimList.containsKey(dimId))
                			{
                			dimHelper.dimList.put(dimId, dimDataToAdd);
                			}
                			if(dimDataToAdd.isPocket)
                			{
                			dimHelper.registerDimension(dimId, mod_pocketDim.providerID);
                			//System.out.println("regsitered dim ID" + dimId);
                			}

                		}
                		catch (Exception e)
                		{
                		//	e.printStackTrace();
                			if(dimId!=0)
                			{
                			//	System.out.println(String.valueOf(dimId)+"dimID already registered");
                			}
                		}
                	
                
                }
                
                if(id==registerLinkPacketID)
                {
                
                
                
                	
                		int dimId = data.readInt();
                		try
                		{
                			DimData dimDataToAddLink= dimHelper.dimList.get(dimId);

                			LinkData linkToAdd = new LinkData(dimId, data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readBoolean());
                			linkToAdd.hasGennedDoor=data.readBoolean();
                			linkToAdd.linkOrientation=data.readInt();
                			
                			dimHelper.instance.createLink(linkToAdd);
                			
                		}
                		catch (Exception e)
                		{
                			e.printStackTrace();
                			System.out.println("Tried to update client link data & failed!");
                		}
                	
                
                }
                if(id==removeLinkPacketID)
                {
                
                
                
                	
                		int dimId = data.readInt();
                		try
                		{
                			DimData dimDataToRemoveFrom= dimHelper.dimList.get(dimId);

                			LinkData linkToAdd = new LinkData(dimId, data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readBoolean());
                			dimDataToRemoveFrom.removeLinkAtCoords(linkToAdd.locDimID, linkToAdd.locXCoord,linkToAdd.locYCoord, linkToAdd.locZCoord);
                			
                			dimHelper.instance.linksForRendering.remove(linkToAdd);
                		}
                		catch (Exception e)
                		{
                			//e.printStackTrace();
                			System.out.println("Tried to update client link data & failed!");
                		}
                	
                
                }
                if(id==this.linkKeyPacketID)
                {
                	LinkData link = new LinkData(data.readInt(), data.readInt(), data.readInt(), data.readInt());
                	dimHelper.instance.interDimLinkList.put(data.readInt(), link);
                }
               
              
               
        }
        
		public static void onClientJoinPacket(INetworkManager manager, HashMap<Integer, DimData> dimList)
		{
			
			Collection<Integer> dimIDs= dimList.keySet();
			Collection<DimData> dimDataSet= dimList.values();
			Collection<Packet250CustomPayload> packetsToSend = new HashSet();
			

			
			for(DimData data : dimDataSet)
			{
				
				manager.addToSendQueue(PacketHandler.onDimCreatedPacket(data));
				 	
				 	Collection <HashMap<Integer, HashMap<Integer,  LinkData>>> linkList = data.linksInThisDim.values();
				 	
				 	for(HashMap map :  linkList )
				 	{
				 		
				 		Collection <HashMap<Integer,  LinkData>> linkList2 = map.values();
				 		
				 		for(HashMap map2 : linkList2)
				 		{
					 		Collection <LinkData> linkList3 = map2.values();
					 		
					 		for(LinkData link : linkList3)
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
	
		
		public static Packet250CustomPayload onLinkCreatedPacket(LinkData link)
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
			    	dataOut.writeBoolean(link.hasGennedDoor);
			    	dataOut.writeInt(link.linkOrientation);

			    	
			    	
			    }
			    
			    
			    
			    catch (IOException e) 
			    {
			        e.printStackTrace();
			    }
			 
			 	Packet250CustomPayload packet= new Packet250CustomPayload();
			 	packet.channel="GenericRandom";
			 	packet.data = bos.toByteArray();
			 	packet.length = bos.size();;
			 	PacketDispatcher.sendPacketToAllPlayers(packet);
			 	return packet;
		}
		
		
		public static Packet250CustomPayload linkKeyPacket(LinkData link, int key)
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream  dataOut = new DataOutputStream(bos);
			 
			 try
			    {
				 	
				 	dataOut.writeByte(PacketHandler.linkKeyPacketID);
			    	
			    	dataOut.writeInt(link.destDimID);
			    	dataOut.writeInt(link.destXCoord);
			    	dataOut.writeInt(link.destYCoord);
			    	dataOut.writeInt(link.destZCoord);
			    	dataOut.writeInt(key);

			    	
			    	
			    }
			    
			    
			    
			    catch (IOException e) 
			    {
			        e.printStackTrace();
			    }
			 
			 	Packet250CustomPayload packet= new Packet250CustomPayload();
			 	packet.channel="GenericRandom";
			 	packet.data = bos.toByteArray();
			 	packet.length = bos.size();;
			 	PacketDispatcher.sendPacketToAllPlayers(packet);
			 	return packet;
		}
		
		
		public static void onLinkRemovedPacket(LinkData link)
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
			 	packet.channel="GenericRandom";
			 	packet.data = bos.toByteArray();
			 	packet.length = bos.size();;
			 	PacketDispatcher.sendPacketToAllPlayers(packet);
		}
		
		
		public static Packet250CustomPayload onDimCreatedPacket(DimData data)
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
			 	packet.channel="GenericRandom";
			 	packet.data = bos.toByteArray();
			 	packet.length = bos.size();
			 	
			 	PacketDispatcher.sendPacketToAllPlayers(packet);
			 	return packet;
         
		
		}
		/**
		 private void handleObjectPacket(Packet250CustomPayload packet, Player player) 
	        {
	                ObjectInputStream data =  new ObjectInputStream;
	                int length = data.readInt();
	                int id=data.readByte();
	                System.out.println(id);
	                if(id==dimPacketID)
	                {
	                
	                
	                
	                	
	                		
	                		try
	                		{
	                			DimData dimData = data.read
	                			
	                			dimHelper.dimList.put(key, value)
	                			
	                		}
	                		catch (Exception e)
	                		{
	                			e.printStackTrace();
	                		}
	                	
	                
	                }
	        }
		**/
		public static void sendDimObject(DimData dim)
		{
			 try 
			 {
				 ByteArrayOutputStream bos = new ByteArrayOutputStream();
				 ObjectOutputStream dataOut = new ObjectOutputStream(bos);
				 dataOut.writeObject(dim);
				 
					Packet250CustomPayload packet= new Packet250CustomPayload();
				 	packet.channel="GenericRandom";
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