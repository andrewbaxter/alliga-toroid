package com.zarbosoft.alligatoroid.compiler.mortar.builtinother;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Evaluator;
import com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen;
import com.zarbosoft.alligatoroid.compiler.ObjId;
import com.zarbosoft.alligatoroid.compiler.Scope;
import com.zarbosoft.alligatoroid.compiler.inout.graph.GraphDeferred;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaClass;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaInternalName;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.mortar.DefinitionSet;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataPrototype;
import com.zarbosoft.rendaw.common.Common;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSOrderedMap;

public class StaticMethodMeta {
  public final StaticAutogen.FuncInfo funcInfo;
  public DefinitionSet definitionSet;

  public StaticMethodMeta(StaticAutogen.FuncInfo funcInfo, DefinitionSet definitionSet) {
    this.funcInfo = funcInfo;
    this.definitionSet = definitionSet;
  }

  @StaticAutogen.WrapExpose
  public void implement(Evaluation2Context context, LanguageElement tree) {
    if (definitionSet == null)
      throw new RuntimeException("This is a builtin function, it is already implemented.");
    if (definitionSet.isResolved())
      throw new RuntimeException("This definition set is already resolved.");
    final String entryMethodName = "call";
    JavaInternalName jvmClassName = funcInfo.base.asInternalName();

    TSList<JavaBytecodeBindingKey> initialIndexes = new TSList<>();
    final TSOrderedMap<Object, Binding> initialBindings = new TSOrderedMap<>();
    for (ROPair<Object, MortarDataPrototype> argument : funcInfo.arguments) {
      final JavaBytecodeBindingKey key = new JavaBytecodeBindingKey();
      initialIndexes.add(key);
      initialBindings.putNew(
          argument.first, new MortarDataBinding(key, argument.second.prototype_newType()));
    }

    // Do evaluation
    MortarTargetModuleContext targetContext = new MortarTargetModuleContext(jvmClassName.value);
    EvaluationContext evaluationContext = new EvaluationContext(context.moduleContext, targetContext, true, Scope.create(initialBindings));
    final Evaluator.RootEvaluateResult firstPass =
        Evaluator.evaluate(evaluationContext, tree);
        JavaBytecode resultBytecode = funcInfo.returnType.prototype_cast(evaluationContext, tree.id, firstPass.value);
    context.moduleContext.log.addAll(evaluationContext.log);
    context.moduleContext.errors.addAll(evaluationContext.errors);
    if (evaluationContext.errors.some()) {
      throw new RuntimeException("Couldn't implement function, see body for specific errors.");
    }
    for (DefinitionSet dependency : targetContext.dependencies) {
      definitionSet.dependencies.add(GraphDeferred.create(dependency.id, dependency));
    }

    // Render bytecode
    JavaClass preClass = new JavaClass(jvmClassName);
    for (ROPair<ObjId<Object>, String> e : Common.iterable(targetContext.transfers.iterator())) {
      preClass.defineStaticField(
          e.second, StaticAutogen.autoMortarHalfObjectTypes.get(e.first.getClass()).prototype_jvmDesc());
    }
    preClass.defineFunction(
        entryMethodName,
        JavaMethodDescriptor.fromParts(funcInfo.returnType.prototype_jvmDesc(), funcInfo.argDescriptor()),
        new JavaBytecodeSequence()
            .add(((MortarTargetCode) firstPass.code).e)
            .add(resultBytecode)
            .add(funcInfo.returnType.prototype_returnBytecode()),
        initialIndexes);

    // Register definition in set
    final TSList<DefinitionSet.Transfer> transfers = new TSList<>();
    for (ROPair<ObjId<Object>, String> transfer : targetContext.transfers) {
      transfers.add(DefinitionSet.Transfer.create(transfer.first.obj, transfer.second));
    }
    definitionSet.definitions.put(
        funcInfo.base.toString(), DefinitionSet.Definition.create(preClass.render(), transfers));
  }
}
