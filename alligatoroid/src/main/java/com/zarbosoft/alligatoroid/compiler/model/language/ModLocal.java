package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.model.ids.BundleModuleSubId;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.model.LanguageValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ModuleIdValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.RemoteModuleId;
import com.zarbosoft.alligatoroid.compiler.model.Value;
import com.zarbosoft.alligatoroid.compiler.model.error.ImportOutsideOwningBundleModule;
import com.zarbosoft.alligatoroid.compiler.mortar.WholeValue;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ModLocal extends LanguageValue {
  public final Value path;

  public ModLocal(Location id, Value path) {
    super(id, hasLowerInSubtree(path));
    this.path = path;
  }

  @Override
  public EvaluateResult evaluate(EvaluationContext context) {
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    WholeValue path0 = WholeValue.getWhole(context, location, ectx.evaluate(this.path));
    if (path0 == null) return EvaluateResult.error;
    final String path = (String) path0.concreteValue();
    ModuleId newId =
        context.moduleContext.spec().moduleId.dispatch(
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
                  context.moduleContext.log.errors.add(
                      new ImportOutsideOwningBundleModule(location, subpath.toString(), id));
                  return null;
                }
                return new BundleModuleSubId(id, subpath.toString());
              }

              @Override
              public ModuleId handle(BundleModuleSubId id) {
                Path subpath = Paths.get(id.path).resolveSibling(path).normalize();
                if (subpath.startsWith("..")) {
                  context.moduleContext.log.errors.add(
                      new ImportOutsideOwningBundleModule(
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
