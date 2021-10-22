package com.zarbosoft.alligatoroid.compiler.language;

import com.zarbosoft.alligatoroid.compiler.Context;
import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.ImportSpec;
import com.zarbosoft.alligatoroid.compiler.ImportSpecValue;
import com.zarbosoft.alligatoroid.compiler.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.alligatoroid.compiler.ModuleId;
import com.zarbosoft.alligatoroid.compiler.RemoteModuleId;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.WholeValue;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ModLocal extends LanguageValue {
  public final Value path;

  public ModLocal(Location id, Value path) {
    super(id, hasLowerInSubtree(path));
    this.path = path;
  }

  public Object graphDeserialize(Record data) {
    return graphDeserialize(this.getClass(), data);
  }

  @Override
  public EvaluateResult evaluate(Context context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    WholeValue path0 = WholeValue.getWhole(context, location, ectx.evaluate(this.path));
    if (path0 == null) return EvaluateResult.error;
    ModuleId newId =
        context.module.spec.moduleId.dispatch(
            new ModuleId.Dispatcher<ModuleId>() {
              @Override
              public ModuleId handle(LocalModuleId id) {
                Path path =
                    Paths.get(id.path).resolveSibling((String) path0.concreteValue()).normalize();
                return new LocalModuleId(path.toString());
              }

              @Override
              public ModuleId handle(RemoteModuleId id) {
                Path subpath = Paths.get((String) path0.concreteValue()).normalize();
                if (subpath.startsWith("..")) {
                  context.module.log.errors.add(Error.importOutsideOwningRemoteModule(subpath, id));
                  return null;
                }
                return new RemoteModuleSubId(id, subpath.toString());
              }

              @Override
              public ModuleId handle(RemoteModuleSubId id) {
                Path subpath =
                    Paths.get(id.path).resolveSibling((String) path0.concreteValue()).normalize();
                if (subpath.startsWith("..")) {
                  context.module.log.errors.add(Error.importOutsideOwningRemoteModule(subpath, id));
                  return null;
                }
                return new RemoteModuleSubId(id.module, subpath.toString());
              }
            });
    if (newId == null) return EvaluateResult.error;
    return ectx.build(new ImportSpecValue(new ImportSpec(newId)));
  }
}
