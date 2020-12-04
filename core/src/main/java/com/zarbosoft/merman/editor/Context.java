package com.zarbosoft.merman.editor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.zarbosoft.luxem.read.InvalidStream;
import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.document.Document;
import com.zarbosoft.merman.document.InvalidDocument;
import com.zarbosoft.merman.document.values.Value;
import com.zarbosoft.merman.editor.banner.Banner;
import com.zarbosoft.merman.editor.details.Details;
import com.zarbosoft.merman.editor.display.Display;
import com.zarbosoft.merman.editor.display.Group;
import com.zarbosoft.merman.editor.hid.HIDEvent;
import com.zarbosoft.merman.editor.history.Change;
import com.zarbosoft.merman.editor.history.History;
import com.zarbosoft.merman.editor.serialization.Load;
import com.zarbosoft.merman.editor.serialization.Write;
import com.zarbosoft.merman.editor.visual.Vector;
import com.zarbosoft.merman.editor.visual.Visual;
import com.zarbosoft.merman.editor.visual.VisualParent;
import com.zarbosoft.merman.editor.visual.tags.FreeTag;
import com.zarbosoft.merman.editor.visual.tags.GlobalTag;
import com.zarbosoft.merman.editor.visual.tags.StateTag;
import com.zarbosoft.merman.editor.visual.tags.Tag;
import com.zarbosoft.merman.editor.visual.tags.TagsChange;
import com.zarbosoft.merman.editor.visual.visuals.VisualAtom;
import com.zarbosoft.merman.editor.wall.Attachment;
import com.zarbosoft.merman.editor.wall.Brick;
import com.zarbosoft.merman.editor.wall.Wall;
import com.zarbosoft.merman.modules.Module;
import com.zarbosoft.merman.syntax.Syntax;
import com.zarbosoft.merman.syntax.front.FrontGapBase;
import com.zarbosoft.merman.syntax.style.Style;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ChainComparator;
import com.zarbosoft.rendaw.common.Pair;
import com.zarbosoft.rendaw.common.WeakCache;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.zarbosoft.rendaw.common.Common.last;

public class Context {
  public final History history;
  /** Contains the cursor and other marks. Scrolls. */
  public final Group overlay;
  /** Contains the source code. Scrolls. */
  public final Wall foreground;
  public final Display display;
  public final Syntax syntax;
  public final Document document;
  private final Set<SelectionListener> selectionListeners = new HashSet<>();
  private final Set<HoverListener> hoverListeners = new HashSet<>();
  private final Set<TagsListener> selectionTagsChangeListeners = new HashSet<>();
  private final Set<TagsListener> globalTagsChangeListeners = new HashSet<>();
  private final Set<ActionChangeListener> actionChangeListeners = new HashSet<>();
  private final Map<Object, List<Action>> actions = new HashMap<>();
  private final Consumer<IterationTask> addIteration;
  private final Consumer<Integer> flushIteration;
  public boolean window;
  public Atom windowAtom;
  public List<Module.State> modules;
  public PSet<Tag> globalTags = HashTreePSet.empty();
  public List<KeyListener> keyListeners = new ArrayList<>();
  public List<GapChoiceListener> gapChoiceListeners = new ArrayList<>();
  public ClipboardEngine clipboardEngine;
  /** Contains banner/details and icons. Doesn't scroll. */
  public Group midground;
  /** Contains source borders. Scrolls. */
  public Group background;
  public Banner banner;
  public Details details;
  public int scroll = 0;
  public int peek = 0;
  public IterationLayBricks idleLayBricks = null;
  public int edge;
  public int transverseEdge;
  public Brick hoverBrick;
  public Hoverable hover;
  public HoverIteration hoverIdle;
  public Selection selection;
  WeakCache<Set<Tag>, Style.Baked> styleCache = new WeakCache<>(v -> v.tags);
  List<ContextIntListener> converseEdgeListeners = new ArrayList<>();
  List<ContextIntListener> transverseEdgeListeners = new ArrayList<>();
  int scrollStart;
  int scrollEnd;
  int scrollStartBeddingBefore;
  int scrollStartBeddingAfter;
  int selectToken = 0;
  boolean keyIgnore = false;
  boolean debugInHover = false;
  private IterationNotifyBricksCreated idleNotifyBricksCreated;

