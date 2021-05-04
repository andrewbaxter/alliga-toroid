package com.zarbosoft.merman.core.syntax.front;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.document.fields.Field;
import com.zarbosoft.merman.core.document.fields.FieldArray;
import com.zarbosoft.merman.core.document.fields.FieldPrimitive;
import com.zarbosoft.merman.core.visual.condition.ConditionAttachment;
import com.zarbosoft.rendaw.common.Assertion;
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
      return new PrimitiveCondition(invert, (FieldPrimitive) field, is);
    } else if (field instanceof FieldArray) {
      return new ArrayCondition(invert, (FieldArray) field, is);
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
    private final Is is;

    PrimitiveCondition(boolean invert, FieldPrimitive value, Is is) {
      super(invert);
      this.value = value;
      this.is = is;
    }

    @Override
    public void destroy(final Context context) {}

    @Override
    public void changed(Context context, int index, int remove, String add) {
      switch (is) {
        case EMPTY:
          {
            if (value.data.length() == 0) {
              setState(context, true);
            } else setState(context, false);
          }
          break;
        default:
          throw new Assertion();
      }
    }
  }

  private static class ArrayCondition extends ConditionAttachment implements FieldArray.Listener {
    private final FieldArray value;
    private final Is is;

    ArrayCondition(boolean invert, FieldArray value, Is is) {
      super(invert);
      this.value = value;
      this.is = is;
    }

    @Override
    public void destroy(final Context context) {}

    @Override
    public void changed(
        final Context context, final int index, final int remove, final ROList<Atom> add) {
      switch (is) {
        case EMPTY:
          {
            if (((FieldArray) value).data.isEmpty()) {
              setState(context, true);
            } else setState(context, false);
          }
          break;
        default:
          throw new Assertion();
      }
    }
  }
}
