package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeDumpable;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.Format;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class WarnUnexpected implements TreeDumpable {
  public final Throwable exception;
  public final String path;

  public WarnUnexpected(String path, Throwable exception) {
    this.exception = exception;
    this.path = path;
  }

  @Override
  public void treeDump(Writer writer) {
    writer.type(this.getClass().getName()).recordBegin();
    writer.primitive("message").primitive(this.toString());
    for (Field field : this.getClass().getFields()) {
      if (Modifier.isStatic(field.getModifiers())) continue;
      writer.primitive(field.getName());
      TreeDumpable.treeDump(writer, uncheck(() -> field.get(this)));
    }
    writer.recordEnd();
  }

  @Override
  public String toString() {
    return Format.format("An unexpected error occurred while processing [%s]: %s", path, exception);
  }
}
