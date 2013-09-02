package StevenDimDoors.mod_pocketDim.util;

public class Pair<P, Q>
{
	//Pair is an implementation of a 2-tuple with generic parameters for strongly-typed elements.
	//It's used instead of Minecraft's Tuple type because Tuple doesn't have strongly-typed elements.
	
	private P first;
	private Q second;
	
	public Pair(P first, Q second)
	{
		this.first = first;
		this.second = second;
	}
	
	public P getFirst()
	{
		return first;
	}
	
	public Q getSecond()
	{
		return second;
	}
}
