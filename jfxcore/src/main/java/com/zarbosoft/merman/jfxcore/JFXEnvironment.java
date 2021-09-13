package com.zarbosoft.merman.jfxcore;

import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.rendaw.common.TSMap;
import javafx.application.Platform;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;

import java.nio.charset.StandardCharsets;
import java.text.BreakIterator;
import java.time.Instant;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class JFXEnvironment implements Environment {
  public static TSMap<String, DataFormat> dataFormats = new TSMap<>();
  private final Locale locale;
  private final Timer timer = new Timer();
  private Clipboard clipboard;

  public JFXEnvironment(Locale locale) {
    this.locale = locale;
  }

  protected static boolean isWhitespace(String s, int i) {
    if (i < 0 || i >= s.length()) return true;
    return Character.isWhitespace(s.codePointAt(i));
  }

  @Override
  public Time now() {
    return new JavaTime(Instant.now());
  }

  @Override
  public Environment.HandleDelay delay(long ms, Runnable r) {
    AtomicBoolean alive = new AtomicBoolean(true);
    try {
      timer.schedule(
          new TimerTask() {
            @Override
            public void run() {
              if (!alive.get()) return;
              Platform.runLater(r);
            }
          },
          ms);
    } catch (IllegalStateException ignore) {
      // When trying to schedule while shutting down
    }
    return new HandleDelay(alive);
  }

  @Override
  public void clipboardSet(String mime, Object bytes) {
    if (clipboard == null) {
      this.clipboard = Clipboard.getSystemClipboard();
    }
    final ClipboardContent content = new ClipboardContent();
    content.put(dataFormats.getCreate(mime, () -> new DataFormat(mime)), bytes);
    content.putString(new String((byte[]) bytes, StandardCharsets.UTF_8));
    clipboard.setContent(content);
  }

  @Override
  public void clipboardSetString(String string) {
    if (clipboard == null) {
      this.clipboard = Clipboard.getSystemClipboard();
    }
    final ClipboardContent content = new ClipboardContent();
    content.putString(string);
    clipboard.setContent(content);
  }

  @Override
  public void clipboardGet(String mime, Consumer<Object> cb) {
    if (clipboard == null) {
      this.clipboard = Clipboard.getSystemClipboard();
    }
    byte[] out =
        (byte[]) clipboard.getContent(dataFormats.getCreate(mime, () -> new DataFormat(mime)));
    if (out == null) {
      final String temp = clipboard.getString();
      if (temp != null) {
        out = temp.getBytes(StandardCharsets.UTF_8);
      }
    }
    cb.accept(out);
  }

  @Override
  public void clipboardGetString(Consumer<String> cb) {
    if (clipboard == null) {
      this.clipboard = Clipboard.getSystemClipboard();
    }
    cb.accept(clipboard.getString());
  }

  @Override
  public Environment.GlyphWalker glyphWalker(String s) {
    return new GlyphWalker() {
      private final BreakIterator iter;

      {
        iter = BreakIterator.getCharacterInstance(locale);
        iter.setText(s);
      }

      @Override
      public int before(int offset) {
        return iter.preceding(offset);
      }

      @Override
      public int after(int offset) {
        return iter.following(offset);
      }
    };
  }

  @Override
  public Environment.WordWalker wordWalker(String s) {
    return new WordWalker() {
      private final String text;
      private final BreakIterator iter;

      {
        iter = BreakIterator.getWordInstance(locale);
        iter.setText(s);
        text = s;
      }

      @Override
      protected int anyBeforeOrAt(int offset) {
        if (offset == length()) return offset;
        return iter.preceding(offset + 1);
      }

      @Override
      protected int anyAtOrAfter(int offset) {
        if (offset == 0) return offset;
        return iter.following(offset - 1);
      }

      @Override
      protected boolean isWhitespace(int offset) {
        return JFXEnvironment.isWhitespace(text, offset);
      }

      @Override
      protected int length() {
        return text.length();
      }
    };
  }

  @Override
  public Environment.LineWalker lineWalker(String s) {
    return new LineWalker() {
      private final BreakIterator iter;

      {
        iter = BreakIterator.getLineInstance(locale);
        iter.setText(s);
      }

      @Override
      protected int anyBeforeOrAt(int offset) {
        if (offset == length()) return offset;
        return iter.preceding(offset + 1);
      }

      @Override
      protected int anyAtOrAfter(int offset) {
        if (offset == 0) return offset;
        return iter.following(offset);
      }

      @Override
      protected boolean isWhitespace(int offset) {
        return JFXEnvironment.isWhitespace(s, offset);
      }

      @Override
      protected int length() {
        return s.length();
      }
    };
  }

  @Override
  public void destroy() {
    timer.cancel();
  }

  public static class JavaTime implements Time {
    public final Instant data;

    private JavaTime(Instant data) {
      this.data = data;
    }

    @Override
    public boolean isBefore(Time other) {
      return data.isBefore(((JavaTime) other).data);
    }

    @Override
    public Time plusMillis(int count) {
      return new JavaTime(data.plusMillis(count));
    }
  }

  public static class HandleDelay implements Environment.HandleDelay {
    private final AtomicBoolean alive;

    public HandleDelay(AtomicBoolean alive) {
      this.alive = alive;
    }

    @Override
    public void cancel() {
      alive.set(false);
    }
  }
}
