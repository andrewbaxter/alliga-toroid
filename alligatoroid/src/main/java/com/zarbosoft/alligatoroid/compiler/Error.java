package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.mortar.WholeValue;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.lang.reflect.Field;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public abstract class Error implements TreeSerializable {
  public static final String DESCRIPTION_KEY = "description";
  public static final String LOCATION_KEY = "location";
  public static final String PATH_KEY = "path";

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

  public abstract <T> T dispatch(Dispatcher<T> dispatcher);

  @Override
  public void treeSerialize(Writer writer) {
    writer.type(this.getClass().getName()).recordBegin();
    for (Field field : this.getClass().getFields()) {
      writer.primitive(field.getName());
      TreeSerializable.treeSerialize(writer, uncheck(() -> field.get(this)));
    }
    writer.recordEnd();
  }

  public interface Dispatcher<T> {
    T handle(PreDeserializeError e);

    T handle(LocationError e);

    T handle(DeserializeError e);

    T handle(CacheFileError e);
  }

  public abstract static class PreDeserializeError extends Error {
    @Override
    public <T> T dispatch(Dispatcher<T> dispatcher) {
      return dispatcher.handle(this);
    }

    @Override
    public abstract String toString();
  }

  public abstract static class LocationError extends Error {
    public final Location location;

    public LocationError(Location location) {
      this.location = location;
    }

    @Override
    public <T> T dispatch(Dispatcher<T> dispatcher) {
      return dispatcher.handle(this);
    }

    @Override
    public abstract String toString();
  }

  public abstract static class DeserializeError extends Error {
    public final LuxemPath backPath;

    public DeserializeError(LuxemPath backPath) {
      this.backPath = backPath;
    }

    @Override
    public <T> T dispatch(Dispatcher<T> dispatcher) {
      return dispatcher.handle(this);
    }

    @Override
    public abstract String toString();
  }

  public static class DeserializeNotArray extends DeserializeError {
    public DeserializeNotArray(LuxemPath path) {
      super(path);
    }

    @Override
    public String toString() {
      return "A luxem array is not allowed at this location in the source";
    }
  }

  public static class DeserializeNotRecord extends DeserializeError {
    public DeserializeNotRecord(LuxemPath path) {
      super(path);
    }

    @Override
    public String toString() {
      return "A luxem record is not allowed at this location in the source";
    }
  }

  public static class DeserializeNotPrimitive extends DeserializeError {
    public DeserializeNotPrimitive(LuxemPath path) {
      super(path);
    }

    @Override
    public String toString() {
      return "A luxem primitive is not allowed at this location in the source";
    }
  }

  public static class DeserializeNotTyped extends DeserializeError {
    public DeserializeNotTyped(LuxemPath path) {
      super(path);
    }

    @Override
    public String toString() {
      return "A luxem type is not allowed at this location in the source";
    }
  }

  public static class DeserializeUnknownType extends DeserializeError {
    public final String type;
    public final ROList<String> knownTypes;

    public DeserializeUnknownType(LuxemPath path, String type, ROList<String> knownTypes) {
      super(path);
      this.type = type;
      this.knownTypes = knownTypes;
    }

    @Override
    public String toString() {
      return Format.format("Unknown luxem type [%s]", type);
    }
  }

  public static class DeserializeUnknownField extends DeserializeError {
    public final String type;
    public final String field;
    public final ROList<String> fields;

    public DeserializeUnknownField(
        LuxemPath path, String type, String field, ROList<String> fields) {
      super(path);
      this.type = type;
      this.field = field;
      this.fields = fields;
    }

    @Override
    public String toString() {
      return Format.format("Luxem type [%s] does not have a field named [%s]", type, field);
    }
  }

  public static class DeserializeMissingField extends DeserializeError {
    public final String type;
    public final String field;

    public DeserializeMissingField(LuxemPath path, String type, String field) {
      super(path);
      this.type = type;
      this.field = field;
    }

    @Override
    public String toString() {
      return Format.format(
          "Luxem type [%s] requires a field [%s] but a value was not provided", type, field);
    }
  }

  public static class DeserializeUnknownLanguageVersion extends DeserializeError {
    public final String version;

    public DeserializeUnknownLanguageVersion(LuxemPath path, String version) {
      super(path);
      this.version = version;
    }

    @Override
    public String toString() {
      return Format.format("Language version (luxem root type) %s is unknown", version);
    }
  }

  public static class DeserializeNotBool extends DeserializeError {
    public final String value;

    public DeserializeNotBool(LuxemPath path, String value) {
      super(path);
      this.value = value;
    }

    @Override
    public String toString() {
      return Format.format("Expected a bool (true/false) in luxem but got [%s]", value);
    }
  }

  public static class DeserializeNotInteger extends DeserializeError {
    public final String value;

    public DeserializeNotInteger(LuxemPath path, String value) {
      super(path);
      this.value = value;
    }

    @Override
    public String toString() {
      return Format.format("Expected an integer in luxem but got [%s]", value);
    }
  }

  public static class IncompatibleTargetValues extends LocationError {
    public final String expectedTarget;
    public final String gotTarget;

    public IncompatibleTargetValues(Location location, String expectedTarget, String gotTarget) {
      super(location);
      this.expectedTarget = expectedTarget;
      this.gotTarget = gotTarget;
    }

    @Override
    public String toString() {
      return "ASSERTION! This block contains values for incompatible targets";
    }
  }

  public static class NoField extends LocationError {
    public final WholeValue field;

    public NoField(Location location, WholeValue field) {
      super(location);
      this.field = field;
    }

    @Override
    public String toString() {
      return Format.format("Field [%s] doesn't exist", field.concreteValue());
    }
  }

  public static class Unexpected extends LocationError {
    public final Throwable exception;

    public Unexpected(Location location, Throwable exception) {
      super(location);
      this.exception = exception;
    }

    @Override
    public String toString() {
      return Format.format("An unexpected error occurred while processing: %s", exception);
    }
  }

  public static class PreDeserializeUnexpected extends PreDeserializeError {
    public final Throwable exception;

    public PreDeserializeUnexpected(Throwable exception) {
      this.exception = exception;
    }

    @Override
    public String toString() {
      return Format.format("An unexpected error occurred while processing: %s", exception);
    }
  }

  public static class CacheUnexpected extends CacheFileError {
    public final Throwable exception;

    public CacheUnexpected(String cachePath, Throwable exception) {
      super(cachePath);
      this.exception = exception;
    }

    @Override
    public String toString() {
      return Format.format("An unexpected error occurred while loading cache file: %s", exception);
    }
  }

  public static class DeserializeUnexpected extends DeserializeError {
    public final Throwable exception;

    public DeserializeUnexpected(LuxemPath backPath, Throwable exception) {
      super(backPath);
      this.exception = exception;
    }

    @Override
    public String toString() {
      return Format.format("An unexpected error occurred while deserializing: %s", exception);
    }
  }

  public static class CallNotSupported extends LocationError {
    public CallNotSupported(Location location) {
      super(location);
    }

    @Override
    public String toString() {
      return "This value cannot be called";
    }
  }

  public static class AccessNotSupported extends LocationError {
    public AccessNotSupported(Location location) {
      super(location);
    }

    @Override
    public String toString() {
      return "Fields of this value cannot be accessed";
    }
  }

  public static class BindNotSupported extends LocationError {
    public BindNotSupported(Location location) {
      super(location);
    }

    @Override
    public String toString() {
      return "This value cannot be bound to a variable";
    }
  }

  public static class ValueNotWhole extends LocationError {
    public final Value value;

    public ValueNotWhole(Location location, Value value) {
      super(location);
      this.value = value;
    }

    @Override
    public String toString() {
      return "This value needs to be known completely in phase 1 for use here";
    }
  }

  public static class NotRecordPair extends LocationError {
    public final String gotType;

    public NotRecordPair(Location location, String gotType) {
      super(location);
      this.gotType = gotType;
    }

    @Override
    public String toString() {
      return "This element in a record literal is not a record pair";
    }
  }

  public static class LowerTooDeep extends LocationError {
    public LowerTooDeep(Location location) {
      super(location);
    }

    @Override
    public String toString() {
      return "This lower element isn't in a matching stage element. If multiple stage elements are nested, the number of corresponding nested lower elements can't exceed the number of stage elements.";
    }
  }

  public static class DeserializeMissingSourceFile extends PreDeserializeError {
    @Override
    public String toString() {
      return "The source file was not found";
    }
  }

  public static class DeserializeIncompleteFile extends CacheFileError {
    public DeserializeIncompleteFile(String cachePath) {
      super(cachePath);
    }

    @Override
    public String toString() {
      return "This source file ended before all expected data was read";
    }
  }

  public abstract static class CacheFileError extends Error {
    public final String cachePath;

    public CacheFileError(String cachePath) {
      this.cachePath = cachePath;
    }

    @Override
    public <T> T dispatch(Dispatcher<T> dispatcher) {
      return dispatcher.handle(this);
    }

    @Override
    public abstract String toString();
  }

  public static class CacheLoop extends CacheFileError {
    public CacheLoop(String cachePath) {
      super(cachePath);
    }

    @Override
    public String toString() {
      return "This cache file eventually references itself";
    }
  }

  public static class DeserializePairTooManyValues extends DeserializeError {
    public DeserializePairTooManyValues(LuxemPath backPath) {
      super(backPath);
    }

    @Override
    public String toString() {
      return "This value is a 2-element array, but found more than 2 elements.";
    }
  }

  public static class ImportLoop extends LocationError {
    public final ROList<ImportSpec> loop;

    public ImportLoop(Location location, ROList<ImportSpec> loop) {
      super(location);
      this.loop = loop;
    }

    @Override
    public String toString() {
      return "This import creates an import loop.";
    }
  }

  public static class WrongType extends LocationError {
    public final Value got;
    public final String expected;

    public WrongType(Location location, Value got, String expected) {
      super(location);
      this.got = got;
      this.expected = expected;
    }

    @Override
    public String toString() {
      return Format.format("Expected [%s] but got value [%s]", expected, got);
    }
  }

  public static class RemoteModuleHashMismatch extends PreDeserializeError {
    public final String url;
    public final String wantHash;
    public final String foundHash;

    public RemoteModuleHashMismatch(String url, String wantHash, String foundHash) {
      this.url = url;
      this.wantHash = wantHash;
      this.foundHash = foundHash;
    }

    @Override
    public String toString() {
      return Format.format(
          "Downloaded module at %s has hash %s but expected hash %s", url, foundHash, wantHash);
    }
  }

  public static class UnknownImportFileType extends PreDeserializeError {
    @Override
    public String toString() {
      return "The file type of this module is not recognized";
    }
  }

  public static class ImportOutsideOwningRemoteModule extends LocationError {
    public final String subpath;
    public final RemoteModuleId module;

    public ImportOutsideOwningRemoteModule(
        Location location, String subpath, RemoteModuleId module) {
      super(location);
      this.subpath = subpath;
      this.module = module;
    }

    @Override
    public String toString() {
      return Format.format(
          "Local import of %s within remote submodule %s goes outside the module", subpath, module);
    }
  }

  public abstract static class PreError extends RuntimeException {
    public abstract Error toError(Location location);
  }

  public static class CacheError extends PreDeserializeError {
    public final String path;
    public final ROList<Error> errors;

    public CacheError(String path, ROList<Error> errors) {
      this.path = path;
      this.errors = errors;
    }

    @Override
    public String toString() {
      return Format.format("Errors loading cache object %s", path);
    }
  }
}
