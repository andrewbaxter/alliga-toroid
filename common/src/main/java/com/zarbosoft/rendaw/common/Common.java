package com.zarbosoft.rendaw.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.NoSuchFileException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class Common {
  public static <T> Iterable<T> iterable(final Iterator<T> arg) {
    return new IteratorIterable<>(arg);
  }

  public static <T> boolean isOrdered(final Comparator<T> comparator, final T a, final T b) {
    return comparator.compare(a, b) <= 0;
  }

  public static <T extends Comparable<T>> boolean isOrdered(final T a, final T b) {
    return a.compareTo(b) <= 0;
  }

  public static <T extends Comparable<T>> boolean isOrderedExclusive(final T a, final T b) {
    return a.compareTo(b) < 0;
  }

  public static RuntimeException uncheck(final Throwable e) {
    if (e instanceof RuntimeException) return (RuntimeException) e;
    if (e instanceof InvocationTargetException)
      return uncheck(((InvocationTargetException) e).getTargetException());
    if (e instanceof ExecutionException) return uncheck(((ExecutionException) e).getCause());
    if (e instanceof RuntimeException) return (RuntimeException) e;
    if (e instanceof NoSuchFileException)
      return new UncheckedFileNotFoundException((NoSuchFileException) e);
    if (e instanceof FileNotFoundException)
      return new UncheckedFileNotFoundException((FileNotFoundException) e);
    if (e instanceof IOException) return new UncheckedIOException((IOException) e);
    return new UncheckedException(e);
  }

  public static <T> T uncheck(final Thrower1<T> code) {
    try {
      return code.get();
    } catch (InvocationTargetException e) {
      throw uncheck(((InvocationTargetException) e).getTargetException());
    } catch (ExecutionException e) {
      throw uncheck(((ExecutionException) e).getCause());
    } catch (NoSuchFileException e) {
      throw new UncheckedFileNotFoundException(e);
    } catch (FileNotFoundException e) {
      throw new UncheckedFileNotFoundException(e);
    } catch (IOException e) {
      throw new UncheckedIOException((IOException) e);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new UncheckedException(e);
    }
  }

  public static void uncheck(final Thrower2 code) {
    try {
      code.get();
    } catch (InvocationTargetException e) {
      throw uncheck(((InvocationTargetException) e).getTargetException());
    } catch (ExecutionException e) {
      throw uncheck(((ExecutionException) e).getCause());
    } catch (NoSuchFileException e) {
      throw new UncheckedFileNotFoundException(e);
    } catch (FileNotFoundException e) {
      throw new UncheckedFileNotFoundException(e);
    } catch (IOException e) {
      throw new UncheckedIOException((IOException) e);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new UncheckedException(e);
    }
  }

  public static ROList<String> splitN(String text, String delim, int n) {
    TSList<String> out = new TSList<>();
    int at = 0;
    while (at < text.length()) {
      int nextAt;
      if (out.size() == n) {
        nextAt = text.length();
      } else {
        nextAt = text.indexOf(delim, 0);
        if (nextAt == -1) nextAt = text.length();
      }
      out.add(text.substring(at, nextAt));
      at = nextAt + delim.length();
    }
    return out;
  }

  public static ROList<String> split(String text, String delim) {
    TSList<String> out = new TSList<>();
    int at = 0;
    while (at < text.length()) {
      int nextAt = text.indexOf(delim, 0);
      if (nextAt == -1) nextAt = text.length();
      out.add(text.substring(at, nextAt));
      at = nextAt + delim.length();
    }
    return out;
  }

  @FunctionalInterface
  public interface Thrower1<T> {
    T get() throws Exception, Error;
  }

  @FunctionalInterface
  public interface Thrower2 {
    void get() throws Exception, Error;
  }

  public static class UncheckedException extends RuntimeException {
    public UncheckedException(final Throwable e) {
      super(e);
    }
  }

  public static class UncheckedFileNotFoundException extends UncheckedException {
    public UncheckedFileNotFoundException(Exception e) {
      super(e);
    }
  }

  private static class IteratorIterable<T> implements Iterable<T> {
    private final Iterator<T> arg;

    public IteratorIterable(Iterator<T> arg) {
      this.arg = arg;
    }

    @Override
    public void forEach(Consumer<? super T> action) {
      throw new Assertion();
    }

    @Override
    public Iterator<T> iterator() {
      return arg;
    }
  }
}
