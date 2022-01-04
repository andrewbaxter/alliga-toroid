package com.zarbosoft.alligatoroid.compiler.modules.modulecompiler;

import com.zarbosoft.alligatoroid.compiler.CompileContext;
import com.zarbosoft.alligatoroid.compiler.Evaluator;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialModule;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRef;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.model.ImportPath;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.ImportLoopPre;
import com.zarbosoft.alligatoroid.compiler.model.error.ImportNotFoundPre;
import com.zarbosoft.alligatoroid.compiler.model.error.UnknownImportFileTypePre;
import com.zarbosoft.alligatoroid.compiler.model.ids.BundleModuleSubId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RemoteModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RootModuleId;
import com.zarbosoft.alligatoroid.compiler.modules.Module;
import com.zarbosoft.alligatoroid.compiler.modules.ModuleResolver;
import com.zarbosoft.alligatoroid.compiler.modules.Source;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.BundleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.ErrorValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSSet;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class ModuleCompiler implements ModuleResolver {
  @Override
  public Module get(
      CompileContext context, ImportPath fromImportPath, ImportId importId, Source source) {
    if (fromImportPath != null) {
      TSList<ImportId> found = fromImportPath.find(new TSSet<>(), importId);
      if (found != null) {
        throw new ImportLoopPre(found);
      }
    }

    ImportPath importPath = new ImportPath(importId);
    if (fromImportPath != null) importPath.add(fromImportPath);

    ModuleCompileContext moduleContext = new ModuleCompileContext(importId, context, importPath);
    context.moduleErrors.put(importId, moduleContext.errors);

    Value res =
        importId.moduleId.dispatch(
            new ModuleId.Dispatcher<Value>() {

              public Value handleTopLevel() {
                final String stringPath = source.path.toString();
                if (stringPath.endsWith(".at")) {
                  return uncheck(
                      () -> {
                        try (InputStream stream = Files.newInputStream(source.path)) {
                          return Evaluator.evaluate(moduleContext, importId, stringPath, stream);
                        }
                      });
                } else if (stringPath.endsWith(".zip")) {
                  return new BundleValue(importId, "");
                } else {
                  throw new UnknownImportFileTypePre(importId.moduleId);
                }
              }

              @Override
              public Value handleLocal(LocalModuleId id) {
                return handleTopLevel();
              }

              @Override
              public Value handleRemote(RemoteModuleId id) {
                return handleTopLevel();
              }

              @Override
              public Value handleBundle(BundleModuleSubId id) {
                return uncheck(
                    () -> {
                      try (ZipFile bundle = new ZipFile(source.path.toFile())) {
                        String path = id.path + ".at";
                        ZipEntry e = bundle.getEntry(path);
                        if (e == null) {
                          path = id.path;
                          e = bundle.getEntry(path);
                        }
                        if (e == null) {
                          throw new ImportNotFoundPre(id.toString());
                        }
                        if (e.isDirectory()) {
                          return new BundleValue(new ImportId(id.module), path);
                        }
                        if (path.endsWith(".at")) {
                          try (InputStream stream = bundle.getInputStream(e)) {
                            return Evaluator.evaluate(
                                moduleContext, importId, id.toString(), stream);
                          }
                        } else {
                          throw new UnknownImportFileTypePre(id);
                        }
                      }
                    });
              }

              @Override
              public Value handleRoot(RootModuleId id) {
                throw new Assertion();
              }
            });
    if (res == ErrorValue.error) throw Error.moduleError;
    Semiserializer s = new Semiserializer(moduleContext.backArtifactLookup);
    SemiserialRef root = s.process(importId, res, new TSList<>(), new TSList<>());
    SemiserialModule semiserialModule = new SemiserialModule(root, s.artifacts);
    return new Module() {
      @Override
      public ImportId spec() {
        return importId;
      }

      @Override
      public SemiserialModule result() {
        return semiserialModule;
      }
    };
  }
}
