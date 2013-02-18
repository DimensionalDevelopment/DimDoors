
package StevenDimDoors.mod_pocketDim;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.HashMap;

public class OldSaveHandler {

  private OldSaveHandler() {}
 
  public static final void copy( File source, File destination ) throws IOException 
  {
    if( source.isDirectory() ) 
    {
      copyDirectory( source, destination );
    } else 
    {
      copyFile( source, destination );
    }
  }
 
  public static final void copyDirectory( File source, File destination ) throws IOException 
  {
    if( !source.isDirectory() ) 
    {
      throw new IllegalArgumentException( "Source (" + source.getPath() + ") must be a directory." );
    }
   
    if( !source.exists() ) 
    {
      throw new IllegalArgumentException( "Source directory (" + source.getPath() + ") doesn't exist." );
    }
   
    if( destination.exists() ) 
    {
      throw new IllegalArgumentException( "Destination (" + destination.getPath() + ") exists." );
    }
   
    destination.mkdirs();
    File[] files = source.listFiles();
   
    for( File file : files ) 
    {
      if( file.isDirectory() ) 
      {
        copyDirectory( file, new File( destination, file.getName() ) );
      } else 
      {
        copyFile( file, new File( destination, file.getName() ) );
      }
    }
  }
 
  public static void handleOldSaveData(File oldSaveData) throws IOException, ClassNotFoundException
	{
    
		
      
      try
      {
    	  

  		FileInputStream oldSaveInput = new FileInputStream(oldSaveData);
  	    ObjectSaveInputStream save = new ObjectSaveInputStream(oldSaveInput);
  	    HashMap comboSave =((HashMap)save.readObject());
  	         
  		System.out.println("FOUND OLD SAVE DATA");
    
     	HashMap oldDimList =(HashMap) comboSave.get("dimList");
     	
     	Collection<Steven.Common.mod_pocketDim.DimData> AllDims = oldDimList.values();
     	for(Steven.Common.mod_pocketDim.DimData oldDimData : AllDims)
     	{
     			DimData newDimData = new DimData(oldDimData.dimID, oldDimData.isPocket, oldDimData.depth,  oldDimData.exitDimLink.destDimID, oldDimData.exitDimLink.destXCoord , oldDimData.exitDimLink.destYCoord,oldDimData.exitDimLink.destZCoord);
     			newDimData.hasBeenFilled=oldDimData.hasBeenFilled;
     			newDimData.isDimRandomRift=oldDimData.isDimRandomRift;
     			dimHelper.dimList.put(newDimData.dimID, newDimData);
     			
     			
     			for(Steven.Common.mod_pocketDim.LinkData oldLinkData : oldDimData.getAllLinkData())
     			{
     				dimHelper.instance.createLink(oldLinkData.locDimID, oldLinkData.destDimID, oldLinkData.locXCoord, oldLinkData.locYCoord, oldLinkData.locZCoord, oldLinkData.destXCoord, oldLinkData.destYCoord, oldLinkData.destZCoord);
     				oldLinkData.printLinkData();
     			}
     		
     			
     			File oldSave= new File(oldSaveData.getParentFile().getParent()+ "/DIM"+oldDimData.dimID);
				System.out.println(oldSaveData.getParentFile().getParent()+ "/DIM"+oldDimData.dimID+" is Being Copied to "+ oldSaveData.getParentFile().getParent()+"DimensionalDoors"  );

  			if(oldSave.exists())
  			{
  				
  				try
  				{
  					OldSaveHandler.copyDirectory(  oldSave, new File( oldSaveData.getParentFile().getParent()+"/DimensionalDoors/pocketDimID"+oldDimData.dimID ));
  					
  				}
  				catch(Exception e)
  				{
  					e.printStackTrace();
  				}
  			}
     			
     			
     			
     			
     		
     		
     		}
     	save.close();
     		oldSaveInput.close();
     		dimHelper.instance.save();
     		System.out.println(oldSaveData.delete());
     	
      } 
      catch(Exception e)
      {
    	oldSaveData.renameTo(new File(oldSaveData.getAbsoluteFile()+"-restore failed"));
      	e.printStackTrace();
      	System.out.println("Could not load pocket dim list. Saves probably lost, but repairable. Move the files from indivual pocket dim files to active ones. See MC thread for details.");
      }
    
	}
  public static final void copyFile( File source, File destination ) throws IOException
  {
    FileChannel sourceChannel = new FileInputStream( source ).getChannel();
    FileChannel targetChannel = new FileOutputStream( destination ).getChannel();
    sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
    sourceChannel.close();
    targetChannel.close();
  }
} 