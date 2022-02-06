package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarAutoObjectType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.DataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

import static com.zarbosoft.alligatoroid.compiler.Meta.autoMortarHalfDataTypes;
import static com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext.newWrapCode1;
import static com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext.newWrapCode2;
import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Stage extends LanguageElement {
  public LanguageElement child;

  public Stage(Location id, LanguageElement child) {
    super(id, hasLowerInSubtree(child));
    this.child = child;
  }

  public static EvaluateResult stageLower(
      EvaluationContext context, Location location, LanguageElement element) {
    MortarDataType languageElementType = autoMortarHalfDataTypes.get(LanguageElement.class);
    MortarDataType locationType = autoMortarHalfDataTypes.get(Location.class);
    if (element instanceof Lower) {
      final EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
      Value evalRes = ectx.evaluate(((Lower) element).child);
      if (languageElementType.checkAssignableFrom(location, evalRes)) {
        return ectx.build(evalRes);
      }
      final MortarAutoObjectType wrapType = autoMortarHalfDataTypes.get(Wrap.class);
      if (evalRes instanceof VariableDataValue) {
        return ectx.build(
            wrapType.stackAsValue(
                new JVMSharedCode()
                    .add(newWrapCode1)
                    .add(
                        ((VariableDataValue)
                                ectx.record(
                                    locationType.constAsValue(location).vary(context, location)))
                            .mortarVaryCode(context, location)
                            .half(context))
                    .add(
                        ((MortarTargetModuleContext) context.target)
                            .transfer(((VariableDataValue) evalRes).mortarType()))
                    .add(
                        ((VariableDataValue) evalRes)
                            .mortarVaryCode(context, location)
                            .half(context))
                    .add(
                        JVMSharedCode.callMethod(
                            context.sourceLocation(location),
                            JVMSharedJVMName.fromClass(MortarDataType.class),
                            "constAsValue",
                            JVMSharedFuncDescriptor.fromParts(
                                JVMSharedDataDescriptor.fromClass(Value.class),
                                JVMSharedDataDescriptor.OBJECT)))
                    .add(newWrapCode2)));
      } else {
        return ectx.build(wrapType.constAsValue(new Wrap(location, evalRes)));
      }
    }

    if (element.hasLowerInSubtree) {
      Class klass = element.getClass();

      Constructor<?> constructor = klass.getConstructors()[0];
      Parameter[] parameters = constructor.getParameters();
      Object[] evaluations = new Object[parameters.length];
      boolean allImmediate = true;
      boolean bad = false;
      for (int i = 0; i < parameters.length; i++) {
        Parameter parameter = parameters[i];
        if (parameter.getType() == Location.class) {
          // Location
          evaluations[i] =
              EvaluateResult.pure(
                  locationType.constAsValue(
                      uncheck(() -> klass.getField("location").get(element))));

        } else if (ROList.class.isAssignableFrom(parameter.getType())) {
          // List of language elements
          TSList immediateList = new TSList();
          TSList evaluationList = new TSList();
          Object parameterValue = uncheck(() -> klass.getField(parameter.getName()).get(element));
          for (Object o : ((TSList) parameterValue)) {
            EvaluateResult stageRes = stageLower(context, location, (LanguageElement) o);
            if (stageRes == EvaluateResult.error) {
              bad = true;
              continue;
            }
            evaluationList.add(stageRes);
            if (stageRes.value instanceof VariableDataValue) {
              allImmediate = false;
            } else {
              immediateList.add(((ConstDataValue) stageRes.value).getInner());
            }
          }
          if (bad) return null;
          evaluations[i] = evaluationList;

        } else if (parameter.getType() == LanguageElement.class) {
          // Single language element
          EvaluateResult stageRes =
              stageLower(
                  context,
                  location,
                  (LanguageElement)
                      uncheck(() -> klass.getField(parameter.getName()).get(element)));
          if (stageRes == EvaluateResult.error) {
            bad = true;
            continue;
          }
          evaluations[i] = stageRes;
          if (stageRes.value instanceof VariableDataValue) {
            allImmediate = false;
          }

        } else {
          throw new Assertion();
        }
      }
      if (bad) return EvaluateResult.error;

      EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
      if (allImmediate) {
        Object[] immediateArgs = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
          if (evaluations[i] instanceof ROList) {
            TSList elementList = new TSList();
            for (Object o : ((ROList) ectx.record((EvaluateResult) evaluations[i]))) {
              elementList.add(((ConstDataValue) o).getInner());
            }
            immediateArgs[i] = element;
          } else {
            immediateArgs[i] =
                ((ConstDataValue) ectx.record((EvaluateResult) evaluations[i])).getInner();
          }
        }
        return ectx.build(
            languageElementType.constAsValue(
                uncheck(() -> constructor.newInstance(immediateArgs))));
      } else {
        final JVMSharedCode args = new JVMSharedCode();
        JVMSharedDataDescriptor[] argDesc = new JVMSharedDataDescriptor[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
          if (evaluations[i] instanceof ROList) {
            argDesc[i] = JVMSharedDataDescriptor.fromClass(ROList.class);
            args.add(MortarTargetModuleContext.newTSListCode);
            for (Object sub : ((ROList) ectx.record((EvaluateResult) evaluations[i]))) {
              final Value subValue =
                  ectx.record(ectx.record((EvaluateResult) sub).vary(context, location));
              if (subValue == ErrorValue.error) {
                bad = true;
                continue;
              }
              args.add(((DataValue) subValue).mortarVaryCode(context, location).half(context));
              args.add(MortarTargetModuleContext.tsListAddCode);
            }
          } else {
            final Value val =
                ectx.record(ectx.record((EvaluateResult) evaluations[i]).vary(context, location));
            if (val == ErrorValue.error) {
              bad = true;
              continue;
            }
            args.add(((DataValue) val).mortarVaryCode(context, location).half(context));
          }
        }
        if (bad) return EvaluateResult.error;
        return ectx.build(
            languageElementType.stackAsValue(
                JVMSharedCode.instantiate(
                    -1,
                    JVMSharedJVMName.fromClass(klass),
                    JVMSharedFuncDescriptor.fromConstructorParts(argDesc),
                    args)));
      }
    }

    return EvaluateResult.pure(languageElementType.constAsValue(element));
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    return stageLower(context, location, child);
  }
}
