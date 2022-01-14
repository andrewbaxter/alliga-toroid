package com.zarbosoft.alligatoroid.compiler.modules.modulecompiler;

import com.zarbosoft.alligatoroid.compiler.CompileContext;
import com.zarbosoft.alligatoroid.compiler.Evaluator;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialModule;
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
import com.zarbosoft.alligatoroid.compiler.mortar.value.BundleValue;
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

    SemiserialModule res =
        importId.moduleId.dispatch(
            new ModuleId.Dispatcher<SemiserialModule>() {

              public SemiserialModule handleTopLevel() {
                final String stringPath = source.path.toString();
                if (stringPath.endsWith(".at")) {
                  return uncheck(
                      () -> {
                        try (InputStream stream = Files.newInputStream(source.path)) {
                          return Evaluator.evaluate(moduleContext, importId, stringPath, stream);
                        }
                      });
                } else if (stringPath.endsWith(".zip")) {
                  return Semiserializer.semiserialize(
                      moduleContext, new BundleValue(importId, ""), null);
                } else {
                  throw new UnknownImportFileTypePre(importId.moduleId);
                }
              }

              @Override
              public SemiserialModule handleLocal(LocalModuleId id) {
                return handleTopLevel();
              }

              @Override
              public SemiserialModule handleRemote(RemoteModuleId id) {
                return handleTopLevel();
              }

              @Override
              public SemiserialModule handleBundle(BundleModuleSubId id) {
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
                          return Semiserializer.semiserialize(
                              moduleContext, new BundleValue(new ImportId(id.module), path), null);
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
              public SemiserialModule handleRoot(RootModuleId id) {
                throw new Assertion();
              }
            });
    if (res == null) throw Error.moduleError;
    return new Module() {
      @Override
      public ImportId spec() {
        return importId;
      }

      @Override
      public SemiserialModule result() {
        return res;
      }
    };
  }
}
