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
	//The name of the version ID where it is stored in the JSON
	public final String JSON_VERSION_PROPERTY_NAME = "SAVE_DATA_VERSION_ID_INSTANCE";
	
	//mapping of version IDs to their corresponding schema. Prevents reloading of schema during save/load cycles
	private HashMap<Integer, JsonObject> SAVE_DATA_SCHEMA; 
	
	//The parser used to read in the JSON Files
	private static final JsonParser jsonParser = new JsonParser();
	
	//The directory for JSON schema files
	public static final String BASE_SCHEMA_PATH = "/assets/dimdoors/text/";
	
	/**
	 * Need to manually include a schema defintion for every save file version currently supported
	 */
	public DimDataProcessor()
	{	
		SAVE_DATA_SCHEMA = new HashMap<Integer, JsonObject>();
		
		//Load the old schema/s
		SAVE_DATA_SCHEMA.put(982405775, loadSchema(BASE_SCHEMA_PATH+"Dim_Data_Schema_v982405775.json"));
		
		//load the schema representing the current save data format
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
		int incomingSaveVersionID = save.get(JSON_VERSION_PROPERTY_NAME).getAsInt();
		
		// Handle save data versions that are current
		if(incomingSaveVersionID == PackedDimData.SAVE_DATA_VERSION_ID)
		{
			JSONValidator.validate(this.getSaveDataSchema(save), save);
			return save;
		}
		
		// Handle save data versions that are older, starting with the random one. 
		// We have to 
		if(incomingSaveVersionID== 982405775)
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
			
			//Need to hardcode the version number here, so if we change the current version then this still updates to the proper version
			save.addProperty(this.JSON_VERSION_PROPERTY_NAME, 100);
		}
		
		return processSaveData(this.getSaveDataSchema(save), save);
	}
}
