package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeDumpable;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.Format;

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
public abstract class Error implements TreeDumpable {
  public static final PreError moduleError =
      new PreError() {
        @Override
        public String toString() {
          return Format.format("There were errors compiling this module.");
        }
      };
  public final PreError inner;

  protected Error(PreError inner) {
    this.inner = inner;
  }

  @Override
  public String toString() {
    return inner.toString();
  }

  public abstract <T> T dispatch(Dispatcher<T> dispatcher);

  @Override
  public void treeDump(Writer writer) {
    writer.type(getClass().getName()).recordBegin();
    writer.primitive("message").primitive(inner.toString());
    for (Field field : getClass().getFields()) {
      if (Modifier.isStatic(field.getModifiers())) {
          continue;
      }
      if ("inner".equals(field.getName())) {
          continue;
      }
      writer.primitive(field.getName());
      TreeDumpable.treeDump(writer, uncheck(() -> field.get(this)));
    }
    for (Field field : inner.getClass().getFields()) {
      if (Modifier.isStatic(field.getModifiers())) {
          continue;
      }
      writer.primitive(field.getName());
      TreeDumpable.treeDump(writer, uncheck(() -> field.get(inner)));
    }
    writer.recordEnd();
  }

  public interface Dispatcher<T> {
    T handle(LocationError e);

    T handle(DeserializeError e);
  }

  public abstract static class PreError extends RuntimeException implements TreeDumpable {
    public void treeDump(Writer writer) {
      writer.type(this.getClass().getName()).recordBegin();
      writer.primitive("message").primitive(this.toString());
      for (Field field : this.getClass().getFields()) {
        if (Modifier.isStatic(field.getModifiers())) {
            continue;
        }
        writer.primitive(field.getName());
        TreeDumpable.treeDump(writer, uncheck(() -> field.get(this)));
      }
      writer.recordEnd();
    }

    @Override
    public abstract String toString();

    public final Error toError(Location location) {
      return new LocationError(location, this);
    }

    public final Error toError(LuxemPath backPath) {
      return new DeserializeError(backPath, this);
    }
  }

  public static class LocationError extends Error {
    public final Location location;

    public LocationError(Location location, PreError inner) {
      super(inner);
      this.location = location;
    }

    @Override
    public <T> T dispatch(Dispatcher<T> dispatcher) {
      return dispatcher.handle(this);
    }
  }

  public static class DeserializeError extends Error {
    public final LuxemPath backPath;

    public DeserializeError(LuxemPath backPath, PreError inner) {
      super(inner);
      this.backPath = backPath;
    }

    @Override
    public <T> T dispatch(Dispatcher<T> dispatcher) {
      return dispatcher.handle(this);
    }
  }
}