  public Context(
      final Syntax syntax,
      final Document document,
      final Display display,
      final Consumer<IterationTask> addIteration,
      final Consumer<Integer> flushIteration,
      final History history,
      final ClipboardEngine clipboardEngine) {
    actions.put(
        this,
        ImmutableList.of(
            new ActionWindowClear(),
            new ActionWindowUp(document),
            new ActionWindowDown(),
            new ActionScrollNext(),
            new ActionScrollNextAlot(),
            new ActionScrollPrevious(),
            new ActionScrollPreviousAlot(),
            new ActionScrollReset()));
    this.syntax = syntax;
    this.document = document;
    this.display = display;
    display.setBackgroundColor(syntax.background);
    edge = display.edge(this);
    transverseEdge = display.transverseEdge(this);
    background = display.group();
    midground = display.group();
    this.foreground = new Wall(this);
    this.overlay = display.group();
    display.add(background);
    display.add(midground);
    display.add(foreground.visual);
    display.add(overlay);
    this.addIteration = addIteration;
    this.flushIteration = flushIteration;
    banner = new Banner(this);
    details = new Details(this);
    this.history = history;
    history.addModifiedStateListener(
        modified -> {
          final Tag tag = new GlobalTag("modified");
          if (modified) changeGlobalTags(new TagsChange().add(tag));
          else changeGlobalTags(new TagsChange().remove(tag));
        });
    this.clipboardEngine = clipboardEngine;
    display.addConverseEdgeListener(
        (oldValue, newValue) -> {
          edge =
              Math.max(
                  0,
                  newValue - document.syntax.pad.converseStart - document.syntax.pad.converseEnd);
          converseEdgeListeners.forEach(listener -> listener.changed(this, oldValue, newValue));
        });
    display.addTransverseEdgeListener(
        ((oldValue, newValue) -> {
          transverseEdge = newValue;
          scrollVisible();
          transverseEdgeListeners.forEach(listener -> listener.changed(this, oldValue, newValue));
        }));
    display.addHIDEventListener(
        hidEvent -> {
          keyIgnore = false;
          if (!keyListeners.stream().allMatch(l -> l.handleKey(this, hidEvent))) return;
          keyIgnore = true;
          flushIteration(100);
        });
    display.addTypingListener(
        text -> {
          if (keyIgnore) {
            keyIgnore = false;
            return;
          }
          if (text.isEmpty()) return;
          selection.receiveText(this, text);
          flushIteration(100);
        });
    display.addMouseExitListener(
        () -> {
          if (hoverIdle != null) {
            hoverIdle.point = null;
          } else if (hover != null) {
            clearHover();
          }
        });
    display.addMouseMoveListener(
        vector -> {
          if (hoverIdle == null) {
            hoverIdle = new HoverIteration(this);
            addIteration.accept(hoverIdle);
          }
          hoverIdle.point = vector.add(new Vector(-syntax.pad.converseStart, scroll + peek));
        });
    history.addListener(
        new History.Listener() {
          @Override
          public void applied(final Context context, final Change change) {
            if (hoverIdle != null) {
              hoverIdle.destroy();
            }
          }
        });
    foreground.addCornerstoneListener(
        this,
        new Wall.CornerstoneListener() {
          Brick cornerstone = null;
          private final Attachment selectionBrickAttachment =
              new Attachment() {
                @Override
                public void setTransverse(final Context context, final int transverse) {
                  final int oldScrollStart = scrollStart;
                  scrollStart = transverse;
                  scrollEnd += scrollStart - oldScrollStart;
                  scrollVisible();
                }

                @Override
                public void setTransverseSpan(
                    final Context context, final int ascent, final int descent) {
                  scrollEnd = scrollStart + ascent + descent;
                  scrollVisible();
                }

                @Override
                public void destroy(final Context context) {
                  cornerstone = null;
                }
              };

          @Override
          public void cornerstoneChanged(final Context context, final Brick brick) {
            if (cornerstone != null) {
              cornerstone.removeAttachment(context, selectionBrickAttachment);
            }
            this.cornerstone = brick;
            cornerstone.addAttachment(context, selectionBrickAttachment);
          }
        });
    foreground.addBeddingListener(
        this,
        new Wall.BeddingListener() {
          @Override
          public void beddingChanged(
              final Context context, final int beddingBefore, final int beddingAfter) {
            scrollStartBeddingBefore = beddingBefore;
            scrollStartBeddingAfter = beddingAfter;
            scrollVisible();
          }
        });
    if (!syntax.startWindowed) windowClearNoLayBricks();
    else {
      window = true;
      windowAtom = null;
      document.root.createVisual(this, null, ImmutableMap.of(), 0, 0);
      changeGlobalTags(
          new TagsChange(
              ImmutableSet.of(new StateTag("windowed"), new StateTag("root_window")),
              ImmutableSet.of()));
    }
    modules =
        document.syntax.modules.stream().map(p -> p.initialize(this)).collect(Collectors.toList());
    display.addHIDEventListener(
        event -> {
          clearHover();
        });
    document.root.visual.selectDown(this);
    idleLayBricksOutward();
  }

