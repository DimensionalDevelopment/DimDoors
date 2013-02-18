package Steven.Common.mod_pocketDim;

import java.awt.List;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class DimData
  implements Serializable
{
  public int dimID;
  public boolean isPocket;
  public int depth;
  public World world;
  public LinkData exitDimLink;
  public boolean hasBeenFilled = false;
  public boolean hasDoor = false;
  public int dimOrientation;
  public boolean isDimRandomRift = false;
  public HashMap linksInThisDim = new HashMap();
  HashMap dimX;
  HashMap dimY;

  public DimData(int dimID, boolean isPocket, int depth, LinkData exitLinkData)
  {
    this.dimID = dimID;
    this.depth = depth;
    this.isPocket = isPocket;

    this.exitDimLink = exitLinkData;
  }

  public DimData(int dimID, boolean isPocket, int depth, int exitLinkDimID, int exitX, int exitY, int exitZ)
  {
    this.dimID = dimID;
    this.depth = depth;
    this.isPocket = isPocket;

    this.exitDimLink = new LinkData(exitLinkDimID, exitX, exitY, exitZ);
  }

  public LinkData findNearestRift(World world, int range, int x, int y, int z)
  {
    LinkData nearest = null;
    float distance = range + 1;
    int i = -range;
    int j = -range;
    int k = -range;

    while (i < range)
    {
      while (j < range)
      {
        while (k < range)
        {
          if ((world.getBlockId(x + i, y + j, z + k) == mod_pocketDim.blockRiftID) && (MathHelper.abs(i) + MathHelper.abs(j) + MathHelper.abs(k) < distance))
          {
            if (MathHelper.abs(i) + MathHelper.abs(j) + MathHelper.abs(k) != 0.0F)
            {
              nearest = findLinkAtCoords(x + i, y + j, z + k);
              distance = MathHelper.abs(i) + MathHelper.abs(j) + MathHelper.abs(k);
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

  public LinkData addLinkToDim(int destinationDimID, int locationXCoord, int locationYCoord, int locationZCoord, int destinationXCoord, int destinationYCoord, int destinationZCoord)
  {
    if (this.linksInThisDim.containsKey(Integer.valueOf(locationZCoord)))
    {
      this.dimY = ((HashMap)this.linksInThisDim.get(Integer.valueOf(locationZCoord)));

      if (this.dimY.containsKey(Integer.valueOf(locationYCoord)))
      {
        this.dimX = ((HashMap)this.dimY.get(Integer.valueOf(locationYCoord)));
      }
      else
      {
        this.dimX = new HashMap();
      }
    }
    else
    {
      this.dimX = new HashMap();
      this.dimY = new HashMap();
    }

    LinkData linkData = new LinkData(this.dimID, destinationDimID, locationXCoord, locationYCoord, locationZCoord, destinationXCoord, destinationYCoord, destinationZCoord, this.isPocket);

    this.dimX.put(Integer.valueOf(locationXCoord), linkData);
    this.dimY.put(Integer.valueOf(locationYCoord), this.dimX);
    this.linksInThisDim.put(Integer.valueOf(locationZCoord), this.dimY);

    return linkData;
  }

  public void removeLinkAtCoords(int locationID, int locationXCoord, int locationYCoord, int locationZCoord)
  {
    if (this.linksInThisDim.containsKey(Integer.valueOf(locationZCoord)))
    {
      this.dimY = ((HashMap)this.linksInThisDim.get(Integer.valueOf(locationZCoord)));

      if (this.dimY.containsKey(Integer.valueOf(locationYCoord)))
      {
        this.dimX = ((HashMap)this.dimY.get(Integer.valueOf(locationYCoord)));
      }
      else
      {
        this.dimX = new HashMap();
      }
    }
    else
    {
      this.dimX = new HashMap();
      this.dimY = new HashMap();
    }

    this.dimX.remove(Integer.valueOf(locationXCoord));
    this.dimY.put(Integer.valueOf(locationYCoord), this.dimX);
    this.linksInThisDim.put(Integer.valueOf(locationZCoord), this.dimY);
  }

  public LinkData findLinkAtCoords(int locationXCoord, int locationYCoord, int locationZCoord)
  {
    try
    {
      if (this.linksInThisDim.containsKey(Integer.valueOf(locationZCoord)))
      {
        this.dimY = ((HashMap)this.linksInThisDim.get(Integer.valueOf(locationZCoord)));

        if (this.dimY.containsKey(Integer.valueOf(locationYCoord)))
        {
          this.dimX = ((HashMap)this.dimY.get(Integer.valueOf(locationYCoord)));

          if (this.dimX.containsKey(Integer.valueOf(locationXCoord)))
          {
            return (LinkData)this.dimX.get(Integer.valueOf(locationXCoord));
          }
        }
      }

    }
    catch (Exception E)
    {
      return null;
    }

    return null;
  }

  public Collection<LinkData> getAllLinkData()
  {
    Iterator itr = this.linksInThisDim.keySet().iterator();
    Collection<LinkData> linksInDim = (Collection<LinkData>) new HashSet<LinkData>();

    while (itr.hasNext())
    {
      HashMap first = (HashMap)this.linksInThisDim.get((Integer)itr.next());

      Iterator itrfirst = first.keySet().iterator();

      while (itrfirst.hasNext())
      {
        HashMap second = (HashMap)first.get((Integer)itrfirst.next());

        Iterator itrsecond = second.keySet().iterator();
        while (itrsecond.hasNext())
        {
          LinkData link = (LinkData)second.get((Integer)itrsecond.next());

          linksInDim.add(link);
        }
      }
    }
    return linksInDim;
  }
 
}