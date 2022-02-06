package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialModule;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.languageinout.LanguageDeserializer;
import com.zarbosoft.alligatoroid.compiler.jvm.modelother.Pass2Failed;
import com.zarbosoft.alligatoroid.compiler.jvmshared.DynamicClassLoader;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptorUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedClass;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeInstruction;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedNormalName;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.Unexpected;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.language.Block;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarNullType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.DataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataStackValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.Common;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROOrderedMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.ReverseIterable;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import org.objectweb.asm.tree.InsnNode;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Evaluator {
  public static final String ENTRY_METHOD_NAME = "enter";
  public static final String METHOD_DESCRIPTOR =
      JVMDescriptorUtils.func(JVMDescriptorUtils.VOID_DESCRIPTOR);
  public static final String GENERATED_CLASS_PREFIX = "com.zarbosoft.alligatoroidmortar.Generated";
  private static final Evaluator instance = new Evaluator();
  public final AtomicInteger uniqueClass = new AtomicInteger();

  public static <T> SemiserialModule evaluate(
      ModuleCompileContext moduleContext,
      ROList<LanguageElement> rootStatements,
      /** Only whole-ish values */
      ROOrderedMap<Object, VariableDataStackValue> initialScope) {
    return instance.evaluateInner(moduleContext, rootStatements, initialScope);
  }

  public static <T> SemiserialModule evaluate(
      ModuleCompileContext moduleContext, ImportId spec, String path, InputStream source) {
    final TSList<LanguageElement> res =
        LanguageDeserializer.deserialize(spec.moduleId, moduleContext.errors, path, source);
    if (res == null) {
      return null;
    }
    return evaluate(moduleContext, res, ROOrderedMap.empty);
  }

  private <T> SemiserialModule evaluateInner(
      ModuleCompileContext moduleContext,
      ROList<LanguageElement> rootStatements,
      /** Only whole-ish values */
      ROOrderedMap<Object, VariableDataStackValue> initialScope) {
    String className = GENERATED_CLASS_PREFIX + uniqueClass.getAndIncrement();
    JVMSharedJVMName jvmClassName =
        JVMSharedJVMName.fromNormalName(JVMSharedNormalName.fromString(className));

    // Do first pass flat evaluation
    MortarTargetModuleContext targetContext =
        new MortarTargetModuleContext(JVMDescriptorUtils.jvmName(className));
    EvaluationContext context =
        new EvaluationContext(moduleContext, targetContext, new Scope(null));
    for (ROPair<Object, VariableDataStackValue> local : initialScope) {
      context.scope.put(local.first, local.second.bind(context, null).second);
    }
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, null);
    final Value resultValue =
        ectx.record(
            new com.zarbosoft.alligatoroid.compiler.model.language.Scope(
                    null, new Block(null, rootStatements))
                .evaluate(context));
    if (resultValue == ErrorValue.error) {
      return null;
    }
    for (ROPair<Location, CompletableFuture> d : context.deferredErrors) {
      try {
        Utils.await(d.second);
      } catch (Error.PreError e) {
        moduleContext.errors.add(e.toError(d.first));
      } catch (Exception e) {
        moduleContext.errors.add(new Unexpected(d.first, e));
      }
    }

    MortarDataType resultType;
    boolean resultIsVariable = false;

    EvaluateResult evaluateResult = ectx.build(null);
    TSList<TargetCode> codePieces = new TSList<>();
    codePieces.add(evaluateResult.preEffect);
    if (resultValue instanceof DataValue) {
      resultType = ((DataValue) resultValue).mortarType();
      if (resultValue instanceof VariableDataValue) {
        resultIsVariable = true;
        codePieces.add(
            ((VariableDataValue) resultValue).mortarVaryCode(context, null).half(context));
      } else {
        codePieces.add(JVMSharedCodeInstruction.null_);
      }
    } else {
      resultType = MortarNullType.type;
    }
    codePieces.add(evaluateResult.postEffect);
    JVMSharedCode code = new JVMSharedCode();
    code.add(targetContext.merge(context, null, codePieces));
    if (moduleContext.errors.some()) {
      return null;
    }

    // Do 2nd pass jvm evaluation
    JVMSharedClass preClass = new JVMSharedClass(jvmClassName);
    for (ROPair<Object, String> e : Common.iterable(targetContext.transfers.iterator())) {
      preClass.defineStaticField(e.second, e.first.getClass());
    }
    preClass.defineFunction(
        ENTRY_METHOD_NAME,
        JVMSharedFuncDescriptor.fromParts(resultType.jvmDesc()),
        new JVMSharedCode().add(code).addI(resultType.returnOpcode()),
        new TSList<>());
    Class klass =
        DynamicClassLoader.loadTree(
            className, new TSMap<String, byte[]>().put(className, preClass.render()));
    for (ROPair<Object, String> e : Common.iterable(targetContext.transfers.iterator())) {
      uncheck(() -> klass.getDeclaredField(e.second).set(null, e.first));
    }
    Object resultVariable;
    try {
      resultVariable = klass.getMethod(ENTRY_METHOD_NAME).invoke(null);
    } catch (IllegalAccessException | NoSuchMethodException e) {
      throw new Assertion();
    } catch (InvocationTargetException e0) {
      final Throwable e = e0.getTargetException();
      if (e.getClass() == Pass2Failed.class) {
        // Errors in module errors already
        resultVariable = null;
      } else {
        Location location = null; // TODO convert whole stack?
        for (StackTraceElement t : new ReverseIterable<>(Arrays.asList(e.getStackTrace()))) {
          if (t.getClassName().startsWith(GENERATED_CLASS_PREFIX)) {
            location = context.sourceMapReverse.get(t.getLineNumber());
            break;
          }
        }
        if (location != null) {
          if (e instanceof Error.PreError) {
            context.moduleContext.errors.add(((Error.PreError) e).toError(location));
          } else {
            context.moduleContext.errors.add(new Unexpected(location, e));
          }
          return null;
        } else {
          throw uncheck(e);
        }
      }
    }
    if (moduleContext.errors.some()) return null;
    return Semiserializer.semiserialize(
        moduleContext,
        resultIsVariable ? resultType.constAsValue(resultVariable) : resultValue,
        rootStatements.last().location);
  }
}
