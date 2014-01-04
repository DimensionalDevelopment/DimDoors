package StevenDimDoors.experimental;

/**
 * Provides a complete implementation of a directed graph.
 * @author SenseiKiwi
 *
 * @param <U> The type of data to store in the graph's nodes
 * @param <V> The type of data to store in the graph's edges
 */
public class DirectedGraph<U, V>
{
	private static class GraphNode<P, Q> implements IGraphNode<P, Q>
	{
		private LinkedList<Edge<P, Q>> inbound;
		private LinkedList<Edge<P, Q>> outbound;
		private ILinkedListNode<GraphNode<P, Q>> graphEntry;
		private P data;
		
		public GraphNode(P data, LinkedList<GraphNode<P, Q>> graphList)
		{
			this.data = data;
			this.inbound = new LinkedList<Edge<P, Q>>();
			this.outbound = new LinkedList<Edge<P, Q>>();
			this.graphEntry = graphList.addLast(this);
		}
		
		public int indegree()
		{
			return inbound.size();
		}
		
		public int outdegree()
		{
			return outbound.size();
		}
		
		public Iterable<Edge<P, Q>> inbound()
		{
			return inbound;
		}
		
		public Iterable<Edge<P, Q>> outbound()
		{
			return outbound;
		}
		
		public P data()
		{
			return data;
		}
		
		public void remove()
		{
			graphEntry.remove();
			graphEntry = null;
			
			for (Edge<P, Q> edge : inbound)
				edge.remove();

			for (Edge<P, Q> edge : outbound)
				edge.remove();
			
			inbound = null;
			outbound = null;
			data = null;
		}
	}
	
	private static class Edge<P, Q> implements IEdge<P, Q>
	{
		private GraphNode<P, Q> head;
		private GraphNode<P, Q> tail;
		private ILinkedListNode<Edge<P, Q>> headEntry;
		private ILinkedListNode<Edge<P, Q>> tailEntry;
		private ILinkedListNode<Edge<P, Q>> graphEntry;
		private Q data;
		
		public Edge(GraphNode<P, Q> head, GraphNode<P, Q> tail, Q data, LinkedList<Edge<P, Q>> graphList)
		{
			this.head = head;
			this.tail = tail;
			this.data = data;
			this.graphEntry = graphList.addLast(this);
			this.headEntry = head.outbound.addLast(this);
			this.tailEntry = tail.inbound.addLast(this);
		}
		
		public IGraphNode<P, Q> head()
		{
			return head;
		}
		
		public IGraphNode<P, Q> tail()
		{
			return tail;
		}
		
		public Q data()
		{
			return data;
		}
		
		public void remove()
		{
			headEntry.remove();
			tailEntry.remove();
			graphEntry.remove();
			headEntry = null;
			tailEntry = null;
			graphEntry = null;
			head = null;
			tail = null;
			data = null;
		}
	}
	
	private LinkedList<GraphNode<U, V>> nodes;
	private LinkedList<Edge<U, V>> edges;
	
	public DirectedGraph()
	{
		nodes = new LinkedList<GraphNode<U, V>>();
		edges = new LinkedList<Edge<U, V>>();
	}
	
	public int nodeCount()
	{
		return nodes.size();
	}
	
	public int edgeCount()
	{
		return edges.size();
	}
	
	public boolean isEmpty()
	{
		return nodes.isEmpty();
	}
	
	public Iterable<? extends IGraphNode<U, V>> nodes()
	{
		return nodes;
	}
	
	public Iterable<? extends IEdge<U, V>> edges()
	{
		return edges;
	}
	
	private GraphNode<U, V> checkNode(IGraphNode<U, V> node)
	{
		GraphNode<U, V> innerNode = (GraphNode<U, V>) node;

		// Check that this node actually belongs to this graph instance.
		// Accepting foreign nodes could corrupt the graph's internal state.
		if (innerNode.graphEntry.owner() != nodes)
		{
			throw new IllegalArgumentException("The specified node does not belong to this graph.");
		}
		return innerNode;
	}
	
	private Edge<U, V> checkEdge(IEdge<U, V> edge)
	{
		Edge<U, V> innerEdge = (Edge<U, V>) edge;

		// Check that this node actually belongs to this graph instance.
		// Accepting foreign nodes could corrupt the graph's internal state.
		if (innerEdge.graphEntry.owner() != edges)
		{
			throw new IllegalArgumentException("The specified edge does not belong to this graph.");
		}
		return innerEdge;
	}
	
	public IGraphNode<U, V> addNode(U data)
	{
		return new GraphNode<U, V>(data, nodes);
	}
	
	public IEdge<U, V> addEdge(IGraphNode<U, V> head, IGraphNode<U, V> tail, V data)
	{
		GraphNode<U, V> innerHead = checkNode(head);
		GraphNode<U, V> innerTail = checkNode(tail);
		return new Edge<U, V>(innerHead, innerTail, data, edges);
	}
	
	public U removeNode(IGraphNode<U, V> node)
	{
		GraphNode<U, V> innerNode = checkNode(node);
		U data = innerNode.data();
		innerNode.remove();
		return data;
	}
	
	public V removeEdge(IEdge<U, V> edge)
	{
		Edge<U, V> innerEdge = checkEdge(edge);
		V data = innerEdge.data();
		innerEdge.remove();
		return data;
	}
	
	public IEdge<U, V> findEdge(IGraphNode<U, V> head, IGraphNode<U, V> tail)
	{
		for (IEdge<U, V> edge : head.outbound())
		{
			if (edge.tail() == tail)
				return edge;
		}
		return null;
	}
	
	public void clear()
	{
		// Remove each node individually to guarantee that all external
		// references are invalidated. That'll prevent memory leaks and
		// keep external code from using removed nodes or edges.
		for (GraphNode<U, V> node : nodes)
		{
			node.remove();
		}
	}
}
