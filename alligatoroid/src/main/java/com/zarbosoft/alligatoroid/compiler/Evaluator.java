package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.inout.utils.languageinout.LanguageDeserializer;
import com.zarbosoft.alligatoroid.compiler.jvmshared.DynamicClassLoader;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptorUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedClass;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedNormalName;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.LocationlessUnexpected;
import com.zarbosoft.alligatoroid.compiler.model.error.Unexpected;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.language.Block;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.WholeValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.ErrorValue;
import com.zarbosoft.rendaw.common.Common;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROOrderedMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.ReverseIterable;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Evaluator {
  public static final String ENTRY_METHOD_NAME = "enter";
  public static final String METHOD_DESCRIPTOR =
      JVMDescriptorUtils.func(JVMDescriptorUtils.VOID_DESCRIPTOR);
  public static final String GENERATED_CLASS_PREFIX = "com.zarbosoft.alligatoroidmortar.Generated";
  private static final Evaluator instance = new Evaluator();
  public final AtomicInteger uniqueClass = new AtomicInteger();

  public static void processError(EvaluationContext context, Throwable e) {
    if (e instanceof Common.UncheckedException) {
      processError(context, e.getCause());
    } else if (e instanceof ExecutionException) {
      processError(context, e.getCause());
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
      } else {
        context.moduleContext.errors.add(new LocationlessUnexpected(e));
      }
    }
  }

  public static <T> Value evaluate(
      ModuleCompileContext moduleContext,
      ROList<Value> rootStatements,
      /** Only whole-ish values */
      ROOrderedMap<WholeValue, Value> initialScope) {
    return instance.evaluateInner(moduleContext, rootStatements, initialScope);
  }

  public static <T> Value evaluate(
      ModuleCompileContext moduleContext, ImportId spec, String path, InputStream source) {
    final ROList<Value> res =
        LanguageDeserializer.deserialize(spec.moduleId, moduleContext.errors, path, source);
    if (res == null) {
      return ErrorValue.error;
    }
    return evaluate(moduleContext, res, ROOrderedMap.empty);
  }

  private <T> Value evaluateInner(
      ModuleCompileContext moduleContext,
      ROList<Value> rootStatements,
      /** Only whole-ish values */
      ROOrderedMap<WholeValue, Value> initialScope) {
    String className = GENERATED_CLASS_PREFIX + uniqueClass.getAndIncrement();
    JVMSharedJVMName jvmClassName =
        JVMSharedJVMName.fromNormalName(JVMSharedNormalName.fromString(className));

    // Do first pass flat evaluation
    MortarTargetModuleContext targetContext =
        new MortarTargetModuleContext(JVMDescriptorUtils.jvmName(className));
    EvaluationContext context =
        new EvaluationContext(moduleContext, targetContext, new Scope(null));
    for (ROPair<WholeValue, Value> local : initialScope) {
      context.scope.put(local.first, local.second.bind(context, null).second);
    }
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, null);
    MortarTargetModuleContext.LowerResult lowered =
        MortarTargetModuleContext.lower(
            context,
            ectx.record(
                new com.zarbosoft.alligatoroid.compiler.model.language.Scope(
                        null, new Block(null, rootStatements))
                    .evaluate(context)));
    EvaluateResult evaluateResult = ectx.build(null);
    for (ROPair<Location, CompletableFuture> d : context.deferredErrors) {
      try {
        d.second.get();
      } catch (Error.PreError e) {
        moduleContext.errors.add(e.toError(d.first));
      } catch (Exception e) {
        moduleContext.errors.add(new Unexpected(d.first, e));
      }
    }
    JVMSharedCode code = new JVMSharedCode();
    code.add(
        targetContext.merge(
            context,
            null,
            new TSList<>(evaluateResult.preEffect, lowered.valueCode, evaluateResult.postEffect)));
    if (moduleContext.errors.some()) {
      return ErrorValue.error;
    }

    // Do 2nd pass jvm evaluation
    JVMSharedClass preClass = new JVMSharedClass(jvmClassName);
    for (ROPair<Object, String> e : Common.iterable(targetContext.transfers.iterator())) {
      preClass.defineStaticField(e.second, e.first.getClass());
    }
    preClass.defineFunction(
        ENTRY_METHOD_NAME,
        JVMSharedFuncDescriptor.fromParts(lowered.dataType.jvmDesc()),
        new JVMSharedCode().add(code).addI(lowered.dataType.returnOpcode()),
        new TSList<>());
    Class klass =
        DynamicClassLoader.loadTree(
            className, new TSMap<String, byte[]>().put(className, preClass.render()));
    for (ROPair<Object, String> e : Common.iterable(targetContext.transfers.iterator())) {
      uncheck(() -> klass.getDeclaredField(e.second).set(null, e.first));
    }
    Object pass2;
    try {
      pass2 = uncheck(() -> klass.getMethod(ENTRY_METHOD_NAME).invoke(null));
    } catch (Exception e) {
      processError(context, e);
      return ErrorValue.error;
    }
    return lowered.dataType.unlower(pass2);
  }
}
