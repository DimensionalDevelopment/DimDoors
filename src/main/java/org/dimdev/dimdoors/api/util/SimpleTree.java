package org.dimdev.dimdoors.api.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: someone clean this up please, this implementation seems mediocre at best
public class SimpleTree<K, T> implements Map<Path<K>, T> {
	final TreeNode<K, T> entries = new TreeNode<>();
	final Class<K> clazz;

	public SimpleTree(Class<K> clazz) {
		this.clazz = clazz;
	}

	public Node<K, T> getNode(Path<K> path) {
		return entries.getNode(path.asQueue());
	}

	@Override
	public int size() {
		return entries.size();
	}

	@Override
	public boolean isEmpty() {
		return entries.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		Path<K> path = convertKeyToPath(key);
		if (path == null) return false;
		return entries.getNode(path.asQueue()) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		if (!(clazz.isInstance(value))) return false;
		return values().contains(value);
	}

	@Override
	public T get(Object key) {
		Path<K> path = convertKeyToPath(key);
		if (path == null) return null;
		return entries.get(path.asQueue());
	}

	@Override
	public T remove(Object key) {
		Path<K> path = convertKeyToPath(key);
		if (path == null) return null;
		return entries.remove(path.asQueue());
	}

	private Path<K> convertKeyToPath(Object key) {
		if (!(key instanceof Path)) return null;
		Path<?> pathUnknown = (Path<?>) key;
		if (!pathUnknown.asQueue().stream().allMatch(clazz::isInstance)) return null;
		return new Path<>(pathUnknown.asQueue().stream().filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toList()));
	}

	@Override
	public void clear() {
		entries.clear();
	}

	@NotNull
	@Override
	public Set<Path<K>> keySet() {
		return entries.keySet();
	}

	@NotNull
	@Override
	public Collection<T> values() {
		return entries.values();
	}

	@NotNull
	@Override
	public Set<Entry<Path<K>, T>> entrySet() {
		return entries.entrySet();
	}

	@Override
	public void putAll(@NotNull Map<? extends Path<K>, ? extends T> m) {
		m.forEach(this::put);
	}

	@Nullable
	@Override
	public T put(Path<K> key, T value) {
		return entries.put(key.asQueue(), value);
	}


	public interface Node<K, T> {
		Node<K, T> getNode(Queue<K> path);

		T get(Queue<K> path);

		T put(Queue<K> path, T entry);

		T remove(Queue<K> path);

		boolean isEmpty();

		int size();

		Set<Path<K>> keySet();

		Set<Entry<Path<K>, T>> entrySet();

		Collection<T> values();
	}

	private static class TreeNode<K, T> implements Node<K, T> {
		final Map<K, Node<K, T>> entries = new HashMap<>();

		public Node<K, T> getNode(Queue<K> path) {
			if (path.peek() == null) return this;
			Node<K, T> node = entries.get(path.remove());
			if (node == null) return null;
			return node.getNode(path);
		}

		@Override
		public T get(Queue<K> path) {
			if (path.peek() == null) return null;
			Node<K, T> node = entries.get(path.remove());
			if (node == null) return null;
			return node.get(path);
		}

		@Override
		public T put(Queue<K> path, T entry) { // TODO: better Exception throwing, should propagate up through the stack to SimpleTree object so full path can be included in Exception.
			K key = path.poll();
			if (key == null) throw new RuntimeException("Cannot set Entry of TreeNode!");

			Node<K, T> node = entries.get(key);
			if (node != null) return node.put(path, entry);

			if (path.peek() == null) {
				this.entries.put(key, new EntryNode<>(entry));
			} else {
				TreeNode<K, T> treeNode = new TreeNode<>();
				treeNode.put(path, entry);
				entries.put(key, treeNode);
			}
			return null;
		}

		@Override
		public T remove(Queue<K> path) {
			if (path.peek() == null) return null;
			K key = path.remove();
			Node<K, T> node = entries.get(key);
			if (node == null) return null;
			T value = node.remove(path);
			if (node.isEmpty()) entries.remove(key);
			return value;
		}

		@Override
		public boolean isEmpty() {
			return entries.isEmpty();
		}

		@Override
		public int size() {
			return entries.values().stream().mapToInt(Node::size).sum();
		}

		@Override
		public Set<Path<K>> keySet() {
			return entries.entrySet().stream().map(entry -> {
				Path<K> key = new Path<>(entry.getKey());
				return entry.getValue().keySet().stream().map(key::subPath);
			}).reduce(Stream::concat).orElseGet(Stream::empty).collect(Collectors.toSet());
		}

		@Override
		public Set<Entry<Path<K>, T>> entrySet() {
			return entries.entrySet().stream().map(entry -> {
				Path<K> key = new Path<>(entry.getKey());
				return entry.getValue().entrySet().stream().map(nodeEntry -> new AbstractMap.SimpleEntry<>(key.subPath(nodeEntry.getKey()), nodeEntry.getValue()));
			}).reduce(Stream::concat).orElseGet(Stream::empty).collect(Collectors.toSet());
		}

		@Override
		public Collection<T> values() {
			return entries.values().stream().map(node -> node.values().stream()).reduce(Stream::concat).orElseGet(Stream::empty).collect(Collectors.toList());
		}

		public void clear() {
			entries.clear();
		}
	}

	private static class EntryNode<K, T> implements Node<K, T> {
		T entry;
		boolean empty = false;

		public EntryNode(T entry) {
			this.entry = entry;
		}

		@Override
		public Node<K, T> getNode(Queue<K> path) {
			if (path.isEmpty()) return this;
			return null;
		}

		@Override
		public T get(Queue<K> path) {
			if (path.peek() != null) return null;
			return entry;
		}

		@Override
		public T put(Queue<K> path, T entry) { // TODO: better Exception throwing, should propagate up through the stack to SimpleTree object so full path can be included in Exception.
			if (path.peek() != null) throw new RuntimeException("Cannot set entry further below EntryNode!");
			T temp = this.entry;
			this.entry = entry;
			return temp;
		}

		@Override
		public T remove(Queue<K> path) {
			if (path.peek() != null) return null;
			T temp = entry;
			entry = null;
			empty = true;
			return temp;
		}

		@Override
		public boolean isEmpty() {
			return empty;
		}

		@Override
		public int size() {
			return isEmpty() ? 0 : 1;
		}

		@Override
		public Set<Path<K>> keySet() {
			return Collections.singleton(new Path<>());
		}

		@Override
		public Set<Entry<Path<K>, T>> entrySet() {
			return Collections.singleton(new AbstractMap.SimpleEntry<>(new Path<>(), entry));
		}

		@Override
		public Collection<T> values() {
			return Collections.singleton(entry);
		}
	}
}
