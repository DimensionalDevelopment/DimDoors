package StevenDimDoors.mod_pocketDim.saving;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import scala.Char;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.util.BaseConfigurationProcessor;
import StevenDimDoors.mod_pocketDim.util.ConfigurationProcessingException;
import StevenDimDoors.mod_pocketDim.util.Point4D;

public class DimDataProcessor extends BaseConfigurationProcessor<PackedDimData>
{
	private static final String dimID = "DIM_ID";
	private static final String depth = "DEPTH";
	private static final String children = "CHILDREN_DIM_IDS";
	private static final String linkTails = "LINK_TAILS";
	private static final String filled = "IS_FILLED";
	private static final String isDungeon = "IS_DUNGEON";
	private static final String orientation = "ORIENTATION";
	private static final String parentID = "PARENT_DIM_ID";
	private static final String rootID = "ROOT_DIM_ID";
	private static final String packDepth = "PACK_DEPTH";
	private static final String links = "LINKS";
	private static final String origin = "ORIGIN_POINT";


	@Override
	public PackedDimData readFromStream(InputStream inputStream)
		throws ConfigurationProcessingException
	{		
		return null;
	}

	@Override
	public void writeToStream(OutputStream outputStream, PackedDimData data)
		throws ConfigurationProcessingException
	{
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.setPrettyPrinting().create();
		
		try 
		{
			outputStream.write(gson.toJson(data).getBytes("UTF-8"));
			outputStream.close();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			throw new ConfigurationProcessingException();
		}
		// TODO Auto-generated method stub
		
	}
}
