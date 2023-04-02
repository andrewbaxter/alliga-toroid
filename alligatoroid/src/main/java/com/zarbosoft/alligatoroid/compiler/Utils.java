package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.Format;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Utils {
  public static final String UNIQUE_PATH_FILENAME = "path";

  public static <T> T await(Future<T> f) {
    try {
      return f.get();
    } catch (ExecutionException e) {
      throw uncheck(e.getCause());
    } catch (InterruptedException e) {
      throw uncheck(e);
    }
  }

  public static Type[] genericArgs(Parameter param) {
    return ((ParameterizedType) param.getParameterizedType()).getActualTypeArguments();
  }

  public static Path uniqueDir(Path rootCachePath, byte[] id) {
    return uncheck(
        () -> {
          String hash = new SHA256().add(id).buildHex();
          Path tryRelPath = rootCachePath;
          int cuts = 3;
          for (int i = 0; i < cuts; ++i) {
            String seg = hash.substring(i * 2, (i + 1) * 2);
            tryRelPath = tryRelPath.resolve(seg);
          }
          Path useRelPath = null;
          for (int i = 0; i < 1000; ++i) {
            Path tryRelPath1 =
                tryRelPath.resolve(Format.format("%s-%s", hash.substring(cuts * 2), i));
            Path importSpecPath = tryRelPath1.resolve(UNIQUE_PATH_FILENAME);
            byte[] foundIdBytes;
            try {
              foundIdBytes = Files.readAllBytes(importSpecPath);
            } catch (NoSuchFileException e) {
              useRelPath = tryRelPath1;
              break;
            }
            if (Arrays.equals(foundIdBytes, id)) {
              useRelPath = tryRelPath1;
              break;
            }
          }
          if (useRelPath == null)
            // Something's probably wrong with the hashing code if this is reached
          {
              throw new Assertion();
          }

          Files.createDirectories(useRelPath);
          Files.write(useRelPath.resolve(UNIQUE_PATH_FILENAME), id);
          return useRelPath;
        });
  }

  public static void recursiveDelete(Path path) {
    uncheck(
        () -> {
          try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
          } catch (NoSuchFileException e) {
          }
        });
  }

  public static int reflectHashCode(Object o) {
    return uncheck(
        () -> {
          Field[] fields = o.getClass().getDeclaredFields();
          Object[] values = new Object[fields.length];
          for (int i = 0; i < fields.length; i++) {
            values[i] = fields[i].get(o);
          }
          return Arrays.hashCode(values);
        });
  }

  public static boolean reflectEquals(Object o, Object o2) {
    if (o == o2) {
        return true;
    }
    if (o2 == null || o.getClass() != o2.getClass()) {
        return false;
    }
    return uncheck(
        () -> {
          for (Field f : o.getClass().getDeclaredFields()) {
            if (!Objects.equals(f.get(o), f.get(o2))) {
                return false;
            }
          }
          return true;
        });
  }

  public static byte[] toBytes(int x) {
    ByteBuffer bb = ByteBuffer.allocate(4);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    bb.putInt(x);
    return bb.array();
  }

  public static byte[] toBytes(long x) {
    ByteBuffer bb = ByteBuffer.allocate(8);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    bb.putLong(x);
    return bb.array();
  }

  public static String toUnderscore(Class klass) {
    return toUnderscore(klass.getSimpleName());
  }

  public static String toUnderscore(String name) {
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < name.length(); ++i) {
      if (Character.isUpperCase(name.codePointAt(i))) {
        if (i > 0) {
          out.append('_');
        }
        out.appendCodePoint(Character.toLowerCase(name.codePointAt(i)));
      } else {
        out.appendCodePoint(name.codePointAt(i));
      }
    }
    return out.toString();
  }

  public static class SHA256 {
    private final MessageDigest digest;

    public SHA256() {
      digest = uncheck(() -> MessageDigest.getInstance("SHA-256"));
    }

    public SHA256 add(byte[] value) {
      digest.update(toBytes(value.length));
      digest.update(value);
      return this;
    }

    public SHA256 add(String value) {
      return add(value.getBytes(StandardCharsets.UTF_8));
    }

    public SHA256 add(Path path) {
      return uncheck(
          () -> {
            digest.update(toBytes(Files.size(path)));
            Files.copy(
                path,
                new OutputStream() {
                  @Override
                  public void write(int b) throws IOException {
                    digest.update((byte) b);
                  }

                  @Override
                  public void write(@NotNull byte[] b) throws IOException {
                    digest.update(b);
                  }
                });
            return this;
          });
    }

    public String buildHex() {
      byte[] hash = digest.digest();
      StringBuilder out = new StringBuilder(2 * hash.length);
      for (int i = 0; i < hash.length; ++i) {
        String hex = Integer.toHexString(Byte.toUnsignedInt(hash[i]));
        if (hex.length() == 1) {
          out.append('0');
        }
        out.append(hex);
      }
      return out.toString();
    }
  }
}
