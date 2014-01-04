package StevenDimDoors.experimental;

public interface IGraphNode<U, V>
{
	public Iterable<? extends IEdge<U, V>> inbound();
	public Iterable<? extends IEdge<U, V>> outbound();
	public int indegree();
	public int outdegree();
	public U data();
}
