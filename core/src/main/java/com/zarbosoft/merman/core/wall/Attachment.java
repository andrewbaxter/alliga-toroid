package com.zarbosoft.merman.core.wall;

import com.zarbosoft.merman.core.Context;

public abstract class Attachment {
	public void setTransverse(final Context context, final double transverse) {
	}

	public void setConverse(final Context context, final double converse) {
	}

	public void setTransverseSpan(final Context context, final double ascent, final double descent) {
	}

	public abstract void destroy(Context context);
}
