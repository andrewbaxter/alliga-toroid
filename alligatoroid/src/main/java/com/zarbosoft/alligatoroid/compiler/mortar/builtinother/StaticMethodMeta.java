package com.zarbosoft.alligatoroid.compiler.mortar.builtinother;

import com.zarbosoft.alligatoroid.compiler.Evaluator;
import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.inout.graph.GraphDeferred;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeInstruction;
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
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.rendaw.common.Common;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSOrderedMap;
import org.objectweb.asm.tree.InsnNode;

public class StaticMethodMeta {
  public final Meta.FuncInfo funcInfo;
  public DefinitionSet definitionSet;

  public StaticMethodMeta(Meta.FuncInfo funcInfo, DefinitionSet definitionSet) {
    this.funcInfo = funcInfo;
    this.definitionSet = definitionSet;
  }

  @Meta.WrapExpose
  public void implement(Evaluation2Context context, LanguageElement tree) {
    if (definitionSet == null)
      throw new RuntimeException("This is a builtin function, it is already implemented.");
    if (definitionSet.isResolved())
      throw new RuntimeException("This definition set is already resolved.");
    final String entryMethodName = "call";
    JavaInternalName jvmClassName = funcInfo.base.asInternalName();

    TSList<JavaBytecodeBindingKey> initialIndexes = new TSList<>();
    final TSOrderedMap<Object, Binding> initialBindings = new TSOrderedMap<>();
    for (ROPair<Object, MortarDataType> argument : funcInfo.arguments) {
      final JavaBytecodeBindingKey key = new JavaBytecodeBindingKey();
      initialIndexes.add(key);
      initialBindings.putNew(argument.first, new MortarDataBinding(key, argument.second));
    }

    // Do evaluation
    MortarTargetModuleContext targetContext = new MortarTargetModuleContext(jvmClassName.value);
    final Evaluator.RootEvaluateResult firstPass =
        Evaluator.evaluate(context.moduleContext, targetContext, true, tree, initialBindings);
    context.moduleContext.log.addAll(firstPass.log);
    context.moduleContext.errors.addAll(firstPass.errors);
    if (firstPass.errors.some()) {
      throw new RuntimeException("Couldn't implement function, see body for specific errors.");
    }
    if (!funcInfo.returnType.assertAssignableFrom(
        context.moduleContext.errors, tree.id, firstPass.value)) {
      throw new RuntimeException("Couldn't implement function, see body for specific errors.");
    }
    for (DefinitionSet dependency : targetContext.dependencies) {
      definitionSet.dependencies.add(GraphDeferred.create(dependency.id, dependency));
    }

    // Render bytecode
    JavaClass preClass = new JavaClass(jvmClassName);
    for (ROPair<Exportable, String> e : Common.iterable(targetContext.transfers.iterator())) {
      preClass.defineStaticField(
          e.second, Meta.autoMortarHalfDataTypes.get(e.first.getClass()).jvmDesc());
    }
    preClass.defineFunction(
        entryMethodName,
        JavaMethodDescriptor.fromParts(funcInfo.returnType.jvmDesc(), funcInfo.argDescriptor()),
        new JavaBytecodeSequence()
            .add(((MortarTargetCode) firstPass.code).e)
            .add(funcInfo.returnType.assignFrom(firstPass.value))
            .add(new JavaBytecodeInstruction(new InsnNode(funcInfo.returnType.returnBytecode()))),
        initialIndexes);

    // Register definition in set
    final TSList<DefinitionSet.Transfer> transfers = new TSList<>();
    for (ROPair<Exportable, String> transfer : targetContext.transfers) {
      transfers.add(DefinitionSet.Transfer.create(transfer.first, transfer.second));
    }
    definitionSet.definitions.put(
        funcInfo.base.toString(), DefinitionSet.Definition.create(preClass.render(), transfers));
  }
}