  public void clearHover() {
    if (debugInHover) throw new AssertionError();
    if (hover != null) {
      hover.clear(this);
      hover = null;
    }
    if (hoverIdle != null) {
      hoverIdle.destroy();
    }
    hoverBrick = null;
  }

  private void scrollVisible() {
    final int minimum = scrollStart - scrollStartBeddingBefore - syntax.pad.transverseStart;
    final int maximum = scrollEnd + scrollStartBeddingAfter + syntax.pad.transverseEnd;

    // Change to scroll required to make it match the start of the window that ends at the max
    final int maxDiff = maximum - transverseEdge - scroll;

    Integer newScroll = null;
    if (minimum < scroll) {
      // Minimum is above scroll
      newScroll = minimum;
    } else if (maxDiff > 0) {
      // Maximum is below scroll window
      if (scroll + maxDiff < minimum) {
        // Adjusted scroll doesn't go past the minimum
        newScroll = scroll + maxDiff;
      } else {
        // Adjusted scroll goes past the minimum, so just go to minimum
        newScroll = minimum;
      }
    }
    if (newScroll != null) {
      scroll = newScroll;
      applyScroll();
    }
  }

  public void applyScroll() {
    final int newScroll = scroll + peek;
    foreground.visual.setPosition(
        this, new Vector(syntax.pad.converseStart, -newScroll), syntax.animateCoursePlacement);
    background.setPosition(
        this, new Vector(syntax.pad.converseStart, -newScroll), syntax.animateCoursePlacement);
    overlay.setPosition(
        this, new Vector(syntax.pad.converseStart, -newScroll), syntax.animateCoursePlacement);
    banner.setScroll(this, newScroll);
    details.setScroll(this, newScroll);
  }

  public void flushIteration(final int limit) {
    this.flushIteration.accept(limit);
  }

  public static PSet<Tag> asFreeTags(final Set<String> tags) {
    return HashTreePSet.from(
        tags.stream().map(tag -> new FreeTag(tag)).collect(Collectors.toList()));
  }

  public void addGapChoiceListener(final GapChoiceListener listener) {
    gapChoiceListeners.add(listener);
  }

  public void removeGapChoiceListener(final GapChoiceListener listener) {
    gapChoiceListeners.remove(listener);
  }

  public void addActions(final Object key, final List<Action> actions) {
    this.actions.put(key, actions);
    ImmutableSet.copyOf(actionChangeListeners).forEach(listener -> listener.actionsAdded(this));
  }

