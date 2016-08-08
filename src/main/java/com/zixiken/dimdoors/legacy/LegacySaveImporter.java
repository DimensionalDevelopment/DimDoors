package com.zixiken.dimdoors.legacy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.zixiken.dimdoors.ObjectSaveInputStream;
import com.zixiken.dimdoors.core.DimensionType;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.saving.DDSaveHandler;
import com.zixiken.dimdoors.saving.PackedDimData;
import com.zixiken.dimdoors.saving.PackedLinkData;
import com.zixiken.dimdoors.saving.PackedLinkTail;
import com.zixiken.dimdoors.util.Point4D;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class LegacySaveImporter {
	public static void importOldSave(File file) throws IOException, ClassNotFoundException {
		FileInputStream saveFile = new FileInputStream(file);
        ObjectSaveInputStream save = new ObjectSaveInputStream(saveFile);
        HashMap comboSave =((HashMap) save.readObject());
        save.close();
        
        List<PackedLinkData> allPackedLinks = new ArrayList<PackedLinkData>();
        HashMap<Integer,PackedDimData> newPackedDimData = new   HashMap<Integer,PackedDimData>();
       
        HashMap<Integer, LegacyDimData> dimMap;
        
        try {
        	dimMap = (HashMap<Integer, LegacyDimData>) comboSave.get("dimList");
        } catch(Exception e) {
            System.out.println("Could not import old save data");
            return;
        }
        
        //build the child list
        HashMap<Integer, ArrayList<Integer>> parentChildMapping = new HashMap<Integer, ArrayList<Integer>>();
        for(LegacyDimData data : dimMap.values()) {
        	if(data.isPocket) {
	        	LegacyLinkData link = data.exitDimLink;
	        	
	        	if(parentChildMapping.containsKey(link.destDimID))
                    parentChildMapping.get(link.destDimID).add(data.dimID);
	        	else {
	        		parentChildMapping.put(link.destDimID, new ArrayList<Integer>());
	        		parentChildMapping.get(link.destDimID).add(data.dimID);
	        	}
	        	parentChildMapping.remove(data.dimID);
        	}
        }
        
        for(LegacyDimData data : dimMap.values()) {
            List<PackedLinkData> newPackedLinkData = new ArrayList<PackedLinkData>();
            List<Integer> childDims;
        	if(parentChildMapping.containsKey(data.dimID)) childDims = parentChildMapping.get(data.dimID);
            else childDims = new ArrayList<Integer>();

            for(LegacyLinkData link : data.getLinksInDim()) {
            	Point4D source = new Point4D(new BlockPos(link.locXCoord, link.locYCoord, link.locZCoord),
                        link.locDimID);
            	Point4D destintion = new Point4D(new BlockPos(link.destXCoord, link.destYCoord, link.destZCoord),
                        link.destDimID);
            	PackedLinkTail tail = new PackedLinkTail(destintion, LinkType.NORMAL);
            	List<BlockPos> children = new ArrayList<BlockPos>();

            	PackedLinkData newPackedLink = new PackedLinkData(source, new BlockPos(-1,-1,-1), tail,
                        EnumFacing.getHorizontal(link.linkOrientation), children, null);
            	
            	newPackedLinkData.add(newPackedLink);
            	allPackedLinks.add(newPackedLink);
            }
            PackedDimData dim;
            DimensionType type;
            
            if(data.isPocket) {
            	if(data.dungeonGenerator != null) type = DimensionType.DUNGEON;
            	else type = DimensionType.POCKET;
            } else type = DimensionType.ROOT;
            if(data.isPocket) dim = new PackedDimData(data.dimID, data.depth, data.depth, data.exitDimLink.locDimID,
                        data.exitDimLink.locDimID, EnumFacing.SOUTH, type, data.hasBeenFilled, null,
                        new BlockPos(0,64,0), childDims, newPackedLinkData, null);
            else dim = new PackedDimData(data.dimID, data.depth, data.depth, data.dimID, data.dimID,
                        EnumFacing.EAST.SOUTH, type, data.hasBeenFilled, null, new BlockPos(0,64,0),
                        childDims, newPackedLinkData, null);
            newPackedDimData.put(dim.ID,dim);
        }
        
        DDSaveHandler.unpackDimData(newPackedDimData);
        DDSaveHandler.unpackLinkData(allPackedLinks);
	}
}
