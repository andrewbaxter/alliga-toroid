package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Scope;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.LeafExportable;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.language.Access;
import com.zarbosoft.alligatoroid.compiler.model.language.Bind;
import com.zarbosoft.alligatoroid.compiler.model.language.Block;
import com.zarbosoft.alligatoroid.compiler.model.language.Builtin;
import com.zarbosoft.alligatoroid.compiler.model.language.Call;
import com.zarbosoft.alligatoroid.compiler.model.language.Import;
import com.zarbosoft.alligatoroid.compiler.model.language.LiteralBool;
import com.zarbosoft.alligatoroid.compiler.model.language.LiteralString;
import com.zarbosoft.alligatoroid.compiler.model.language.Local;
import com.zarbosoft.alligatoroid.compiler.model.language.Lower;
import com.zarbosoft.alligatoroid.compiler.model.language.ModLocal;
import com.zarbosoft.alligatoroid.compiler.model.language.ModRemote;
import com.zarbosoft.alligatoroid.compiler.model.language.Record;
import com.zarbosoft.alligatoroid.compiler.model.language.RecordElement;
import com.zarbosoft.alligatoroid.compiler.model.language.Stage;
import com.zarbosoft.alligatoroid.compiler.model.language.Tuple;
import com.zarbosoft.rendaw.common.ROList;

public abstract class LanguageElement implements AutoBuiltinExportable, LeafExportable {
  public static final Class[] SERIAL_UNION =
      new Class[] {
        Access.class,
        Bind.class,
        Block.class,
        Builtin.class,
        Call.class,
        Import.class,
        LiteralBool.class,
        LiteralString.class,
        Local.class,
        Lower.class,
        ModLocal.class,
        ModRemote.class,
        Record.class,
        RecordElement.class,
        Scope.class,
        Stage.class,
        Tuple.class,
      };
  public final boolean hasLowerInSubtree;
  public Location location;

  public LanguageElement(Location id, boolean hasLowerInSubtree) {
    this.location = id;
    this.hasLowerInSubtree = hasLowerInSubtree;
  }

  protected static boolean hasLowerInSubtreeList(ROList<?> elements) {
    boolean out = false;
    for (Object element : elements) {
      if (element instanceof LanguageElement)
        out = out || ((LanguageElement) element).hasLowerInSubtree;
    }
    return out;
  }

  protected static boolean hasLowerInSubtree(Object... elements) {
    boolean out = false;
    for (Object element : elements) {
      if (element instanceof LanguageElement)
        out = out || ((LanguageElement) element).hasLowerInSubtree;
    }
    return out;
  }

  public abstract EvaluateResult evaluate(EvaluationContext context);

  public Location location() {
    return location;
  }
}
