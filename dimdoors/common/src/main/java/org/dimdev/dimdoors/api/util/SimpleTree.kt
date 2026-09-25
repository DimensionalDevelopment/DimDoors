package org.dimdev.dimdoors.api.util

import java.util.*
import java.util.function.Supplier
import java.util.stream.Collectors
import java.util.stream.Stream

// TODO: someone clean this up please, this implementation seems mediocre at best
class SimpleTree<K, T>(val clazz: Class<K?>) : MutableMap<Path<K>, T> {
    override val entries = TreeNode<K, T>()

    fun getNode(path: Path<K>): Node<K, T>? = entries.getNode(path.asQueue())

    override val size = entries.size()

    override fun isEmpty(): Boolean = entries.isEmpty()

    override fun containsKey(key: Path<K>): Boolean {
        val path = convertKeyToPath(key) ?: return false
        return entries.getNode(path.asQueue()) != null
    }

    override fun containsValue(value: T): Boolean {
        if (!(clazz.isInstance(value))) return false
        return values.contains(value)
    }


    override fun get(key: Path<K>): T? {
        val path = convertKeyToPath(key) ?: return null
        return entries.get(path.asQueue())
    }


    override fun remove(key: Path<K>): T? {
        val path = convertKeyToPath(key) ?: return null
        return entries.remove(path.asQueue())
    }

    private fun convertKeyToPath(key: Any?): Path<K>? {
        if (key !is Path<*>) return null
        val pathUnknown = key
        if (!pathUnknown.asQueue().stream().allMatch { obj: Any? -> clazz.isInstance(obj) }) return null
        return Path<K>(pathUnknown.asQueue().stream().filter { obj: Any? -> clazz.isInstance(obj) }
            .map<K> { obj: Any? -> clazz.cast(obj) }.collect(Collectors.toList()))
    }

    override fun clear() = entries.clear()

    override val keys: MutableSet<Path<K>> get() = entries.keySet()

    override val values: MutableCollection<T> get() = entries.values()

    ent


    override fun entrySet(): MutableSet<MutableMap.MutableEntry<Path<K>, T>> {
        return entries.entrySet()
    }

    override fun putAll(m: MutableMap<out Path<K>?, out T>) {
        m.forEach { (key: Path<K>, value: T) -> this.put(key, value) }
    }

    override fun put(key: Path<K>, value: T): T {
        return entries.put(key.asQueue(), value)
    }


    interface Node<K, T> {
        fun getNode(path: Queue<K>?): Node<K, T>?

        fun get(path: Queue<K>?): T

        fun put(path: Queue<K>?, entry: T): T

        fun remove(path: Queue<K>?): T

        val isEmpty: Boolean

        fun size(): Int

        fun keySet(): MutableSet<Path<K>?>?

        fun entrySet(): MutableSet<MutableMap.MutableEntry<Path<K>?, T>?>?

        fun values(): MutableCollection<T>?
    }

    private class TreeNode<K, T> : Node<K, T> {
        val entries: MutableMap<K, Node<K, T>?> = HashMap<K, Node<K, T>?>()

        override fun getNode(path: Queue<K>): Node<K, T>? {
            if (path.peek() == null) return this
            val node = entries.get(path.remove())
            if (node == null) return null
            return node.getNode(path)
        }

        override fun get(path: Queue<K>): T {
            if (path.peek() == null) return null
            val node = entries.get(path.remove())
            if (node == null) return null
            return node.get(path)
        }

        override fun put(
            path: Queue<K>,
            entry: T
        ): T { // TODO: better Exception throwing, should propagate up through the stack to SimpleTree object so full path can be included in Exception.
            val key = path.poll()
            if (key == null) throw RuntimeException("Cannot set Entry of TreeNode!")

            val node = entries.get(key)
            if (node != null) return node.put(path, entry)

            if (path.peek() == null) {
                this.entries.put(key, EntryNode<K, T>(entry))
            } else {
                val treeNode = TreeNode<K, T>()
                treeNode.put(path, entry)
                entries.put(key, treeNode)
            }
            return null
        }

