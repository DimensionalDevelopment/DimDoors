package StevenDimDoors.mod_pocketDim.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class BaseConfigurationProcessor<T>
{
	public BaseConfigurationProcessor() { }
	
	public boolean canRead()
	{
		return true;
	}
	
	public boolean canWrite()
	{
		return true;
	}
	
	public T readFromFile(String path) throws ConfigurationProcessingException, IOException
	{
		return readFromFile(new File(path));
	}
	
	public T readFromFile(File file) throws ConfigurationProcessingException, IOException
	{
		FileInputStream t = new FileInputStream(file);
		T ret = readFromStream(t);
		t.close();
		return ret;
	}
	
	public T readFromResource(String resourcePath) throws ConfigurationProcessingException
	{
		return readFromStream(this.getClass().getResourceAsStream(resourcePath));
	}
	
	public abstract T readFromStream(InputStream inputStream) throws ConfigurationProcessingException;
	
	public void writeToFile(File file, T data) throws ConfigurationProcessingException, IOException
	{
		FileOutputStream t = new FileOutputStream(file);
		writeToStream(t, data);
		t.close();
	}
	
	public void writeToFile(String path, T data) throws ConfigurationProcessingException, IOException
	{
		writeToFile(new File(path), data);
	}
	
	public abstract void writeToStream(OutputStream outputStream, T data) throws ConfigurationProcessingException;
}
