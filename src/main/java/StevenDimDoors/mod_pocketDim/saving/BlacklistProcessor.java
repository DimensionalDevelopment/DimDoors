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

import StevenDimDoors.mod_pocketDim.util.BaseConfigurationProcessor;
import StevenDimDoors.mod_pocketDim.util.ConfigurationProcessingException;

public class BlacklistProcessor extends BaseConfigurationProcessor<List<Integer>>
{

	@Override
	public List<Integer> readFromStream(InputStream inputStream) throws ConfigurationProcessingException
	{
		try
		{
			JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
			List<Integer> data = this.createBlacklistFromJson(reader);
			reader.close();
			return data;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ConfigurationProcessingException("Could not read blacklist");
		}
	}

	private List<Integer> createBlacklistFromJson(JsonReader reader) throws IOException
	{
		List<Integer> blacklist;
		blacklist = this.createIntListFromJson(reader);		
		return blacklist;
	}

	@Override
	public void writeToStream(OutputStream outputStream, List<Integer> data) throws ConfigurationProcessingException
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
	
	private List<Integer> createIntListFromJson(JsonReader reader) throws IOException
	{
		List<Integer> list = new ArrayList<Integer>();
		reader.beginArray();
		
		while(reader.peek()!= JsonToken.END_ARRAY)
		{
			list.add(reader.nextInt());
		}
		reader.endArray();
		return list;
	}

}
