package StevenDimDoors.mod_pocketDim.saving;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import StevenDimDoors.mod_pocketDim.util.BaseConfigurationProcessor;
import StevenDimDoors.mod_pocketDim.util.ConfigurationProcessingException;

public class PersonalPocketMappingProcessor extends BaseConfigurationProcessor<HashMap<String, Integer>>
{

	@Override
	public HashMap<String, Integer> readFromStream(InputStream inputStream) throws ConfigurationProcessingException
	{
		try
		{
			JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
			HashMap<String, Integer> data = this.createPersonalPocketsMapFromJson(reader);
			reader.close();
			return data;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ConfigurationProcessingException("Could not read personal pocket mapping");
		}
	}

	private HashMap<String, Integer> createPersonalPocketsMapFromJson(JsonReader reader) throws IOException
	{
		HashMap<String, Integer> ppMap;
		ppMap = this.createMapFromJson(reader);		
		return ppMap;
	}

	private HashMap<String, Integer> createMapFromJson(JsonReader reader) throws IOException
	{
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		
		reader.beginObject();
		while(reader.peek()!= JsonToken.END_OBJECT)
		{
			map.put(reader.nextName(), reader.nextInt());
		}
		reader.endObject();
		
		return map;
	}

	@Override
	public void writeToStream(OutputStream outputStream, HashMap<String, Integer> data) throws ConfigurationProcessingException
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
			// not sure if this is kosher, we need it to explode, but not by throwing the IO exception. 
			throw new ConfigurationProcessingException("Incorrectly formatted save data");
		}
		
	}

}
