package StevenDimDoors.mod_pocketDim.saving;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.util.BaseConfigurationProcessor;
import StevenDimDoors.mod_pocketDim.util.ConfigurationProcessingException;
import StevenDimDoors.mod_pocketDim.util.JSONValidator;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class DimDataProcessor extends BaseConfigurationProcessor<PackedDimData>
{
	private static final String JSON_SCHEMA_PATH = "/assets/dimdoors/text/Dim_Data_Schema.json";
	private static final JsonParser jsonParser = new JsonParser();

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
			validateJson(ele);
			outputStream.write(data.toString().getBytes("UTF-8"));
			outputStream.close();
		} 
		catch (Exception e) 
		{
			// not sure if this is kosher, we need it to explode, but not by throwing the IO exception. 
			throw new ConfigurationProcessingException("Incorrectly formatted save data");
		}		
	}
	
	public PackedDimData readDimDataJson(JsonReader reader) throws IOException
	{
		JsonElement ele = jsonParser.parse(reader);
		this.validateJson(ele);
		GsonBuilder gsonBuilder = new GsonBuilder();
		return gsonBuilder.create().fromJson(ele, PackedDimData.class);
	}
	
	/**
	 * checks our json against the dim data schema
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public boolean validateJson(JsonElement data) throws IOException
	{
		InputStream in = this.getClass().getResourceAsStream(JSON_SCHEMA_PATH);
		JsonReader reader = new JsonReader(new InputStreamReader(in));
		JSONValidator.validate((JsonObject) jsonParser.parse(reader), data);
		reader.close();
		in.close();
		return true;
	}
}
