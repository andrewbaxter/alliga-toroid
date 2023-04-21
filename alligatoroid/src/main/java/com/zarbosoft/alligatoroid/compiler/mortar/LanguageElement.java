package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExporter;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.language.Access;
import com.zarbosoft.alligatoroid.compiler.model.language.Bind;
import com.zarbosoft.alligatoroid.compiler.model.language.Block;
import com.zarbosoft.alligatoroid.compiler.model.language.Builtin;
import com.zarbosoft.alligatoroid.compiler.model.language.Call;
import com.zarbosoft.alligatoroid.compiler.model.language.Label;
import com.zarbosoft.alligatoroid.compiler.model.language.LiteralBool;
import com.zarbosoft.alligatoroid.compiler.model.language.LiteralString;
import com.zarbosoft.alligatoroid.compiler.model.language.Local;
import com.zarbosoft.alligatoroid.compiler.model.language.Lower;
import com.zarbosoft.alligatoroid.compiler.model.language.Record;
import com.zarbosoft.alligatoroid.compiler.model.language.RecordElement;
import com.zarbosoft.alligatoroid.compiler.model.language.Return;
import com.zarbosoft.alligatoroid.compiler.model.language.Scope;
import com.zarbosoft.alligatoroid.compiler.model.language.Stage;
import com.zarbosoft.alligatoroid.compiler.model.language.Tuple;
import com.zarbosoft.rendaw.common.ROList;

public abstract class LanguageElement implements BuiltinAutoExportable {
  public static final Class<? extends LanguageElement>[] SERIAL_UNION =
      new Class[] {
        Access.class,
        Bind.class,
        Block.class,
        Builtin.class,
        Call.class,
        Label.class,
        LiteralBool.class,
        LiteralString.class,
        Local.class,
        Lower.class,
        Record.class,
        RecordElement.class,
        Return.class,
        Scope.class,
        Stage.class,
        Tuple.class,
      };
  @BuiltinAutoExporter.Param public Location id;
  private Boolean hasLowerInSubtree;

  protected static boolean hasLowerInSubtreeList(ROList<?> elements) {
    boolean out = false;
    for (Object element : elements) {
      if (element instanceof LanguageElement) {
        out = out || ((LanguageElement) element).hasLowerInSubtree();
      }
    }
    return out;
  }

  protected static boolean hasLowerInSubtree(Object... elements) {
    boolean out = false;
    for (Object element : elements) {
      if (element instanceof LanguageElement) {
        out = out || ((LanguageElement) element).hasLowerInSubtree();
      }
    }
    return out;
  }

  public final boolean hasLowerInSubtree() {
    if (hasLowerInSubtree != null) {
      return hasLowerInSubtree;
    }
    return hasLowerInSubtree = innerHasLowerInSubtree();
  }

  protected abstract boolean innerHasLowerInSubtree();

  public abstract <V extends Value> EvaluateResult evaluate(EvaluationContext context);

  public Location location() {
    return id;
  }
}
