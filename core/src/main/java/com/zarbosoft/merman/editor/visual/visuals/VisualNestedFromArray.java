package com.zarbosoft.merman.editor.visual.visuals;

import com.google.common.collect.ImmutableList;
import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.document.values.Value;
import com.zarbosoft.merman.document.values.ValueArray;
import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.Path;
import com.zarbosoft.merman.editor.visual.Alignment;
import com.zarbosoft.merman.editor.visual.Visual;
import com.zarbosoft.merman.editor.visual.VisualParent;
import com.zarbosoft.merman.editor.visual.tags.Tag;
import org.pcollections.PSet;

import java.util.List;
import java.util.Map;

public abstract class VisualNestedFromArray extends VisualNestedBase {
  public final ValueArray value;
  private final ValueArray.Listener dataListener;

  public VisualNestedFromArray(
      final Context context,
      final VisualParent parent,
      final ValueArray value,
      final PSet<Tag> tags,
      final Map<String, Alignment> alignments,
      final int visualDepth,
      final int depthScore) {
    super(tags, visualDepth);
    this.value = value;
    dataListener =
        new ValueArray.Listener() {
          @Override
          public void changed(
              final Context context, final int index, final int remove, final List<Atom> add) {
            set(context, add.get(0));
          }
        };
    value.addListener(dataListener);
    value.visual = this;
    root(context, parent, alignments, visualDepth, depthScore);
  }

  @Override
  public void dispatch(VisualNestedDispatcher dispatcher) {
    dispatcher.handle(this);
  }

  @Override
  public Atom atomGet() {
    if (value.data.isEmpty()) return null;
    return value.data.get(0);
  }

  @Override
  protected Value value() {
    return value;
  }

  @Override
  protected Path getBackPath() {
    return value.getSyntaxPath();
  }

  @Override
  public void uproot(final Context context, final Visual root) {
    value.removeListener(dataListener);
    value.visual = null;
    super.uproot(context, root);
  }

  @Override
  public void tagsChanged(final Context context) {}
}
