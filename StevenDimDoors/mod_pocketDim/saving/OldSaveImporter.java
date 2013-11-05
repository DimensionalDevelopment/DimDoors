package StevenDimDoors.mod_pocketDim.saving;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.Point3D;

import StevenDimDoors.mod_pocketDim.ObjectSaveInputStream;
import StevenDimDoors.mod_pocketDim.util.Point4D;

public class OldSaveImporter
{
	public static void importOldSave(File file) throws IOException, ClassNotFoundException
	{
		FileInputStream saveFile = new FileInputStream(file);
        ObjectSaveInputStream save = new ObjectSaveInputStream(saveFile);
        HashMap comboSave =((HashMap) save.readObject());
        save.close();
        
        List<PackedLinkData> allPackedLinks = new ArrayList<PackedLinkData>();
        List<PackedDimData> newPackedDimData = new ArrayList<PackedDimData>();
       
        HashMap<Integer, DimData> dimMap;
        
        try
        {
        	dimMap = (HashMap<Integer, DimData>) comboSave.get("dimList");
        }
        catch(Exception e)
        {
                 System.out.println("Could not import old save data");
                 return;
        }
        
        for(DimData data : dimMap.values())
        {
            List<PackedLinkData> newPackedLinkData = new ArrayList<PackedLinkData>();
        	List<Integer> childDims = new ArrayList<Integer>();

            for(LinkData link : data.getLinksInDim())
            {
            	Point4D source = new Point4D(link.locXCoord,link.locYCoord,link.locZCoord,link.locDimID);
            	Point4D destintion = new Point4D(link.destXCoord,link.destYCoord,link.destZCoord,link.destDimID);
            	PackedLinkTail tail = new PackedLinkTail(destintion, link.linkOrientation);
            	List<Point3D> children = new ArrayList<Point3D>();

            	PackedLinkData newPackedLink = new PackedLinkData(source, new Point3D(-1,-1,-1), tail, link.linkOrientation,children);
            	
            	newPackedLinkData.add(newPackedLink);
            	allPackedLinks.add(newPackedLink);

            }
            
            PackedDimData dim = new PackedDimData(data.dimID, data.depth, data.depth, data.exitDimLink.locDimID, data.exitDimLink.locDimID, 0, data.dungeonGenerator!=null, data.hasBeenFilled, null, new Point3D(0,64,0), childDims, newPackedLinkData, null);
            newPackedDimData.add(dim);
            
            DDSaveHandler.unpackDimData(newPackedDimData);
            DDSaveHandler.unpackLinkData(allPackedLinks);

            
        }
       
    
	}
	
}
