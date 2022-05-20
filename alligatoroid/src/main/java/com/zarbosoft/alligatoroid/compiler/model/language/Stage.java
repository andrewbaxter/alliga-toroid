package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportableType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaInternalName;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.DataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static com.zarbosoft.alligatoroid.compiler.Meta.autoMortarHalfDataTypes;
import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Stage extends LanguageElement {
  @AutoBuiltinExportableType.Param
  public LanguageElement child;

  /**
   * @param context
   * @param location
   * @param element
   * @return Value of LanguageElement
   */
  public static EvaluateResult stageLower(
      EvaluationContext context, Location location, LanguageElement element) {
    MortarDataType languageElementType = autoMortarHalfDataTypes.get(LanguageElement.class);
    MortarDataType valueType = autoMortarHalfDataTypes.get(Value.class);
    MortarDataType locationType = autoMortarHalfDataTypes.get(Location.class);

    // 1. Encountered lower = evaluate and embed result
    if (element instanceof Lower) {
      final EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
      Value evalRes = ectx.evaluate(((Lower) element).child);
      if (languageElementType.checkAssignableFrom(location, evalRes)) {
        return ectx.build(evalRes);
      }
      final MortarDataType wrapType = autoMortarHalfDataTypes.get(Wrap.class);
      if (evalRes instanceof VariableDataValue) {
        final JavaBytecode body;
        if (valueType.checkAssignableFrom(location, evalRes)) {
          body = ((MortarTargetCode) evalRes.consume(context, location)).e;
        } else {
          final VariableDataValue evalRes1 = (VariableDataValue) evalRes;
          body =
              new JavaBytecodeSequence()
                  .add(((MortarTargetModuleContext) context.target).transfer(evalRes1.mortarType()))
                  .add(((MortarTargetCode) evalRes1.consume(context, location)).e)
                  .add(
                      JavaBytecodeUtils.callStaticMethod(
                          context.sourceLocation(location),
                          JavaBytecodeUtils.internalNameFromClass(MortarDataType.class),
                          "constAsValue",
                          JavaMethodDescriptor.fromParts(
                              JavaDataDescriptor.fromObjectClass(Value.class),
                              new TSList<>(JavaDataDescriptor.OBJECT))));
        }
        return ectx.build(
            wrapType.stackAsValue(
                new JavaBytecodeSequence()
                    .add(body)
                    .add(JavaBytecodeUtils.callStaticMethodReflect(Wrap.class, "create"))));
      } else {
        Value body;
        if (valueType.checkAssignableFrom(location, evalRes))
          body = (Value) ((ConstDataValue) evalRes).getInner();
        else body = evalRes;
        return ectx.build(wrapType.constAsValue(Wrap.create(body)));
      }
    }

    // 2. Encountered subtree with lower somewhere below
    // Do a shallow copy (varied) + recurse lowerSubtree
    if (element.hasLowerInSubtree()) {
      Class klass = element.getClass();
      EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
      final JavaInternalName klassJvmName = JavaBytecodeUtils.internalNameFromClass(klass);
      final JavaBytecodeSequence code =
          new JavaBytecodeSequence()
              .add(
                  JavaBytecodeUtils.instantiate(
                      -1,
                      klassJvmName,
                      JavaMethodDescriptor.fromConstructorParts(ROList.empty),
                      null))
              .add(JavaBytecodeUtils.dup);

      boolean bad = false;
      for (Field field : klass.getFields()) {
        if (Modifier.isStatic(field.getModifiers())) continue;
        if (field.getAnnotation(AutoBuiltinExportableType.Param.class) == null) continue;
        JavaDataDescriptor fieldDesc;
        code.add(JavaBytecodeUtils.dup);

        if (ROList.class.isAssignableFrom(field.getType())) {
          // List of language elements

          fieldDesc = JavaDataDescriptor.fromObjectClass(ROList.class);
          code.add(MortarTargetModuleContext.newTSListCode);
          Object parameterValue = uncheck(() -> klass.getField(field.getName()).get(element));
          boolean listBad = false;
          for (Object o : ((TSList) parameterValue)) {
            final Value subValue =
                ectx.record(
                    context.target.vary(
                        context,
                        location,
                        ectx.record(stageLower(context, location, (LanguageElement) o))));
            if (subValue == ErrorValue.error) {
              listBad = true;
              continue;
            }
            code.add(((MortarTargetCode) ((DataValue) subValue).consume(context, location)).e);
            code.add(MortarTargetModuleContext.tsListAddCode);
          }
          if (listBad) {
            bad = true;
            continue;
          }

        } else {
          // Scalar value - only Location and LanguageElement appear in LanguageElement

          EvaluateResult evaluation;
          if (field.getType() == LanguageElement.class) {
            // Single language element
            evaluation =
                stageLower(
                    context,
                    location,
                    (LanguageElement) uncheck(() -> klass.getField(field.getName()).get(element)));
            if (evaluation == EvaluateResult.error) {
              bad = true;
              continue;
            }

          } else if (field.getType() == Location.class) {
            // Location
            evaluation =
                EvaluateResult.pure(
                    locationType.constAsValue(uncheck(() -> klass.getField("id").get(element))));

          } else {
            // Other values (primitives)
            evaluation =
                EvaluateResult.pure(
                    autoMortarHalfDataTypes
                        .get(field.getType())
                        .constAsValue(uncheck(() -> field.get(element))));
          }

          fieldDesc = JavaDataDescriptor.fromClass(field.getType());
          final Value val =
              ectx.record(context.target.vary(context, location, ectx.record(evaluation)));
          if (val == ErrorValue.error) {
            bad = true;
            continue;
          }
          code.add(((MortarTargetCode) ((DataValue) val).consume(context, location)).e);
        }

        code.add(JavaBytecodeUtils.setField(-1, klassJvmName, field.getName(), fieldDesc));
      }
      if (bad) return EvaluateResult.error;

      return ectx.build(
          languageElementType.stackAsValue(
              code.add(JavaBytecodeUtils.dup)
                  .add(
                      JavaBytecodeUtils.callInterfaceMthod(
                          -1,
                          JavaBytecodeUtils.internalNameFromClass(Exportable.class),
                          "postInit",
                          JavaMethodDescriptor.fromParts(JavaDataDescriptor.VOID, ROList.empty)))));
    }

    // 3. Subtree has no lower, transfer directly (stays constant)
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