  public void removeActions(final Object key) {
    this.actions.remove(key);
    ImmutableSet.copyOf(actionChangeListeners).forEach(listener -> listener.actionsRemoved(this));
  }

  public void addActionChangeListener(final ActionChangeListener listener) {
    actionChangeListeners.add(listener);
  }

  public void removeActionChangeListener(final ActionChangeListener listener) {
    actionChangeListeners.remove(listener);
  }

  public Stream<Action> actions() {
    return actions.entrySet().stream().flatMap(e -> e.getValue().stream());
  }

  public void addConverseEdgeListener(final ContextIntListener listener) {
    converseEdgeListeners.add(listener);
  }

  public void removeConverseEdgeListener(final ContextIntListener listener) {
    converseEdgeListeners.remove(listener);
  }

  public void addTransverseEdgeListener(final ContextIntListener listener) {
    transverseEdgeListeners.add(listener);
  }

  public void removeTransverseEdgeListener(final ContextIntListener listener) {
    transverseEdgeListeners.remove(listener);
  }

  public void copy(final List<Atom> atoms) {
    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    Write.write(atoms, document.syntax, stream);
    clipboardEngine.set(stream.toByteArray());
  }

  public void copy(final String string) {
    clipboardEngine.setString(string);
  }

  public List<Atom> uncopy(final String type) {
    final byte[] bytes = clipboardEngine.get();
    if (bytes == null) return ImmutableList.of();
    try {
      return Load.loadMultiple(syntax, type, new ByteArrayInputStream(bytes));
    } catch (final InvalidStream ignored) {
    } catch (final InvalidDocument ignored) {
    }
    return ImmutableList.of();
  }

  public String uncopyString() {
    return clipboardEngine.getString();
  }

  public Object syntaxLocate(final Path path) {
    Object at = document.root;
    for (int i = 0; i < path.segments.size(); ++i) {
      String segment = path.segments.get(i);
      if (at instanceof Atom) {
        at = ((Atom) at).syntaxLocateStep(segment);
        if (at == null) throw new InvalidPath(path.segments.subList(0, i), path.segments);
      } else if (at instanceof Value) {
        at = ((Value) at).syntaxLocateStep(segment);
        if (at == null) throw new InvalidPath(path.segments.subList(0, i), path.segments);
      } else throw new Assertion();
    }
    return at;
  }

  public void addSelectionListener(final SelectionListener listener) {
    this.selectionListeners.add(listener);
  }

  public void removeSelectionListener(final SelectionListener listener) {
    this.selectionListeners.remove(listener);
  }

  public void addHoverListener(final HoverListener listener) {
    this.hoverListeners.add(listener);
  }

  public void removeHoverListener(final HoverListener listener) {
    this.hoverListeners.remove(listener);
  }

  public void addSelectionTagsChangeListener(final TagsListener listener) {
    this.selectionTagsChangeListeners.add(listener);
  }

  public void removeSelectionTagsChangeListener(final TagsListener listener) {
    this.selectionTagsChangeListeners.remove(listener);
  }

  public void addGlobalTagsChangeListener(final TagsListener listener) {
    this.globalTagsChangeListeners.add(listener);
  }

  public void removeGlobalTagsChangeListener(final TagsListener listener) {
    this.globalTagsChangeListeners.remove(listener);
  }

  public void addKeyListener(final KeyListener listener) {
    this.keyListeners.add(listener);
  }

  public void removeKeyListener(final KeyListener listener) {
    this.keyListeners.remove(listener);
  }

