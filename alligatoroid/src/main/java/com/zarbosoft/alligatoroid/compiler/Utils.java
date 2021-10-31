package com.zarbosoft.alligatoroid.compiler;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
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
import java.util.stream.Stream;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Utils {
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
    if (o == o2) return true;
    if (o2 == null || o.getClass() != o2.getClass()) return false;
    return uncheck(
        () -> {
          for (Field f : o.getClass().getDeclaredFields()) {
            if (!Objects.equals(f.get(o), f.get(o2))) return false;
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
