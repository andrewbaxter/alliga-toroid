package com.zarbosoft.alligatoroid.compiler.cache;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.ImportSpec;
import com.zarbosoft.alligatoroid.compiler.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.alligatoroid.compiler.TreeSerializable;
import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.deserialize.Deserializer;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMArrayType;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMExternClassType;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMExternConstructor;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMExternStaticField;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMShallowMethodFieldType;
import com.zarbosoft.alligatoroid.compiler.language.Builtin;
import com.zarbosoft.alligatoroid.compiler.languagedeserialize.LanguageDeserializer;
import com.zarbosoft.alligatoroid.compiler.mortar.LooseRecord;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.Tuple;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSSet;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

import static com.zarbosoft.alligatoroid.compiler.deserialize.Deserializer.errorRet;
import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Cache {
  public static final String CACHE_FILENAME_ID = "id.luxem";
  public static final String CACHE_FILENAME_OUTPUT = "output.luxem";
  public static final String CACHE_DIRECTORY_OBJECTS = "objects";
  /**
   * cacheLock
   *
   * <p>Maps mortar-defined classes to cache paths
   */
  public static final ROMap<String, Method> builtinTypeMap;

  public static final String CACHE_OBJECT_TYPE_IMPORT_SPEC = "importSpec";
  public static final String CACHE_OBJECT_TYPE_BUILTIN = "builtin";
  public static final String CACHE_OBJECT_TYPE_OUTPUT = "output";
  public static final String CACHE_SUBVALUE_TYPE_STRING = "string";
  public static final String CACHE_SUBVALUE_TYPE_INT = "int";
  public static final String CACHE_SUBVALUE_TYPE_BOOL = "bool";
  public static final String CACHE_SUBVALUE_TYPE_BUILTIN = "builtin";
  public static final String CACHE_SUBVALUE_TYPE_CACHE = "cache";
  /** Builtin values - values appearing in builtin */
  public static final ROMap<String, Object> builtinMap;

  public static final ROMap<Object, String> builtinMapReverse;
  public static final String CACHE_SUBVALUE_TYPE_NULL = "null";

  static {
    TSMap<String, Method> builtinTypeMap1 = new TSMap<>();
    Class[] builtinTypes = {
      Location.class,
      LocalModuleId.class,
      JVMExternClassType.class,
      JVMExternStaticField.class,
      JVMShallowMethodFieldType.class,
      JVMArrayType.class,
      JVMExternConstructor.class
    };
    for (Class klass : builtinTypes) {
      builtinTypeMap1.put(
          builtinTypeKey(klass), uncheck(() -> klass.getMethod("graphDeserialize", Record.class)));
    }
    for (Class klass : LanguageDeserializer.LANGUAGE) {
      builtinTypeMap1.put(
          builtinTypeKey(klass), uncheck(() -> klass.getMethod("graphDeserialize", Record.class)));
    }
    builtinTypeMap = builtinTypeMap1;

    TSMap<String, Object> builtinMap1 = new TSMap<>();
    TSMap<Object, String> builtinMapReverse1 = new TSMap<>();
    recordBuiltin(builtinMap1, builtinMapReverse1, "", Builtin.builtin);
    builtinMap = builtinMap1;
    builtinMapReverse = builtinMapReverse1;
  }

  public final TSSet<String> seenPaths = new TSSet<>(); // Prevent loops
  /**
   * When new types are generated in mortar, they're registered here with their cache path.
   *
   * <p>Only on deserialize? If new and serializing don't need this lookup
   */
  public final TSMap<String, Class> loadedClasses = new TSMap<>();

  public final Path rootCachePath;
  public final Object cacheDirLock = new Object();
  public final Object cacheLock = new Object();
  /**
   * cacheLock
   *
   * <p>Maps cache paths to deserialized cached objects
   */
  public final TSMap<String, Object> loadedCache = new TSMap<>();
  /**
   * cacheLock
   *
   * <p>Maps de/serialized cacheable objects to cache paths
   */
  public final TSMap<Object, String> loadedCacheReverse = new TSMap<>();

  public Cache(Path rootCachePath) {
    this.rootCachePath = rootCachePath;
  }

  private static void recordBuiltin(
      TSMap<String, Object> builtinMap,
      TSMap<Object, String> builtinMapReverse,
      String path,
      Value value) {
    builtinMap.put(path, value);
    builtinMapReverse.put(value, path);
    if (value instanceof LooseRecord) {
      for (ROPair<Object, EvaluateResult> pair : ((LooseRecord) value).data) {
        recordBuiltin(builtinMap, builtinMapReverse, path + "/" + pair.first, pair.second.value);
      }
    }
  }

  public static String builtinTypeKey(Class klass) {
    return klass.getName();
  }

  public void serializeSubValue(
      TSList<Error> warnings, Path currentModuleCachePath, Writer writer, Object value) {
    String key;
    if (value == null) {
      writer.type(CACHE_SUBVALUE_TYPE_NULL).arrayBegin().arrayEnd();
    } else if (value.getClass() == String.class) {
      writer.type(CACHE_SUBVALUE_TYPE_STRING).primitive((String) value);
    } else if (value.getClass() == Integer.class) {
      writer.type(CACHE_SUBVALUE_TYPE_INT).primitive(((Integer) value).toString());
    } else if (value.getClass() == Boolean.class) {
      writer.type(CACHE_SUBVALUE_TYPE_BOOL).primitive(((Boolean) value) ? "true" : "false");
    } else if (value.getClass() == Record.class) {
      writer.recordBegin();
      for (Map.Entry<Object, Object> e : ((Record) value).data) {
        writer.primitive((String) e.getKey());
        serializeSubValue(warnings, currentModuleCachePath, writer, e.getValue());
      }
      writer.recordEnd();
    } else if (value.getClass() == Tuple.class) {
      writer.recordBegin();
      for (Object e : ((Tuple) value).data) {
        serializeSubValue(warnings, currentModuleCachePath, writer, e);
      }
      writer.recordEnd();
    } else if ((key = builtinMapReverse.getOpt(value)) != null) {
      writer.type(CACHE_SUBVALUE_TYPE_BUILTIN).primitive(key);
    } else {
      synchronized (cacheLock) {
        key = loadedCacheReverse.getOpt(value);
      }
      if (key == null) {
        key = serializeObject(warnings, currentModuleCachePath, value);
      }
      writer.type(CACHE_SUBVALUE_TYPE_CACHE).primitive(key);
    }
  }

  public String serializeObject(TSList<Error> warnings, Path currentModuleCachePath, Object value) {
    return uncheck(
        () -> {
          Path keyPath;
          synchronized (cacheLock) {
            String keyPath0 = loadedCacheReverse.getOpt(value);
            if (keyPath0 != null) {
              return keyPath0;
            }
            keyPath = nextObjectCachePath(currentModuleCachePath, false);
            loadedCache.put(keyPath.toString(), value);
            loadedCacheReverse.put(value, keyPath.toString());
          }
          try (OutputStream stream = Files.newOutputStream(cachePath(keyPath))) {
            Writer writer = new Writer(stream, (byte) ' ', 4);
            if (value.getClass() == ImportSpec.class) {
              writer.type(CACHE_OBJECT_TYPE_IMPORT_SPEC);
              ImportSpec importSpec = (ImportSpec) value;
              Path path = ensureCachePath(warnings, importSpec.hash(), importSpec);
              writer.primitive(path.toString());
            } else {
              String key;
              String builtinKey = builtinTypeKey(value.getClass());
              if (builtinTypeMap.has(builtinKey)) {
                key = builtinKey;
                writer.type(CACHE_OBJECT_TYPE_BUILTIN);
              } else {
                Object definition = value.getClass().getMethod("definition").invoke(null);
                synchronized (cacheLock) {
                  key = loadedCacheReverse.getOpt(definition);
                }
                if (key == null) {
                  key = serializeObject(warnings, currentModuleCachePath, definition);
                }
                writer.type(CACHE_OBJECT_TYPE_OUTPUT);
              }
              writer.arrayBegin();
              writer.primitive(key);
              serializeSubValue(
                  warnings,
                  currentModuleCachePath,
                  writer,
                  ((GraphSerializable) value).graphSerialize());
              writer.arrayEnd();
            }
          }
          return keyPath.toString();
        });
  }

  public Object loadObject(TSList<Error> errors, Path objectRelPath) {
    ObjectRootState typeState = new ObjectRootState(this);
    TSList<Error> subErrors = new TSList<>();
    Path path = cachePath(objectRelPath);
    synchronized (cacheLock) {
      if (seenPaths.contains(path.toString())) {
        errors.add(Error.deserializeCacheLoop(objectRelPath));
        return null;
      }
      seenPaths.add(path.toString());
    }
    Deserializer.deserialize(subErrors, path, new TSList<>(typeState));
    Object out = typeState.build(subErrors);
    if (subErrors.some()) {
      errors.add(new Error.CacheError(objectRelPath, subErrors));
      return null;
    } else {
      return out;
    }
  }

  public void writeOutput(TSList<Error> warnings, Path moduleCacheRelPath, Value value) {
    Path outputPath = cachePath(moduleCacheRelPath).resolve(CACHE_FILENAME_OUTPUT);
    Utils.recursiveDelete(outputPath);
    Utils.recursiveDelete(cachePath(moduleCacheRelPath).resolve(CACHE_DIRECTORY_OBJECTS));
    try (OutputStream stream = Files.newOutputStream(outputPath)) {
      Writer writer = new Writer(stream, (byte) ' ', 4);
      serializeSubValue(warnings, moduleCacheRelPath, writer, value);
    } catch (Throwable e) {
      warnings.add(Error.unexpected(e));
    }
  }

  /**
   * Loads module from cache or else calls inner to rebuild it. Saves modue output to cache if
   * built.
   *
   * @param module
   * @param inner
   * @return
   */
  public Value loadOutput(TSList<Error> warnings, Path moduleCacheRelPath) {
    Path outputPath = cachePath(moduleCacheRelPath).resolve(CACHE_FILENAME_OUTPUT);

    TSList<Error> errors = new TSList<>();
    ValueState valueState = new ValueState(this);
    if (!Files.exists(outputPath)) return ErrorValue.error;
    Deserializer.deserialize(errors, outputPath, new TSList<>(valueState));
    Object out = valueState.build(errors);
    if (errors.some()) {
      warnings.add(new Error.CacheError(moduleCacheRelPath, errors));
      return ErrorValue.error;
    } else if (out == errorRet) {
      return ErrorValue.error;
    } else {
      return (Value) out;
    }
  }

  public Path nextObjectCachePath(Path moduleCachePath, boolean binary) {
    Path objectsDir = moduleCachePath.resolve(CACHE_DIRECTORY_OBJECTS);
    uncheck(() -> Files.createDirectories(cachePath(objectsDir)));
    for (int i = 0; i < Integer.MAX_VALUE; ++i) {
      Path luxemPath = objectsDir.resolve(Format.format("%s.luxem", i));
      if (Files.exists(cachePath(luxemPath))) continue;
      Path binaryPath = objectsDir.resolve(Format.format("%s.bin", i));
      if (Files.exists(cachePath(binaryPath))) continue;
      if (binary) return binaryPath;
      else return luxemPath;
    }
    throw new Assertion();
  }

  public Path cachePath(Path rel) {
    return rootCachePath.resolve("specs").resolve(rel);
  }

  public Path ensureCachePath(TSList<Error> warnings, String hash, TreeSerializable spec) {
    return uncheck(
        () -> {
          synchronized (cacheDirLock) {
            byte[] wantIdBytes;
            {
              ByteArrayOutputStream wantIdBytes1 = new ByteArrayOutputStream();
              Writer writer = new Writer(wantIdBytes1, (byte) ' ', 4);
              spec.treeSerialize(writer);
              wantIdBytes = wantIdBytes1.toByteArray();
            }

            Path tryRelPath = null;
            int cuts = 3;
            for (int i = 0; i < cuts; ++i) {
              String seg = hash.substring(i * 2, (i + 1) * 2);
              if (tryRelPath == null) tryRelPath = Paths.get(seg);
              else tryRelPath = tryRelPath.resolve(seg);
            }
            Path useRelPath = null;
            for (int i = 0; i < 1000; ++i) {
              tryRelPath = tryRelPath.resolve(Format.format("%s-%s", hash.substring(cuts * 2), i));
              TSList<Error> errors1 = new TSList<>();
              Path moduleIdPath = cachePath(tryRelPath).resolve(CACHE_FILENAME_ID);
              byte[] foundIdBytes = Files.readAllBytes(moduleIdPath);
              if (errors1.some()) warnings.add(new Error.CacheError(moduleIdPath, errors1));
              if (errors1.some() || Arrays.equals(foundIdBytes, wantIdBytes)) {
                useRelPath = tryRelPath;
                break;
              }
            }
            if (useRelPath == null)
              throw new Assertion(); // Something's probably wrong with the hashing code if this is
            // reached

            Files.createDirectories(cachePath(useRelPath));
            Files.write(cachePath(useRelPath).resolve(CACHE_FILENAME_ID), wantIdBytes);
            return useRelPath;
          }
        });
  }
}
