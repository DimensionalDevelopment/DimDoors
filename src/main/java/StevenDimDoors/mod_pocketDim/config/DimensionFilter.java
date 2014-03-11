package StevenDimDoors.mod_pocketDim.config;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

public class DimensionFilter
{
	private RangeSet<Integer> blacklist;
	
	private DimensionFilter(RangeSet<Integer> blacklist)
	{
		this.blacklist = blacklist;
	}
	
	public boolean isAccepted(int dimensionID)
	{
		return !blacklist.contains(dimensionID);
	}
	
	public boolean isRejected(int dimensionID)
	{
		return blacklist.contains(dimensionID);
	}
	
	private static RangeSet<Integer> parseRangeSet(String list)
	{
		int index;
		int start;
		int end;
		String startPart;
		String endPart;
		String[] intervals;
		RangeSet<Integer> ranges = TreeRangeSet.create();
		
		// Strip out all whitespace characters
		list = list.replaceAll("\\s", "");
		if (list.isEmpty())
		{
			return ranges;
		}
		intervals = list.split(",");

		// Iterate over all the interval strings
		for (String interval : intervals)
		{
			// Check if the interval contains a minus sign after the first character
			// That indicates that we're dealing with an interval and not a single number
			if (interval.length() > 1)
			{
				index = interval.indexOf("-", 1);
			}
			else
			{
				index = -1;
			}
			try
			{
				if (index >= 0)
				{
					// Parse this as a range with two values as endpoints
					startPart = interval.substring(0, index);
					endPart = interval.substring(index + 1);
					start = Integer.parseInt(startPart);
					end = Integer.parseInt(endPart);
				}
				else
				{
					// Parse this as a single value
					start = Integer.parseInt(interval);
					end = start;
				}
				// Add the interval to the set of intervals
				ranges.add( Range.closed(start, end) );
			}
			catch (Exception e)
			{
				throw new IllegalArgumentException("\"" + interval + "\" is not a valid value or range for dimension IDs");
			}
		}
		return ranges;
	}
	
	public static DimensionFilter parseWhitelist(String list)
	{
		return new DimensionFilter(parseRangeSet(list).complement());
	}
	
	public static DimensionFilter parseBlacklist(String list)
	{
		return new DimensionFilter(parseRangeSet(list));
	}
}
