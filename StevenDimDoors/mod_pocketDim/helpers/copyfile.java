package StevenDimDoors.mod_pocketDim.helpers;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import StevenDimDoors.mod_pocketDim.mod_pocketDim;

public class copyfile
{
	public static boolean copyFile(String ori, String dest)
	{
		try
		{
			InputStream in = (mod_pocketDim.class.getClass().getResourceAsStream(ori));
			OutputStream out = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close(); 
		}
		catch(Exception e)
		{
			return false;
		}
		return true;
	}
}