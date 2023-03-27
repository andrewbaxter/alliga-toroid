package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.CompileContext;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.GraphDeferred;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.UniqueId;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.StaticMethodMeta;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataPrototype;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarStaticMethodType;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSSet;

import java.util.Map;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class DefinitionSet implements BuiltinAutoExportable {
  public Location location;
  public long moduleId;
  public long definitionSetId;
  public UniqueId id;
  public TSSet<GraphDeferred<DefinitionSet>> dependencies;
  public int nextId = 0;
  public TSMap<String, Definition> definitions = new TSMap<>();
  private boolean resolved = false;

  public static DefinitionSet create(Location location, long moduleId, long definitionSetId) {
    final DefinitionSet out = new DefinitionSet();
    out.location = location;
    out.moduleId = moduleId;
    out.definitionSetId = definitionSetId;
    return out;
  }

  @StaticAutogen.WrapExpose
  public FunctionRet function(TSList<ROPair<Object, MortarDataPrototype>> arguments, MortarDataPrototype ret) {
    String className =
        Format.format(
            "com.zarbosoft.alligatoroidmortar.ModuleClass%s_%s_%s",
            moduleId, definitionSetId, ++nextId);
    final StaticMethodMeta meta =
        new StaticMethodMeta(
            new StaticAutogen.FuncInfo(
                "call", JavaBytecodeUtils.qualifiedName(className), arguments, ret, false),
            this);
    return new FunctionRet(MortarStaticMethodType.type.type_constAsValue(meta), meta);
  }

  public boolean isResolved() {
    return resolved;
  }

  public boolean resolve(ModuleCompileContext context) {
    if (isResolved()) return true;
    if (context.importCacheId != id.importCacheId) {
      context.errors.add(new DefinitionNotResolved(location, context.importId));
      return false;
    }

    // Ensure dependencies are resolved
    for (GraphDeferred<DefinitionSet> dependency : dependencies) {
      if (context.compileContext.loadedDefinitionSets.contains(dependency.id)) continue;
      if (dependency.artifact == null) {
        dependency.artifact = (DefinitionSet) context.lookupRef(dependency.ref);
      }
      dependency.artifact.resolve(context);
    }

    // Make own definitions available
    for (Map.Entry<String, Definition> definition : definitions) {
      context.compileContext.loadedDefinitions.put(definition.getKey(), definition.getValue());
    }
    context.compileContext.loadedDefinitionSets.put(id, null);

    resolved = true;
    return true;
  }

  public static class Definition implements CompileContext.Definition, BuiltinAutoExportable {
    public byte[] bytecode;
    public ROList<Transfer> transfers;

    public static Definition create(byte[] bytecode, ROList<Transfer> transfers) {
      final Definition out = new Definition();
      out.bytecode = bytecode;
      out.transfers = transfers;
      return out;
    }

    @Override
    public byte[] bytecode() {
      return bytecode;
    }

    @Override
    public void postLoad(Class klass) {
      uncheck(
          () -> {
            for (Transfer transfer : transfers) {
              klass.getDeclaredField(transfer.name).set(null, transfer.data);
            }
          });
    }
  }

  public static class Transfer implements BuiltinAutoExportable {
    public Object data;
    public String name;

    public static Transfer create(Object data, String name) {
      final Transfer out = new Transfer();
      out.data = data;
      out.name = name;
      return out;
    }
  }

  public static class FunctionRet {
    public final Value function;
    public final StaticMethodMeta builder;

    public FunctionRet(Value function, StaticMethodMeta builder) {
      this.function = function;
      this.builder = builder;
    }
  }
}
