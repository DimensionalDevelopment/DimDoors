package org.dimdev.dimcore.api.errata.schema

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import java.util.stream.Stream

object SchemaOps : DynamicOps<Any> {
    override fun empty(): Any = ""

    override fun createString(value: String): Any = value

    override fun <U> convertTo(outOps: DynamicOps<U>, input: Any): U = throw UnsupportedOperationException()

    override fun getNumberValue(input: Any): DataResult<Number> = unsupported()

    override fun createNumeric(i: Number): Any = throw UnsupportedOperationException()

    override fun getStringValue(input: Any): DataResult<String> = unsupported()

    override fun mergeToList(list: Any, value: Any): DataResult<Any> = unsupported()

    override fun mergeToMap(map: Any, key: Any, value: Any): DataResult<Any> = unsupported()

    override fun getMapValues(input: Any): DataResult<Stream<Pair<Any, Any>>> = unsupported()

    override fun createMap(map: Stream<Pair<Any, Any>>): Any = throw UnsupportedOperationException()

    override fun getStream(input: Any): DataResult<Stream<Any>> = unsupported()

    override fun createList(input: Stream<Any>): Any = throw UnsupportedOperationException()

    override fun remove(input: Any, key: String): Any = throw UnsupportedOperationException()

    private fun <R> unsupported(): DataResult<R> = DataResult.error { "SchemaOps only collects keys" }
}
