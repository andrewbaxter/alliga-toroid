package com.zarbosoft.merman.core.syntax.front;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.MultiError;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.back.BaseBackPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.style.SplitMode;
import com.zarbosoft.merman.core.visual.Visual;
import com.zarbosoft.merman.core.visual.VisualParent;
import com.zarbosoft.merman.core.visual.visuals.VisualFieldPrimitive;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.TSSet;

public class FrontPrimitiveSpec extends FrontSpec {
  public final String fieldId;
  public final SplitMode splitMode;
  public final String firstAlignmentId;
  public final String firstSplitAlignmentId;
  public final String hardSplitAlignmentId;
  public final String softSplitAlignmentId;
  public final ROMap<String, Object> meta;
  public BaseBackPrimitiveSpec field;

  public FrontPrimitiveSpec(Config config) {
    fieldId = config.fieldId;
    splitMode = config.splitMode;
    firstAlignmentId = config.firstAlignmentId;
    firstSplitAlignmentId = config.firstSplitAlignmentId;
    hardSplitAlignmentId = config.hardSplitAlignmentId;
    softSplitAlignmentId = config.softSplitAlignmentId;
    meta = config.meta;
  }

  @Override
  public Visual createVisual(
      final Context context,
      final VisualParent parent,
      final Atom atom,
      final int visualDepth,
      final int depthScore) {
    return new VisualFieldPrimitive(
        context, parent, this, field.get(atom.namedFields), visualDepth);
  }

  @Override
  public void finish(
      MultiError errors,
      SyntaxPath typePath,
      final AtomType atomType,
      final TSSet<String> middleUsed) {
    middleUsed.add(fieldId);
    this.field = atomType.getDataPrimitive(errors, typePath, fieldId, "front primitive spec");
  }

  @Override
  public String fieldId() {
    return fieldId;
  }

  public static class Config {
    public final String fieldId;
    public SplitMode splitMode = SplitMode.NEVER;
    public String firstAlignmentId;
    public String firstSplitAlignmentId;
    public String hardSplitAlignmentId;
    public String softSplitAlignmentId;
    public ROMap<String, Object> meta = ROMap.empty;

    public Config(String fieldId) {
      this.fieldId = fieldId;
    }

    public Config splitMode(SplitMode splitMode) {
      this.splitMode = splitMode;
      return this;
    }

    public Config firstAlignmentId(String id) {
      firstAlignmentId = id;
      return this;
    }

    public Config splitAlignmentId(String id) {
      firstSplitAlignmentId = id;
      hardSplitAlignmentId = id;
      softSplitAlignmentId = id;
      return this;
    }

    public Config firstSplitAlignmentId(String id) {
      firstSplitAlignmentId = id;
      return this;
    }

    public Config hardSplitAlignmentId(String id) {
      hardSplitAlignmentId = id;
      return this;
    }

    public Config softSplitAlignmentId(String id) {
      softSplitAlignmentId = id;
      return this;
    }

    public Config meta(ROMap<String, Object> meta) {
      this.meta = meta;
      return this;
    }
  }
}
