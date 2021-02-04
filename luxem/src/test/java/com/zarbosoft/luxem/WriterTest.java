package com.zarbosoft.luxem;

import com.zarbosoft.luxem.write.Writer;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.zarbosoft.rendaw.common.Common.uncheck;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class WriterTest {
  private void check(final String expected, final Case consumer) {
    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    final Writer writer = new Writer(stream, (byte) '\t', 1);
    uncheck(() -> consumer.accept(writer));
    assertThat(new String(stream.toByteArray(), StandardCharsets.UTF_8), equalTo(expected));
  }

  @Test
  public void testArrayBreakIndent() {
    check(
        "[\n\ta,\n\tb,\n],",
        rawWriter ->
            rawWriter
                .arrayBegin()
                .primitive("a".getBytes(StandardCharsets.UTF_8))
                .primitive("b".getBytes(StandardCharsets.UTF_8))
                .arrayEnd());
  }

  @Test
  public void testRootBreak() {
    check(
        "a,\nb,",
        rawWriter ->
            rawWriter
                .primitive("a".getBytes(StandardCharsets.UTF_8))
                .primitive("b".getBytes(StandardCharsets.UTF_8)));
  }

  @Test
  public void testKey() {
    check(
        "{\n\ta: b,\n},",
        rawWriter ->
            rawWriter
                .recordBegin()
                .key("a".getBytes(StandardCharsets.UTF_8))
                .primitive("b".getBytes(StandardCharsets.UTF_8))
                .recordEnd());
  }

  @Test
  public void testPrimitiveEscaping() {
    check("\"a\\\\b\\\"\",", rawWriter -> rawWriter.primitive("a\\b\""));
  }

  @Test
  public void testPrimitiveWhitespaceEscaping() {
    check("\"a\\tb\\n\",", rawWriter -> rawWriter.primitive("a\tb\n"));
  }

  @FunctionalInterface
  public interface Case {
    public void accept(Writer writer) throws IOException;
  }

  // TODO test escaping
}
