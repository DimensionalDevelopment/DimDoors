package StevenDimDoors.mod_pocketDim;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class DeathTracker
{
	private ArrayList<String> usernameList;
	private HashSet<String> usernameSet;
	private String filePath;
	private boolean modified;
	
	public DeathTracker(String filePath)
	{
		this.usernameList = new ArrayList<String>();
		this.usernameSet = new HashSet<String>();
		this.filePath = filePath;
		this.modified = false;
		
		readFromFile();
	}
	
	private void readFromFile()
	{
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			for (String line = reader.readLine(); line != null; line = reader.readLine())
			{
				line = line.trim();
				if (!line.isEmpty())
				{
					usernameSet.add(line);
				}
			}
			reader.close();
		}
		catch (FileNotFoundException e) { }
		catch (IOException e)
		{
			System.err.println("An unexpected exception occurred while trying to read DeathTracker data:");
			System.err.println(e.toString());
		}
		usernameList.addAll(usernameSet);
	}
	
	public void writeToFile()
	{
		try
		{
			PrintWriter writer = new PrintWriter(filePath);
			for (String username : usernameList)
			{
				writer.println(username);
			}
			writer.close();
			modified = false;
		}
		catch (FileNotFoundException e)
		{
			System.err.println("An unexpected exception occurred while trying to read DeathTracker data:");
			System.err.println(e.toString());
		}
	}
	
	public boolean isModified()
	{
		return modified;
	}
	
	public boolean isEmpty()
	{
		return usernameList.isEmpty();
	}

	public String getRandomUsername(Random random)
	{
		if (usernameList.isEmpty())
		{
			throw new IllegalStateException("Cannot retrieve a random username from an empty list.");
		}
		return usernameList.get(random.nextInt(usernameList.size()));
	}
	
	public boolean addUsername(String username)
	{
		if (usernameSet.add(username))
		{
			usernameList.add(username);
			modified = true;
			return true;
		}
		return false;
	}
}
