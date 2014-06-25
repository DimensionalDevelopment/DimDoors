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
	private HashMap<Integer, String> SAVE_DATA_DEFINITIONS; 
	private static final JsonParser jsonParser = new JsonParser();
	public static final String currentSaveVersionPath = "/assets/dimdoors/text/Dim_Data_Schema_v1-0-0.json";


	//TODO dont load the schemas every time
	public DimDataProcessor()
	{	
		SAVE_DATA_DEFINITIONS = new HashMap<Integer, String>();
		SAVE_DATA_DEFINITIONS.put(982405775, "/assets/dimdoors/text/Dim_Data_Schema_v982405775.json");
		SAVE_DATA_DEFINITIONS.put(PackedDimData.SAVE_DATA_VERSION_ID, currentSaveVersionPath);

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
		Gson gson = gsonBuilder.setPrettyPrinting().create();
		JsonElement ele = gson.toJsonTree(data);
		
		try 
		{
			//ensure our json object corresponds to our schema
			JSONValidator.validate(getSaveDataSchema(ele.getAsJsonObject()), ele);
			outputStream.write(ele.toString().getBytes("UTF-8"));
			outputStream.close();
		} 
		catch (Exception e) 
		{
			// not sure if this is kosher, we need it to explode, but not by throwing the IO exception. 
			throw new ConfigurationProcessingException("Could not access save data");
		}
		
	}
	
	
	public PackedDimData readDimDataJson(JsonReader reader) throws IOException
	{
		JsonElement ele = jsonParser.parse(reader);
		JsonObject schema = this.getSaveDataSchema(ele.getAsJsonObject());
		JSONValidator.validate(schema, ele);
		ele = processSaveData(schema, ele.getAsJsonObject());
		
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
		String schemaPath =  this.SAVE_DATA_DEFINITIONS.get(obj.get(JSON_VERSION_PROPERTY_NAME).getAsInt());
		
		if(schemaPath == null)
		{
			throw new IllegalStateException("Invalid save data version");
		}
		InputStream in = this.getClass().getResourceAsStream(schemaPath);
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
		}
		
		JSONValidator.validate(this.getSaveDataSchema(save), save);
		return save;
	}
}
