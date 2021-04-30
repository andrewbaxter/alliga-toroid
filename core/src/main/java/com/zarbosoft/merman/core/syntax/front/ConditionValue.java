package com.zarbosoft.merman.core.syntax.front;

import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.document.fields.Field;
import com.zarbosoft.merman.core.document.fields.FieldArray;
import com.zarbosoft.merman.core.document.fields.FieldPrimitive;
import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.visual.condition.ConditionAttachment;
import com.zarbosoft.rendaw.common.DeadCode;
import com.zarbosoft.rendaw.common.ROList;

public class ConditionValue extends ConditionType {
  public final String field;
  public final Is is;

  public ConditionValue(Config config) {
    super(config.invert);
    this.field = config.field;
    this.is = config.is;
  }

  @Override
  public ConditionAttachment create(final Context context, final Atom atom) {
    final Field field = atom.fields.getOpt(this.field);
    if (field instanceof FieldPrimitive) {
      return new PrimitiveCondition(invert, (FieldPrimitive) field);
    } else if (field instanceof FieldArray) {
      return new ArrayCondition(invert, (FieldArray) field);
    } else throw new DeadCode();
  }

  @Override
  protected boolean defaultOnImplementation() {
    if (is == ConditionValue.Is.EMPTY) return false;
    return true;
  }

  public static enum Is {
    EMPTY,
  }

  public static class Config {
    public final String field;
    public final Is is;
    public final boolean invert;

    public Config(String field, Is is, boolean invert) {
      this.field = field;
      this.is = is;
      this.invert = invert;
    }
  }

  private static class PrimitiveCondition extends ConditionAttachment
      implements FieldPrimitive.Listener {
    private final FieldPrimitive value;

    PrimitiveCondition(boolean invert, FieldPrimitive value) {
      super(invert);
      this.value = value;
    }

    @Override
    public void destroy(final Context context) {}

    @Override
    public void changed(Context context, int index, int remove, String add) {
      if (value.data.length() == 0) {
        setState(context, false);
      } else setState(context, true);
    }
  }

  private static class ArrayCondition extends ConditionAttachment implements FieldArray.Listener {
    private final FieldArray value;

    ArrayCondition(boolean invert, FieldArray value) {
      super(invert);
      this.value = value;
    }

    @Override
    public void destroy(final Context context) {}

    @Override
    public void changed(
        final Context context, final int index, final int remove, final ROList<Atom> add) {
      if (((FieldArray) value).data.isEmpty()) {
        setState(context, false);
      } else setState(context, true);
    }
  }
}
