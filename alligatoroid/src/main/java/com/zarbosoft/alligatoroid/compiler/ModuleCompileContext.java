package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.model.ImportPath;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.ArtifactId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

public class ModuleCompileContext {
  public final ImportId importId;
  public final CompileContext compileContext;
  /**
   * TODO if module generates method that does evaluation then calls it in parallel, this will have
   * concurrent access. This should probably be moved into evaluation context then compiled into
   * module context thread safe post-evaluation (?)
   */
  public final TSList<Error> errors = new TSList<>();

  public final ImportPath importPath;
  /** Already desemiserialized artifacts generated via imports. */
  public final TSMap<ArtifactId, Exportable> artifactLookup = new TSMap<>();
  /**
   * A mapping of artifacts imported, used while semiserializing output after compilation to prevent
   * re-semiserializing artifacts from other modules.
   */
  public final TSMap<ObjId<Exportable>, ArtifactId> backArtifactLookup = new TSMap<>();

  public ModuleCompileContext(
      ImportId importId, CompileContext compileContext, ImportPath importPath) {
    this.importId = importId;
    this.compileContext = compileContext;
    this.importPath = importPath;
  }
}
