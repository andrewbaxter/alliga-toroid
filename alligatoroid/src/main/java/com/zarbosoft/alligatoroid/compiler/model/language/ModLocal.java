package com.zarbosoft.alligatoroid.compiler.model.language;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.model.error.ImportOutsideOwningBundleModule;
import com.zarbosoft.alligatoroid.compiler.model.ids.BundleModuleSubId;
import com.zarbosoft.alligatoroid.compiler.model.ids.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RemoteModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RootModuleId;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ModuleIdValue;
import com.zarbosoft.rendaw.common.Assertion;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ModLocal extends LanguageElement {
  public final LanguageElement path;

  public ModLocal(Location id, LanguageElement path) {
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
        context.moduleContext.importId.moduleId.dispatch(
            new ModuleId.Dispatcher<ModuleId>() {

              @Override
              public ModuleId handleLocal(LocalModuleId id) {
                return new LocalModuleId(
                    Paths.get(id.path).resolveSibling(path).normalize().toString());
              }

              @Override
              public ModuleId handleRemote(RemoteModuleId id) {
                Path subpath = Paths.get(path).normalize();
                if (subpath.startsWith("..")) {
                  context.moduleContext.errors.add(
                      new ImportOutsideOwningBundleModule(location, subpath.toString(), id));
                  return null;
                }
                return new BundleModuleSubId(id, subpath.toString());
              }

              @Override
              public ModuleId handleBundle(BundleModuleSubId id) {
                Path subpath = Paths.get(id.path).resolveSibling(path).normalize();
                if (subpath.startsWith("..")) {
                  context.moduleContext.errors.add(
                      new ImportOutsideOwningBundleModule(
                          location, subpath.toString(), id.module));
                  return null;
                }
                return new BundleModuleSubId(id.module, subpath.toString());
              }

                @Override
                public ModuleId handleRoot(RootModuleId id) {
                    throw new Assertion();
                }
            });
    if (newId == null) return EvaluateResult.error;
    return ectx.build(new ModuleIdValue(newId));
  }
}
