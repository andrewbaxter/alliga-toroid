package com.zarbosoft.merman.editorcore.helper;

import com.zarbosoft.merman.core.Environment;

import java.util.function.Consumer;

public class TestEnvironment implements Environment {
  byte[] data = null;
  String string = null;

  protected static boolean isWhitespace(String s, int i) {
    if (i < 0 || i >= s.length()) {
        return true;
    }
    return s.charAt(i) == ' ' || s.charAt(i) == '\n';
  }

  protected static int anyBeforeOrAt(String s, int offset) {
    for (int i = offset; i > 0; --i) {
      boolean prevWs = isWhitespace(s, i - 1);
      boolean ws = isWhitespace(s, i);
      if (prevWs && !ws || !prevWs && ws) {
          return i;
      }
    }
    return 0;
  }

  protected static int anyAtOrAfter(String s, int offset) {
    for (int i = offset; i < s.length(); ++i) {
      boolean prevWs = isWhitespace(s, i - 1);
      boolean ws = isWhitespace(s, i);
      if (prevWs && !ws || !prevWs && ws) {
          return i;
      }
    }
    return s.length();
  }

  @Override
  public Time now() {
    return new Time() {
      @Override
      public boolean isBefore(Time other) {
        return false;
      }

      @Override
      public Time plusMillis(int count) {
        return this;
      }
    };
  }

  @Override
  public HandleDelay delay(long ms, Runnable r) {
    // nop
    return new HandleDelay() {
      @Override
      public void cancel() {}
    };
  }

  @Override
  public void clipboardSet(String mime, Object bytes) {
    data = (byte[]) bytes;
  }

  @Override
  public void clipboardSetString(String string) {
    this.string = string;
  }

  @Override
  public void clipboardGet(String mime, Consumer<Object> cb) {
    cb.accept(data);
  }

  @Override
  public void clipboardGetString(Consumer<String> cb) {
    cb.accept(string);
  }

  @Override
  public void destroy() {}

  @Override
  public GlyphWalker glyphWalker(String s) {
    return new GlyphWalker() {
      @Override
      public int before(int offset) {
        if (offset == 0) {
            return I18N_DONE;
        }
        return offset - 1;
      }

      public int after(int offset) {
        if (offset == s.length()) {
            return I18N_DONE;
        }
        return offset + 1;
      }
    };
  }

  @Override
  public WordWalker wordWalker(String s) {
    return new WordWalker() {
      @Override
      protected int anyBeforeOrAt(int offset) {
        return TestEnvironment.anyBeforeOrAt(s, offset);
      }

      @Override
      protected int anyAtOrAfter(int offset) {
        return TestEnvironment.anyAtOrAfter(s, offset);
      }

      @Override
      protected boolean isWhitespace(int i) {
        return TestEnvironment.isWhitespace(s, i);
      }

      @Override
      protected int length() {
        return s.length();
      }
    };
  }

  @Override
  public LineWalker lineWalker(String s) {
    return new LineWalker() {
      @Override
      protected int anyBeforeOrAt(int offset) {
        return TestEnvironment.anyBeforeOrAt(s, offset);
      }

      @Override
      protected int anyAtOrAfter(int offset) {
        return TestEnvironment.anyAtOrAfter(s, offset);
      }

      @Override
      protected boolean isWhitespace(int i) {
        return TestEnvironment.isWhitespace(s, i);
      }

      @Override
      protected int length() {
        return s.length();
      }
    };
  }
}
