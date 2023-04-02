package com.zarbosoft.merman.core.syntax.front;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.MultiError;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.FreeAtomType;
import com.zarbosoft.merman.core.visual.condition.ConditionAttachment;
import com.zarbosoft.rendaw.common.Assertion;

public class ConditionNode extends ConditionType {
  public final Is is;

  public ConditionNode(Config config) {
    super(config.invert);
    this.is = config.is;
  }

  @Override
  public ConditionAttachment create(final Context context, final Atom atom) {
    final boolean show;
    switch (is) {
      case PRECEDENT:
        {
          if (atom.fieldParentRef == null) {
            show = true;
            break;
          }
          if (!(atom.type instanceof FreeAtomType)) {
            show = true;
            break;
          }
          show = AtomType.isPrecedent(atom);
          break;
        }
      default:
        throw new Assertion();
    }
    final ConditionAttachment condition =
        new ConditionAttachment(invert, show) {
          @Override
          public void destroy(final Context context) {}
        };
    return condition;
  }

  @Override
  protected boolean defaultOnImplementation() {
    if (is == ConditionNode.Is.PRECEDENT && !invert) {
        return false;
    }
    return true;
  }

  @Override
  public void finish(MultiError errors, SyntaxPath typePath, AtomType atomType) {}

  public static enum Is {
    PRECEDENT,
  }

  public static class Config {
    public final Is is;
    public final boolean invert;

    public Config(Is is, boolean invert) {
      this.is = is;
      this.invert = invert;
    }
  }
}