  public void idleLayBricks(
      final VisualParent parent,
      final int index,
      final int addCount,
      final int size,
      final Function<Integer, Brick> accessFirst,
      final Function<Integer, Brick> accessLast,
      final Function<Integer, Brick> create) {
    if (size == 0) throw new AssertionError();
    if (index > 0) {
      final Brick previousBrick = accessLast.apply(index - 1);
      if (previousBrick != null) {
        idleLayBricksAfterEnd(previousBrick);
        return;
      }
      if (index + addCount < size) {
        // Hits neither edge
        final Brick nextBrick = accessFirst.apply(index + addCount);
        if (nextBrick == null) return;
        idleLayBricksBeforeStart(nextBrick);
      } else {
        // Hits end edge
        if (parent == null) return;
        final Brick nextBrick = parent.getNextBrick(this);
        if (nextBrick == null) return;
        idleLayBricksBeforeStart(nextBrick);
      }
    } else {
      if (index + addCount < size) {
        // Hits index edge
        final Brick nextBrick = accessFirst.apply(index + addCount);
        if (nextBrick != null) {
          idleLayBricksBeforeStart(nextBrick);
          return;
        }
        final Brick previousBrick = parent.getPreviousBrick(this);
        if (previousBrick == null) return;
        idleLayBricksAfterEnd(previousBrick);
      } else {
        // Hits both edges
        if (parent == null) return;
        final Brick previousBrick = parent.getPreviousBrick(this);
        if (previousBrick != null) {
          idleLayBricksAfterEnd(previousBrick);
          return;
        }
        final Brick nextBrick = parent.getNextBrick(this);
        if (nextBrick == null) return;
        idleLayBricksBeforeStart(nextBrick);
      }
    }
  }

  public void idleLayBricksAfterEnd(final Brick end) {
    if (idleLayBricks == null) {
      idleLayBricks = new IterationLayBricks();
      addIteration(idleLayBricks);
    }
    idleLayBricks.ends.add(end);
  }

  public void addIteration(final IterationTask task) {
    this.addIteration.accept(task);
  }

  public void idleLayBricksBeforeStart(final Brick start) {
    if (idleLayBricks == null) {
      idleLayBricks = new IterationLayBricks();
      addIteration(idleLayBricks);
    }
    idleLayBricks.starts.add(start);
  }

  public void bricksCreated(final Visual visual, final Brick brick) {
    final ArrayList<Brick> out = new ArrayList<>();
    out.add(brick);
    bricksCreated(visual, out);
  }

  public void bricksCreated(final Visual visual, final ArrayList<Brick> bricks) {
    if (idleNotifyBricksCreated == null) {
      idleNotifyBricksCreated = new IterationNotifyBricksCreated();
    }
    idleNotifyBricksCreated.newBricks.add(new Pair<>(visual, bricks));
  }

  public void createWindowForSelection(final Value value, final int depthThreshold) {
    final Visual oldWindow = windowAtom == null ? document.root.visual : windowAtom.visual;
    Visual windowVisual = null;

    // Try just going up
    if (windowAtom != null) {
      Value at = windowAtom.parent.value();
      while (true) {
        if (at == value) {
          windowAtom = at.parent.atom();
          windowVisual = windowAtom.createVisual(this, null, ImmutableMap.of(), 0, 0);
        }
        final Atom atom = at.parent.atom();
        if (atom.parent == null) break;
        at = atom.parent.value();
      }
    }

    // Otherwise go up from the selection to find the highest parent where this is still visible
    if (windowVisual == null) {
      windowAtom = value.parent.atom();
      int depth = 0;
      while (true) {
        depth += windowAtom.type.depthScore();
        if (depth >= depthThreshold) break;
        if (windowAtom.parent == null) break;
        windowAtom = windowAtom.parent.value().parent.atom();
      }

      if (depth < depthThreshold) {
        windowAtom = null;
        windowVisual = document.root.createVisual(this, null, ImmutableMap.of(), 0, 0);
      } else {
        windowVisual = windowAtom.createVisual(this, null, ImmutableMap.of(), 0, 0);
      }
    }

    if (!overlapsWindow(oldWindow)) oldWindow.uproot(this, windowVisual);
  }

  private boolean overlapsWindow(final Visual visual) {
    final Visual stop = windowAtom == null ? document.root.visual : windowAtom.visual;
    Visual at = visual;
    while (true) {
      if (at == stop) return true;
      if (at.parent() == null) break;
      at = at.parent().visual();
    }
    return false;
  }

