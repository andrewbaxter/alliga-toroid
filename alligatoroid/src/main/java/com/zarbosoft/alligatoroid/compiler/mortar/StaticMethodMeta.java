package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.ObjId;
import com.zarbosoft.alligatoroid.compiler.inout.graph.GraphDeferred;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaClass;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaInternalName;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.language.Scope;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Evaluation2Context;
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
    if (definitionSet == null) {
      throw new RuntimeException("This is a builtin function, it is already implemented.");
    }
    if (definitionSet.isResolved()) {
      throw new RuntimeException("This definition set is already resolved.");
    }
    final String entryMethodName = "call";
    JavaInternalName jvmClassName = funcInfo.base.asInternalName();

    TSList<JavaBytecodeBindingKey> initialIndexes = new TSList<>();
    final TSOrderedMap<Object, Binding> initialBindings = new TSOrderedMap<>();
    for (ROPair<Object, MortarDataType> argument : funcInfo.arguments) {
      final JavaBytecodeBindingKey key = new JavaBytecodeBindingKey();
      initialIndexes.add(key);
      initialBindings.putNew(argument.first, argument.second.type_newInitialBinding(key));
    }

    // Do evaluation
    MortarTargetModuleContext targetContext = new MortarTargetModuleContext(jvmClassName.value);
    EvaluationContext evaluationContext =
        EvaluationContext.create(context.moduleContext, targetContext, true);
    final EvaluateResult.Context ectx =
        new EvaluateResult.Context(evaluationContext, Location.rootLocation);
    EvaluateResult firstPass =
        ectx.build(
            ectx.record(
                funcInfo.returnType.type_cast(
                    evaluationContext,
                    Location.rootLocation,
                    ectx.record(
                        Scope.evaluateRaw(
                            evaluationContext, Location.rootLocation, tree, initialBindings)))));
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
          e.second, StaticAutogen.autoMortarHalfObjectTypes.get(e.first.getClass()).type_jvmDesc());
    }
    preClass.defineFunction(
        entryMethodName,
        JavaMethodDescriptor.fromParts(
            funcInfo.returnType.type_jvmDesc(), funcInfo.argDescriptor()),
        new JavaBytecodeSequence()
            .add(((MortarTargetCode) firstPass.preEffect).e)
            .add(((MortarTargetCode) firstPass.postEffect).e)
            .add(funcInfo.returnType.type_returnBytecode()),
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
