package com.zarbosoft.alligatoroid.compiler.modules.modulecompiler;

import com.zarbosoft.alligatoroid.compiler.CompileContext;
import com.zarbosoft.alligatoroid.compiler.Evaluator;
import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialModule;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.languageinout.LanguageDeserializer;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaClass;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaInternalName;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.ImportPath;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.ImportLoopPre;
import com.zarbosoft.alligatoroid.compiler.model.error.ImportNotFoundPre;
import com.zarbosoft.alligatoroid.compiler.model.error.Unexpected;
import com.zarbosoft.alligatoroid.compiler.model.error.UnknownImportFileTypePre;
import com.zarbosoft.alligatoroid.compiler.model.ids.BundleModuleSubId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RemoteModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RootModuleId;
import com.zarbosoft.alligatoroid.compiler.model.language.Block;
import com.zarbosoft.alligatoroid.compiler.modules.CacheImportIdRes;
import com.zarbosoft.alligatoroid.compiler.modules.Module;
import com.zarbosoft.alligatoroid.compiler.modules.ModuleResolver;
import com.zarbosoft.alligatoroid.compiler.modules.Source;
import com.zarbosoft.alligatoroid.compiler.mortar.ContinueError;
import com.zarbosoft.alligatoroid.compiler.mortar.DefinitionSet;
import com.zarbosoft.alligatoroid.compiler.mortar.LanguageElement;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Evaluation2Context;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarNullType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.BundleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.DataValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.Common;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.ReverseIterable;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;
import com.zarbosoft.rendaw.common.TSSet;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class ModuleCompiler implements ModuleResolver {
  TSMap<ImportId, Long> cacheIds = new TSMap<>();

  public static Value rootEvaluate(ModuleCompileContext moduleContext, LanguageElement rootStatement) {
    final String entryMethodName = "enter";
    String className = "com.zarbosoft.alligatoroidmortar.ModuleRoot";
    JavaInternalName jvmClassName = JavaBytecodeUtils.qualifiedName(className).asInternalName();

    // Do first pass flat evaluation + prep for 2nd pass
    MortarTargetModuleContext targetContext = new MortarTargetModuleContext(jvmClassName.value);
    JavaBytecodeBindingKey ectxKey = new JavaBytecodeBindingKey();
    final MortarDataType ectxType = Meta.autoMortarHalfDataTypes.get(Evaluation2Context.class);
    final Evaluator.RootEvaluateResult firstPass =
            Evaluator.evaluate(
                    moduleContext,
                    targetContext,
                    true,
                    rootStatement,
                    new TSOrderedMap<>(m -> m.put("ectx", new MortarDataBinding(ectxKey, ectxType))));
    moduleContext.log.addAll(firstPass.log);
    moduleContext.errors.addAll(firstPass.errors);
    if (firstPass.errors.some()) {
      return null;
    }
    MortarDataType resultType;
    if (firstPass.value instanceof DataValue) {
      resultType = ((DataValue) firstPass.value).mortarType();
    } else {
      resultType = MortarNullType.type;
    }

    boolean noDepErrors = true;
    for (DefinitionSet dependency : targetContext.dependencies) {
      if (!dependency.resolve(moduleContext)) {
        noDepErrors = false;
      }
    }
    if (!noDepErrors) return null;

    // Do 2nd pass jvm evaluation
    JavaClass preClass = new JavaClass(jvmClassName);
    for (ROPair<Exportable, String> e : Common.iterable(targetContext.transfers.iterator())) {
      preClass.defineStaticField(
              e.second, Meta.autoMortarHalfDataTypes.get(e.first.getClass()).jvmDesc());
    }
    preClass.defineFunction(
            entryMethodName,
            JavaMethodDescriptor.fromParts(resultType.jvmDesc(), new TSList<>(ectxType.jvmDesc())),
            new JavaBytecodeSequence()
                    .add(((MortarTargetCode) firstPass.code).e)
                    .add(resultType.returnBytecode()),
            new TSList<>(ectxKey));
    Class klass = moduleContext.compileContext.loadRootClass(className, preClass.render());
    for (ROPair<Exportable, String> e : Common.iterable(targetContext.transfers.iterator())) {
      uncheck(() -> klass.getDeclaredField(e.second).set(null, e.first));
    }
    Object resultVariable;
    final Evaluation2Context ectx2 = new Evaluation2Context(moduleContext.log, moduleContext);
    try {
      resultVariable =
              klass.getMethod(entryMethodName, Evaluation2Context.class).invoke(null, ectx2);
    } catch (IllegalAccessException | NoSuchMethodException e) {
      throw new Assertion();
    } catch (InvocationTargetException e0) {
      final Throwable e = e0.getTargetException();
      if (e.getClass() == ContinueError.class) {
        // Errors in module errors already
        resultVariable = null;
      } else {
        Location location = null; // TODO convert whole stack?
        for (StackTraceElement t : new ReverseIterable<>(Arrays.asList(e.getStackTrace()))) {
          if (t.getClassName().equals(className)) {
            location = firstPass.sourceMap.get(t.getLineNumber());
            break;
          }
        }
        if (location != null) {
          if (e instanceof Error.PreError) {
            moduleContext.errors.add(((Error.PreError) e).toError(location));
          } else {
            moduleContext.errors.add(new Unexpected(location, e));
          }
          return null;
        } else {
          throw uncheck(e);
        }
      }
    }
    for (ROPair<Location, CompletableFuture> d : moduleContext.deferredErrors) {
      try {
        Utils.await(d.second);
      } catch (Error.PreError e) {
        moduleContext.errors.add(e.toError(d.first));
      } catch (Exception e) {
        moduleContext.errors.add(new Unexpected(d.first, e));
      }
    }
    if (moduleContext.errors.some()) return null;
            return resultType.constAsValue(resultVariable);
  }

  private static SemiserialModule rootEvaluate(
      ModuleCompileContext moduleContext, ImportId spec, String path, InputStream source) {
    Block rootStatement;
    {
      final TSList<LanguageElement> res =
          LanguageDeserializer.deserialize(spec.moduleId, moduleContext.errors, path, source);
      if (res == null) {
        return null;
      }
      rootStatement = Block.create(null, res);
    }
    Value result = rootEvaluate(moduleContext, rootStatement);
    return Semiserializer.semiserialize(
            moduleContext, result, rootStatement.id);
  }

  /** Only whole-ish values */
  @Override
  public Module get(
      CompileContext context, ImportPath fromImportPath, CacheImportIdRes cacheId, Source source) {
    final ImportId importId = cacheId.importId;
    if (fromImportPath != null) {
      TSList<ImportId> found = fromImportPath.find(new TSSet<>(), importId);
      if (found != null) {
        throw new ImportLoopPre(found);
      }
    }

    ImportPath importPath = new ImportPath(importId);
    if (fromImportPath != null) importPath.add(fromImportPath);

    ModuleCompileContext moduleContext =
        new ModuleCompileContext(importId, cacheId.cacheId, context, importPath);
    context.moduleErrors.put(importId, moduleContext.errors);
    context.moduleLog.put(importId, moduleContext.log);

    SemiserialModule res =
        importId.moduleId.dispatch(
            new ModuleId.Dispatcher<SemiserialModule>() {
              public SemiserialModule handleTopLevel() {
                final String stringPath = source.path.toString();
                if (stringPath.endsWith(".at")) {
                  return uncheck(
                      () -> {
                        try (InputStream stream = Files.newInputStream(source.path)) {
                          return rootEvaluate(moduleContext, importId, stringPath, stream);
                        }
                      });
                } else if (stringPath.endsWith(".zip")) {
                  return Semiserializer.semiserialize(
                      moduleContext, BundleValue.create(importId, ""), null);
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
                              moduleContext,
                              BundleValue.create(ImportId.create(id.module), path),
                              null);
                        }
                        if (path.endsWith(".at")) {
                          try (InputStream stream = bundle.getInputStream(e)) {
                            return rootEvaluate(moduleContext, importId, id.toString(), stream);
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
      public long cacheId() {
        return cacheId.cacheId;
      }

      @Override
      public ImportId spec() {
        return cacheId.importId;
      }

      @Override
      public SemiserialModule result() {
        return res;
      }
    };
  }

  @Override
  public CacheImportIdRes getCacheId(ImportId id) {
    synchronized (cacheIds) {
      return new CacheImportIdRes(id, cacheIds.getCreate(id, () -> (long) (cacheIds.size() + 1)));
    }
  }
}
