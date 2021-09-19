package com.zarbosoft.atstdlib;

import com.zarbosoft.luxem.write.Writer;

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

  public GenerateClass(Class klass) {
    this.path = outPath(klass);
    this.klass = klass;
    Main.sync.push(this);
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
      writer.key("elements").arrayBegin();
      for (ROPair<LSubtree, LSubtree> value : values) {
        writer.type("record-element").recordBegin().key("key");
        value.first.write(writer);
        writer.key("value");
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
    return writer -> {
      writer.type("tuple").recordBegin();
      ids.write(writer);
      writer.key("values").arrayBegin();
      for (LSubtree value : values) {
        value.write(writer);
      }
      writer.arrayEnd();
      writer.recordEnd();
    };
  }

  public LSubtree lTuple(IdManager ids, List<LSubtree> values) {
    return writer -> {
      writer.type("tuple").recordBegin();
      ids.write(writer);
      writer.key("values").arrayBegin();
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
      writer.key("value").primitive(text);
      writer.recordEnd();
    };
  }

  public LSubtree lBool(IdManager ids, boolean value) {
    return writer -> {
      writer.type("literal_bool").recordBegin();
      ids.write(writer);
      writer.key("value").primitive(value ? "true" : "false");
      writer.recordEnd();
    };
  }

  public LSubtree lCall(IdManager ids, LSubtree f, LSubtree args) {
    return writer -> {
      writer.type("call").recordBegin();
      ids.write(writer);
      writer.key("target");
      f.write(writer);
      writer.key("argument");
      args.write(writer);
      writer.recordEnd();
    };
  }

  public LSubtree lAccess(IdManager ids, LSubtree target, LSubtree key) {
    return writer -> {
      writer.type("access").recordBegin();
      ids.write(writer);
      writer.key("base");
      target.write(writer);
      writer.key("key");
      key.write(writer);
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
    return lAccess(
        ids,
        writer -> {
          writer.type("local").recordBegin();
          ids.write(writer);
          writer.recordEnd();
        },
        key);
  }

  public LSubtree lBind(IdManager ids, LSubtree name, LSubtree value) {
    return writer -> {
      writer.type("bind").recordBegin();
      ids.write(writer);
      writer.key("name");
      name.write(writer);
      writer.key("value");
      value.write(writer);
      writer.recordEnd();
    };
  }

  public LSubtree lType(IdManager ids, Class klass) {
    if (klass == this.klass) {
      return lLocal(ids, lString(ids, "type"));
    } else if (klass.isArray()) {
      return lCall(
          ids,
          lAccess(ids, lLocal(ids, lString(ids, "jvm")), lString(ids, "array")),
          lType(ids, klass.getComponentType()));
    } else if (klass == void.class) {
      return lAccess(ids, lBuiltin(ids), lString(ids, "void"));
    } else if (klass == int.class
        || klass == short.class
        || klass == long.class
        || klass == byte.class
        || klass == float.class
        || klass == double.class
        || klass == boolean.class
        || klass == char.class) {
      return lAccess(ids, lLocal(ids, lString(ids, "jvm")), lString(ids, klass.getName()));
    } else {
      return lCall(
          ids,
          lAccess(ids, lBuiltin(ids), lString(ids, "module_local")),
          lString(ids, path.relativize(new GenerateClass(klass).path).toString()));
    }
  }

  public LSubtree lType(IdManager ids, Parameter[] params) {
    List<LSubtree> values = new ArrayList<>();
    for (Parameter param : params) {
      values.add(lType(ids, param.getType()));
    }
    return lTuple(ids, values);
  }

  public LSubtree lReturn(LSubtree value) {
    return writer -> {
      writer.type("return").recordBegin().key("value");
      value.write(writer);
      writer.recordEnd();
    };
  }

  public LSubtree accessBuilder(IdManager ids) {
    return lAccess(ids, lLocal(ids, lString(ids, "res")), lString(ids, "builder"));
  }

  public void generate() {
    System.out.format("Generating %s\n", path);
    uncheck(
        () -> {
          Files.createDirectories(path.getParent());
          try (OutputStream os = Files.newOutputStream(path)) {
            Writer writer = new Writer(os, (byte) '\t', 1);
            IdManager ids = new IdManager();
            writer.type("alligatoroid:0.0.1").arrayBegin();

            List<ROPair<LSubtree, LSubtree>> out = new ArrayList<>();

            lBind(ids, lString(ids, "jvm"), lAccess(ids, lBuiltin(ids), lString(ids, "jvm")))
                .write(writer);

            lBind(
                    ids,
                    lString(ids, "res"),
                    lCall(
                        ids,
                        lAccess(ids, lLocal(ids, lString(ids, "jvm")), lString(ids, "externClass")),
                        lString(ids, klass.getCanonicalName())))
                .write(writer);
            if (klass.getSuperclass() != null) {
              lCall(
                  ids,
                  lAccess(ids, accessBuilder(ids), lString(ids, "inherit")),
                  lType(ids, klass.getSuperclass()));
            }
            for (Class parent : klass.getInterfaces()) {
              lCall(
                  ids,
                  lAccess(ids, accessBuilder(ids), lString(ids, "inherit")),
                  lType(ids, parent));
            }

            out.add(
                new ROPair<>(
                    lString(ids, "type"),
                    lAccess(ids, lLocal(ids, lString(ids, "res")), lString(ids, "type"))));

            // Instance stuff
            List<Constructor> publicConstructors = new ArrayList<>();
            for (Constructor constructor : klass.getConstructors()) {
              if (!Modifier.isPublic(constructor.getModifiers())) continue;
              publicConstructors.add(constructor);
            }
            if (publicConstructors.size() > 0) {
              lBind(
                      ids,
                      lString(ids, "new"),
                      lCall(
                          ids,
                          lAccess(ids, accessBuilder(ids), lString(ids, "constructor")),
                          lTuple(ids)))
                  .write(writer);
              for (Constructor constructor : publicConstructors) {
                lCall(
                        ids,
                        lAccess(ids, accessBuilder(ids), lString(ids, "declareConstructor")),
                        lRecord(
                            ids,
                            new ROPair<>(
                                lString(ids, "in"), lType(ids, constructor.getParameters()))))
                    .write(writer);
              }
              out.add(new ROPair<>(lString(ids, "new"), lLocal(ids, lString(ids, "new"))));
            }

            for (Method m : klass.getDeclaredMethods()) {
              boolean isPublic = Modifier.isPublic(m.getModifiers());
              boolean isProtected = Modifier.isProtected(m.getModifiers());
              boolean isFinal = Modifier.isFinal(m.getModifiers());
              boolean isStatic = Modifier.isStatic(m.getModifiers());
              if (!(isPublic || isProtected)) continue;
              lCall(
                      ids,
                      lAccess(ids, accessBuilder(ids), lString(ids, "declareMethod")),
                      lTuple(
                          ids,
                          lString(ids, m.getName()),
                          lRecord(
                              ids,
                              new ROPair<>(lString(ids, "in"), lType(ids, m.getParameters())),
                              new ROPair<>(lString(ids, "out"), lType(ids, m.getReturnType())),
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
              if (!(isPublic || isProtected)) continue;
              lCall(
                  ids,
                  lAccess(ids, accessBuilder(ids), lString(ids, "declareData")),
                  lTuple(
                      ids,
                      lString(ids, f.getName()),
                      lRecord(
                          ids,
                          new ROPair<>(lString(ids, "type"), lType(ids, f.getType())),
                          new ROPair<>(lString(ids, "protected"), lBool(ids, isProtected)),
                          new ROPair<>(lString(ids, "final"), lBool(ids, isFinal)),
                          new ROPair<>(lString(ids, "static"), lBool(ids, isStatic)))));
            }

            lReturn(lRecord(ids, out)).write(writer);
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
      writer.key("id").primitive(Integer.toString(nextId++));
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
