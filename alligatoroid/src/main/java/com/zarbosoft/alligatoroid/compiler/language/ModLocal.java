package com.zarbosoft.alligatoroid.compiler.language;

import com.zarbosoft.alligatoroid.compiler.BundleModuleSubId;
import com.zarbosoft.alligatoroid.compiler.Context;
import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.alligatoroid.compiler.ModuleId;
import com.zarbosoft.alligatoroid.compiler.ModuleIdValue;
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
    final String path = (String) path0.concreteValue();
    ModuleId newId =
        context.module.spec.moduleId.dispatch(
            new ModuleId.Dispatcher<ModuleId>() {

              @Override
              public ModuleId handle(LocalModuleId id) {
                return new LocalModuleId(
                    Paths.get(id.path).resolveSibling(path).normalize().toString());
              }

              @Override
              public ModuleId handle(RemoteModuleId id) {
                Path subpath = Paths.get(path).normalize();
                if (subpath.startsWith("..")) {
                  context.module.log.errors.add(
                      new Error.ImportOutsideOwningBundleModule(location, subpath.toString(), id));
                  return null;
                }
                return new BundleModuleSubId(id, subpath.toString());
              }

              @Override
              public ModuleId handle(BundleModuleSubId id) {
                Path subpath = Paths.get(id.path).resolveSibling(path).normalize();
                if (subpath.startsWith("..")) {
                  context.module.log.errors.add(
                      new Error.ImportOutsideOwningBundleModule(
                          location, subpath.toString(), id.module));
                  return null;
                }
                return new BundleModuleSubId(id.module, subpath.toString());
              }
            });
    if (newId == null) return EvaluateResult.error;
    return ectx.build(new ModuleIdValue(newId));
  }
}
