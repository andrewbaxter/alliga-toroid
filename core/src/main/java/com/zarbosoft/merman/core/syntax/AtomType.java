package com.zarbosoft.merman.core.syntax;

import com.zarbosoft.merman.core.AtomKey;
import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.MultiError;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.document.fields.Field;
import com.zarbosoft.merman.core.document.fields.FieldArray;
import com.zarbosoft.merman.core.document.fields.FieldAtom;
import com.zarbosoft.merman.core.syntax.alignments.AlignmentSpec;
import com.zarbosoft.merman.core.syntax.back.BackArraySpec;
import com.zarbosoft.merman.core.syntax.back.BackAtomSpec;
import com.zarbosoft.merman.core.syntax.back.BackFixedArraySpec;
import com.zarbosoft.merman.core.syntax.back.BackFixedRecordSpec;
import com.zarbosoft.merman.core.syntax.back.BackFixedTypeSpec;
import com.zarbosoft.merman.core.syntax.back.BackIdSpec;
import com.zarbosoft.merman.core.syntax.back.BackPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.back.BackRecordSpec;
import com.zarbosoft.merman.core.syntax.back.BackSpec;
import com.zarbosoft.merman.core.syntax.back.BackSpecData;
import com.zarbosoft.merman.core.syntax.back.BackSubArraySpec;
import com.zarbosoft.merman.core.syntax.back.BackTypeSpec;
import com.zarbosoft.merman.core.syntax.back.BaseBackArraySpec;
import com.zarbosoft.merman.core.syntax.back.BaseBackPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.error.AtomTypeErrors;
import com.zarbosoft.merman.core.syntax.error.BackFieldWrongType;
import com.zarbosoft.merman.core.syntax.error.DuplicateBackId;
import com.zarbosoft.merman.core.syntax.error.MissingBack;
import com.zarbosoft.merman.core.syntax.error.NonexistentDefaultSelection;
import com.zarbosoft.merman.core.syntax.error.UnusedBackData;
import com.zarbosoft.merman.core.syntax.front.FrontArraySpecBase;
import com.zarbosoft.merman.core.syntax.front.FrontAtomSpec;
import com.zarbosoft.merman.core.syntax.front.FrontPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.front.FrontSpec;
import com.zarbosoft.merman.core.syntax.front.FrontSymbolSpec;
import com.zarbosoft.merman.core.syntax.symbol.SymbolSpaceSpec;
import com.zarbosoft.merman.core.syntax.symbol.SymbolTextSpec;
import com.zarbosoft.pidgoon.model.Node;
import com.zarbosoft.pidgoon.nodes.Color;
import com.zarbosoft.pidgoon.nodes.MergeSequence;
import com.zarbosoft.pidgoon.nodes.Operator;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.DeadCode;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSSet;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public abstract class AtomType {
  public final ROList<BackSpecData> unnamedFields;
  public final ROMap<String, BackSpecData> namedFields;
  public final String id;
  public final AtomKey key;
  public final String defaultSelection;
  private final BackSpec back;
  private final ROList<FrontSpec> front;

  public AtomType(Config config) {
    id = config.id;
    key = new AtomKey(this.id);
    back = config.back;
    front = config.front;
    defaultSelection = config.defaultSelection;
    TSList<BackSpecData> unnamedFields = new TSList<>();
    TSMap<String, BackSpecData> namedFields = new TSMap<>();
    MultiError errors = new MultiError();
    BackSpec.walkTypeBack(
        back,
        s -> {
          if (!(s instanceof BackSpecData)) return true;
          BackSpecData s1 = (BackSpecData) s;
          if (s1.id == null) {
            unnamedFields.add(s1);
          } else {
            BackSpecData old = namedFields.putReplace(s1.id, s1);
            if (old != null) {
              errors.add(new DuplicateBackId(s1.id));
            }
            if (s instanceof BaseBackArraySpec) return false;
          }
          return true;
        });
    this.unnamedFields = unnamedFields;
    this.namedFields = namedFields;
    errors.raise();
  }

  private static boolean symbolDelimits(FrontSymbolSpec s) {
    if (s.type instanceof SymbolSpaceSpec) return false;
    if (s.type instanceof SymbolTextSpec) {
      final String text = ((SymbolTextSpec) s.type).text;
      if (text.trim().isEmpty()) return false;
    }
    if (s.condition != null) return false;
    return true;
  }

  /**
   * @param type
   * @param test
   * @param allowed type is allowed to be placed here. Only for sliding suffix gaps.
   * @return
   */
  public static boolean isPrecedent(Atom atom) {
    final Atom parentAtom = atom.fieldParentRef.field.atomParentRef.atom();
    final String id = atom.fieldParentRef.id();

    boolean foreChild = true; // This atom isn't bound by succeeding symbols
    {
      boolean backChild = true; // This atom isn't bound by preceding symbols
      boolean foundField = false;
      for (FrontSpec front : parentAtom.type.front) {
        if (front instanceof FrontSymbolSpec) {
          if (symbolDelimits((FrontSymbolSpec) front)) {
            if (!foundField) backChild = false;
            else foreChild = false;
          }
        } else if (front instanceof FrontPrimitiveSpec) {
          if (!foundField) backChild = false;
          else foreChild = false;
        } else if (front instanceof FrontArraySpecBase) {
          final FrontArraySpecBase arrayFront = (FrontArraySpecBase) front;
          if (!foundField) {
            for (FrontSymbolSpec prefix : arrayFront.prefix) {
              if (!symbolDelimits(prefix)) continue;
              backChild = false;
            }
          }
          if (id.equals(arrayFront.fieldId())) {
            if (((FieldArray.Parent) atom.fieldParentRef).index > 0) backChild = false;
            foundField = true;
            if (((FieldArray.Parent) atom.fieldParentRef).index
                < ((FieldArray) atom.fieldParentRef.field).data.size() - 1) foreChild = false;
          }
          if (foundField) {
            for (FrontSymbolSpec suffix : arrayFront.suffix) {
              if (!symbolDelimits(suffix)) continue;
              foreChild = false;
            }
          }
        } else if (front instanceof FrontAtomSpec) {
          if (id.equals(((FrontAtomSpec) front).fieldId())) foundField = true;
        } else throw new Assertion();
      }
      if (!backChild && !foreChild) return true;
      // thus backChild == !foreChild
    }

    // Precedent if precedence is higher than parent
    if (parentAtom.type.precedence() < atom.type.precedence()) {
      return true;
    }

    // Precedent if precedences equal and matches associativity
    if (parentAtom.type.precedence() == atom.type.precedence()
        && foreChild == parentAtom.type.associateForward()) {
      return true;
    }

    return false;
  }

  public abstract ROMap<String, AlignmentSpec> alignments();

  public abstract int precedence();

  public abstract boolean associateForward();

  public abstract int depthScore();

  public void finish(MultiError errors, final Syntax syntax) {
    MultiError subErrors = new MultiError();
    back.finish(subErrors, syntax, new SyntaxPath("back"));
    back.parent = new AtomBackParent();
    {
      final TSSet<String> fieldsUsedFront = new TSSet<>();
      for (int i = 0; i < front().size(); ++i) {
        FrontSpec e = front().get(i);
        e.finish(
            subErrors, new SyntaxPath("front").add(Integer.toString(i)), this, fieldsUsedFront);
      }
      TSSet<String> missing = new TSSet<>();
      for (Map.Entry<String, BackSpecData> field : namedFields) {
        if (field.getValue() instanceof BackIdSpec) continue;
        missing.add(field.getKey());
      }
      missing.removeAll(fieldsUsedFront);
      if (!missing.isEmpty()) {
        subErrors.add(new UnusedBackData(missing.ro()));
      }
    }
    if (defaultSelection != null && !namedFields.has(defaultSelection)) {
      subErrors.add(new NonexistentDefaultSelection(defaultSelection));
    }
    if (!subErrors.isEmpty()) {
      errors.add(new AtomTypeErrors(this, subErrors));
    }
  }

  public final ROList<FrontSpec> front() {
    return front;
  }

  public final BackSpec back() {
    return back;
  }

  public Node<AtomParseResult> buildBackRule(Environment env, final Syntax syntax) {
    final MergeSequence<FieldParseResult> seq = new MergeSequence<>();
    seq.add(back.buildBackRule(env, syntax));
    return new Color<AtomParseResult>(
        "atom " + id,
        new Operator<ROList<FieldParseResult>, AtomParseResult>(seq) {
          @Override
          protected AtomParseResult process(ROList<FieldParseResult> value) {
            return new AtomParseResult(new Atom(AtomType.this), value);
          }
        });
  }

  public abstract String name();

  public BackSpec getBackPart(final String id) {
    final TSList<Iterator<BackSpec>> stack = new TSList<>();
    stack.add(new TSList<>(back()).iterator());
    while (!stack.isEmpty()) {
      final Iterator<BackSpec> iterator = stack.last();
      if (!iterator.hasNext()) {
        stack.removeLast();
        continue;
      }
      final BackSpec next = iterator.next();
      if (next instanceof BackFixedArraySpec) {
        stack.add(((BackFixedArraySpec) next).elements.iterator());
      } else if (next instanceof BackFixedRecordSpec) {
        stack.add(((BackFixedRecordSpec) next).pairs.iterator());
      } else if (next instanceof BackArraySpec) {
        if (((BackArraySpec) next).id.equals(id)) return next;
      } else if (next instanceof BackSubArraySpec) {
        if (((BackSubArraySpec) next).id.equals(id)) return next;
      } else if (next instanceof BackAtomSpec) {
        if (((BackAtomSpec) next).id.equals(id)) return next;
      } else if (next instanceof BackFixedTypeSpec) {
        stack.add(Arrays.asList(((BackFixedTypeSpec) next).value).iterator());
      } else if (next instanceof BackTypeSpec) {
        if (((BackTypeSpec) next).type.equals(id)) return next;
      } else if (next instanceof BackPrimitiveSpec) {
        if (((BackPrimitiveSpec) next).id.equals(id)) return next;
      } else if (next instanceof BackRecordSpec) {
        if (((BackRecordSpec) next).id.equals(id)) return next;
      }
    }
    throw new DeadCode();
  }

  public BaseBackPrimitiveSpec getDataPrimitive(
      MultiError errors, SyntaxPath typePath, final String key, String forName) {
    BackSpecData found = getBack(errors, typePath, key, forName);
    try {
      return (BaseBackPrimitiveSpec) found;
    } catch (ClassCastException e) {
      errors.add(new BackFieldWrongType(typePath, id, found, "primitive", forName));
      return null;
    }
  }

  public BackSpecData getBack(
      MultiError errors, SyntaxPath typePath, final String id, String forName) {
    final BackSpecData found = namedFields.getOpt(id);
    if (found == null) {
      errors.add(new MissingBack(typePath, id, forName));
      return null;
    }
    return found;
  }

  public BackAtomSpec getDataAtom(
      MultiError errors, SyntaxPath typePath, final String key, String forName) {
    BackSpecData found = getBack(errors, typePath, key, forName);
    try {
      return (BackAtomSpec) found;
    } catch (ClassCastException e) {
      errors.add(new BackFieldWrongType(typePath, id, found, "atom", forName));
      return null;
    }
  }

  public BaseBackArraySpec getDataArray(
      MultiError errors, SyntaxPath typePath, final String key, String forName) {
    BackSpecData found = getBack(errors, typePath, key, forName);
    try {
      return (BaseBackArraySpec) found;
    } catch (ClassCastException e) {
      errors.add(new BackFieldWrongType(typePath, id, found, "array", forName));
      return null;
    }
  }

  public final String id() {
    return id;
  }

  public abstract static class FieldParseResult {
    public final String key;

    protected FieldParseResult(String key) {
      this.key = key;
    }

    public abstract Field field();

    public abstract void finish();
  }

  public static class IdFieldParseResult extends FieldParseResult {
    final Field field;

    public IdFieldParseResult(Field field) {
      super(null);
      this.field = field;
    }

    @Override
    public Field field() {
      return field;
    }

    @Override
    public void finish() {}
  }

  public static class PrimitiveFieldParseResult extends FieldParseResult {
    final Field field;

    public PrimitiveFieldParseResult(String key, Field field) {
      super(key);
      this.field = field;
    }

    @Override
    public Field field() {
      return field;
    }

    @Override
    public void finish() {}
  }

  public static class AtomFieldParseResult extends FieldParseResult {
    public final AtomParseResult data;
    final FieldAtom field;

    public AtomFieldParseResult(String key, FieldAtom field, AtomParseResult data) {
      super(key);
      this.field = field;
      this.data = data;
    }

    @Override
    public Field field() {
      return field;
    }

    @Override
    public void finish() {
      field.initialSet(data.finish());
    }
  }

  public static class ArrayFieldParseResult extends FieldParseResult {
    final FieldArray field;
    final ROList<AtomParseResult> data;

    public ArrayFieldParseResult(String key, FieldArray field, ROList<AtomParseResult> data) {
      super(key);
      this.field = field;
      this.data = data;
    }

    @Override
    public Field field() {
      return field;
    }

    @Override
    public void finish() {
      TSList<Atom> fieldData = new TSList<>();
      for (AtomParseResult element : data) {
        fieldData.add(element.finish());
      }
      field.initialSet(fieldData);
    }
  }

  public static class AtomParseResult {
    public final Atom atom;
    public final ROList<FieldParseResult> fields;

    /**
     * @param atom
     * @param fields FieldParseResult or null if no field parsed for a back element
     */
    public AtomParseResult(Atom atom, ROList<FieldParseResult> fields) {
      this.atom = atom;
      this.fields = fields;
    }

    public Atom finish() {
      TSList<Field> initialUnnamedFields = new TSList<>();
      TSMap<String, Field> initialNamedFields = new TSMap<>();
      for (FieldParseResult field : fields) {
        if (field == null) continue;
        field.finish();
        if (field.key == null) {
          initialUnnamedFields.add(field.field());
        } else {
          initialNamedFields.put(field.key, field.field());
        }
      }
      atom.initialSet(initialUnnamedFields, initialNamedFields);
      return atom;
    }
  }

  public static final class Config {
    public final String id;
    public final BackSpec back;
    public final ROList<FrontSpec> front;
    /**
     * If this has multiple selectable front elements, this is the default selection when selecting
     * in. If not specified, defaults to first one.
     */
    public String defaultSelection;

    public Config(String id, BackSpec back, ROList<FrontSpec> front) {
      this.id = id;
      this.back = back;
      this.front = front;
    }

    public Config defaultSelection(String id) {
      this.defaultSelection = id;
      return this;
    }
  }

  public static class AtomBackParent extends BackSpec.Parent {}
}
