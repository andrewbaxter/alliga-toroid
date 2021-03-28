package com.zarbosoft.merman.editorcore.hotkeys;

import com.zarbosoft.merman.editor.Action;
import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.details.DetailsPage;
import com.zarbosoft.merman.editor.display.Group;
import com.zarbosoft.merman.editor.display.Text;
import com.zarbosoft.merman.editor.display.derived.ColumnarTableLayout;
import com.zarbosoft.merman.editor.display.derived.TLayout;
import com.zarbosoft.merman.editor.hid.HIDEvent;
import com.zarbosoft.merman.syntax.style.Style;
import com.zarbosoft.pidgoon.model.Grammar;
import com.zarbosoft.pidgoon.errors.InvalidStream;
import com.zarbosoft.pidgoon.events.ParseBuilder;
import com.zarbosoft.pidgoon.events.ParseEventSink;
import com.zarbosoft.pidgoon.events.StackStore;
import com.zarbosoft.pidgoon.model.Parse;
import com.zarbosoft.pidgoon.nodes.Operator;
import com.zarbosoft.pidgoon.nodes.Sequence;
import com.zarbosoft.pidgoon.nodes.Union;
import com.zarbosoft.rendaw.common.Pair;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROSet;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

public class HotkeysExtension {
  // Settings
  public final TSList<HotkeyRule> rules = new TSList<>();
  public final boolean showDetails;

  // State
  private TSMap<String, ROList<Node>> hotkeys = new TSMap<>();
  private boolean freeTyping = true;
  private Grammar hotkeyGrammar;
  private ParseEventSink<Pair<Integer, Action>> hotkeyParse;
  private String hotkeySequence = "";
  private HotkeyDetails hotkeyDetails = null;

  public HotkeysExtension(final Context context, boolean showDetails) {
    this.showDetails = showDetails;
    context.keyListener = (context1, event) -> handleEvent(context1, event);
    final Context.TagsListener tagsListener = new TagsListener(this);
    context.addSelectionTagsChangeListener(tagsListener);
    context.addGlobalTagsChangeListener(tagsListener);
    context.addActionChangeListener(new ActionChangeListener(this));
    update(context);
  }

  private void update(final Context context) {
    if (context.cursor == null) return;
    final ROSet<String> tags =
        context.getGlobalTags().mut().addAll(context.cursor.getTags(context)).ro();
    clean(context);
    hotkeys = new TSMap<>();
    freeTyping = true;
    for (final HotkeyRule rule : rules) {
      if (!tags.containsAll(rule.with) || !tags.intersect(rule.without).isEmpty()) continue;
      hotkeys.putAll(rule.hotkeys);
      freeTyping = freeTyping && rule.freeTyping;
    }
    hotkeyGrammar = new Grammar();
    final Union union = new Union();
    for (Action action : context.actions()) {
      if (hotkeys.contains(action.id())) {
        for (final Node hotkey : hotkeys.get(action.id())) {
          union.add(
              new Operator<StackStore>(hotkey.build()) {
                @Override
                protected StackStore process(StackStore store) {
                  final Pair<Integer, Action> out = new Pair<>(store.stackTop(), action);
                  store = store.popVarSingle(e -> {});
                  return store.pushStack(out);
                }
              });
        }
      }
    }
    hotkeyGrammar.add(
        Grammar.DEFAULT_ROOT_KEY, new Sequence().add(StackStore.prepVarStack).add(union));
  }

  public boolean handleEvent(final Context context, final HIDEvent event) {
    if (hotkeyParse == null) {
      hotkeyParse = new ParseBuilder<Pair<Integer, Action>>().grammar(hotkeyGrammar).parse();
    }
    if (hotkeySequence.isEmpty()) hotkeySequence += event.toString();
    else hotkeySequence += ", " + event.toString();
    boolean ok = false;
    if (!hotkeyParse.ended())
      try {
        hotkeyParse = hotkeyParse.push(event, hotkeySequence);
        ok = true;
      } catch (final InvalidStream ignored) {
      }
    if (!ok) {
      clean(context);
      return freeTyping ? false : true;
    }
    if (hotkeyParse.ended()) {
      Pair<Integer, Action> best = null;
      for (Pair<Integer, Action> res : hotkeyParse.allResults()) {
        if (best == null || res.first > best.first) {
          best = res;
        }
      }
      final Action action = best.second;
      clean(context);
      action.run(context);
    } else {
      if (showDetails) {
        if (hotkeyDetails != null) context.details.removePage(context, hotkeyDetails);
        hotkeyDetails = new HotkeyDetails(this, context);
        context.details.addPage(context, hotkeyDetails);
      }
    }
    return true;
  }

  private void clean(final Context context) {
    hotkeySequence = "";
    hotkeyParse = null;
    if (hotkeyDetails != null) {
      context.details.removePage(context, hotkeyDetails);
      hotkeyDetails = null;
    }
  }

  private static class TagsListener extends Context.TagsListener {
    private final HotkeysExtension hotkeysExtension;

    public TagsListener(HotkeysExtension hotkeysExtension) {
      this.hotkeysExtension = hotkeysExtension;
    }

    @Override
    public void tagsChanged(final Context context) {
      hotkeysExtension.update(context);
    }
  }

  private static class HotkeyDetails extends DetailsPage {
    public HotkeyDetails(HotkeysExtension hotkeysExtension, final Context context) {
      final Group group = context.display.group();
      this.node = group;
      final TLayout layout = new TLayout(group);

      final Text first = context.display.text();
      final Style firstStyle =
          context.getStyle(
              context
                  .getGlobalTags()
                  .mut()
                  .add(Tags.TAG_PART_DETAILS_PROMPT)
                  .add(Tags.TAG_PART_DETAILS)
                  .ro());
      first.setColor(context, firstStyle.color);
      first.setFont(context, Context.getFont(context, firstStyle));
      first.setText(context, hotkeysExtension.hotkeySequence);
      layout.add(first);

      final Style lineStyle =
          context.getStyle(
              context
                  .getGlobalTags()
                  .mut()
                  .add(Tags.TAG_PART_DETAILS_LINE)
                  .add(Tags.TAG_PART_DETAILS)
                  .ro());
      final ColumnarTableLayout table = new ColumnarTableLayout(context, context.syntax.detailSpan);
      for (final Parse.State leaf : hotkeysExtension.hotkeyParse.context().leaves) {
        final Action action = leaf.color();
        final Text rule = context.display.text();
        rule.setColor(context, lineStyle.color);
        rule.setFont(context, Context.getFont(context, lineStyle));
        rule.setText(context, hotkeysExtension.hotkeyGrammar.getNode(action.id()).toString());
        final Text name = context.display.text();
        name.setColor(context, lineStyle.color);
        name.setFont(context, Context.getFont(context, lineStyle));
        name.setText(context, action.id());
        table.add(TSList.of(rule, name));
      }
      table.layout();
      layout.add(table.group);
      layout.layout();
    }

    @Override
    public void tagsChanged(final Context context) {}
  }

  private static class ActionChangeListener implements Context.ActionChangeListener {
    private final HotkeysExtension hotkeysExtension;

    public ActionChangeListener(HotkeysExtension hotkeysExtension) {
      this.hotkeysExtension = hotkeysExtension;
    }

    @Override
    public void actionsAdded(final Context context) {
      hotkeysExtension.update(context);
    }

    @Override
    public void actionsRemoved(final Context context) {
      hotkeysExtension.update(context);
    }
  }
}
