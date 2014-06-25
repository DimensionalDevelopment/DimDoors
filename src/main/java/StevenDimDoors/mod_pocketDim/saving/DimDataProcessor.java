package StevenDimDoors.mod_pocketDim.saving;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import StevenDimDoors.mod_pocketDim.core.DimensionType;
import StevenDimDoors.mod_pocketDim.util.BaseConfigurationProcessor;
import StevenDimDoors.mod_pocketDim.util.ConfigurationProcessingException;
import StevenDimDoors.mod_pocketDim.util.JSONValidator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class DimDataProcessor extends BaseConfigurationProcessor<PackedDimData>
{
	public final String JSON_VERSION_PROPERTY_NAME = "SAVE_DATA_VERSION_ID_INSTANCE";
	private HashMap<Integer, JsonObject> SAVE_DATA_SCHEMA; 
	private static final JsonParser jsonParser = new JsonParser();
	
	public static final String BASE_SCHEMA_PATH = "/assets/dimdoors/text/";
	
	
	//TODO dont load the schemas every time
	public DimDataProcessor()
	{	
		SAVE_DATA_SCHEMA = new HashMap<Integer, JsonObject>();
		
		//Load the old schema/s
		SAVE_DATA_SCHEMA.put(982405775, loadSchema(BASE_SCHEMA_PATH+"Dim_Data_Schema_v982405775.json"));
		
		//load the current schema
		SAVE_DATA_SCHEMA.put(PackedDimData.SAVE_DATA_VERSION_ID, loadSchema(BASE_SCHEMA_PATH+"Dim_Data_Schema_v1-0-0.json"));

	}
	@Override
	public PackedDimData readFromStream(InputStream inputStream)
		throws ConfigurationProcessingException
	{	
		try
		{
			//read in the json save file represeting a single dimension
			JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
			PackedDimData data = this.readDimDataJson(reader);
			reader.close();
			return data;
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new ConfigurationProcessingException("Could not read packedDimData");
		}
	
	}

	@Override
	public void writeToStream(OutputStream outputStream, PackedDimData data)
		throws ConfigurationProcessingException
	{
		//create a json object from a packedDimData instance
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setPrettyPrinting();
		Gson gson = gsonBuilder.create();
		JsonElement ele = gson.toJsonTree(data);
	
		try 
		{
			//ensure our json object corresponds to our schema
			JSONValidator.validate(getSaveDataSchema(ele.getAsJsonObject()), ele);
			outputStream.write(gson.toJson(ele).getBytes("UTF-8"));
			outputStream.close();
		} 
		catch (Exception e) 
		{
			// not sure if this is kosher, we need it to explode, but not by throwing the IO exception. 
			throw new ConfigurationProcessingException("Could not access save data");
		}
		
	}
	
	/**
	 * validates the save file against it's current version, then updates and validates it again if it needs it
	 * then it loads it
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	public PackedDimData readDimDataJson(JsonReader reader) throws IOException
	{
		//read the save file into a Json element
		JsonElement ele = jsonParser.parse(reader);
		
		//get the schema that corresponds to the save file's listed version number
		JsonObject schema = this.getSaveDataSchema(ele.getAsJsonObject());
		
		//validate the save file against its schema
		JSONValidator.validate(schema, ele);
		
		//handle updating old save data
		ele = processSaveData(schema, ele.getAsJsonObject());
		
		//convert the updated and verified json into an instance of PackedDimData
		GsonBuilder gsonBuilder = new GsonBuilder();
		return gsonBuilder.create().fromJson(ele, PackedDimData.class);
	}
	
	/**
	 * Gets the schema that corresponds to a version of our save data
	 * @param obj
	 * @return
	 * @throws IOException 
	 */
	public JsonObject getSaveDataSchema(JsonObject obj)
	{
		JsonObject schema =  this.SAVE_DATA_SCHEMA.get(obj.get(JSON_VERSION_PROPERTY_NAME).getAsInt());
		
		if(schema == null)
		{
			throw new IllegalStateException("Invalid save data version");
		}
		
		return schema;
	}
	
	/**
	 * Internally load the save data schema so we dont load them every single time we validate save data
	 * @param path
	 * @return
	 */
	private JsonObject loadSchema(String path)
	{
		InputStream in = this.getClass().getResourceAsStream(path);
		JsonReader reader = new JsonReader(new InputStreamReader(in));

		JsonObject schema = jsonParser.parse(reader).getAsJsonObject();
		try
		{
			reader.close();
			in.close();
		}
		catch (IOException e)
		{
			System.err.println("Could not load Json Save Data definitions");
			e.printStackTrace();
			throw new IllegalStateException("Could not load Json Save Data definitions");
		}
		
		return schema;
	}
	
	/**
	 * I use this method to update old save data files to the new format before actually loading them. 
	 * @return
	 */
	public JsonObject processSaveData(JsonObject schema, JsonObject save)
	{
		if(save.get(JSON_VERSION_PROPERTY_NAME).getAsInt()== 982405775)
		{
			DimensionType type;
			
			//see if the dim is a pocket
			if(save.get("RootID").getAsInt() != save.get("ID").getAsInt())
			{
				if(save.get("IsDungeon").getAsBoolean())
				{
					type = DimensionType.DUNGEON;
				}
				else
				{
					type = DimensionType.POCKET;
				}
			}
			else
			{
				type = DimensionType.ROOT;
			}
			
			save.remove("IsDungeon");
			save.addProperty("DimensionType",type.index);
			save.remove(this.JSON_VERSION_PROPERTY_NAME);
			save.addProperty(this.JSON_VERSION_PROPERTY_NAME, PackedDimData.SAVE_DATA_VERSION_ID);
			return processSaveData(this.getSaveDataSchema(save), save);
		}
		
		JSONValidator.validate(this.getSaveDataSchema(save), save);
		return save;
	}
}
