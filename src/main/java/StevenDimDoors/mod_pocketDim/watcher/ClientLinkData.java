package StevenDimDoors.mod_pocketDim.watcher;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.util.Point4D;

public class ClientLinkData
{
        public Point4D point;
        public int orientation;
        public boolean isLocked;
        
        public ClientLinkData(DimLink link)
        {
                this.point= link.source();
                this.orientation=link.orientation();
                this.isLocked = link.isLocked();
        }
        
        public ClientLinkData(Point4D point, int orientation, boolean isLocked)
        {
                this.point = point;
                this.orientation=orientation;
                this.isLocked = isLocked;
        }
        
        public void write(DataOutputStream output) throws IOException
        {
                Point4D.write(point, output);
                output.writeInt(orientation);
                output.writeBoolean(isLocked);
        }
        
        public static ClientLinkData read(DataInputStream input) throws IOException
        {
	        Point4D point = Point4D.read(input);
	        int orientation = input.readInt();
	        boolean isLocked = input.readBoolean();
            return new ClientLinkData(point, orientation, isLocked);
        }
        
}
