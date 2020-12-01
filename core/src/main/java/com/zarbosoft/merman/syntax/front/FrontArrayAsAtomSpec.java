package com.zarbosoft.merman.syntax.front;

import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.visual.Alignment;
import com.zarbosoft.merman.editor.visual.Visual;
import com.zarbosoft.merman.editor.visual.VisualParent;
import com.zarbosoft.merman.editor.visual.tags.FreeTag;
import com.zarbosoft.merman.editor.visual.tags.PartTag;
import com.zarbosoft.merman.editor.visual.tags.Tag;
import com.zarbosoft.merman.editor.visual.visuals.VisualNestedFromArray;
import com.zarbosoft.merman.syntax.AtomType;
import com.zarbosoft.merman.syntax.back.BaseBackArraySpec;
import com.zarbosoft.merman.syntax.symbol.Symbol;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FrontArrayAsAtomSpec extends FrontSpec {

  public String middle;
  private BaseBackArraySpec dataType;

  @Override
  public Visual createVisual(
      final Context context,
      final VisualParent parent,
      final Atom atom,
      final PSet<Tag> tags,
      final Map<String, Alignment> alignments,
      final int visualDepth,
      final int depthScore) {
    return new VisualNestedFromArray(
        context,
        parent,
        dataType.get(atom.fields),
        HashTreePSet.from(tags)
            .plus(new PartTag("nested"))
            .plusAll(this.tags.stream().map(s -> new FreeTag(s)).collect(Collectors.toSet())),
        alignments,
        visualDepth,
        depthScore) {

      @Override
      protected Symbol ellipsis() {
        return null;
      }
    };
  }

  @Override
  public void finish(final AtomType atomType, final Set<String> middleUsed) {
    middleUsed.add(middle);
    dataType = (BaseBackArraySpec) atomType.getDataArray(middle);
  }

  @Override
  public String field() {
    return middle;
  }

  @Override
  public void dispatch(final DispatchHandler handler) {}
}
