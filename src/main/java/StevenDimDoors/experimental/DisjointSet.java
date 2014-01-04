package StevenDimDoors.experimental;

import java.util.HashMap;

public class DisjointSet<T>
{
	// This class implements a disjoint set data structure that associates objects with sets.
	
	private static class SetNode<P>
	{
		private int rank;
		private SetNode<P> parent;
		private P data;
		
		public SetNode(P data)
		{
			this.data = data;
			this.rank = 0;
			this.parent = null;
		}
	}
	
	private HashMap<T, SetNode<T>> mapping;
	
	public DisjointSet(int initialCapacity)
	{
		mapping = new HashMap<T, SetNode<T>>(initialCapacity);
	}
	
	public boolean makeSet(T element)
	{
		if (!mapping.containsKey(element))
		{
			mapping.put(element, new SetNode<T>(element));
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private SetNode<T> findRootNode(T element)
	{
		SetNode<T> node = mapping.get(element);
		if (node == null)
		{
			return null;
		}
		if (node.parent != null)
		{
			node.parent = findRootNode(node.parent);
			return node.parent;
		}
		else
		{
			return node;
		}
	}
	
	private SetNode<T> findRootNode(SetNode<T> node)
	{
		if (node.parent != null)
		{
			node.parent = findRootNode(node.parent);
			return node.parent;
		}
		else
		{
			return node;
		}
	}
	
	public boolean mergeSets(T first, T second)
	{
		SetNode<T> firstRoot = findRootNode(first);
		SetNode<T> secondRoot = findRootNode(second);
		
		if (firstRoot == null || secondRoot == null ||
				firstRoot == secondRoot)
		{
			return false;
		}
		
		if (firstRoot.rank < secondRoot.rank)
		{
			firstRoot.parent = secondRoot;
		}
		else if (firstRoot.rank > secondRoot.rank)
		{
			secondRoot.parent = firstRoot;
		}
		else
		{
			secondRoot.parent = firstRoot;
			firstRoot.rank++;
		}
		return true;
	}
	
	public T find(T element)
	{
		SetNode<T> root = findRootNode(element);
		
		if (root != null)
		{
			return root.data;
		}
		else
		{
			return null;
		}
	}
	
	public boolean haveSameSet(T first, T second)
	{
		SetNode<T> firstRoot = findRootNode(first);
		SetNode<T> secondRoot = findRootNode(second);
		
		if (firstRoot == null || secondRoot == null)
		{
			return false;
		}
		else
		{
			return (firstRoot == secondRoot);
		}
	}
	
	public void clear()
	{
		mapping.clear();
	}
}