  public void setAtomWindow(final Atom atom) {
    TagsChange tagsChange = new TagsChange();
    if (!window) {
      window = true;
      tagsChange = tagsChange.add(new StateTag("windowed"));
    }
    if (windowAtom == null) tagsChange = tagsChange.remove(new StateTag("root_window"));
    final Visual oldWindow = windowAtom == null ? document.root.visual : windowAtom.visual;
    windowAtom = atom;
    final Visual windowVisual = atom.createVisual(this, null, ImmutableMap.of(), 0, 0);
    if (!overlapsWindow(oldWindow)) oldWindow.uproot(this, windowVisual);
    if (!tagsChange.add.isEmpty() || !tagsChange.remove.isEmpty()) changeGlobalTags(tagsChange);
    idleLayBricksOutward();
  }

  public void changeGlobalTags(final TagsChange change) {
    globalTags = change.apply(globalTags);
    banner.tagsChanged(this);
    details.tagsChanged(this);
    ImmutableList.copyOf(globalTagsChangeListeners).forEach(listener -> listener.tagsChanged(this));
  }

  private void idleLayBricksOutward() {
    idleLayBricksBeforeStart(foreground.children.get(0).children.get(0));
    idleLayBricksAfterEnd(last(last(foreground.children).children));
  }

  public void clearSelection() {
    selection.clear(this);
    selection = null;
  }

  public void setSelection(final Selection selection) {
    final int localToken = ++selectToken;
    final Selection oldSelection = this.selection;
    this.selection = selection;

    if (oldSelection != null) {
      oldSelection.clear(this);
    }

    if (localToken != selectToken) return;

    ImmutableSet.copyOf(selectionListeners).forEach(l -> l.selectionChanged(this, selection));
    selectionTagsChanged();
  }

  public void selectionTagsChanged() {
    if (selection == null) return;
    banner.tagsChanged(this);
    details.tagsChanged(this);
    ImmutableList.copyOf(selectionTagsChangeListeners)
        .forEach(listener -> listener.tagsChanged(this));
  }

  public Style.Baked getStyle(final Set<Tag> tags) {
    return styleCache.getOrCreate(
        tags,
        tags1 -> {
          final Style.Baked out = new Style.Baked(tags);
          for (final Style style : syntax.styles) {
            if (!tags.containsAll(style.with) || !Sets.intersection(tags, style.without).isEmpty())
              continue;
            out.merge(style);
          }
          return out;
        });
  }

  public void windowClear() {
    windowClearNoLayBricks();
    idleLayBricksOutward();
  }

  public void windowClearNoLayBricks() {
    window = false;
    windowAtom = null;
    document.root.createVisual(this, null, ImmutableMap.of(), 0, 0);
    changeGlobalTags(
        new TagsChange(
            ImmutableSet.of(),
            ImmutableSet.of(new StateTag("windowed"), new StateTag("root_window"))));
  }
  public static interface GapChoiceListener {
    void changed(Context context, List<? extends FrontGapBase.Choice> choices);
  }

  public static interface ContextIntListener {
    void changed(Context context, int oldValue, int newValue);
  }

  public static interface ActionChangeListener {
    void actionsAdded(Context context);

    void actionsRemoved(Context context);
  }

  @FunctionalInterface
  public interface KeyListener {
    boolean handleKey(Context context, HIDEvent event);
  }

  public abstract static class SelectionListener {

    public abstract void selectionChanged(Context context, Selection selection);
  }

  public abstract static class HoverListener {

    public abstract void hoverChanged(Context context, Hoverable selection);
  }

  public abstract static class TagsListener {

    public abstract void tagsChanged(Context context);
  }

  private abstract static class ActionBase extends Action {
    public static String group() {
      return "editor";
    }
  }

  public class IterationLayBricks extends IterationTask {
    public Set<Brick> ends = new HashSet<>();
    public Set<Brick> starts = new HashSet<>();

