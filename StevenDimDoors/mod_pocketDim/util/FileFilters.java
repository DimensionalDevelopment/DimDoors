package StevenDimDoors.mod_pocketDim.util;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public class FileFilters
{
	private FileFilters() { }
	
	public static class DirectoryFilter implements FileFilter
	{
		@Override
		public boolean accept(File file)
		{
			return file.isDirectory();
		}
	}
	
	public static class FileExtensionFilter implements FileFilter
	{
		private final String extension;
		
		public FileExtensionFilter(String extension)
		{
			this.extension = extension;
		}
		
		@Override
		public boolean accept(File file)
		{
			return file.isFile() && file.getName().endsWith(extension);
		}
	}
	
	public static class RegexFileFilter implements FileFilter
	{
		private final Pattern pattern;
		
		public RegexFileFilter(String expression)
		{
			this.pattern = Pattern.compile(expression);
		}
		
		@Override
		public boolean accept(File file)
		{
			return file.isFile() && pattern.matcher(file.getName()).matches();
		}
	}
}
