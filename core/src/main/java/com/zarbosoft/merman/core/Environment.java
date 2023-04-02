package com.zarbosoft.merman.core;

import com.zarbosoft.merman.core.syntax.primitivepattern.CharacterEvent;
import com.zarbosoft.pidgoon.events.Event;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import java.util.function.Consumer;

/** Abstraction over the runtime environment (javascript, javafx, etc) */
public interface Environment {
  public static final int I18N_DONE = -1;

  public static String joinGlyphEvents(ROList<CharacterEvent> glyphs) {
    int size = 0;
    for (CharacterEvent glyph : glyphs) {
      size += glyph.value.length();
    }
    StringBuilder builder = new StringBuilder(size);
    for (CharacterEvent glyph : glyphs) {
      builder.append(glyph.value);
    }
    return builder.toString();
  }

  public static String joinGlyphs(ROList<String> glyphs) {
    int size = 0;
    for (String glyph : glyphs) {
      size += glyph.length();
    }
    StringBuilder builder = new StringBuilder(size);
    for (String glyph : glyphs) {
      builder.append(glyph);
    }
    return builder.toString();
  }

  Time now();

  HandleDelay delay(long ms, Runnable r);

  void clipboardSet(String mime, Object bytes);

  void clipboardSetString(String string);

  void clipboardGet(String mime, Consumer<Object> cb);

  void clipboardGetString(Consumer<String> cb);

  /**
   * @param text
   * @return list of CharacterEvent
   */
  default TSList<Event> splitGlyphEvents(String text) {
    TSList<String> pre = splitGlyphs(text);
    TSList<Event> glyphs = TSList.of();
    for (String s : pre) {
      glyphs.add(new CharacterEvent(s));
    }
    return glyphs;
  }

  /**
   * @param text
   * @return list of CharacterEvent
   */
  default TSList<String> splitGlyphs(String text) {
    GlyphWalker walker = glyphWalker(text);
    TSList<String> glyphs = TSList.of();
    int end = 0;
    while (true) {
      int start = end;
      end = walker.after(end);
      if (end == I18N_DONE) {
          break;
      }
      glyphs.add(text.substring(start, end));
    }
    return glyphs;
  }

  public GlyphWalker glyphWalker(String s);

  public WordWalker wordWalker(String s);

  /**
   * Suitable places to break lines
   *
   * @param s
   * @return
   */
  public LineWalker lineWalker(String s);

  void destroy();

  public interface Time {
    boolean isBefore(Time other);

    Time plusMillis(int count);
  }

  public interface HandleDelay {
    void cancel();
  }

  public static interface GlyphWalker {
    /**
     * Offset of glyph before offset, I18N_DONE if offset == 0
     *
     * <p>Includes 0
     *
     * @param offset
     * @return
     */
    int before(int offset);

    /**
     * Offset of glyph after offset, I18N_DONE if offset == length
     *
     * <p>Includes 0
     *
     * @param offset
     * @return
     */
    int after(int offset);
  }

  /**
   * There is whitespace between the end of one word and the start of the next. Index 0 and length
   * are both starts and ends.
   *
   * <p>Moves in larger increments: doesn't flop before/after whitespace between words (1 glyph
   * movement)
   */
  public abstract static class WordWalker {
    /**
     * Word start or end before or at offset
     *
     * @param offset
     * @return
     */
    protected abstract int anyBeforeOrAt(int offset);

    /**
     * Word start or end at or after offset
     *
     * @param offset
     * @return
     */
    protected abstract int anyAtOrAfter(int offset);

    /**
     * Also true for anything < 0 or gte length
     *
     * @param offset
     * @return
     */
    protected abstract boolean isWhitespace(int offset);

    protected abstract int length();

    public int startBeforeOrAt(int offset) {
      if (offset < 0 || offset > length()) {
          throw new Assertion();
      }
      int out = anyBeforeOrAt(offset);
      if (!isWhitespace(out) || out == 0) {
          return out;
      }
      return anyBeforeOrAt(out - 1);
    }

    public int startBefore(int offset) {
      if (offset < 0 || offset > length()) {
          throw new Assertion();
      }
      if (offset == 0) {
          return I18N_DONE;
      }
      return startBeforeOrAt(offset - 1);
    }

    public int endBeforeOrAt(int offset) {
      if (offset < 0 || offset > length()) {
          throw new Assertion();
      }
      int out = anyBeforeOrAt(offset);
      if (isWhitespace(out) || out == 0) {
          return out;
      }
      return anyBeforeOrAt(out - 1);
    }

    public int endBefore(int offset) {
      if (offset < 0 || offset > length()) {
          throw new Assertion();
      }
      if (offset == 0) {
          return I18N_DONE;
      }
      return endBeforeOrAt(offset - 1);
    }

    public int startAfterOrAt(int offset) {
      if (offset < 0 || offset > length()) {
          throw new Assertion();
      }
      int out = anyAtOrAfter(offset);
      if (!isWhitespace(out) || out == length()) {
          return out;
      }
      return anyAtOrAfter(out + 1);
    }

    public int startAfter(int offset) {
      if (offset < 0 || offset > length()) {
          throw new Assertion();
      }
      if (offset == length()) {
          return I18N_DONE;
      }
      return startAfterOrAt(offset + 1);
    }

    public int endAfterOrAt(int offset) {
      if (offset < 0 || offset > length()) {
          throw new Assertion();
      }
      int out = anyAtOrAfter(offset);
      if (isWhitespace(out)) {
          return out;
      }
      return anyAtOrAfter(out + 1);
    }

    public int endAfter(int offset) {
      if (offset < 0 || offset > length()) {
          throw new Assertion();
      }
      if (offset == length()) {
          return I18N_DONE;
      }
      return endAfterOrAt(offset + 1);
    }
  }

  /**
   * Line: the end of one line is somewhere after text and before next text. The start of a line is
   * always at the start of text, except the first line which may start with whitespace. A line
   * contains as much following whitespace as possible. The first line contains preceding
   * whitespace. Bounds are 0 and size
   */
  public abstract static class LineWalker {
    protected abstract int anyBeforeOrAt(int offset);

    protected abstract int anyAtOrAfter(int offset);

    protected abstract boolean isWhitespace(int offset);

    protected abstract int length();

    public int beforeOrAt(int offset) {
      if (offset < 0 || offset > length()) {
          throw new Assertion();
      }
      int out = anyBeforeOrAt(offset);
      while (out < offset && isWhitespace(out + 1)) {
          out += 1;
      }
      return out;
    }
  }
}
