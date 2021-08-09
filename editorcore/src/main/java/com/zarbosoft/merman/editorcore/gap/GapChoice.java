package com.zarbosoft.merman.editorcore.gap;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.display.CourseDisplayNode;
import com.zarbosoft.merman.core.display.Text;
import com.zarbosoft.merman.core.display.derived.CourseGroup;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.document.fields.Field;
import com.zarbosoft.merman.core.document.fields.FieldArray;
import com.zarbosoft.merman.core.document.fields.FieldAtom;
import com.zarbosoft.merman.core.document.fields.FieldPrimitive;
import com.zarbosoft.merman.core.syntax.FreeAtomType;
import com.zarbosoft.merman.core.syntax.GapAtomType;
import com.zarbosoft.merman.core.syntax.SuffixGapAtomType;
import com.zarbosoft.merman.core.syntax.back.BackSpecData;
import com.zarbosoft.merman.core.syntax.front.FrontPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.front.FrontSpec;
import com.zarbosoft.merman.core.syntax.front.FrontSymbolSpec;
import com.zarbosoft.merman.core.syntax.primitivepattern.CharacterEvent;
import com.zarbosoft.merman.core.syntax.primitivepattern.ForceEndCharacterEvent;
import com.zarbosoft.merman.core.syntax.symbol.SymbolSpaceSpec;
import com.zarbosoft.merman.core.syntax.symbol.SymbolTextSpec;
import com.zarbosoft.merman.editorcore.Editor;
import com.zarbosoft.merman.editorcore.history.History;
import com.zarbosoft.merman.editorcore.history.changes.ChangePrimitive;
import com.zarbosoft.pidgoon.events.EscapableResult;
import com.zarbosoft.pidgoon.events.Event;
import com.zarbosoft.pidgoon.model.Leaf;
import com.zarbosoft.pidgoon.model.Step;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.DeadCode;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.ReverseIterable;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.function.Consumer;

public class GapChoice extends TwoColumnChoice {
  public final Atom gap;
  public final FreeAtomType type;
  public final int consumePrecedingAtoms;
  public final ROList<EditGapCursorFieldPrimitive.PrepareAtomField> supplyFillAtoms;
  public final int consumeText;
  public final Leaf incompleteKeyParse;
  private final TSList<Event> glyphs;
  private final FrontSpec followingSpec;
  private final ROList<FrontSpec> allKeyFrontSpecs;
  private ROList<ROPair<FieldPrimitive, Boolean>> fields;

  public GapChoice(
      Atom gap,
      FreeAtomType type,
      int consumePrecedingAtoms,
      ROList<EditGapCursorFieldPrimitive.PrepareAtomField> supplyFillAtoms,
      TSList<Event> glyphs,
      int consumeText,
      ROList<ROPair<FieldPrimitive, Boolean>> fields,
      Leaf incompleteKeyParse,
      ROList<FrontSpec> allKeyFrontSpecs,
      FrontSpec followingSpec) {
    this.gap = gap;
    this.type = type;
    this.consumePrecedingAtoms = consumePrecedingAtoms;
    this.supplyFillAtoms = supplyFillAtoms;
    this.glyphs = glyphs;
    this.consumeText = consumeText;
    this.fields = fields;
    this.incompleteKeyParse = incompleteKeyParse;
    this.allKeyFrontSpecs = allKeyFrontSpecs;
    this.followingSpec = followingSpec;
  }

  /**
   * Creates a new primitive from the next primtive spec following id, or the first if id is null
   *
   * @param fields
   * @param allKeyFrontSpecs
   * @param id
   * @return
   */
  private static FieldPrimitive generateNextEmptyPrimitive(
      TSMap<String, Field> fields, ROList<FrontSpec> allKeyFrontSpecs, String id) {
    boolean next = id == null;
    for (FrontSpec spec : allKeyFrontSpecs) {
      if (spec instanceof FrontPrimitiveSpec) {
        if (next) {
          FieldPrimitive generated = new FieldPrimitive(((FrontPrimitiveSpec) spec).field, "");
          fields.put(generated.back.id, generated);
          return generated;
        }
        if (((FrontPrimitiveSpec) spec).fieldId.equals(id)) {
          next = true;
        }
      }
    }
    return null;
  }

