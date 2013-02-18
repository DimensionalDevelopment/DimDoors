

// This is my package declaration, do not mess with the standard (package net.minecraft.src;) like I did,
// Because I know what Im doing in this part, If you don't know what your doing keep it the normal (package net.minecraft.src;)
package StevenDimDoors.mod_pocketDimClient;

// Theses are all the imports you need
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

// Create a class and implement IPacketHandler
// This just handles the data packets in the server
public class ClientPacketHandler implements IPacketHandler{




@Override
public void onPacketData(INetworkManager manager,
		Packet250CustomPayload packet, Player player) {
	// TODO Auto-generated method stub
	
}
}