    @Override
    protected double priority() {
      return P.layBricks;
    }

    @Override
    public boolean runImplementation(final IterationContext iterationContext) {
      for (int i = 0; i < syntax.layBrickBatchSize; ++i) {
        if (ends.isEmpty() && starts.isEmpty()) {
          return false;
        }
        if (!ends.isEmpty()) {
          final Brick next = ends.iterator().next();
          ends.remove(next);
          if (next.parent != null) {
            final Brick created = next.createNext(Context.this);
            if (created != null) {
              next.addAfter(Context.this, created);
              ends.add(created);
            }
          }
        }
        if (!starts.isEmpty()) {
          final Brick previous = starts.iterator().next();
          starts.remove(previous);
          if (previous.parent != null) {
            final Brick created = previous.createPrevious(Context.this);
            if (created != null) {
              previous.addBefore(Context.this, created);
              starts.add(created);
            }
          }
        }
      }
      if (idleNotifyBricksCreated != null) {
        idleNotifyBricksCreated.run(iterationContext);
      }
      return true;
    }

    @Override
    protected void destroyed() {
      idleLayBricks = null;
    }
  }

  private class IterationNotifyBricksCreated extends IterationTask {
    private final Queue<Pair<Visual, ArrayList<Brick>>> newBricks =
        new PriorityQueue<>(
            11,
            new ChainComparator<Pair<Visual, ArrayList<Brick>>>()
                .greaterFirst(pair -> pair.first.visualDepth)
                .build());

    @Override
    protected double priority() {
      // After laying bricks.  May be prematurely flushed after a brick laying batch.
      return P.notifyBricks;
    }

    @Override
    protected boolean runImplementation(final IterationContext iterationContext) {
      final List<Pair<Visual, ArrayList<Brick>>> level = new ArrayList<>();
      while (!newBricks.isEmpty()) {
        // Find all bricks at next highest depth
        level.clear();
        Integer levelDepth = null;
        while (!newBricks.isEmpty()) {
          final Pair<Visual, ArrayList<Brick>> next = newBricks.poll();
          final int depth = next.first.visualDepth;
          if (levelDepth == null) {
            levelDepth = depth;
          } else if (depth == levelDepth) {

          } else {
            newBricks.add(next);
            break;
          }
        }

        // Group by visual and pass to bricksCreated in that visual
        level.stream()
            .collect(
                Collectors.groupingBy(
                    pair -> pair.first,
                    Collectors.reducing(
                        null,
                        pair -> pair.second,
                        (a, b) -> {
                          a.addAll(b);
                          return a;
                        })))
            .entrySet()
            .stream()
            .forEach(
                entry -> {
                  final Visual visual = entry.getKey();
                  if (visual.parent() == null) return;
                  visual.parent().bricksCreated(Context.this, entry.getValue());
                });
      }
      return false;
    }

    @Override
    protected void destroyed() {
      idleNotifyBricksCreated = null;
    }
  }

  public class HoverIteration extends IterationTask {
    public Vector point = null;
    Context context;
    Brick at;

    public HoverIteration(final Context context) {
      this.context = context;
      at = hoverBrick == null ? context.foreground.children.get(0).children.get(0) : hoverBrick;
    }

    @Override
    protected double priority() {
      return 500;
    }

    @Override
    public boolean runImplementation(final IterationContext iterationContext) {
      debugInHover = true;
      if (at == null || at.parent == null) {
        debugInHover = false;
        return false;
      }
      if (point == null) {
        hoverBrick = null;
        debugInHover = false;
        return false;
      }
      if (point.transverse < at.parent.transverseStart && at.parent.index > 0) {
        at = context.foreground.children.get(at.parent.index - 1).children.get(0);
      } else if (point.transverse > at.parent.transverseEdge(context)
          && at.parent.index < foreground.children.size() - 1) {
        at = context.foreground.children.get(at.parent.index + 1).children.get(0);
      } else {
        while (point.converse < at.getConverse(context) && at.index > 0) {
          at = at.parent.children.get(at.index - 1);
        }
        while (point.converse >= at.converseEdge(context)
            && at.index < at.parent.children.size() - 1) {
          at = at.parent.children.get(at.index + 1);
        }
        final Hoverable old = hover;
        hover = at.hover(context, point);
        if (hover != old) {
          if (old != null) old.clear(context);
          ImmutableSet.copyOf(hoverListeners).forEach(l -> l.hoverChanged(context, hover));
        }
        hoverBrick = at;
        hoverIdle = null;
        debugInHover = false;
        return false;
      }
      debugInHover = false;
      return true;
    }

