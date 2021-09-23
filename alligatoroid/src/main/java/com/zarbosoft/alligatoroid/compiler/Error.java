package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.mortar.WholeValue;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSSet;

import java.nio.file.Path;

public class Error implements TreeSerializable {
  public static final String DESCRIPTION_KEY = "description";
  public static final String LOCATION_KEY = "location";
  public final ROMap<String, Object> data;
  private final String type;

  public Error(String type, TSMap<String, Object> data) {
    this.type = type;
    this.data = data;
  }

  public static Error deserializeNotArray(LuxemPath path) {
    return new Error(
        "deserialize_not_array",
        new TSMap<String, Object>()
            .put("path", path.toString())
            .put(DESCRIPTION_KEY, "A luxem array is not allowed at this location in the source"));
  }

  public static Error deserializeNotRecord(LuxemPath path) {
    return new Error(
        "deserialize_not_record",
        new TSMap<String, Object>()
            .put("path", path.toString())
            .put(DESCRIPTION_KEY, "A luxem record is not allowed at this location in the source"));
  }

  public static Error deserializeNotPrimitive(LuxemPath path) {
    return new Error(
        "deserialize_not_primitive",
        new TSMap<String, Object>()
            .put("path", path.toString())
            .put(
                DESCRIPTION_KEY,
                "A luxem primitive is not allowed at this location in the source"));
  }

  public static Error deserializeNotTyped(LuxemPath path) {
    return new Error(
        "deserialize_not_typed",
        new TSMap<String, Object>()
            .put("path", path.toString())
            .put(
                DESCRIPTION_KEY,
                "A typed luxem value is not allowed at this location in the source"));
  }

  public static Error deserializeUnknownType(
      LuxemPath path, String type, TSList<String> knownTypes) {
    return new Error(
        "deserialize_unknown_type",
        new TSMap<String, Object>()
            .put("path", path.toString())
            .put("got", type)
            .put("expected", knownTypes)
            .put(DESCRIPTION_KEY, "This luxem type is not known"));
  }

  public static Error deserializeUnknownField(
      LuxemPath path, String type, String field, ROList<String> fields) {
    return new Error(
        "deserialize_unknown_field",
        new TSMap<String, Object>()
            .put("path", path.toString())
            .put("got", field)
            .put("expected", fields)
            .put("type", type)
            .put(
                DESCRIPTION_KEY,
                Format.format("Luxem type %s does not have a field named %s", type, field)));
  }

  public static Error deserializeMissingField(LuxemPath path, String type, String field) {
    return new Error(
        "deserialize_missing_field",
        new TSMap<String, Object>()
            .put("path", path.toString())
            .put("field", field)
            .put("type", type)
            .put(
                DESCRIPTION_KEY,
                Format.format(
                    "%s is required in luxem type %s but a value was not provided", field, type)));
  }

  public static Error deserializeUnknownLanguageVersion(LuxemPath path, String version) {
    return new Error(
        "deserialize_unknown_language_version",
        new TSMap<String, Object>()
            .put("path", path.toString())
            .put("version", version)
            .put(
                DESCRIPTION_KEY,
                Format.format("Language version (luxem root type) %s is unknown", version)));
  }

  public static Error deserializeMissingVersion() {
    return new Error(
        "deserialize_missing_version",
        new TSMap<String, Object>()
            .put(DESCRIPTION_KEY, "The source version (luxem root type) is missing"));
  }

  public static Error deserializeNotBool(LuxemPath path, String value) {
    return new Error(
        "deserialize_not_bool",
        new TSMap<String, Object>()
            .put("path", path.toString())
            .put("got", value)
            .put(
                DESCRIPTION_KEY,
                Format.format("Expected a bool (true/false) in luxem but got [%s]", value)));
  }

  public static Error deserializeNotInteger(LuxemPath path, String value) {
    return new Error(
        "deserialize_not_integer",
        new TSMap<String, Object>()
            .put("path", path.toString())
            .put("got", value)
            .put(
                DESCRIPTION_KEY,
                Format.format("Expected an integer in luxem but got [%s]", value)));
  }

  public static Error incompatibleTargetValues(
      Location location, String expectedTarget, String gotTarget) {
    return new Error(
        "incompatible_target_values",
        new TSMap<String, Object>()
            .put(LOCATION_KEY, location)
            .put("got", gotTarget)
            .put("expected", expectedTarget)
            .put(
                DESCRIPTION_KEY, "ASSERTION! This block contains values for incompatible targets"));
  }

  public static Error noField(Location location, WholeValue field) {
    return new Error(
        "no_field",
        new TSMap<String, Object>()
            .put(LOCATION_KEY, location)
            .put("field", field)
            .put(
                DESCRIPTION_KEY, Format.format("Field [%s] doesn't exist", field.concreteValue())));
  }

  public static TSMap<String, Object> convertThrowable(Throwable e) {
    TSList<Object> stack = new TSList<>();
    for (StackTraceElement element : e.getStackTrace()) {
      stack.add(
          new TSMap<String, Object>()
              .put("class", element.getClassName())
              .put("method", element.getMethodName())
              .put("line", element.getLineNumber()));
    }
    TSMap<String, Object> out =
        new TSMap<String, Object>().put("exception", e.toString()).put("stacktrace", stack);
    if (e.getCause() != null) out.put("cause", convertThrowable(e.getCause()));
    return out;
  }

