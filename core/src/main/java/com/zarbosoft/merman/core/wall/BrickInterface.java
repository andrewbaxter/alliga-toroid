package com.zarbosoft.merman.core.wall;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.visual.alignment.Alignment;
import com.zarbosoft.merman.core.visual.VisualLeaf;

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

  Alignment findAlignment(String alignment);
}
