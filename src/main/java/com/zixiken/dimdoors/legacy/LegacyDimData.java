package com.zixiken.dimdoors.legacy;
/**Class that contains all the information about a specific dim that is pertienent to Dim Doors. Holds all the rifts present in the dim sorted by x,y,z and
* wether or not the dim is a pocket or not, along with its depth.
* @Return
*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.config.DDProperties;

import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
@Deprecated
public class LegacyDimData implements Serializable {
        public int dimID;
        public int depth;
        public int dimOrientation;

        public World world;

        public LegacyLinkData exitDimLink;

        public boolean isPocket;
        public boolean hasBeenFilled = false;
        public boolean hasDoor = false;
        public boolean isDimRandomRift = false;
        public Object dungeonGenerator = null;
        //public boolean isPrivatePocket = false;
        public HashMap<Integer, HashMap<Integer, HashMap<Integer, LegacyLinkData>>> linksInThisDim =
                new HashMap<Integer, HashMap<Integer, HashMap<Integer, LegacyLinkData>>>();
        HashMap<Integer, LegacyLinkData> dimX;
        HashMap<Integer, HashMap<Integer, LegacyLinkData>> dimY ;

        static final long serialVersionUID = 454342L;

        public LegacyDimData(int dimID, boolean isPocket, int depth, LegacyLinkData exitLinkData) {
            this.dimID = dimID;
            this.depth = depth;
            this.isPocket = isPocket;
            this.exitDimLink = exitLinkData;
        }

        public LegacyDimData(int dimID, boolean isPocket, int depth, int exitLinkDimID, int exitX, int exitY, int exitZ) {
                this(dimID, isPocket, depth, new LegacyLinkData(exitLinkDimID, exitX, exitY, exitZ));
        }

        public LegacyLinkData findNearestRift(World world, int range, int x, int y, int z) {
            LegacyLinkData nearest = null;
            float distance = range+1;
            int i = -range;
            int j = -range;
            int k = -range;

            while (i < range) {
                while (j < range) {
                    while (k < range) {
                        if (world.getBlockState(new BlockPos(x+i, y+j, z+k)).getBlock() == DimDoors.blockRift &&
                                MathHelper.abs(i)+MathHelper.abs(j)+MathHelper.abs(k) < distance) {
                            if(MathHelper.abs(i)+MathHelper.abs(j)+MathHelper.abs(k) != 0) {
                                nearest = this.findLinkAtCoords(x+i, y+j, z+k);
                                distance = MathHelper.abs(i)+MathHelper.abs(j)+MathHelper.abs(k);
                            }
                        }
                        k++;
                    }
                    k = -range;
                    j++;
                }
                j = -range;
                i++;
            }

            return nearest;
        }

        public ArrayList findRiftsInRange(World world, int range, int x, int y, int z) {
            LegacyLinkData nearest;
            ArrayList<LegacyLinkData> rifts = new ArrayList<LegacyLinkData>();
            int i = -range;
            int j = -range;
            int k = -range;

            while (i < range) {
                while (j < range) {
                    while (k < range) {
                        if(world.getBlockState(new BlockPos(x+i, y+j, z+k)).getBlock() == DimDoors.blockRift) {
                            if(MathHelper.abs(i)+MathHelper.abs(j)+MathHelper.abs(k) != 0) {
                                nearest = this.findLinkAtCoords(x+i, y+j, z+k);
                                if(nearest != null) rifts.add(nearest);
                            }
                        }
                        k++;
                    }
                    k = -range;
                    j++;
                }
                j = -range;
                i++;
            }

            return rifts;
        }



        public LegacyLinkData addLinkToDim(LegacyLinkData link) {
            if(this.linksInThisDim.containsKey(link.locZCoord)) {
                this.dimY=this.linksInThisDim.get(link.locZCoord);

                if(this.dimY.containsKey(link.locYCoord)) this.dimX=this.dimY.get(link.locYCoord);
                else this.dimX=new HashMap<Integer, LegacyLinkData>();
            } else {
                this.dimX=new HashMap<Integer, LegacyLinkData>();
                this.dimY=new HashMap<Integer, HashMap<Integer, LegacyLinkData>>();
            }

            this.dimX.put(link.locXCoord, link);
            this.dimY.put(link.locYCoord, dimX);
            this.linksInThisDim.put(link.locZCoord, dimY);

            //System.out.println("added link to dim "+this.dimID);
            return link;
        }

        public LegacyLinkData addLinkToDim(int destinationDimID, int locationXCoord, int locationYCoord, int locationZCoord,
                                           int destinationXCoord, int destinationYCoord, int destinationZCoord, int linkOrientation) {
            LegacyLinkData linkData = new LegacyLinkData(this.dimID, destinationDimID, locationXCoord, locationYCoord,
                    locationZCoord, destinationXCoord, destinationYCoord,
                    destinationZCoord, this.isPocket, linkOrientation);

            return this.addLinkToDim(linkData);
        }

        public boolean isLimbo() {return (this.dimID == DDProperties.instance().LimboDimensionID);}

        public void removeLinkAtCoords(LegacyLinkData link) {
            this.removeLinkAtCoords(link.locDimID, link.locXCoord, link.locYCoord, link.locZCoord);
        }

        public void removeLinkAtCoords(int locationID, int locationXCoord, int locationYCoord, int locationZCoord) {
            if (this.linksInThisDim.containsKey(locationZCoord)) {
                this.dimY=this.linksInThisDim.get(locationZCoord);
                if(this.dimY.containsKey(locationYCoord)) this.dimX=this.dimY.get(locationYCoord);
                else this.dimX=new HashMap<Integer, LegacyLinkData>();
            } else {
                this.dimX=new HashMap<Integer, LegacyLinkData>();
                this.dimY=new HashMap<Integer, HashMap<Integer, LegacyLinkData>>();
            }

            this.dimX.remove(locationXCoord);
            this.dimY.put(locationYCoord, dimX);
            this.linksInThisDim.put(locationZCoord, dimY);
        }

        public LegacyLinkData findLinkAtCoords(int locationXCoord, int locationYCoord, int locationZCoord) {
            try {
                if(this.linksInThisDim.containsKey(locationZCoord)) {
                    this.dimY=this.linksInThisDim.get(locationZCoord);
                    if(this.dimY.containsKey(locationYCoord)) {
                        this.dimX=this.dimY.get(locationYCoord);
                        if(this.dimX.containsKey(locationXCoord)) return this.dimX.get(locationXCoord);
                    }
                }
            } catch(Exception E) {return null;}
            return null;
        }

        public ArrayList<LegacyLinkData> getLinksInDim() {
            //TODO: We might want to modify this function, but I'm afraid of breaking something right now.
            //To begin with, the name is wrong. This doesn't print anything! >_o ~SenseiKiwi

            ArrayList<LegacyLinkData> links = new ArrayList<LegacyLinkData>();
            if (this.linksInThisDim == null) return links;
            for (HashMap<Integer, HashMap<Integer, LegacyLinkData>> first : this.linksInThisDim.values())
                for (HashMap<Integer, LegacyLinkData> second : first.values())
                    for (LegacyLinkData linkData : second.values()) links.add(linkData);
            return links;
        }
}