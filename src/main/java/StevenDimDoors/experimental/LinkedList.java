package StevenDimDoors.experimental;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Provides an implementation of a linked list that exposes its internal nodes.
 * This differs from Java's implementation, which does not expose nodes. Access
 * to the nodes allows certain operations to be implemented more efficiently.
 * Not all operations are supported, but we can add them as the need arises.
 * @author SenseiKiwi
 *
 * @param <T> The type of data to be stored in the LinkedList
 */
public class LinkedList<T> implements Iterable<T>
{
	private static class Node<P> implements ILinkedListNode<P>
	{
		private Node<P> next;
		private Node<P> prev;
		private P data;
		private LinkedList<P> owner;
		
		public Node(Node<P> prev, Node<P> next, P data, LinkedList<P> owner)
		{
			this.prev = prev;
			this.next = next;
			this.data = data;
			this.owner = owner;
		}

		@Override
		public ILinkedListNode<P> next()
		{
			return next;
		}

		@Override
		public ILinkedListNode<P> prev()
		{
			return prev;
		}

		@Override
		public P data()
		{
			return data;
		}

		@Override
		public void setData(P data)
		{
			if (this == owner.header || this == owner.trailer)
			{
				throw new IllegalStateException("Cannot set data for the header and trailer nodes of a list.");
			}
			
			this.data = data;
		}

		@Override
		public LinkedList<P> owner()
		{
			return owner;
		}

		@Override
		public P remove()
		{
			if (this == owner.header || this == owner.trailer)
			{
				throw new IllegalStateException("Cannot remove the header and trailer nodes of a list.");
			}
			
			P data = this.data;
			this.prev.next = this.next;
			this.next.prev = this.prev;
			this.owner.size--;

			this.clear();
			return data;
		}
		
		public void clear()
		{
			this.data = null;
			this.prev = null;
			this.next = null;
			this.owner = null;
		}
	}
	
	private static class LinkedListIterator<P> implements Iterator<P>
	{
		private Node<P> current;
		private Node<P> trailer;
		
		public LinkedListIterator(LinkedList<P> list)
		{
			current = list.header.next;
			trailer = list.trailer;
		}

		@Override
		public boolean hasNext()
		{
			return (current != trailer);
		}

		@Override
		public P next()
		{
			if (current == trailer)
			{
				throw new NoSuchElementException();
			}
			else
			{
				P result = current.data;
				current = current.next;
				return result;
			}
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}
	
	private Node<T> header; // Sentinel node
	private Node<T> trailer; // Sentinel node
	private int size;
	
	public LinkedList()
	{
		size = 0;
		header = new Node<T>(null, null, null, this);
		trailer = new Node<T>(null, null, null, this);
		header.next = trailer;
		trailer.prev = header;
	}
	
	public ILinkedListNode<T> header()
	{
		return header;
	}
	
	public ILinkedListNode<T> trailer()
	{
		return trailer;
	}
	
	public int size()
	{
		return size;
	}
	
	public boolean isEmpty()
	{
		return (size == 0);
	}
	
	public void clear()
	{
		// Go through the list and wipe everything out
		Node<T> current;
		Node<T> next;
		
		size = 0;
		current = header.next;
		while (current != trailer)
		{
			next = current.next;
			current.clear();
			current = next;
		}
		header.next = trailer;
		trailer.prev = header;
	}
	
	private Node<T> checkNode(ILinkedListNode<T> node)
	{
		Node<T> innerNode = (Node<T>) node;

		// Check that this node actually belongs to this list instance.
		// Accepting foreign nodes could corrupt the list's internal state.
		if (innerNode.owner() != this)
		{
			throw new IllegalArgumentException("The specified node does not belong to this list.");
		}
		return innerNode;
	}
	
	public ILinkedListNode<T> addFirst(T data)
	{
		return addAfter(header, data);
	}
	
	public ILinkedListNode<T> addLast(T data)
	{
		return addBefore(trailer, data);
	}
	
	public ILinkedListNode<T> addBefore(ILinkedListNode<T> node, T data)
	{
		if (node == header)
		{
			throw new IllegalArgumentException("Cannot add a node before the header node.");
		}
		return addAfter( checkNode(node).prev, data );
	}
	
	public ILinkedListNode<T> addAfter(ILinkedListNode<T> node, T data)
	{
		if (node == trailer)
		{
			throw new IllegalArgumentException("Cannot add a node after the trailer node.");
		}
		return addAfter( checkNode(node), data );
	}
	
	private Node<T> addAfter(Node<T> node, T data)
	{
		Node<T> addition = new Node(node, node.next, data, this);
		node.next = addition;
		addition.next.prev = addition;
		size++;
		return addition;
	}
	
	public Iterator<T> iterator()
	{
		return new LinkedListIterator<T>(this);
	}
}
