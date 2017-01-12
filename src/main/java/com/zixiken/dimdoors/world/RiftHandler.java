package com.zixiken.dimdoors.world;

import com.zixiken.dimdoors.tileentities.DDTileEntityBase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.tileentity.TileEntity;

/**
 *
 * @author Robijnvogel
 */
public class RiftHandler {
    private Map<Integer, Location> riftLocations; //it needs to be a Map if we want to be able to remove rifts without the IDs shifting (as would happen in a List
    private int nextRiftID;
    
    RiftHandler() { //@todo don't forget to create one object of this class at world-load
        riftLocations = new HashMap<>();
        nextRiftID = 0;
        readfromNBT(); //and also add a method to write to NBT
    }
    
    public int addRift(Location location) {
        int riftID = nextRiftID;
        nextRiftID++;        
        riftLocations.put(riftID, location);
        return riftID;
    }
    
    public void removeRift(int riftID, Location location) {
        riftLocations.remove(riftID);
    }
    
    public Location getLocation(int riftID) {
        Location location = riftLocations.get(riftID);
        return location;
    }
    
    public void pair(int riftID, int riftID2) {
        Location location = riftLocations.get(riftID);
        TileEntity tileEntity = location.getTileEntity(); //@todo this method might need to be in another class?
        if (tileEntity != null && tileEntity instanceof DDTileEntityBase) {
            DDTileEntityBase rift = (DDTileEntityBase) tileEntity;
            rift.pair(riftID2);
        }
    }
    
    public void unpair(int riftID) {
        Location location = riftLocations.get(riftID);
        TileEntity tileEntity = location.getTileEntity();
        if (tileEntity != null && tileEntity instanceof DDTileEntityBase) {
            DDTileEntityBase rift = (DDTileEntityBase) tileEntity;
            rift.unpair();
        }
    }
}
