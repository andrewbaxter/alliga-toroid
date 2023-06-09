package com.zarbosoft.merman.webview;

import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.webview.compat.CompatOverlay;
import com.zarbosoft.merman.webview.compat.Segmenter;
import com.zarbosoft.rendaw.common.TSList;
import elemental2.core.JsDate;
import elemental2.core.JsIIterableResult;
import elemental2.core.JsIteratorIterable;
import elemental2.core.JsObject;
import elemental2.core.JsRegExp;
import elemental2.core.Symbol;
import elemental2.dom.DataTransfer;
import elemental2.dom.DataTransferItem;
import elemental2.dom.DomGlobal;
import elemental2.promise.IThenable;
import jsinterop.base.JsPropertyMap;

import java.util.function.Consumer;

public class JSEnvironment implements Environment {
  public static JsRegExp isWhitespace = new JsRegExp("\\s");
  private final Segmenter wordSegmenter;
  private final Segmenter glyphSegmenter;

  public JSEnvironment(String lang) {
    wordSegmenter = new Segmenter(lang, JsPropertyMap.of("granularity", "word"));
    glyphSegmenter = new Segmenter(lang, JsPropertyMap.of("granularity", "grapheme"));
  }

  @Override
  public Time now() {
    return new JSTime((long) new JsDate().getTime());
  }

  @Override
  public Environment.GlyphWalker glyphWalker(String s) {
    return new GlyphWalker() {
      BaseI18nWalker inner = new BaseI18nWalker(glyphSegmenter, s);

      @Override
      public int before(int offset) {
        if (offset == 0) {
            return I18N_DONE;
        }
        return inner.anyBeforeOrAt(offset - 1);
      }

      @Override
      public int after(int offset) {
        if (offset == inner.length()) {
            return I18N_DONE;
        }
        return inner.anyAtOrAfter(offset + 1);
      }
    };
  }

  @Override
  public Environment.WordWalker wordWalker(String s) {
    return new WordWalker() {
      BaseI18nWalker inner = new BaseI18nWalker(wordSegmenter, s);

      @Override
      protected int anyBeforeOrAt(int offset) {
        return inner.anyBeforeOrAt(offset);
      }

      @Override
      protected int anyAtOrAfter(int offset) {
        return inner.anyAtOrAfter(offset);
      }

      @Override
      protected boolean isWhitespace(int offset) {
        return inner.isWhitespace(offset);
      }

      @Override
      protected int length() {
        return inner.length();
      }
    };
  }

  @Override
  public Environment.LineWalker lineWalker(String s) {
    return new LineWalker() {
      BaseI18nWalker inner = new BaseI18nWalker(wordSegmenter, s);

      @Override
      protected int anyBeforeOrAt(int offset) {
        return inner.anyBeforeOrAt(offset);
      }

      @Override
      protected int anyAtOrAfter(int offset) {
        return inner.anyAtOrAfter(offset);
      }

      @Override
      protected boolean isWhitespace(int offset) {
        return inner.isWhitespace(offset);
      }

      @Override
      protected int length() {
        return inner.length();
      }
    };
  }

  @Override
  public void destroy() {}

  @Override
  public HandleDelay delay(long ms, Runnable r) {
    return new Handle(
        DomGlobal.setTimeout(
            new DomGlobal.SetTimeoutCallbackFn() {
              @Override
              public void onInvoke(Object... p0) {
                r.run();
              }
            },
            ms));
  }

  @Override
  public void clipboardSet(String mime, Object bytes) {
    CompatOverlay.mmCopy(mime, (String) bytes);
  }

  @Override
  public void clipboardGet(String mime, Consumer<Object> cb) {
    CompatOverlay.mmUncopy()
        .then(
            new IThenable.ThenOnFulfilledCallbackFn<DataTransfer, Object>() {
              @Override
              public IThenable<Object> onInvoke(DataTransfer d) {
                DataTransferItem backup = null;
                for (int i = 0; i < d.items.length; ++i) {
                  DataTransferItem f = d.items.getAt(i);
                  if (mime.equals(f.type)) {
                    f.getAsString(
                        new DataTransferItem.GetAsStringCallbackFn() {
                          @Override
                          public Object onInvoke(String p0) {
                            cb.accept(p0);
                            return null;
                          }
                        });
                    return null;
                  } else if (backup == null && "text/plain".equals(f.type)) {
                    backup = f;
                  }
                }
                if (backup != null) {
                  backup.getAsString(
                      s -> {
                        cb.accept(s);
                        return null;
                      });
                }
                return null;
              }
            });
  }

  @Override
  public void clipboardGetString(Consumer<String> cb) {
    CompatOverlay.mmUncopyText()
        .then(
            new IThenable.ThenOnFulfilledCallbackFn<String, Object>() {
              @Override
              public IThenable<Object> onInvoke(String p0) {
                cb.accept(p0);
                return null;
              }
            });
  }

  @Override
  public void clipboardSetString(String string) {
    CompatOverlay.mmCopyText(string);
  }

  public static class JSTime implements Environment.Time {
    public final long data;

    private JSTime(long ms) {
      this.data = ms;
    }

    @Override
    public boolean isBefore(Time other) {
      return data < ((JSTime) other).data;
    }

    @Override
    public Time plusMillis(int count) {
      return new JSTime(data + count);
    }
  }

  private static class BaseI18nWalker {
    public final String text;
    private final TSList<Integer> segments = new TSList<>();
    private int index;

    private BaseI18nWalker(Segmenter segmenter, String text) {
      this.text = text;
      JsObject segments0 = segmenter.segment(this.text);
      JsIteratorIterable<JsPropertyMap> iter =
          (JsIteratorIterable<JsPropertyMap>) CompatOverlay.getSymbol(segments0, Symbol.iterator);
      JsIIterableResult<JsPropertyMap> at = iter.next();
      while (!at.isDone()) {
        JsPropertyMap segment = at.getValue();
        segments.add((int) (double) segment.get("index"));
        at = iter.next();
      }
      segments.add(text.length());
    }

    public boolean isWhitespace(int offset) {
      return isWhitespace.test(text.substring(offset, 1));
    }

    public int length() {
      return text.length();
    }

    public int anyBeforeOrAt(int offset) {
      while (segments.get(index) > offset) {
        index -= 1;
      }
      return segments.get(index);
    }

    public int anyAtOrAfter(int offset) {
      while (segments.get(index) < offset) {
        index += 1;
      }
      return segments.get(index);
    }
  }

  public static class Handle implements Environment.HandleDelay {
    final double id;

    public Handle(double id) {
      this.id = id;
    }

    @Override
    public void cancel() {
      DomGlobal.clearTimeout(id);
    }
  }
}
