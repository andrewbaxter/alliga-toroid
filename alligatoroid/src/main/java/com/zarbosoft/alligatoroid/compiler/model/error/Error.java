package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeSerializable;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static com.zarbosoft.rendaw.common.Common.uncheck;

/**
 * LocationErrors/Deserialization/other Errors get logged in the module that's being processed (they
 * are tied to a location in the source). While compiling, syntax/logic errors will be gathered with
 * this type. This will also trigger a generic PreError for importers.
 *
 * <p>PreErrors are thrown during module processing and get turned into LocationErrons in the
 * module(s) that imported the processed module, or used as is if in the root module.
 */
public abstract class Error implements TreeSerializable {
  public static final String DESCRIPTION_KEY = "description";
  public static final String LOCATION_KEY = "location";
  public static final String PATH_KEY = "path";
  public static final PreError moduleError =
      new PreError() {
        @Override
        public Error toError(Location location) {
          return new ModuleError(location);
        }
      };

  public abstract <T> T dispatch(Dispatcher<T> dispatcher);

  @Override
  public void treeSerialize(Writer writer) {
    writer.type(this.getClass().getName()).recordBegin();
    for (Field field : this.getClass().getFields()) {
      if (Modifier.isStatic(field.getModifiers())) continue;
      writer.primitive(field.getName());
      TreeSerializable.treeSerialize(writer, uncheck(() -> field.get(this)));
    }
    writer.recordEnd();
  }

  public interface Dispatcher<T> {
    T handle(LocationlessError e);

    T handle(LocationError e);

    T handle(DeserializeError e);
  }

  public abstract static class PreError extends RuntimeException {
    public abstract Error toError(Location location);
  }

  public static class CacheError extends LocationlessError {
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

  public abstract static class PreLocationlessError extends RuntimeException {
    public abstract Error toError();
  }

  public abstract static class LocationlessError extends Error {
    @Override
    public <T> T dispatch(Dispatcher<T> dispatcher) {
      return dispatcher.handle(this);
    }

    @Override
    public abstract String toString();
  }
}
