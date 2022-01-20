package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarHalfValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ReverseIterable;
import com.zarbosoft.rendaw.common.TSList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

import static com.zarbosoft.alligatoroid.compiler.Meta.autoMortarHalfDataType;
import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Stage extends LanguageElement {
  public LanguageElement child;

  public Stage(Location id, LanguageElement child) {
    super(id, hasLowerInSubtree(child));
    this.child = child;
  }

  public static StageLowerResult stageLower(
      EvaluationContext context, Location location, LanguageElement element) {
    TSList<TargetCode> post = new TSList<>();
    JVMSharedCodeElement pre;
    if (element instanceof Lower) {
      EvaluateResult evalRes = ((Lower) element).child.evaluate(context);
      if (evalRes.value == ErrorValue.error) return null;
      MortarTargetModuleContext.HalfLowerResult lowerRes =
          MortarTargetModuleContext.halfLower(context, evalRes.value);
      JVMSharedCodeElement pre0;
      if (lowerRes.dataType == autoMortarHalfDataType(LanguageElement.class)) {
        pre0 = lowerRes.valueCode;
      } else {
        pre0 =
            JVMSharedCode.instantiate(
                -1,
                JVMSharedJVMName.fromClass(Wrap.class),
                JVMSharedFuncDescriptor.fromConstructorParts(
                    JVMSharedDataDescriptor.fromClass(Location.class),
                    JVMSharedDataDescriptor.OBJECT),
                new JVMSharedCode()
                    .add(((MortarTargetModuleContext) context.target).transfer(location))
                    .add(lowerRes.valueCode));
      }
      pre =
          (JVMSharedCode)
              context.target.merge(context, location, new TSList<>(evalRes.preEffect, pre0));
      post.add(evalRes.postEffect);
    } else if (element.hasLowerInSubtree) {
      Class klass = element.getClass();

      JVMSharedCode args = new JVMSharedCode();

      Constructor<?> constructor = klass.getConstructors()[0];
      Parameter[] parameters = constructor.getParameters();
      JVMSharedDataDescriptor[] argDesc = new JVMSharedDataDescriptor[parameters.length];
      for (int i = 0; i < parameters.length; i++) {
        Parameter parameter = parameters[i];
        if (parameter.getType() == Location.class) {
          argDesc[i] = JVMSharedDataDescriptor.fromClass(Location.class);
          args.add(
              MortarTargetModuleContext.lowerRaw(
                  context, uncheck(() -> klass.getField("location").get(element)), false));

        } else if (ROList.class.isAssignableFrom(parameter.getType())) {
          argDesc[i] = JVMSharedDataDescriptor.fromClass(ROList.class);
          args.add(MortarTargetModuleContext.newTSListCode);
          Object parameterValue = uncheck(() -> klass.getField(parameter.getName()).get(element));
          boolean bad = false;
          for (Object o : ((TSList) parameterValue)) {
            StageLowerResult stageRes = stageLower(context, location, (LanguageElement) o);
            if (stageRes == null) {
              bad = true;
            } else {
              args.add((JVMSharedCode) stageRes.pre);
              args.add(MortarTargetModuleContext.tsListAddCode);
              post.add(stageRes.post);
            }
          }
          if (bad) return null;

        } else if (parameter.getType() == LanguageElement.class) {
          argDesc[i] = JVMSharedDataDescriptor.fromClass(LanguageElement.class);
          StageLowerResult stageRes =
              stageLower(
                  context,
                  location,
                  (LanguageElement)
                      uncheck(() -> klass.getField(parameter.getName()).get(element)));
          if (stageRes == null) {
            return null;
          }
          args.add((JVMSharedCode) stageRes.pre);
          post.add(stageRes.post);

        } else {
          throw new Assertion();
        }
      }
      pre =
          JVMSharedCode.instantiate(
              -1,
              JVMSharedJVMName.fromClass(klass),
              JVMSharedFuncDescriptor.fromConstructorParts(argDesc),
              args);
    } else {
      pre = ((MortarTargetModuleContext) context.target).transfer(element);
    }
    return new StageLowerResult(
        pre, context.target.merge(context, location, new ReverseIterable<>(post)));
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    StageLowerResult stageRes = stageLower(context, location, child);
    if (stageRes == null) return EvaluateResult.error;
    return new EvaluateResult(
        null,
        stageRes.post,
        new MortarHalfValue(
            autoMortarHalfDataType(MortarValue.class),
            new MortarProtocode() {
              @Override
              public JVMSharedCodeElement mortarHalfLower(EvaluationContext context) {
                return (JVMSharedCode) stageRes.pre;
              }

              @Override
              public JVMSharedCodeElement mortarDrop(EvaluationContext context, Location location) {
                return null;
              }
            }));
  }

  public static class StageLowerResult {
    public final TargetCode pre;
    public final TargetCode post;

    public StageLowerResult(TargetCode pre, TargetCode post) {
      this.pre = pre;
      this.post = post;
    }
  }
}
