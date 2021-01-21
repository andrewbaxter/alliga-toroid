package com.zarbosoft.merman.editor.wall;

import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.visual.Alignment;
import com.zarbosoft.merman.editor.visual.VisualLeaf;
import com.zarbosoft.merman.misc.ROSet;
import com.zarbosoft.merman.misc.ROSetRef;
import com.zarbosoft.merman.misc.TSSet;
import com.zarbosoft.merman.syntax.style.Style;

public interface BrickInterface {
  VisualLeaf getVisual();

  /**
   * @param context
   * @return A new brick or null (no elements before or brick already exists)
   */
  Brick createPrevious(Context context);

  /**
   * @param context
   * @return A new brick or null (no elements afterward or brick already exists)
   */
  Brick createNext(Context context);

  void brickDestroyed(Context context);

  Alignment getAlignment(Style style);

  TSSet<String> getTags(Context context);
}
