package com.zarbosoft.merman.editor.serialization;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.document.Document;
import com.zarbosoft.merman.misc.ROMap;
import com.zarbosoft.merman.misc.TSMap;
import com.zarbosoft.merman.syntax.BackType;
import com.zarbosoft.merman.syntax.Syntax;
import com.zarbosoft.merman.syntax.back.BackSpec;
import com.zarbosoft.rendaw.common.DeadCode;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Write {
  public static void write(final Document document, final Path out) {
    uncheck(
        () -> {
          try (OutputStream stream = Files.newOutputStream(out)) {
            write(document.root, document.syntax, stream);
          }
        });
  }

  public static void write(final Atom atom, final Syntax syntax, final OutputStream stream) {
    uncheck(
        () -> {
          JsonGenerator jsonGenerator = null;
          final EventConsumer writer;
          switch (syntax.backType) {
            case LUXEM:
              writer = luxemEventConsumer(new Writer(stream, (byte) ' ', 4));
              break;
            case JSON:
              {
                jsonGenerator = new JsonFactory().createGenerator(stream);
                jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
                writer = jsonEventConsumer(jsonGenerator);
                break;
              }
            default:
              throw new DeadCode();
          }
          write(atom, writer);
          if (syntax.backType == BackType.JSON) jsonGenerator.flush();
          stream.write('\n');
          stream.flush();
        });
  }

  private static EventConsumer luxemEventConsumer(final Writer writer) {
    return new EventConsumer() {
      @Override
      public void primitive(final String value) {
        writer.primitive(value);
      }

      @Override
      public void type(final String value) {
        writer.type(value);
      }

      @Override
      public void arrayBegin() {
        writer.arrayBegin();
      }

      @Override
      public void arrayEnd() {
        writer.arrayEnd();
      }

      @Override
      public void recordBegin() {
        writer.recordBegin();
      }

      @Override
      public void recordEnd() {
        writer.recordEnd();
      }

      @Override
      public void key(final String s) {
        writer.key(s);
      }

      @Override
      public void jsonInt(final String value) {
        throw new AssertionError();
      }

      @Override
      public void jsonFloat(final String value) {
        throw new AssertionError();
      }

      @Override
      public void jsonTrue() {
        throw new AssertionError();
      }

      @Override
      public void jsonFalse() {
        throw new AssertionError();
      }

      @Override
      public void jsonNull() {
        throw new AssertionError();
      }
    };
  }

  private static EventConsumer jsonEventConsumer(final JsonGenerator generator) {
    return new EventConsumer() {
      @Override
      public void primitive(final String value) {
        uncheck(() -> generator.writeString(value));
      }

      @Override
      public void type(final String value) {
        throw new AssertionError();
      }

      @Override
      public void arrayBegin() {
        uncheck(() -> generator.writeStartArray());
      }

      @Override
      public void arrayEnd() {
        uncheck(() -> generator.writeEndArray());
      }

      @Override
      public void recordBegin() {
        uncheck(() -> generator.writeStartObject());
      }

      @Override
      public void recordEnd() {
        uncheck(() -> generator.writeEndObject());
      }

      @Override
      public void key(final String s) {
        uncheck(() -> generator.writeFieldName(s));
      }

      @Override
      public void jsonInt(final String value) {
        uncheck(() -> generator.writeRaw(value));
      }

      @Override
      public void jsonFloat(final String value) {
        uncheck(() -> generator.writeRaw(value));
      }

      @Override
      public void jsonTrue() {
        uncheck(() -> generator.writeBoolean(true));
      }

      @Override
      public void jsonFalse() {
        uncheck(() -> generator.writeBoolean(false));
      }

      @Override
      public void jsonNull() {
        uncheck(() -> generator.writeNull());
      }
    };
  }

  public static void write(final Atom atom, final EventConsumer writer) {
    final Deque<WriteState> stack = new ArrayDeque<>();
    atom.write(stack);
    uncheck(
        () -> {
          while (!stack.isEmpty()) stack.getLast().run(stack, writer);
        });
  }

  public static void write(final List<Atom> atoms, final Syntax syntax, final OutputStream stream) {
    uncheck(
        () -> {
          JsonGenerator jsonGenerator = null;
          final EventConsumer writer;
          switch (syntax.backType) {
            case LUXEM:
              writer = luxemEventConsumer(new Writer(stream, (byte) ' ', 4));
              break;
            case JSON:
              {
                jsonGenerator = new JsonFactory().createGenerator(stream);
                jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
                jsonGenerator.writeStartArray();
                writer = jsonEventConsumer(jsonGenerator);
                break;
              }
            default:
              throw new DeadCode();
          }
          for (final Atom atom : atoms) write(atom, writer);
          switch (syntax.backType) {
            case LUXEM:
              stream.write('\n');
              break;
            case JSON:
              jsonGenerator.writeEndArray();
              break;
            default:
              throw new DeadCode();
          }
          stream.flush();
        });
  }

  public interface EventConsumer {

    void primitive(String value);

    void type(String value);

    void arrayBegin();

    void arrayEnd();

    void recordBegin();

    void recordEnd();

    void key(String s);

    void jsonInt(String value);

    void jsonFloat(String value);

    void jsonTrue();

    void jsonFalse();

    void jsonNull();
  }

  public abstract static class WriteState {
    public abstract void run(Deque<WriteState> stack, EventConsumer writer);
  }

  public static class WriteStateBack extends WriteState {
    private final TSMap<String, Object> data;
    private final Iterator<BackSpec> iterator;

    public WriteStateBack(TSMap<String, Object> data, final Iterator<BackSpec> iterator) {
      this.data = data;
      this.iterator = iterator;
    }

    @Override
    public void run(final Deque<WriteState> stack, final EventConsumer writer) {
      if (!iterator.hasNext()) {
        stack.removeLast();
        return;
      }
      BackSpec part = iterator.next();
      part.write(stack, data, writer);
    }
  }

  public static class WriteStateRecord extends WriteState {
    private final TSMap<String, Object> data;
    private final Iterator<Map.Entry<String, BackSpec>> iterator;

    public WriteStateRecord(
        final TSMap<String, Object> data, final ROMap<String, BackSpec> record) {
      this.data = data;
      this.iterator = record.iterator();
    }

    @Override
    public void run(final Deque<WriteState> stack, final EventConsumer writer) {
      if (!iterator.hasNext()) {
        stack.removeLast();
        return;
      }
      final Map.Entry<String, BackSpec> next = iterator.next();
      writer.key(next.getKey());
      BackSpec part = next.getValue();
      part.write(stack, data, writer);
    }
  }

  public static class WriteStateDataArray extends WriteState {
    private final Iterator<Atom> iterator;

    public WriteStateDataArray(final List<Atom> array) {
      this.iterator = array.iterator();
    }

    @Override
    public void run(final Deque<WriteState> stack, final EventConsumer writer) {
      if (!iterator.hasNext()) {
        stack.removeLast();
        return;
      }
      iterator.next().write(stack);
    }
  }

  public static class WriteStateDeepDataArray extends WriteState {
    private final Iterator<Atom> iterator;
    private final String elementKey;
    private final BackSpec element;

    public WriteStateDeepDataArray(BackSpec element, String elementKey, final List<Atom> array) {
      this.element = element;
      this.elementKey = elementKey;
      this.iterator = array.iterator();
    }

    @Override
    public void run(final Deque<WriteState> stack, final EventConsumer writer) {
      if (!iterator.hasNext()) {
        stack.removeLast();
        return;
      }
      final Atom next = iterator.next();
      element.write(stack, new TSMap<String, Object>().putChain(elementKey, next), writer);
    }
  }

  public static class WriteStateArrayEnd extends WriteState {

    @Override
    public void run(final Deque<WriteState> stack, final EventConsumer writer) {
      writer.arrayEnd();
      stack.removeLast();
    }
  }

  public static class WriteStateRecordEnd extends WriteState {
    @Override
    public void run(final Deque<WriteState> stack, final EventConsumer writer) {
      writer.recordEnd();
      stack.removeLast();
    }
  }
}
