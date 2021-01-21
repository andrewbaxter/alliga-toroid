package com.zarbosoft.merman.editor.visual;

import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.misc.ROMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Alignment {
	public int converse = 0;
	protected List<AlignmentListener> listeners = new ArrayList<>();

	public abstract void feedback(Context context, int converse);

	public void submit(final Context context) {
		for (final AlignmentListener listener : listeners) {
			listener.align(context);
		}
	}

	public void addListener(final Context context, final AlignmentListener listener) {
		listeners.add(listener);
	}

	public void removeListener(final Context context, final AlignmentListener listener) {
		listeners.remove(listener);
	}

	public abstract void root(Context context, ROMap<String, Alignment> parents);

	public abstract void destroy(Context context);
}