  @Override
  public void choose(Editor editor, History.Recorder recorder) {
    Consumer<History.Recorder> apply =
        recorder1 -> {
          TSMap<String, Field> namedFields = new TSMap<>();

          /// Aggregate typed text parsing results into primitive fields
          // + identify last primitive
          FieldPrimitive nextIncompletePrimitive = null;
          ROList<ROPair<FieldPrimitive, Boolean>> preFields;
          if (this.fields != null) {
            preFields = this.fields;
            ROPair<FieldPrimitive, Boolean> lastField = preFields.lastOpt();
            if (lastField != null) nextIncompletePrimitive = lastField.first;
          } else {
            final Step<
                    ROPair<PreGapChoice, EscapableResult<ROList<ROPair<FieldPrimitive, Boolean>>>>>
                nextStep = new Step<>();
            this.incompleteKeyParse.parse(null, nextStep, new ForceEndCharacterEvent());
            preFields = nextStep.completed.get(0).second.value;

            // Here we look really lookingly to find the last incompletely parsed primitive to
            // move the cursor into later.
            ROPair<FieldPrimitive, Boolean> lastRes = preFields.lastOpt();
            if (lastRes != null) {
              if (lastRes.first == null) {
                // Last parsed was symbol
                if (!lastRes.second) {
                  // But the symbol was not finished, so the one before it might be an incomplete
                  // primitive
                  if (preFields.size() >= 2) {
                    nextIncompletePrimitive = preFields.getRev(1).first;
                  }
                } else {
                  // The symbol was finished, so next primitive would be incomplete if it exists
                  // But to do that, need to find the preceding primitive if there was one
                  for (ROPair<FieldPrimitive, Boolean> pair : new ReverseIterable<>(preFields)) {
                    if (pair.first == null)
                      continue; // penultimate was also a symbol (and therefore must have completed)
                    // Preceding was primitive - since most primitives don't have a maximum length
                    // assume it was still in progress.
                    nextIncompletePrimitive = pair.first;
                    break;
                  }
                }
              } else {
                if (lastRes.second) {
                  // Last parsed primtive was completed (walled in by symbol) - find the next
                  // primitive
                  nextIncompletePrimitive =
                      generateNextEmptyPrimitive(
                          namedFields, allKeyFrontSpecs, lastRes.first.back.id);
                } else {
                  // Last parsed primitive was incomplete
                  nextIncompletePrimitive = lastRes.first;
                }
              }
            } else {
              // No primitives reached - find the first primitive and use
              nextIncompletePrimitive =
                  generateNextEmptyPrimitive(namedFields, allKeyFrontSpecs, null);
            }
          }

          // Add parsed primitive fields
          for (ROPair<FieldPrimitive, Boolean> field : preFields) {
            if (field.first == null) continue;
            namedFields.put(field.first.back.id, field.first);
          }

          // Remainder text
          StringBuilder remainderText = new StringBuilder();
          for (Event event : glyphs.subFrom(consumeText)) {
            remainderText.append(((CharacterEvent) event).value);
          }

          /// Aggregate consumed preceding atoms and remove from prefix
          if (consumePrecedingAtoms > 0) {
            FieldArray precedingField =
                (FieldArray) gap.namedFields.get(SuffixGapAtomType.PRECEDING_KEY);
            TSList<Atom> preceding = precedingField.data;
            Editor.arrayChange(
                editor,
                recorder1,
                precedingField,
                preceding.size() - consumePrecedingAtoms,
                consumePrecedingAtoms,
                ROList.empty);
            for (EditGapCursorFieldPrimitive.PrepareAtomField s : supplyFillAtoms) {
              Field field = s.process(editor, recorder1);
              namedFields.put(field.back().id, field);
            }
          }

          /// Create atom
          Field following = null;
          Atom created = new Atom(type);
          for (String fieldId : type.namedFields.keys().difference(namedFields.keys())) {
            Field field = editor.createEmptyField(type.namedFields.get(fieldId));
            namedFields.put(fieldId, field);
            if (followingSpec != null && followingSpec.fieldId().equals(fieldId)) {
              following = field;
            }
          }

          TSList<Field> unnamedFields = new TSList<>();
          for (BackSpecData field : type.unnamedFields) {
            unnamedFields.add(Editor.createEndEmptyField(editor.fileIds, field));
          }

          created.initialSet(unnamedFields, namedFields);

          /// Place and select next focus
          if (remainderText.length() > 0) {
            if (following != null) {
              place(editor, recorder1, created);
              deepSelectInto(editor, recorder1, following);
            } else {
              placeWithSuffixSelect(editor, recorder1, created);
            }
            ((EditGapCursorFieldPrimitive) editor.context.cursor)
                .editHandleTyping(editor, recorder1, remainderText.toString());
          } else if (nextIncompletePrimitive != null) {
            place(editor, recorder1, created);
            nextIncompletePrimitive.selectInto(editor.context);
          } else if (following != null) {
            place(editor, recorder1, created);
            deepSelectInto(editor, recorder1, following);
          } else {
            placeWithSuffixSelect(editor, recorder1, created);
          }
        };
    if (recorder != null) apply.accept(recorder);
    else editor.history.record(editor, null, apply);
  }

  private void deepSelectInto(Editor editor, History.Recorder recorder, Field following) {
    if (following instanceof FieldPrimitive) {
      following.selectInto(editor.context);
    } else if (following instanceof FieldAtom) {
      following.selectInto(editor.context);
      ((FieldAtom) following).data.selectInto(editor.context);
    } else if (following instanceof FieldArray) {
      editor.arrayInsertNewDefault(recorder, (FieldArray) following, 0);
      following.selectInto(editor.context);
    } else throw new Assertion();
  }

