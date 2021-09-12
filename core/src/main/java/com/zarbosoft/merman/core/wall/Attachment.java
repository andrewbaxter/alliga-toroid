package com.zarbosoft.merman.core.wall;

import com.zarbosoft.merman.core.Context;

public interface Attachment {
  default void setTransverse(Context context, double transverse) {}

  default void setConverse(Context context, double converse) {}

  default void setTransverseSpan(Context context, double ascent, double descent) {}

  void destroy(Context context);

  default void setBaselineTransverse(Context context, double baselineTransverse) {}
}
