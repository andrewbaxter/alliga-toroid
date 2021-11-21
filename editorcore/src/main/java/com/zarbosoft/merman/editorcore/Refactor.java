package com.zarbosoft.merman.editorcore;

import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.rendaw.common.ROList;

public interface Refactor {
  RefactorMatch check(ROList<Atom> atoms);
}
