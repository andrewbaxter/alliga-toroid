package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportableType;
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
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarCastable;
import com.zarbosoft.alligatoroid.compiler.mortar.value.DataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen.autoMortarHalfObjectTypes;
import static com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen.primitivePrototypeLookup;
import static com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen.prototypeLanguageElement;
import static com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen.prototypeLocation;
import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Stage extends LanguageElement {
  @BuiltinAutoExportableType.Param public LanguageElement child;

  /**
   * @param context
   * @param location
   * @param element
   * @return Value of LanguageElement
   */
  public static EvaluateResult stageLower1(
      EvaluationContext context, Location location, LanguageElement element) {

    // 1. Encountered lower = evaluate and embed result
    if (element instanceof Lower) {
      final EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
      Value val = ectx.evaluate(((Lower) element).child);
      if (val instanceof MortarCastable
          && ((MortarCastable) val).canCastTo(prototypeLanguageElement)) {
        // Lowering language value - use directly to interpret in next layer
        return ectx.build(
            prototypeLanguageElement.prototype_stackAsValue(
                ((MortarCastable) val).castTo(prototypeLanguageElement)));
      } else {
        // Some other variable value - wrap in Wrap to embed in language tree
        return ectx.build(
            autoMortarHalfObjectTypes
                .get(Wrap.class)
                .prototype_stackAsValue(
                    new JavaBytecodeSequence()
                        .add(
                            ((MortarTargetCode)
                                    ectx.record(val.vary(context, location))
                                        .consume(context, location))
                                .e)
                        .add(JavaBytecodeUtils.callStaticMethodReflect(Wrap.class, "create"))));
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
              .add(JavaBytecodeUtils.dup);

      boolean bad = false;
      for (Field field : klass.getFields()) {
        if (Modifier.isStatic(field.getModifiers())) continue;
        if (field.getAnnotation(BuiltinAutoExportableType.Param.class) == null) continue;
        JavaDataDescriptor fieldDesc;
        code.add(JavaBytecodeUtils.dup);

        if (ROList.class.isAssignableFrom(field.getType())) {
          // Field type 1: List of language elements

          fieldDesc = JavaDataDescriptor.fromObjectClass(ROList.class);
          code.add(MortarTargetModuleContext.newTSListCode);
          Object parameterValue = uncheck(() -> klass.getField(field.getName()).get(element));
          boolean listBad = false;
          for (Object o : ((TSList) parameterValue)) {
            final Value subValue =
                ectx.record(
                    ectx.record(stageLower1(context, location, (LanguageElement) o))
                        .vary(context, location));
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
          EvaluateResult evaluation;
          if (field.getType() == LanguageElement.class) {
            // Field case 2: Single language element
            evaluation =
                stageLower1(
                    context,
                    location,
                    (LanguageElement) uncheck(() -> klass.getField(field.getName()).get(element)));
            if (evaluation == EvaluateResult.error) {
              bad = true;
              continue;
            }

          } else if (field.getType() == Location.class) {
            // Field case 3: Location
            evaluation =
                EvaluateResult.pure(
                    prototypeLocation.prototype_constAsValue(
                        uncheck(() -> klass.getField("id").get(element))));

          } else {
            // Field case 4: Primitive
            evaluation =
                EvaluateResult.pure(
                    primitivePrototypeLookup
                        .get(field.getType())
                        .prototype_constAsValue(uncheck(() -> field.get(element))));
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
          prototypeLanguageElement.prototype_stackAsValue(
              code.add(JavaBytecodeUtils.dup)
                  .add(
                      JavaBytecodeUtils.callInterfaceMthod(
                          -1,
                          JavaBytecodeUtils.internalNameFromClass(Exportable.class),
                          "postInit",
                          JavaMethodDescriptor.fromParts(JavaDataDescriptor.VOID, ROList.empty)))));
    }

    // 3. Subtree has no lower, transfer directly (stays constant)
    return EvaluateResult.pure(prototypeLanguageElement.prototype_constAsValue(element));
  }

  @Override
  protected boolean innerHasLowerInSubtree() {
    return hasLowerInSubtree(child);
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    return stageLower1(context, id, child);
  }
}