  private void placeWithSuffixSelect(Editor editor, History.Recorder recorder, Atom created) {
    FieldArray precedingField =
        (FieldArray) gap.namedFields.getOpt(SuffixGapAtomType.PRECEDING_KEY);
    if (precedingField != null) {
      /// In a suffix gap - reuse, adding to preceding and clearing/selecting text field
      Editor.arrayChange(
          editor, recorder, precedingField, precedingField.data.size(), 0, TSList.of(created));
      FieldPrimitive gapText = (FieldPrimitive) gap.namedFields.get(GapAtomType.PRIMITIVE_KEY);
      recorder.apply(editor, new ChangePrimitive(gapText, 0, gapText.data.length(), ""));
      gapText.selectInto(editor.context);
    } else {
      /// Wrap in a suffix gap and select text
      Atom wrap = editor.createEmptyGap(editor.context.syntax.suffixGap);
      if (gap.fieldParentRef instanceof FieldArray.Parent) {
        FieldArray.Parent parent = (FieldArray.Parent) gap.fieldParentRef;
        Editor.arrayChange(editor, recorder, parent.field, parent.index, 1, TSList.of(wrap));
      } else if (gap.fieldParentRef instanceof FieldAtom.Parent) {
        Editor.atomSet(editor, recorder, ((FieldAtom.Parent) gap.fieldParentRef).field, wrap);
      }
      Editor.arrayChange(
          editor,
          recorder,
          (FieldArray) wrap.namedFields.get(SuffixGapAtomType.PRECEDING_KEY),
          0,
          0,
          TSList.of(created));
      FieldPrimitive wrapText = (FieldPrimitive) wrap.namedFields.get(GapAtomType.PRIMITIVE_KEY);
      wrapText.selectInto(editor.context);
    }
  }

  private void place(Editor editor, History.Recorder recorder, Atom created) {
    FieldArray precedingField =
        (FieldArray) gap.namedFields.getOpt(SuffixGapAtomType.PRECEDING_KEY);
    if (precedingField != null && precedingField.data.some()) {
      /// Reuse current suffix gap - add to preceding and clear text
      Editor.arrayChange(
          editor, recorder, precedingField, precedingField.data.size(), 0, TSList.of(created));
      FieldPrimitive gapText = (FieldPrimitive) gap.namedFields.get(GapAtomType.PRIMITIVE_KEY);
      recorder.apply(editor, new ChangePrimitive(gapText, 0, gapText.data.length(), ""));
    } else {
      /// Replace gap
      if (gap.fieldParentRef instanceof FieldArray.Parent) {
        FieldArray.Parent parent = (FieldArray.Parent) gap.fieldParentRef;
        Editor.arrayChange(editor, recorder, parent.field, parent.index, 1, TSList.of(created));
      } else if (gap.fieldParentRef instanceof FieldAtom.Parent) {
        Editor.atomSet(editor, recorder, ((FieldAtom.Parent) gap.fieldParentRef).field, created);
      }
    }
  }

  @Override
  public ROPair<CourseDisplayNode, CourseDisplayNode> display(Editor editor) {
    final CourseGroup previewLayout = new CourseGroup(editor.context.display.group());
    previewLayout.setPadding(editor.context, editor.choicePreviewPadding);
    TSList<FrontSymbolSpec> spaces = new TSList<>();
    for (final FrontSpec part : allKeyFrontSpecs) {
      final CourseDisplayNode node;
      if (part instanceof FrontSymbolSpec) {
        if (((FrontSymbolSpec) part).type instanceof SymbolSpaceSpec
            || ((FrontSymbolSpec) part).type instanceof SymbolTextSpec
                && ((SymbolTextSpec) ((FrontSymbolSpec) part).type).text.trim().isEmpty()) {
          spaces.add((FrontSymbolSpec) part);
          continue;
        } else {
          node = ((FrontSymbolSpec) part).createDisplay(editor.context);
        }
      } else if (part instanceof FrontPrimitiveSpec) {
        node = editor.gapPlaceholderSymbol.createDisplay(editor.context);
      } else throw new DeadCode();
      for (FrontSymbolSpec space : spaces) {
        previewLayout.add(space.createDisplay(editor.context));
      }
      spaces.clear();
      previewLayout.add(node);
    }

    final Text text = editor.context.display.text();
    text.setBaselineTransverse(0);
    text.setColor(editor.context, editor.choiceDescriptionStyle.color);
    text.setFont(editor.context, Context.getFont(editor.context, editor.choiceDescriptionStyle));
    text.setText(editor.context, type.name());
    CourseGroup textPad = new CourseGroup(editor.context.display.group());
    textPad.setPadding(editor.context, editor.choiceDescriptionStyle.padding);
    textPad.add(text);

    return new ROPair<CourseDisplayNode, CourseDisplayNode>(previewLayout, textPad);
  }
}
