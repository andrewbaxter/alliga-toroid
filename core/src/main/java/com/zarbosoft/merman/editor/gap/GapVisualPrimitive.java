package com.zarbosoft.merman.editor.gap;

import com.google.common.collect.ImmutableList;
import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.document.values.Value;
import com.zarbosoft.merman.document.values.ValuePrimitive;
import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.visual.VisualParent;
import com.zarbosoft.merman.editor.visual.tags.FreeTag;
import com.zarbosoft.merman.editor.visual.tags.PartTag;
import com.zarbosoft.merman.editor.visual.tags.Tag;
import com.zarbosoft.merman.editor.visual.visuals.VisualPrimitive;
import com.zarbosoft.merman.extensions.gapchoices.TwoColumnChoicePage;
import com.zarbosoft.merman.misc.TSMap;
import com.zarbosoft.merman.syntax.front.FrontGapBase;
import org.pcollections.PSet;

import java.util.stream.Collectors;

public class GapVisualPrimitive extends VisualPrimitive {
  private final TSMap<String, Value> data;
  private final FrontGapBase spec;

  public GapVisualPrimitive(
      final FrontGapBase spec,
      final Context context,
      final VisualParent parent,
      final Atom atom,
      final PSet<Tag> tags,
      final int visualDepth,
      final int depthScore) {
    super(
        context,
        parent,
        spec.dataType.get(atom.fields),
        tags.plus(new PartTag("gap"))
            .plusAll(spec.tags.stream().map(s -> new FreeTag(s)).collect(Collectors.toSet())),
        visualDepth,
        depthScore);
    this.spec = spec;
    this.data = atom.fields;
  }

  @Override
  public PrimitiveCursor createSelection(
      final Context context, final boolean leadFirst, final int beginOffset, final int endOffset) {
    return new GapCursor(context, leadFirst, beginOffset, endOffset);
  }

  public class GapCursor extends PrimitiveCursor {
    private final ValuePrimitive self;
    private final GapCompletionEngine.State engineState;
    private TwoColumnChoicePage choicePage;

    public GapCursor(
        final Context context,
        final boolean leadFirst,
        final int beginOffset,
        final int endOffset) {
      super(context, leadFirst, beginOffset, endOffset);
      self = spec.dataType.get(data);
      engineState = self.createGapEngine();
      updateGap(context);
    }

    public void updateGap(final Context context) {
      engineState.update(context, self.data.toString());
      if (choicePage != null) {
        context.details.removePage(context, choicePage);
        choicePage.destroy(context);
      }
      ImmutableList.copyOf(context.gapChoiceListeners)
          .forEach(listener -> listener.changed(context, engineState.choices()));
      if (!engineState.choices().isEmpty()) {
        choicePage = new TwoColumnChoicePage(context, engineState.choices());
        context.details.addPage(context, choicePage);
      } else {
        if (choicePage != null) {
          context.details.removePage(context, choicePage);
          choicePage.destroy(context);
          choicePage = null;
        }
      }
    }

    @Override
    public void clear(final Context context) {
      super.clear(context);
      spec.deselect(context, self.parent.atom(), self.get());
      if (choicePage != null) {
        context.details.removePage(context, choicePage);
        choicePage.destroy(context);
        choicePage = null;
      }
    }

    @Override
    public void receiveText(final Context context, final String text) {
      super.receiveText(context, text);
      updateGap(context);
    }
  }
}