  public static Error unexpected(ModuleId id, Throwable e) {
    return new Error(
        "unexpected",
        convertThrowable(e)
            .put(
                DESCRIPTION_KEY,
                Format.format(
                    "An unexpected error occurred while processing module %s: %s", id, e)));
  }

  public static Error unexpected(Path path, Throwable e) {
    return new Error(
        "unexpected",
        convertThrowable(e)
            .put(
                DESCRIPTION_KEY,
                Format.format("An unexpected error occurred while deserializing %s: %s", path, e)));
  }

  public static Error callNotSupported(Location location) {
    return new Error(
        "call_not_supported",
        new TSMap<String, Object>()
            .put(LOCATION_KEY, location)
            .put(DESCRIPTION_KEY, "This value cannot be called"));
  }

  public static Error accessNotSupported(Location location) {
    return new Error(
        "access_not_supported",
        new TSMap<String, Object>()
            .put(LOCATION_KEY, location)
            .put(DESCRIPTION_KEY, "Fields of this value cannot be accessed"));
  }

  public static Error bindNotSupported(Location location) {
    return new Error(
        "bind_not_supported",
        new TSMap<String, Object>()
            .put(LOCATION_KEY, location)
            .put(DESCRIPTION_KEY, "This value cannot be bound to a variable"));
  }

  public static Error valueNotWhole(Location location, Value value) {
    return new Error(
        "value_not_known_at_phase_1",
        new TSMap<String, Object>()
            .put(LOCATION_KEY, location)
            .put("value", value.getClass().getCanonicalName())
            .put(
                DESCRIPTION_KEY, "this value needs to be known completely in phase 1 to use here"));
  }

  public static Error methodsNotDefined(TSSet<ROTuple> incompleteMethods) {
    return new Error(
        "methods_not_defined",
        new TSMap<String, Object>()
            .put("methods", TSList.fromSet(incompleteMethods))
            .put(DESCRIPTION_KEY, "these methods were declared but never defined"));
  }

  public static Error notRecordPair(Location location, String gotType) {
    return new Error(
        "record_element_not_record_pair",
        new TSMap<String, Object>()
            .put(LOCATION_KEY, location)
            .put("got", gotType)
            .put("expected", "record pair")
            .put("description", "this element in a record literal is not a record pair"));
  }

  public static Error lowerTooDeep(Location location) {
    return new Error(
        "lower_too_deep",
        new TSMap<String, Object>()
            .put(LOCATION_KEY, location)
            .put("got", "no matching containing stage element")
            .put("expected", "at least one more containing element is a stage")
            .put(
                "description",
                "This lower element isn't in a matching stage element. If multiple stage elements are nested, the number of corresponding nested lower elements can't exceed the number of stage elements."));
  }

  public static Error deserializeCacheSubvalueUnknownType(LuxemPath path, String type) {
    return new Error(
        "deserialize_cache_subvalue_unknown_type",
        new TSMap<String, Object>()
            .put("path", path.toString())
            .put("type", type)
            .put(
                DESCRIPTION_KEY,
                "this subvalue type is not recognized, maybe the cache is corrupt"));
  }

  public static Error deserializeCacheObjectUnknownType(LuxemPath path, String type) {
    return new Error(
        "deserialize_cache_object_unknown_type",
        new TSMap<String, Object>()
            .put("path", path.toString())
            .put("type", type)
            .put(
                DESCRIPTION_KEY, "this object type is not recognized, maybe the cache is corrupt"));
  }

  public static Error deserializeMissingSourceFile(Path path) {
    return new Error(
        "deserialize_missing_source_file",
        new TSMap<String, Object>()
            .put("file", path.toString())
            .put(DESCRIPTION_KEY, "this source file was not found"));
  }

  public static Error deserializeIncompleteFile(Path path) {
    return new Error(
        "deserialize_incomplete_file",
        new TSMap<String, Object>()
            .put("file", path.toString())
            .put(DESCRIPTION_KEY, "this file ended before all expected data was read"));
  }

  public static Error deserializeCacheLoop(Path path) {
    return new Error(
        "deserialize_cache_loop",
        new TSMap<String, Object>()
            .put("file", path.toString())
            .put(DESCRIPTION_KEY, "this cache file eventually references itself"));
  }

  public static Error deserializePairTooManyValues(LuxemPath path) {
    return new Error(
        "deserialize_pair_too_many_values",
        new TSMap<String, Object>()
            .put("path", path.toString())
            .put(
                DESCRIPTION_KEY,
                "This value is a 2-element array, but found more than 2 elements."));
  }

  public static Error importLoop(Location location, ROList<ImportSpec> loop) {
    return new Error(
        "import_loop",
        new TSMap<String, Object>()
            .put(LOCATION_KEY, location)
            .put("loop", loop)
            .put("description", "This import creates an import loop."));
  }

  @Override
  public void serialize(Writer writer) {
    writer.type(type);
    TreeSerializable.serialize(writer, data);
  }

  public static class CacheError extends Error {
    public CacheError(Path path, ROList<Error> errors) {
      super(
          "errors_accessing_cache",
          new TSMap<String, Object>().put("cache_path", path.toString()).put("errors", errors));
    }
  }
}
