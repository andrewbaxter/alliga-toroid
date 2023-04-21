package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Global;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExporter;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaInternalName;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ReverseIterable;
import com.zarbosoft.rendaw.common.TSList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen.autoMortarObjectTypes;
import static com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen.mortarPrimitiveTypes;
import static com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen.typeLanguageElement;
import static com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen.typeLocation;
import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Stage extends LanguageElement {
  @BuiltinAutoExporter.Param public LanguageElement child;

  /**
   * @param context
   * @param location
   * @param loweredValues
   * @param element
   * @return Value of LanguageElement
   */
  public static EvaluateResult stageLower1(
      EvaluationContext context,
      Location location,
      TSList<Value> loweredValues,
      LanguageElement element) {

    // 1. Encountered lower = evaluate and embed result
    if (element instanceof Lower) {
      final EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
      Value val = ectx.evaluate(((Lower) element).child);
      if (val.canCastTo(context, typeLanguageElement)) {
        // Lowering language value - use directly to interpret in next layer
        return ectx.build(ectx.record(val.castTo(context, location, typeLanguageElement)));

      } else {
        // Some other variable value - wrap in Wrap to embed in language tree
        ectx.recordEffect(
            new MortarTargetCode(
                new JavaBytecodeSequence()
                    .add(
                        MortarTargetCode.ex(ectx.record(val.vary(context, location)).consume(context, location)))
                    .add(JavaBytecodeUtils.callStaticMethodReflect(Wrap.class, "create"))));
        return ectx.build(autoMortarObjectTypes.get(Wrap.class).type_stackAsValue());
      }
    }

    // 2. Encountered subtree with lower somewhere below
    // Do a shallow copy + recurse lowerSubtree
    // Language elements are constructed with default constructors and manual field assignments
    // (@Param) since they're simple data objects.
    // Each field is a list of other language elements, a language element, a location, or a scalar
    // (int, string, bool).
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
              .add(Global.JBC_DUP);

      boolean bad = false;
      for (Field field : klass.getFields()) {
        if (Modifier.isStatic(field.getModifiers())) {
          continue;
        }
        if (field.getAnnotation(BuiltinAutoExporter.Param.class) == null) {
          continue;
        }
        JavaDataDescriptor fieldDesc;
        code.add(Global.JBC_DUP);

        if (ROList.class.isAssignableFrom(field.getType())) {
          // Field type 1: List of language elements

          fieldDesc = JavaDataDescriptor.fromObjectClass(ROList.class);
          code.add(MortarTargetModuleContext.newTSListCode);
          Object parameterValue = uncheck(() -> klass.getField(field.getName()).get(element));
          boolean listBad = false;
          for (Object o : ((TSList) parameterValue)) {
            final Value subValue =
                ectx.record(
                    ectx.record(stageLower1(context, location, loweredValues, (LanguageElement) o))
                        .vary(context, location));
            if (subValue == ErrorValue.value) {
              listBad = true;
              continue;
            }
            code.add(
                MortarTargetCode.ex(((MortarDataValue) subValue).consume(context, location)));
            code.add(MortarTargetModuleContext.tsListAddCode);
          }
          if (listBad) {
            bad = true;
            continue;
          }

        } else {
          EvaluateResult evaluation;
          if (field.getType() == LanguageElement.class) {
            // Field case 2: Single language element
            evaluation =
                stageLower1(
                    context,
                    location,
                    loweredValues,
                    (LanguageElement) uncheck(() -> klass.getField(field.getName()).get(element)));
            if (evaluation == EvaluateResult.error) {
              bad = true;
              continue;
            }

          } else if (field.getType() == Location.class) {
            // Field case 3: Location
            evaluation =
                EvaluateResult.pure(
                    typeLocation.type_constAsValue(
                        uncheck(() -> klass.getField("id").get(element))));

          } else {
            // Field case 4: Primitive
            evaluation =
                EvaluateResult.pure(
                    mortarPrimitiveTypes
                        .get(field.getType())
                        .type_constAsValue(uncheck(() -> field.get(element))));
          }

          fieldDesc = JavaDataDescriptor.fromClass(field.getType());
          final Value val = ectx.record(ectx.record(evaluation).vary(context, location));
          if (val == ErrorValue.value) {
            bad = true;
            continue;
          }
          code.add(MortarTargetCode.ex(((MortarDataValue) val).consume(context, location)));
        }

        code.add(JavaBytecodeUtils.setField(-1, klassJvmName, field.getName(), fieldDesc));
      }
      if (bad) {
        return EvaluateResult.error;
      }

      ectx.recordEffect(
          new MortarTargetCode(
              code.add(Global.JBC_DUP)
                  .add(
                      JavaBytecodeUtils.callInterfaceMthod(
                          -1,
                          JavaBytecodeUtils.internalNameFromClass(Exportable.class),
                          "postInit",
                          JavaMethodDescriptor.fromParts(Global.DESC_VOID, ROList.empty)))));
      return ectx.build(typeLanguageElement.type_stackAsValue());
    }

    // 3. Subtree has no lower, transfer directly (stays constant)
    return EvaluateResult.pure(typeLanguageElement.type_constAsValue(element));
  }

  @Override
  protected boolean innerHasLowerInSubtree() {
    return hasLowerInSubtree(child);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    final EvaluateResult.Context ectx = new EvaluateResult.Context(context, id);
    TSList<Value> loweredValues = new TSList<>();
    final Value res = ectx.record(stageLower1(context, id, loweredValues, child));
    for (Value value : new ReverseIterable<>(loweredValues)) {
      ectx.recordEffect(value.cleanup(context, id));
    }
    return ectx.build(res);
  }
}
