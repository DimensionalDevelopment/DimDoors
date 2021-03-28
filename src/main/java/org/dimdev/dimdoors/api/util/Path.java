package org.dimdev.dimdoors.api.util;

import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

public class Path<K> {
	private final ArrayList<K> path;

	@SafeVarargs
	public Path(K... path) {
		this.path = new ArrayList<>(Arrays.asList(path));
	}

	public Path(List<K> path) {
		this.path = new ArrayList<>(path);
	}

	@SafeVarargs
	public final Path<K> subPath(K... subPath) {
		ArrayList<K> arrayList = new ArrayList<>(path);
		arrayList.addAll(Arrays.asList(subPath));
		return new Path<>(arrayList);
	}

	public Path<K> subPath(Path<K> subPath) {
		ArrayList<K> arrayList = new ArrayList<>(path);
		arrayList.addAll(subPath.path);
		return new Path<>(arrayList);
	}

	public Queue<K> asQueue() {
		return new LinkedList<K>(path);
	}

	public Optional<K> reduce(BinaryOperator<K> accumulator) {
		return path.stream().reduce(accumulator);
	}

	public K reduce(K identity, BinaryOperator<K> accumulator) {
		return path.stream().reduce(identity, accumulator);
	}

	public <T> T reduce(T identity, BiFunction<T, ? super K, T> accumulator, BinaryOperator<T> combiner) {
		return path.stream().reduce(identity, accumulator, combiner);
	}

	public static Path<String> stringPath(String str) {
		return new Path<>(str.split("(?<=[/:])"));
	}

	public static Path<String> stringPath(Identifier id) {
		return stringPath(id.toString());
	}

	@Override
	public String toString() {
		return "Path{" +
				"path=" + reduce("", (left, right) -> left + ";" + right.toString(), (left, right) -> left + ";" + right) +
				'}';
	}
}
