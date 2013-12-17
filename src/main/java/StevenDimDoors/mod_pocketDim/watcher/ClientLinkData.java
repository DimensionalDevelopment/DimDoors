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
        
        public ClientLinkData(DimLink link)
        {
                this.point= link.source();
                this.orientation=link.orientation();
        }
        
        public ClientLinkData(Point4D point, int orientation)
        {
                this.point = point;
                this.orientation=orientation;
        }
        
        public void write(DataOutputStream output) throws IOException
        {
                Point4D.write(point, output);
                output.writeInt(orientation);
        }
        
        public static ClientLinkData read(DataInputStream input) throws IOException
        {
	        Point4D point = Point4D.read(input);
	        int orientation = input.readInt();
            return new ClientLinkData(point, orientation);
        }
        
}
