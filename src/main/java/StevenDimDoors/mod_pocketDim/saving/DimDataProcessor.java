package StevenDimDoors.mod_pocketDim.saving;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.core.DDLock;
import StevenDimDoors.mod_pocketDim.util.BaseConfigurationProcessor;
import StevenDimDoors.mod_pocketDim.util.ConfigurationProcessingException;
import StevenDimDoors.mod_pocketDim.util.Point4D;

public class DimDataProcessor extends BaseConfigurationProcessor<PackedDimData>
{
	@Override
	public PackedDimData readFromStream(InputStream inputStream)
		throws ConfigurationProcessingException
	{	
		try
		{
			JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
			PackedDimData data = this.createDImDataFromJson(reader);
			reader.close();
			return data;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ConfigurationProcessingException("Could not read packedDimData");
		}
	
	}

	@Override
	public void writeToStream(OutputStream outputStream, PackedDimData data)
		throws ConfigurationProcessingException
	{
		/** Print out dimData using the GSON built in serializer. I dont feel bad doing this because
		 *  1- We can read it
		 *  2- We are manually reading the data in.
		 *  3- The error messages tell us exactly where its failing, so its easy to fix
		 */
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.setPrettyPrinting().create();
		
		try 
		{
			outputStream.write(gson.toJson(data).getBytes("UTF-8"));
			outputStream.close();
		} 
		catch (IOException e) 
		{
			// not sure if this is kosher, we need it to explode, but not by throwing the IO exception. 
			throw new ConfigurationProcessingException("Incorrectly formatted save data");
		}		
	}
	/**
	 * Nightmare method that takes a JsonReader pointed at a serialized instance of PackedDimData
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	public PackedDimData createDImDataFromJson(JsonReader reader) throws IOException 
	{
		int ID;
		boolean IsDungeon;
		boolean IsFilled;
		int Depth;
		int PackDepth;
		int ParentID;
		int RootID;
		PackedDungeonData Dungeon = null;
		Point3D Origin;
		int Orientation;
		List<Integer> ChildIDs;
		List<PackedLinkData> Links;
		List<PackedLinkTail> Tails = new ArrayList<PackedLinkTail>();
		
		reader.beginObject();
		
		reader.nextName();
		if (reader.nextLong() != PackedDimData.SAVE_DATA_VERSION_ID)
		{
			throw new IOException("Save data version mismatch");
		}
		
		reader.nextName();
		ID = reader.nextInt();
		
		reader.nextName();
		IsDungeon = reader.nextBoolean();
		
		reader.nextName();
		IsFilled = reader.nextBoolean();
		
		reader.nextName();
		Depth = reader.nextInt();
		
		reader.nextName();
		PackDepth = reader.nextInt();
		
		reader.nextName();
		ParentID=reader.nextInt();
		
		reader.nextName();
		RootID= reader.nextInt();
		
		if(reader.nextName().equals("DungeonData"))
		{
			Dungeon = createDungeonDataFromJson(reader);
			reader.nextName();
		}
		
		Origin = createPointFromJson(reader);
		
		reader.nextName();
		Orientation = reader.nextInt();
		
		reader.nextName();
		ChildIDs = this.createIntListFromJson(reader);
		
		reader.nextName();
		Links = this.createLinksListFromJson(reader);
		
		return new PackedDimData(ID, Depth, PackDepth, ParentID, RootID, Orientation, IsDungeon, IsFilled, Dungeon, Origin, ChildIDs, Links, Tails);
	}
	
	private Point3D createPointFromJson(JsonReader reader) throws IOException
	{
		reader.beginObject();
		
		reader.nextName();
		int x = reader.nextInt();
		
		reader.nextName();
		int y = reader.nextInt();
		
		reader.nextName();
		int z = reader.nextInt();
		
		reader.endObject();
		
		return new Point3D(x,y,z);
	}
	
	private Point4D createPoint4DFromJson(JsonReader reader) throws IOException
	{
		reader.beginObject();
		
		reader.nextName();
		int x = reader.nextInt();
		
		reader.nextName();
		int y = reader.nextInt();
		
		reader.nextName();
		int z = reader.nextInt();
		
		reader.nextName();
		int dimension = reader.nextInt();
		
		reader.endObject();
		
		return new Point4D(x,y,z,dimension);
	}
	
	private List<Integer> createIntListFromJson(JsonReader reader) throws IOException
	{
		List<Integer> list = new ArrayList<Integer>();
		reader.beginArray();
		
		while (reader.peek() != JsonToken.END_ARRAY)
		{
			list.add(reader.nextInt());
			
		}
		reader.endArray();
		return list;
	}
	
	private List<PackedLinkData> createLinksListFromJson(JsonReader reader) throws IOException
	{
		List<PackedLinkData> list = new ArrayList<PackedLinkData>();
		
		reader.beginArray();
		
		while (reader.peek() != JsonToken.END_ARRAY)
		{
			list.add(createLinkDataFromJson(reader));
		}
		reader.endArray();
		return list;
	}
	
	private PackedLinkData createLinkDataFromJson(JsonReader reader) throws IOException
	{
		DDLock lock = null;

		Point4D source;
		Point3D parent;
		PackedLinkTail tail;
		int orientation;
		List<Point3D> children = new ArrayList<Point3D>();
		
		reader.beginObject();
		
		reader.nextName();
		source = this.createPoint4DFromJson(reader);
		
		reader.nextName();
		parent = this.createPointFromJson(reader);
		
		reader.nextName();
		tail = this.createLinkTailFromJson(reader);
		
		reader.nextName();
		orientation = reader.nextInt();
		
		reader.nextName();
		reader.beginArray();
		
		while (reader.peek() != JsonToken.END_ARRAY)
		{
			children.add(this.createPointFromJson(reader));
		}
		reader.endArray();
		
		if(reader.peek()== JsonToken.NAME)
		{
			lock = this.createLockFromJson(reader);
		}
		reader.endObject();
		
		return new PackedLinkData(source, parent, tail, orientation, children, lock);
	}
	private PackedDungeonData createDungeonDataFromJson(JsonReader reader) throws IOException
	{
		int Weight;
		boolean IsOpen;
		boolean IsInternal;
		String SchematicPath;
		String SchematicName;
		String DungeonTypeName;
		String DungeonPackName;
		
		reader.beginObject();
		@SuppressWarnings("unused")
		JsonToken test = reader.peek();
		
		if(reader.peek() == JsonToken.END_OBJECT)
		{
			return null;
		}
		
		reader.nextName();
		Weight=reader.nextInt();
		
		reader.nextName();
		IsOpen=reader.nextBoolean();
		
		reader.nextName();
		IsInternal=reader.nextBoolean();
		
		reader.nextName();
		SchematicPath=reader.nextString();
		
		reader.nextName();
		SchematicName=reader.nextString();
		
		reader.nextName();
		DungeonTypeName=reader.nextString();
		
		reader.nextName();
		DungeonPackName=reader.nextString();
		
		reader.endObject();
		return new PackedDungeonData(Weight, IsOpen, IsInternal, SchematicPath, SchematicName, DungeonTypeName, DungeonPackName);
	}
	private PackedLinkTail createLinkTailFromJson(JsonReader reader) throws IOException
	{
		Point4D destination = null;
		int linkType;
		reader.beginObject();
		reader.nextName();
		
		@SuppressWarnings("unused")
		JsonToken test = reader.peek();
		if (reader.peek() == JsonToken.BEGIN_OBJECT)
		{
			destination = this.createPoint4DFromJson(reader);
			reader.nextName();
		}
		
		linkType = reader.nextInt();
		
		reader.endObject();
		
		return new PackedLinkTail(destination, linkType);
	}
	
	private DDLock createLockFromJson(JsonReader reader) throws IOException
	{
		reader.nextName();

		reader.beginObject();
		reader.nextName();

		boolean locked = reader.nextBoolean();
		reader.nextName();

		int key = reader.nextInt();
		reader.endObject();

		return new DDLock(locked, key);
	}
	
}
