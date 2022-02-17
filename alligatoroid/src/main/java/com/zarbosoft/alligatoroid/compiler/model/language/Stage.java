package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.DataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static com.zarbosoft.alligatoroid.compiler.Meta.autoMortarHalfDataTypes;
import static com.zarbosoft.rendaw.common.Common.uncheck;
import static org.objectweb.asm.Opcodes.DUP;

public class Stage extends LanguageElement {
  @Param public LanguageElement child;

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
      final MortarDataType wrapType = autoMortarHalfDataTypes.get(Wrap.class);
      if (evalRes instanceof VariableDataValue) {
        return ectx.build(
            wrapType.stackAsValue(
                new JVMSharedCode()
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
                        JVMSharedCode.callStaticMethod(
                            context.sourceLocation(location),
                            JVMSharedJVMName.fromClass(MortarDataType.class),
                            "constAsValue",
                            JVMSharedFuncDescriptor.fromParts(
                                JVMSharedDataDescriptor.fromObjectClass(Value.class),
                                JVMSharedDataDescriptor.OBJECT)))
                    .add(JVMSharedCode.callStaticMethodReflect(Wrap.class, "create"))));
      } else {
        return ectx.build(wrapType.constAsValue(Wrap.create(location, evalRes)));
      }
    }

    if (element.hasLowerInSubtree()) {
      Class klass = element.getClass();
      TSList<ROPair<Field, Object>> evaluations = new TSList<>();
      boolean allImmediate = true;
      boolean bad = false;
      for (Field field : klass.getFields()) {
        if (Modifier.isStatic(field.getModifiers())) continue;
        if (field.getAnnotation(Param.class) == null) continue;
        if (field.getType() == Location.class) {
          // Location
          evaluations.add(
              new ROPair<>(
                  field,
                  EvaluateResult.pure(
                      locationType.constAsValue(
                          uncheck(() -> klass.getField("id").get(element))))));

        } else if (ROList.class.isAssignableFrom(field.getType())) {
          // List of language elements
          TSList immediateList = new TSList();
          TSList evaluationList = new TSList();
          Object parameterValue = uncheck(() -> klass.getField(field.getName()).get(element));
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
          evaluations.add(new ROPair<>(field, evaluationList));

        } else if (field.getType() == LanguageElement.class) {
          // Single language element
          EvaluateResult stageRes =
              stageLower(
                  context,
                  location,
                  (LanguageElement) uncheck(() -> klass.getField(field.getName()).get(element)));
          if (stageRes == EvaluateResult.error) {
            bad = true;
            continue;
          }
          evaluations.add(new ROPair<>(field, stageRes));
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
        final Object out =
            uncheck(() -> ((Constructor<?>) klass.getConstructors()[0]).newInstance());
        for (ROPair<Field, Object> evaluation : evaluations) {
          if (evaluation.second instanceof ROList) {
            TSList elementList = new TSList();
            for (Object o : ((ROList) evaluation.second)) {
              elementList.add(((ConstDataValue) ectx.record((EvaluateResult) o)).getInner());
            }
            uncheck(() -> evaluation.first.set(out, elementList));
          } else {
            uncheck(
                () ->
                    evaluation.first.set(
                        out,
                        ((ConstDataValue) ectx.record((EvaluateResult) evaluation.second))
                            .getInner()));
          }
        }
        return ectx.build(languageElementType.constAsValue(out));
      } else {
        final JVMSharedCode code = new JVMSharedCode();
        final JVMSharedJVMName klassJvmName = JVMSharedJVMName.fromClass(klass);
        code.add(
            JVMSharedCode.instantiate(
                -1, klassJvmName, JVMSharedFuncDescriptor.fromConstructorParts(), null));
        code.addI(DUP);
        for (ROPair<Field, Object> evaluation : evaluations) {
          JVMSharedDataDescriptor fieldDesc;
          if (evaluation.second instanceof ROList) {
            fieldDesc = JVMSharedDataDescriptor.fromObjectClass(ROList.class);
            code.add(MortarTargetModuleContext.newTSListCode);
            for (Object sub : ((ROList) ectx.record((EvaluateResult) evaluation.second))) {
              final Value subValue =
                  ectx.record(ectx.record((EvaluateResult) sub).vary(context, location));
              if (subValue == ErrorValue.error) {
                bad = true;
                continue;
              }
              code.add(((DataValue) subValue).mortarVaryCode(context, location).half(context));
              code.add(MortarTargetModuleContext.tsListAddCode);
            }
          } else {
            fieldDesc = JVMSharedDataDescriptor.fromClass(evaluation.first.getType());
            final Value val =
                ectx.record(
                    ectx.record((EvaluateResult) evaluation.second).vary(context, location));
            if (val == ErrorValue.error) {
              bad = true;
              continue;
            }
            code.add(((DataValue) val).mortarVaryCode(context, location).half(context));
          }
          code.add(JVMSharedCode.setField(-1, klassJvmName, evaluation.first.getName(), fieldDesc));
        }
        if (bad) return EvaluateResult.error;
        return ectx.build(
            languageElementType.stackAsValue(
                code.addI(DUP)
                    .add(
                        JVMSharedCode.callInterfaceMthod(
                            -1,
                            JVMSharedJVMName.fromClass(Exportable.class),
                            "postInit",
                            JVMSharedFuncDescriptor.fromParts(JVMSharedDataDescriptor.VOID)))));
      }
    }

    return EvaluateResult.pure(languageElementType.constAsValue(element));
  }

  @Override
  protected boolean innerHasLowerInSubtree() {
    return hasLowerInSubtree(child);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    return stageLower(context, id, child);
  }
}
