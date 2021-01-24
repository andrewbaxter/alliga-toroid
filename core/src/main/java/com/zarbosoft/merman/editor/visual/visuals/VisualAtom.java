package com.zarbosoft.merman.editor.visual.visuals;

import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.Hoverable;
import com.zarbosoft.merman.editor.visual.Alignment;
import com.zarbosoft.merman.editor.visual.Vector;
import com.zarbosoft.merman.editor.visual.Visual;
import com.zarbosoft.merman.editor.visual.VisualParent;
import com.zarbosoft.merman.editor.visual.tags.Tags;
import com.zarbosoft.merman.editor.visual.tags.TagsChange;
import com.zarbosoft.merman.editor.wall.Attachment;
import com.zarbosoft.merman.editor.wall.Brick;
import com.zarbosoft.merman.syntax.AtomType;
import com.zarbosoft.merman.syntax.alignments.AlignmentSpec;
import com.zarbosoft.merman.syntax.front.FrontSpec;
import com.zarbosoft.rendaw.common.DeadCode;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VisualAtom extends Visual {
  public final Atom atom;
  private VisualParent parent;
  public int depthScore = 0;
  public boolean compact = false;
  private final TSMap<String, Alignment> alignments = new TSMap<>();
  private final Map<String, Alignment> localAlignments = new HashMap<>();
  public final TSList<Visual> children = new TSList<>();
  private final TSList<Visual> selectable = new TSList<>();
  public Brick firstBrick;
  Attachment firstBrickListener =
      new Attachment() {
        @Override
        public void destroy(final Context context) {
          firstBrick = null;
        }
      };
  public Brick lastBrick;
  Attachment lastBrickListener =
      new Attachment() {
        @Override
        public void destroy(final Context context) {
          lastBrick = null;
        }
      };

  public VisualAtom(
      final Context context,
      final VisualParent parent,
      final Atom atom,
      final ROMap<String, Alignment> alignments,
      final int visualDepth,
      final int depthScore) {
    super(visualDepth);
    this.atom = atom;
    for (final Map.Entry<String, AlignmentSpec> entry : atom.type.alignments()) {
      final Alignment alignment = entry.getValue().create();
      localAlignments.put(entry.getKey(), alignment);
    }
    rootInner(context, parent, alignments, visualDepth, depthScore);
    for (int index = 0; index < atom.type.front().size(); ++index) {
      FrontSpec front = atom.type.front().get(index);
      final Visual visual =
          front.createVisual(
              context,
              front.field() == null
                  ? new ChildParent(index)
                  : new SelectableChildParent(index, selectable.size()),
              atom,
              alignments,
              this.visualDepth + 1,
              this.depthScore);
      children.add(visual);
      if (front.field() != null) selectable.add(visual);
    }
    atom.visual = this;
  }

  @Override
  public VisualParent parent() {
    return parent;
  }

  @Override
  public void tagsChanged(Context context) {
    for (Visual child : children) {
      child.tagsChanged(context);
    }
  }

  public ROMap<String, Alignment> alignments() {
    return alignments;
  }

  public Alignment getAlignment(final String alignment) {
    return alignments.getOpt(alignment);
  }

  @Override
  public boolean selectAnyChild(final Context context) {
    if (selectable.isEmpty()) return false;
    selectable.get(0).selectAnyChild(context);
    return true;
  }

  @Override
  public Brick createOrGetFirstBrick(final Context context) {
    return children.get(0).createOrGetFirstBrick(context);
  }

  @Override
  public Brick createFirstBrick(final Context context) {
    return children.get(0).createFirstBrick(context);
  }

  @Override
  public Brick createLastBrick(final Context context) {
    return children.last().createLastBrick(context);
  }

  @Override
  public Brick getFirstBrick(final Context context) {
    return children.get(0).getFirstBrick(context);
  }

  @Override
  public Brick getLastBrick(final Context context) {
    return children.last().getLastBrick(context);
  }

  public int spacePriority() {
    return -atom.type.precedence();
  }

  @Override
  public void compact(final Context context) {
    children.forEach(c -> c.compact(context));
    boolean wasCompact = compact;
    compact = true;
    if (!wasCompact) tagsChanged(context);
  }

  @Override
  public void expand(final Context context) {
    children.forEach(c -> c.expand(context));
    boolean wasCompact = compact;
    compact = false;
    if (wasCompact) tagsChanged(context);
  }

  @Override
  public void getLeafPropertiesForTagsChange(
          final Context context, TSList<ROPair<Brick, Brick.Properties>> brickProperties, final TagsChange change) {
    for (Visual child : children) {
      child.getLeafPropertiesForTagsChange(context, brickProperties, change);
    }
  }

  private void rootInner(
      final Context context,
      final VisualParent parent,
      final ROMap<String, Alignment> alignments,
      final int visualDepth,
      final int depthScore) {
    compact = false;
    this.parent = parent;
    if (parent == null) {
      this.visualDepth = 0;
      this.depthScore = 0;
    } else {
      this.visualDepth = visualDepth;
      this.depthScore = depthScore + atom.type.depthScore();
    }
    this.alignments.clear();
    this.alignments.putAll(alignments);
    for (final Map.Entry<String, Alignment> alignment : localAlignments.entrySet()) {
      alignment.getValue().root(context, alignments);
      this.alignments.putReplaceNull(alignment.getKey(), alignment.getValue());
    }
  }

  @Override
  public void root(
      final Context context,
      final VisualParent parent,
      final ROMap<String, Alignment> alignments,
      final int visualDepth,
      final int depthScore) {
    rootInner(context, parent, alignments, visualDepth, depthScore);
    for (int index = 0; index < children.size(); ++index) {
      final Visual child = children.get(index);
      child.root(context, child.parent(), this.alignments, this.visualDepth + 1, this.depthScore);
    }
  }

  @Override
  public void uproot(final Context context, final Visual root) {
    if (root == this) return;
    atom.visual = null;
    for (int i = children.size(); i > 0; --i) {
      Visual child = children.get(i - 1);
      child.uproot(context, root);
    }
    for (final Map.Entry<String, Alignment> entry : localAlignments.entrySet())
      entry.getValue().destroy(context);
  }

  public AtomType type() {
    return atom.type;
  }

  public TSSet<String> getTags(Context context) {
    TSSet<String> out = context.getGlobalTags().mut().addAll(atom.getTags()).add(atom.type.id());
    if (compact) out.add(Tags.TAG_COMPACT);
    return out;
  }

  private class ChildParent extends VisualParent {
    private final int index;

    public ChildParent(final int index) {
      this.index = index;
    }

    @Override
    public Visual visual() {
      return VisualAtom.this;
    }

    @Override
    public VisualAtom atomVisual() {
      return VisualAtom.this;
    }

    @Override
    public Brick createNextBrick(final Context context) {
      if (index + 1 < children.size()) return children.get(index + 1).createFirstBrick(context);
      if (parent == null) return null;
      if (context.windowAtom() == VisualAtom.this.atom) return null;
      return parent.createNextBrick(context);
    }

    @Override
    public Brick createPreviousBrick(final Context context) {
      if (index - 1 >= 0) return children.get(index - 1).createLastBrick(context);
      if (parent == null) return null;
      if (context.windowAtom() == VisualAtom.this.atom) return null;
      return parent.createPreviousBrick(context);
    }

    @Override
    public Brick findPreviousBrick(final Context context) {
      for (int at = index - 1; at >= 0; --at) {
        final Brick test = children.get(at).getLastBrick(context);
        if (test != null) return test;
      }
      if (context.windowAtom() == VisualAtom.this.atom) return null;
      if (parent == null) return null;
      return parent.findPreviousBrick(context);
    }

    @Override
    public Brick findNextBrick(final Context context) {
      for (int at = index + 1; at < children.size(); ++at) {
        final Brick test = children.get(at).getLastBrick(context);
        if (test != null) return test;
      }
      if (context.windowAtom() == VisualAtom.this.atom) return null;
      if (parent == null) return null;
      return parent.findNextBrick(context);
    }

    @Override
    public Brick getPreviousBrick(final Context context) {
      if (index == 0) {
        if (context.windowAtom() == VisualAtom.this.atom) return null;
        if (parent == null) return null;
        return parent.getPreviousBrick(context);
      } else return children.get(index - 1).getLastBrick(context);
    }

    @Override
    public Brick getNextBrick(final Context context) {
      if (index + 1 >= children.size()) {
        if (context.windowAtom() == VisualAtom.this.atom) return null;
        if (parent == null) return null;
        return parent.getNextBrick(context);
      } else return children.get(index + 1).getFirstBrick(context);
    }

    @Override
    public Hoverable hover(final Context context, final Vector point) {
      if (parent == null) return null;
      return parent.hover(context, point);
    }

    @Override
    public boolean selectPrevious(final Context context) {
      throw new DeadCode();
    }

    @Override
    public boolean selectNext(final Context context) {
      throw new DeadCode();
    }

    @Override
    public void bricksCreated(final Context context, final ArrayList<Brick> bricks) {
      Brick min = firstBrick;
      Brick max = lastBrick;
      for (final Brick brick : bricks) {
        if (min == null
            || brick.parent.index < min.parent.index
            || brick.parent.index == min.parent.index && brick.index < min.index) {
          min = brick;
        }
        if (max == null
            || brick.parent.index > max.parent.index
            || brick.parent.index == max.parent.index && brick.index > max.index) {
          max = brick;
        }
      }
      final ArrayList<Brick> out = new ArrayList<>();
      if (min != firstBrick) {
        if (firstBrick == null) firstBrick.removeAttachment(firstBrickListener);
        firstBrick = min;
        firstBrick.addAttachment(context, firstBrickListener);
        if (parent != null) parent.firstBrickChanged(context, firstBrick);
        out.add(min);
      }
      if (max != lastBrick) {
        if (lastBrick == null) lastBrick.removeAttachment(lastBrickListener);
        lastBrick = max;
        lastBrick.addAttachment(context, lastBrickListener);
        if (parent != null) parent.lastBrickChanged(context, lastBrick);
        out.add(max);
      }
      context.bricksCreated(VisualAtom.this, out);
    }
  }

  private class SelectableChildParent extends ChildParent {
    private final int selectableIndex;

    public SelectableChildParent(final int index, final int selectableIndex) {
      super(index);
      this.selectableIndex = selectableIndex;
    }

    @Override
    public boolean selectNext(final Context context) {
      int at = selectableIndex;
      while (++at < selectable.size()) if (selectable.get(at).selectAnyChild(context)) return true;
      return false;
    }

    @Override
    public boolean selectPrevious(final Context context) {
      int at = selectableIndex;
      while (--at >= 0) if (selectable.get(at).selectAnyChild(context)) return true;
      return false;
    }
  }
}