    @Override
    protected void destroyed() {
      hoverIdle = null;
    }
  }

  @Action.StaticID(id = "window_clear")
  private class ActionWindowClear extends ActionBase {
    @Override
    public boolean run(final Context context) {
      if (!window) return false;
      windowClear();
      return true;
    }
  }

  @Action.StaticID(id = "window_up")
  private class ActionWindowUp extends ActionBase {
    private final Document document;

    public ActionWindowUp(final Document document) {
      this.document = document;
    }

    @Override
    public boolean run(final Context context) {
      if (!window) return false;
      if (windowAtom == null) return false;
      final Atom atom = windowAtom;
      final Visual oldWindowVisual = windowAtom.visual;
      final Visual windowVisual;
      if (atom == document.root) {
        windowAtom = null;
        windowVisual = document.root.createVisual(context, null, ImmutableMap.of(), 0, 0);
      } else {
        windowAtom = atom.parent.value().parent.atom();
        windowVisual = windowAtom.createVisual(context, null, ImmutableMap.of(), 0, 0);
      }
      idleLayBricksOutward();
      return true;
    }
  }

  @Action.StaticID(id = "window_down")
  private class ActionWindowDown extends ActionBase {
    @Override
    public boolean run(final Context context) {
      if (!window) return false;
      final List<VisualAtom> chain = new ArrayList<>();
      final VisualAtom stop = windowAtom.visual;
      if (selection.getVisual().parent() == null) return false;
      VisualAtom at = selection.getVisual().parent().atomVisual();
      while (at != null) {
        if (at == stop) break;
        if (at.parent() == null) break;
        chain.add(at);
        at = at.parent().atomVisual();
      }
      if (chain.isEmpty()) return false;
      final Visual oldWindowVisual = windowAtom.visual;
      final VisualAtom windowVisual = last(chain);
      windowAtom = windowVisual.atom;
      last(chain).root(context, null, ImmutableMap.of(), 0, 0);
      oldWindowVisual.uproot(context, windowVisual);
      idleLayBricksOutward();
      return true;
    }
  }

  @Action.StaticID(id = "scroll_previous")
  private class ActionScrollNext extends ActionBase {

    @Override
    public boolean run(final Context context) {
      scroll -= syntax.scrollFactor * transverseEdge;
      applyScroll();
      return false;
    }
  }

  @Action.StaticID(id = "scroll_previous_alot")
  private class ActionScrollNextAlot extends ActionBase {

    @Override
    public boolean run(final Context context) {
      scroll -= syntax.scrollAlotFactor * transverseEdge;
      applyScroll();
      return false;
    }
  }

  @Action.StaticID(id = "scroll_next")
  private class ActionScrollPrevious extends ActionBase {

    @Override
    public boolean run(final Context context) {
      scroll += syntax.scrollFactor * transverseEdge;
      applyScroll();
      return false;
    }
  }

  @Action.StaticID(id = "scroll_next_alot")
  private class ActionScrollPreviousAlot extends ActionBase {

    @Override
    public boolean run(final Context context) {
      scroll += syntax.scrollAlotFactor * transverseEdge;
      applyScroll();
      return false;
    }
  }

  @Action.StaticID(id = "scroll_reset")
  private class ActionScrollReset extends ActionBase {

    @Override
    public boolean run(final Context context) {
      scrollVisible();
      return false;
    }
  }
}
