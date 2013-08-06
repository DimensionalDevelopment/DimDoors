package StevenDimDoors.mod_pocketDim.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
	
	public T readFromFile(String path) throws FileNotFoundException, ConfigurationProcessingException
	{
		return readFromFile(new File(path));
	}
	
	public T readFromFile(File file) throws FileNotFoundException, ConfigurationProcessingException
	{
		return readFromStream(new FileInputStream(file));
	}
	
	public T readFromResource(String resourcePath) throws ConfigurationProcessingException
	{
		return readFromStream(this.getClass().getResourceAsStream(resourcePath));
	}
	
	public abstract T readFromStream(InputStream inputStream) throws ConfigurationProcessingException;
	
	public void writeToFile(File file, T data) throws FileNotFoundException, ConfigurationProcessingException
	{
		writeToStream(new FileOutputStream(file), data);
	}
	
	public void writeToFile(String path, T data) throws FileNotFoundException, ConfigurationProcessingException
	{
		writeToFile(new File(path), data);
	}
	
	public abstract void writeToStream(OutputStream outputStream, T data) throws ConfigurationProcessingException;
}