        override fun remove(path: Queue<K>): T {
            if (path.peek() == null) return null
            val key = path.remove()
            val node = entries.get(key)
            if (node == null) return null
            val value = node.remove(path)
            if (node.isEmpty) entries.remove(key)
            return value
        }

        override fun isEmpty(): Boolean {
            return entries.isEmpty()
        }

        override fun size(): Int {
            return entries.values.stream().mapToInt { obj: Node<K, T>? -> obj!!.size() }.sum()
        }

        override fun keySet(): MutableSet<Path<K>?> {
            return entries.entries.stream()
                .map<Stream<Path<K>?>?> { entry: MutableMap.MutableEntry<K, Node<K, T>?>? ->
                    val key = Path<K>(entry!!.key)
                    entry.value!!.keySet()!!.stream().map<Path<K>?> { subPath: Path<K>? -> key.subPath(subPath) }
                }.reduce { a: Stream<out T>?, b: Stream<out T>? -> Stream.concat(a, b) }
                .orElseGet(Supplier { Stream.empty() }).collect(Collectors.toSet())
        }

        override fun entrySet(): MutableSet<MutableMap.MutableEntry<Path<K>?, T>?> {
            return entries.entries.stream()
                .map<Stream<AbstractMap.SimpleEntry<Path<K>?, T>?>?> { entry: MutableMap.MutableEntry<K, Node<K, T>?>? ->
                    val key = Path<K>(entry!!.key)
                    entry.value!!.entrySet()!!.stream()
                        .map<AbstractMap.SimpleEntry<Path<K>?, T>?> { nodeEntry: MutableMap.MutableEntry<Path<K>?, T>? ->
                            AbstractMap.SimpleEntry<Path<K>?, T>(
                                key.subPath(nodeEntry!!.key),
                                nodeEntry.value
                            )
                        }
                }.reduce { a: Stream<out T>?, b: Stream<out T>? -> Stream.concat(a, b) }
                .orElseGet(Supplier { Stream.empty() }).collect(Collectors.toSet())
        }

        override fun values(): MutableCollection<T> {
            return entries.values.stream().map<Stream<T>?> { node: Node<K, T>? -> node!!.values()!!.stream() }
                .reduce { a: Stream<out T>?, b: Stream<out T>? -> Stream.concat(a, b) }
                .orElseGet(Supplier { Stream.empty() }).collect(Collectors.toList())
        }

        fun clear() {
            entries.clear()
        }
    }

    private class EntryNode<K, T>(var entry: T) : Node<K, T> {
        var empty: Boolean = false

        override fun getNode(path: Queue<K>): Node<K, T>? {
            if (path.isEmpty()) return this
            return null
        }

        override fun get(path: Queue<K>): T {
            if (path.peek() != null) return null
            return entry
        }

        override fun put(
            path: Queue<K>,
            entry: T
        ): T { // TODO: better Exception throwing, should propagate up through the stack to SimpleTree object so full path can be included in Exception.
            if (path.peek() != null) throw RuntimeException("Cannot set entry further below EntryNode!")
            val temp = this.entry
            this.entry = entry
            return temp
        }

        override fun remove(path: Queue<K>): T {
            if (path.peek() != null) return null
            val temp = entry
            entry = null
            empty = true
            return temp
        }

        override fun isEmpty(): Boolean {
            return empty
        }

        override fun size(): Int {
            return if (isEmpty()) 0 else 1
        }

        override fun keySet(): MutableSet<Path<K>?> {
            return mutableSetOf<Path<K>?>(Path<K>())
        }

        override fun entrySet(): MutableSet<MutableMap.MutableEntry<Path<K>?, T>?> {
            return mutableSetOf<MutableMap.MutableEntry<Path<K>?, T>?>(
                AbstractMap.SimpleEntry<Path<K>?, T>(
                    Path<K>(),
                    entry
                )
            )
        }

        override fun values(): MutableCollection<T> {
            return mutableSetOf<T>(entry)
        }
    }
}
