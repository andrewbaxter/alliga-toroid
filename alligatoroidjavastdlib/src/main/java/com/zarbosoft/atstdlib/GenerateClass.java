package com.zarbosoft.atstdlib;

import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class GenerateClass {
  public final Path path;
  public final Class klass;

  private GenerateClass(Class klass) {
    this.path = outPath(klass);
    this.klass = klass;
  }

  public static Path generate(Class klass) {
    GenerateClass g = new GenerateClass(klass);
    Main.sync.push(g);
    return g.path;
  }

  private Path outPath(Class klass) {
    return uncheck(
        () -> {
          String[] parts = klass.getCanonicalName().split("\\.");
          Path base = Paths.get("out");
          for (int i = 0; i < parts.length - 1; ++i) {
            String part = parts[i];
            base = base.resolve(part);
          }
          return base.resolve(parts[parts.length - 1] + ".at");
        });
  }

  public LSubtree lRecord(IdManager ids, List<ROPair<LSubtree, LSubtree>> values) {
    return writer -> {
      writer.type("record").recordBegin();
      ids.write(writer);
      writer.primitive("elements").arrayBegin();
      for (ROPair<LSubtree, LSubtree> value : values) {
        writer.type("record_element").recordBegin();
        ids.write(writer);
        writer.primitive("key");
        value.first.write(writer);
        writer.primitive("value");
        value.second.write(writer);
        writer.recordEnd();
      }
      writer.arrayEnd();
      writer.recordEnd();
    };
  }

  public LSubtree lRecord(IdManager ids, ROPair<LSubtree, LSubtree>... values) {
    return lRecord(ids, Arrays.asList(values));
  }

  public LSubtree lTuple(IdManager ids, LSubtree... values) {
    return lTuple(ids, Arrays.asList(values));
  }

  public LSubtree lTuple(IdManager ids, List<LSubtree> values) {
    return writer -> {
      writer.type("tuple").recordBegin();
      ids.write(writer);
      writer.primitive("elements").arrayBegin();
      for (LSubtree value : values) {
        value.write(writer);
      }
      writer.arrayEnd();
      writer.recordEnd();
    };
  }

  public LSubtree lString(IdManager ids, String text) {
    return writer -> {
      writer.type("literal_string").recordBegin();
      ids.write(writer);
      writer.primitive("value").primitive(text);
      writer.recordEnd();
    };
  }

  public LSubtree lBool(IdManager ids, boolean value) {
    return writer -> {
      writer.type("literal_bool").recordBegin();
      ids.write(writer);
      writer.primitive("value").primitive(value ? "true" : "false");
      writer.recordEnd();
    };
  }

  public LSubtree lCall(IdManager ids, LSubtree f, LSubtree args) {
    return writer -> {
      writer.type("call").recordBegin();
      ids.write(writer);
      writer.primitive("target");
      f.write(writer);
      writer.primitive("argument");
      args.write(writer);
      writer.recordEnd();
    };
  }

  public LSubtree lAccess(IdManager ids, LSubtree target, LSubtree primitive) {
    return writer -> {
      writer.type("access").recordBegin();
      ids.write(writer);
      writer.primitive("base");
      target.write(writer);
      writer.primitive("key");
      primitive.write(writer);
      writer.recordEnd();
    };
  }

  public LSubtree lBuiltin(IdManager ids) {
    return writer -> {
      writer.type("builtin").recordBegin();
      ids.write(writer);
      writer.recordEnd();
    };
  }

  public LSubtree lLocal(IdManager ids, LSubtree key) {
    return writer -> {
      writer.type("local").recordBegin();
      ids.write(writer);
      writer.primitive("key");
      key.write(writer);
      writer.recordEnd();
    };
  }

  public LSubtree lBind(IdManager ids, LSubtree name, LSubtree value) {
    return writer -> {
      writer.type("bind").recordBegin();
      ids.write(writer);
      writer.primitive("key");
      name.write(writer);
      writer.primitive("value");
      value.write(writer);
      writer.recordEnd();
    };
  }

  public LSubtree lType(
      IdManager ids, Writer writer0, TSMap<String, String> importKeys, Class klass) {
    if (klass == this.klass) {
      return lAccess(ids, lLocal(ids, lString(ids, "pair")), lString(ids, "type"));
    } else if (klass.isArray()) {
      return lCall(
          ids,
          lAccess(ids, jvm(ids), lString(ids, "deferredArray")),
          lType(ids, writer0, importKeys, klass.getComponentType()));
    } else if (klass == void.class) {
      return lAccess(ids, lBuiltin(ids), lString(ids, "nullType"));
    } else if (klass == boolean.class) {
      return lAccess(ids, jvm(ids), lString(ids, "bool"));
    } else if (klass == int.class
        || klass == short.class
        || klass == long.class
        || klass == byte.class
        || klass == float.class
        || klass == double.class
        || klass == char.class) {
      return lAccess(ids, jvm(ids), lString(ids, klass.getName()));
    } else {
      final Path importPath = generate(klass);
      String key = importKeys.getOpt(importPath.toString());
      if (key == null) {
        key = "itype" + importKeys.size();
        lBind(
                ids,
                lString(ids, key),
                lModLocal(ids, path.getParent().relativize(importPath).toString()))
            .write(writer0);
        importKeys.put(importPath.toString(), key);
      }
      return lLocal(ids, lString(ids, key));
    }
  }

  private LSubtree lModLocal(IdManager ids, String name) {
    /*
     return writer -> {
       writer.type("mod_local").recordBegin();
       ids.write(writer);
       writer.primitive("path");
       lString(ids, path).write(writer);
       writer.recordEnd();
     };
    */
    return lCall(ids, lAccess(ids, lBuiltin(ids), lString(ids, "modLocal")), lString(ids, name));
  }

  public LSubtree lType(
      IdManager ids, Writer writer0, TSMap<String, String> importKeys, Parameter[] params) {
    List<LSubtree> values = new ArrayList<>();
    for (Parameter param : params) {
      values.add(lType(ids, writer0, importKeys, param.getType()));
    }
    return lTuple(ids, values);
  }

  public LSubtree accessBuilder(IdManager ids) {
    return lAccess(ids, lLocal(ids, lString(ids, "pair")), lString(ids, "builder"));
  }

  public LSubtree jvm(IdManager ids) {
    return lAccess(ids, lBuiltin(ids), lString(ids, "jvm"));
  }

  public void generate() {
    System.out.format("Generating %s\n", path);
    uncheck(
        () -> {
          Files.createDirectories(path.getParent());
          try (OutputStream os = Files.newOutputStream(path)) {
            Writer writer = new Writer(os, (byte) '\t', 1);
            IdManager ids = new IdManager();
            writer.type("alligatorus:0.0.1").arrayBegin();
            lBind(
                    ids,
                    lString(ids, "pair"),
                    lCall(
                        ids,
                        lAccess(ids, jvm(ids), lString(ids, "externClass")),
                        lTuple(ids, lString(ids, klass.getCanonicalName()))))
                .write(writer);
            TSMap<String, String> importKeys = new TSMap<>();

            if (klass.getSuperclass() != null) {
              lCall(
                      ids,
                      lAccess(ids, accessBuilder(ids), lString(ids, "inherit")),
                      lType(ids, writer, importKeys, klass.getSuperclass()))
                  .write(writer);
            }
            for (Class parent : klass.getInterfaces()) {
              lCall(
                      ids,
                      lAccess(ids, accessBuilder(ids), lString(ids, "inherit")),
                      lType(ids, writer, importKeys, parent))
                  .write(writer);
            }

            // Instance stuff
            List<Constructor> publicConstructors = new ArrayList<>();
            for (Constructor constructor : klass.getConstructors()) {
              if (!Modifier.isPublic(constructor.getModifiers())) {
                  continue;
              }
              publicConstructors.add(constructor);
            }
            if (publicConstructors.size() > 0) {
              for (Constructor constructor : publicConstructors) {
                lCall(
                        ids,
                        lAccess(ids, accessBuilder(ids), lString(ids, "constructor")),
                        lRecord(
                            ids,
                            new ROPair<>(
                                lString(ids, "in"),
                                lType(ids, writer, importKeys, constructor.getParameters()))))
                    .write(writer);
              }
            }

            for (Method m : klass.getDeclaredMethods()) {
              if (m.isBridge()) {
                  continue;
              }
              if (m.isSynthetic()) {
                  continue;
              }
              boolean isPublic = Modifier.isPublic(m.getModifiers());
              boolean isProtected = Modifier.isProtected(m.getModifiers());
              boolean isFinal = Modifier.isFinal(m.getModifiers());
              boolean isStatic = Modifier.isStatic(m.getModifiers());
              if (!(isPublic || isProtected)) {
                  continue;
              }
              lCall(
                      ids,
                      lAccess(ids, accessBuilder(ids), lString(ids, "method")),
                      lTuple(
                          ids,
                          lString(ids, m.getName()),
                          lRecord(
                              ids,
                              new ROPair<>(
                                  lString(ids, "in"),
                                  lType(ids, writer, importKeys, m.getParameters())),
                              new ROPair<>(
                                  lString(ids, "out"),
                                  lType(ids, writer, importKeys, m.getReturnType())),
                              new ROPair<>(lString(ids, "protected"), lBool(ids, isProtected)),
                              new ROPair<>(lString(ids, "final"), lBool(ids, isFinal)),
                              new ROPair<>(lString(ids, "static"), lBool(ids, isStatic)))))
                  .write(writer);
            }

            for (Field f : klass.getDeclaredFields()) {
              boolean isPublic = Modifier.isPublic(f.getModifiers());
              boolean isProtected = Modifier.isProtected(f.getModifiers());
              boolean isFinal = Modifier.isFinal(f.getModifiers());
              boolean isStatic = Modifier.isStatic(f.getModifiers());
              if (!(isPublic || isProtected)) {
                  continue;
              }
              lCall(
                      ids,
                      lAccess(ids, accessBuilder(ids), lString(ids, "data")),
                      lTuple(
                          ids,
                          lString(ids, f.getName()),
                          lRecord(
                              ids,
                              new ROPair<>(
                                  lString(ids, "type"),
                                  lType(ids, writer, importKeys, f.getType())),
                              new ROPair<>(lString(ids, "protected"), lBool(ids, isProtected)),
                              new ROPair<>(lString(ids, "final"), lBool(ids, isFinal)),
                              new ROPair<>(lString(ids, "static"), lBool(ids, isStatic)))))
                  .write(writer);
            }

            lAccess(ids, lLocal(ids, lString(ids, "pair")), lString(ids, "type")).write(writer);

            writer.arrayEnd();
          }
        });
  }

  private interface LSubtree {
    void write(Writer writer);
  }

  public static class IdManager {
    public int nextId = 0;

    public void write(Writer writer) {
      writer.primitive("id").primitive(Integer.toString(nextId++));
    }

    public void writeNoKey(Writer writer) {
      writer.primitive(Integer.toString(nextId++));
    }
  }

  public static class ROPair<T, U> {
    public final T first;
    public final U second;

    public ROPair(T first, U second) {
      this.first = first;
      this.second = second;
    }
  }
}
