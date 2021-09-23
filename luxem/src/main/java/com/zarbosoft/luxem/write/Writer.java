package com.zarbosoft.luxem.write;

import com.zarbosoft.luxem.events.LArrayCloseEvent;
import com.zarbosoft.luxem.events.LArrayOpenEvent;
import com.zarbosoft.luxem.events.LPrimitiveEvent;
import com.zarbosoft.luxem.events.LRecordCloseEvent;
import com.zarbosoft.luxem.events.LRecordOpenEvent;
import com.zarbosoft.luxem.events.LTypeEvent;
import com.zarbosoft.luxem.events.LuxemEvent;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.TSMap;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Writer {
  private static final ROMap<Byte, Byte> typeEscapes =
      escapeMap()
          .put((byte) ')', (byte) ')')
          .put((byte) '\n', (byte) 'n')
          .put((byte) '\t', (byte) 't')
          .put((byte) '\r', (byte) 'r');
  private static final ROMap<Byte, Byte> quotedPrimitiveEscapes =
      escapeMap()
          .put((byte) '"', (byte) '"')
          .put((byte) '\n', (byte) 'n')
          .put((byte) '\t', (byte) 't')
          .put((byte) '\r', (byte) 'r');
  private final boolean pretty;
  private final byte indentByte;
  private final int indentMultiple;
  private final OutputStream stream;
  private int indentCount = 0;
  private final Deque<State> states = new ArrayDeque<>();
  private boolean first = true;

  public Writer(final OutputStream stream) {
    this(stream, false, (byte) 0, 0);
  }

  public Writer(
      final OutputStream stream,
      final boolean pretty,
      final byte indentByte,
      final int indentMultiple) {
    this.stream = stream;
    this.pretty = pretty;
    this.indentByte = indentByte;
    this.indentMultiple = indentMultiple;
    states.addLast(State.ARRAY);
  }

  public Writer(final OutputStream stream, final byte indentByte, final int indentMultiple) {
    this(stream, true, indentByte, indentMultiple);
  }

  private static TSMap<Byte, Byte> escapeMap() {
    return new TSMap<Byte, Byte>().put((byte) '\\', (byte) '\\');
  }

  private void escape(final byte[] bytes, final ROMap<Byte, Byte> escapes) {
    int lastEscape = 0;
    for (int i = 0; i < bytes.length; ++i) {
      final Byte key = escapes.getOpt(bytes[i]);
      if (key == null) continue;
      streamWrite(bytes, lastEscape, i);
      streamWrite('\\');
      streamWrite(key);
      lastEscape = i + 1;
    }
    streamWrite(bytes, lastEscape, bytes.length);
  }

  private void streamWrite(char c) {
    uncheck(() -> stream.write(c));
  }

  private void streamWrite(byte[] bytes, int start, int end) {
    uncheck(() -> stream.write(bytes, start, end - start));
  }

  public Writer emit(final LuxemEvent event) {
    final Class<?> k = event.getClass();
    if (false) {
      return null; // dead code
    } else if (k == LPrimitiveEvent.class) {
      return primitive(((LPrimitiveEvent) event).value);
    } else if (k == LTypeEvent.class) {
      return type(((LTypeEvent) event).value);
    } else if (k == LArrayOpenEvent.class) {
      return arrayBegin();
    } else if (k == LArrayCloseEvent.class) {
      return arrayEnd();
    } else if (k == LRecordOpenEvent.class) {
      return recordBegin();
    } else if (k == LRecordCloseEvent.class) {
      return recordEnd();
    } else {
      throw new Assertion();
    }
  }

  public Writer type(final String value) {
    return type(value.getBytes(StandardCharsets.UTF_8));
  }

  public Writer primitive(final String value) {
    return primitive(value.getBytes(StandardCharsets.UTF_8));
  }

  private void valueBegin() {
    if (states.peekLast() == State.PREFIXED) {
      states.removeLast();
    } else {
      if (states.peekLast() == State.RECORD) {
        states.addLast(State.KEY);
      }
      if (first) {
        first = false;
      } else if (pretty) {
        streamWrite('\n');
        indent();
      }
    }
  }

  private void valueEnd() {
    if (states.peekLast() == State.KEY) {
      streamWrite((byte) ':');
      if (pretty) streamWrite(' ');
      states.removeLast();
      states.addLast(State.PREFIXED);
    } else {
      streamWrite((byte) ',');
    }
  }

  private void streamWrite(byte c) {
    uncheck(() -> stream.write(c));
  }

  private void indent() {
    if (!pretty) return;
    for (int i = 0; i < indentCount; ++i)
      for (int j = 0; j < indentMultiple; ++j) streamWrite(indentByte);
  }

  public Writer recordBegin() {
    valueBegin();
    streamWrite((byte) '{');
    indentCount += 1;
    states.addLast(State.RECORD);
    return this;
  }

  public Writer recordEnd() {
    states.removeLast();
    indentCount -= 1;
    if (pretty) {
      streamWrite('\n');
      indent();
    }
    streamWrite((byte) '}');
    valueEnd();
    return this;
  }

  public Writer arrayBegin() {
    valueBegin();
    streamWrite((byte) '[');
    indentCount += 1;
    states.addLast(State.ARRAY);
    return this;
  }

  public Writer arrayEnd() {
    states.removeLast();
    indentCount -= 1;
    if (pretty) {
      streamWrite('\n');
      indent();
    }
    streamWrite((byte) ']');
    valueEnd();
    return this;
  }

  public Writer type(final byte[] bytes) {
    typeBegin();
    typeChunk(bytes);
    typeEnd();
    return this;
  }

  public Writer typeBegin() {
    valueBegin();
    streamWrite('(');
    return this;
  }

  public Writer typeEnd() {
    streamWrite(')');
    if (pretty) streamWrite(' ');
    states.addLast(State.PREFIXED);
    return this;
  }

  public Writer typeChunk(final byte[] bytes) {
    escape(bytes, typeEscapes);
    return this;
  }

  public Writer primitive(final byte[] bytes) {
    return quotedPrimitive(bytes);
  }

  public Writer quotedPrimitive(final byte[] bytes) {
    quotedPrimitiveBegin();
    quotedPrimitiveChunk(bytes);
    quotedPrimitiveEnd();
    return this;
  }

  public Writer quotedPrimitiveBegin() {
    valueBegin();
    streamWrite('"');
    return this;
  }

  public Writer quotedPrimitiveEnd() {
    streamWrite('"');
    valueEnd();
    return this;
  }

  public Writer quotedPrimitiveChunk(final byte[] bytes) {
    escape(bytes, quotedPrimitiveEscapes);
    return this;
  }

  private enum State {
    ARRAY,
    RECORD,
    PREFIXED,
    KEY,
  }
}
