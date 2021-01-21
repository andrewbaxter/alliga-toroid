package com.zarbosoft.merman.syntax.back;

import com.google.common.collect.ImmutableList;
import com.zarbosoft.merman.document.values.ValuePrimitive;
import com.zarbosoft.merman.editor.Path;
import com.zarbosoft.merman.editor.backevents.ETypeEvent;
import com.zarbosoft.merman.editor.serialization.Write;
import com.zarbosoft.merman.misc.MultiError;
import com.zarbosoft.merman.misc.TSMap;
import com.zarbosoft.merman.syntax.Syntax;
import com.zarbosoft.merman.syntax.error.TypeInvalidAtLocation;
import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.events.nodes.ClassEqTerminal;
import com.zarbosoft.pidgoon.events.stores.StackStore;
import com.zarbosoft.pidgoon.nodes.Operator;
import com.zarbosoft.pidgoon.nodes.Sequence;

import java.util.Deque;
import java.util.Iterator;

public class BackTypeSpec extends BaseBackPrimitiveSpec {
  public final String type;

  public final BackSpec value;

  public static class Config {
    public final BaseBackPrimitiveSpec.Config base;
    public final String type;

    public final BackSpec value;

    public Config(BaseBackPrimitiveSpec.Config base, String type, BackSpec value) {
      this.base = base;
      this.type = type;
      this.value = value;
    }
  }

  protected BackTypeSpec(Config config) {
    super(config.base);
    this.type = config.type;
    this.value = config.value;
  }

  @Override
  protected Iterator<BackSpec> walkStep() {
    return value.walkStep();
  }

  @Override
  public Node buildBackRule(final Syntax syntax) {
    return new Sequence()
        .add(
            new Operator<StackStore>(new ClassEqTerminal(ETypeEvent.class)) {
              @Override
              protected StackStore process(StackStore store) {
                return store.stackVarDoubleElement(
                    id, new ValuePrimitive(BackTypeSpec.this, ((ETypeEvent) store.top()).value));
              }
            })
        .add(value.buildBackRule(syntax));
  }

  @Override
  public void write(
      Deque<Write.WriteState> stack, TSMap<String, Object> data, Write.EventConsumer writer) {
    writer.type(((StringBuilder) data.get(type)).toString());
    stack.addLast(new Write.WriteStateBack(data, ImmutableList.of(value).iterator()));
  }

  @Override
  protected boolean isSingularValue() {
    return true;
  }

  @Override
  protected boolean isTypedValue() {
    return true;
  }

  public void finish(
      MultiError errors,
      final Syntax syntax,
      Path typePath,
      boolean singularRestriction,
      boolean typeRestriction) {
    super.finish(errors, syntax, typePath, singularRestriction, typeRestriction);
    if (typeRestriction) {
      errors.add(new TypeInvalidAtLocation(typePath));
    }
    value.finish(errors, syntax, typePath.add("value"), true, true);
    value.parent =
        new PartParent() {
          @Override
          public BackSpec part() {
            return BackTypeSpec.this;
          }

          @Override
          public String pathSection() {
            return null;
          }
        };
  }
}
