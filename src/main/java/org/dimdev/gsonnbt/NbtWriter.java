package org.dimdev.gsonnbt;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import static net.minecraft.datafixer.NbtOps.INSTANCE;

public final class NbtWriter extends JsonWriter {
  private static final Writer UNWRITABLE_WRITER = new Writer() {
    @Override public void write(char[] buffer, int offset, int counter) {
      throw new AssertionError();
    }
    @Override public void flush() throws IOException {}
    @Override public void close() throws IOException {}
  };
  /** Added to the top of the stack when this writer is closed to cause following ops to fail. */
  private static final JsonPrimitive SENTINEL_CLOSED = new JsonPrimitive("closed");

  /** The JsonElements and JsonArrays under modification, outermost to innermost. */
  private final List<Tag> stack = new ArrayList<>();

  /** The name for the next JSON object value. If non-null, the top of the stack is a JsonObject. */
  private String pendingName;

  /** the JSON element constructed by this writer. */
  private Tag product = EndTag.INSTANCE; // TODO: is this really what we want?;

  public NbtWriter() {
    super(UNWRITABLE_WRITER);
  }

  /**
   * Returns the top level object produced by this writer.
   */
  public Tag get() {
    if (!stack.isEmpty()) {
      throw new IllegalStateException("Expected one JSON element but was " + stack);
    }
    return product;
  }

  private Tag peek() {
    return stack.get(stack.size() - 1);
  }

  private void put(Tag value) {
    if (pendingName != null) {
      if (!(value instanceof EndTag) || getSerializeNulls()) {
        Tag element = peek();
        if (element instanceof CompoundTag) {
          ((CompoundTag) element).put(pendingName, value);
        } else {
          throw new IllegalStateException();
        }
      }
      pendingName = null;
    } else if (stack.isEmpty()) {
      product = value;
    } else {
      Tag element = peek();
      if (element instanceof ListTag) {
        ((ListTag) element).add(value);
      } else {
        throw new IllegalStateException();
      }
    }
  }

  @Override public JsonWriter beginArray() throws IOException {
    ListTag array = new ListTag();
    put(array);
    stack.add(array);
    return this;
  }

  @Override public JsonWriter endArray() throws IOException {
    if (stack.isEmpty() || pendingName != null) {
      throw new IllegalStateException();
    }
    Tag element = peek();
    if (element instanceof ListTag) {
      stack.remove(stack.size() - 1);
      return this;
    }
    throw new IllegalStateException();
  }

  @Override public JsonWriter beginObject() throws IOException {
    CompoundTag object = new CompoundTag();
    put(object);
    stack.add(object);
    return this;
  }

  @Override public JsonWriter endObject() throws IOException {
    if (stack.isEmpty() || pendingName != null) {
      throw new IllegalStateException();
    }
    Tag element = peek();
    if (element instanceof CompoundTag) {
      stack.remove(stack.size() - 1);
      return this;
    }
    throw new IllegalStateException();
  }

  @Override public JsonWriter name(String name) throws IOException {
    if (stack.isEmpty() || pendingName != null) {
      throw new IllegalStateException();
    }
    Tag element = peek();
    if (element instanceof CompoundTag) {
      pendingName = name;
      return this;
    }
    throw new IllegalStateException();
  }

  @Override public JsonWriter value(String value) throws IOException {
    if (value == null) {
      return nullValue();
    }
    put(INSTANCE.createString(value));
    return this;
  }

  @Override public JsonWriter nullValue() throws IOException {
    put(EndTag.INSTANCE);
    return this;
  }

  @Override public JsonWriter value(boolean value) throws IOException {
    put(INSTANCE.createByte((byte)(value ? 1 : 0)));
    return this;
  }

  @Override public JsonWriter value(Boolean value) throws IOException {
    value(new Byte((byte)(value ? 1 : 0)));
    return this;
  }

  @Override public JsonWriter value(double value) throws IOException {
    if (!isLenient() && (Double.isNaN(value) || Double.isInfinite(value))) {
      throw new IllegalArgumentException("NBT forbids NaN and infinities: " + value);
    }
    put(INSTANCE.createDouble(value));
    return this;
  }

  @Override public JsonWriter value(long value) throws IOException {
    put(INSTANCE.createLong(value));
    return this;
  }

  @Override public JsonWriter value(Number value) throws IOException {
    if (value == null) {
      return nullValue();
    }

    if (!isLenient()) {
      double d = value.doubleValue();
      if (Double.isNaN(d) || Double.isInfinite(d)) {
        throw new IllegalArgumentException("NBT forbids NaN and infinities: " + value);
      }
    }

    if(value instanceof Byte) put(INSTANCE.createByte(value.byteValue()));
    else if(value instanceof Double) put(INSTANCE.createDouble(value.doubleValue()));
    else if(value instanceof Float) put(INSTANCE.createFloat(value.floatValue()));
    else if(value instanceof Integer) put(INSTANCE.createInt(value.intValue()));
    else if(value instanceof Short) put(INSTANCE.createShort(value.shortValue()));
    else throw new IllegalArgumentException("NBT does not support " + value.getClass().getName());

    return this;
  }

  @Override public void flush() throws IOException {
  }

  @Override public void close() throws IOException {
  }
}