package com.zarbosoft.merman.document.values;

import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.history.Change;
import com.zarbosoft.merman.editor.history.changes.ChangePrimitiveAdd;
import com.zarbosoft.merman.editor.history.changes.ChangePrimitiveRemove;
import com.zarbosoft.merman.editor.visual.visuals.VisualPrimitive;
import com.zarbosoft.merman.syntax.back.BackSpecData;
import com.zarbosoft.merman.syntax.back.BaseBackPrimitiveSpec;

import java.util.HashSet;
import java.util.Set;

public class ValuePrimitive extends Value {
	public VisualPrimitive visual;
	public final BaseBackPrimitiveSpec middle;
	public StringBuilder data;
	public final Set<Listener> listeners = new HashSet<>();

	public ValuePrimitive(final BaseBackPrimitiveSpec middle, final String data) {
		this.middle = middle;
		this.data = new StringBuilder(data);
	}

	@Override
	public boolean selectDown(final Context context) {
		if (context.window) {
			if (visual == null) {
				context.createWindowForSelection(this, context.syntax.ellipsizeThreshold);
			}
		}
		final int length = data.length();
		visual.select(context, true, length, length);
		return true;
	}

  @Override
  public Object syntaxLocateStep(String segment) {
    return null;
  }

  public void addListener(final Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(final Listener listener) {
		listeners.remove(listener);
	}

	public String get() {
		return data.toString();
	}

	public int length() {
		return data.length();
	}

	@Override
	public BackSpecData back() {
		return middle;
	}

	public Change changeRemove(final int begin, final int length) {
		return new ChangePrimitiveRemove(this, begin, length);
	}

	public Change changeAdd(final int begin, final String text) {
		return new ChangePrimitiveAdd(this, begin, text);
	}

	public interface Listener {
		void set(Context context, String value);

		void added(Context context, int index, String value);

		void removed(Context context, int index, int count);

	}

}
