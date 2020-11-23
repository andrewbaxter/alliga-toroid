package com.zarbosoft.merman.document.values;

import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.Path;
import com.zarbosoft.merman.editor.history.changes.ChangeNodeSet;
import com.zarbosoft.merman.editor.visual.visuals.VisualNested;
import com.zarbosoft.merman.syntax.middle.MiddleAtomSpec;
import com.zarbosoft.merman.syntax.middle.MiddleSpec;

import java.util.HashSet;
import java.util.Set;

public class ValueAtom extends Value {
	public VisualNested visual;
	private final MiddleAtomSpec middle;
	public Atom data = null; // INVARIANT: Never null when in tree
	public final Set<Listener> listeners = new HashSet<>();

	public abstract static class Listener {
		public abstract void set(Context context, Atom atom);
	}

	public class NodeParent extends Parent {
		@Override
		public void replace(final Context context, final Atom atom) {
			context.history.apply(context, new ChangeNodeSet(ValueAtom.this, atom));
		}

		@Override
		public void delete(final Context context) {
			context.history.apply(context, new ChangeNodeSet(ValueAtom.this, context.syntax.gap.create()));
		}

		@Override
		public String childType() {
			return middle.type;
		}

		@Override
		public Value value() {
			return ValueAtom.this;
		}

		@Override
		public String id() {
			return middle.id;
		}

		@Override
		public Path path() {
			return ValueAtom.this.getPath();
		}

		@Override
		public boolean selectUp(final Context context) {
			select(context);
			return true;
		}
	}

	public ValueAtom(final MiddleAtomSpec middle, final Atom data) {
		this.middle = middle;
		this.data = data;
		if (data != null)
			data.setParent(new NodeParent());
	}

	@Override
	public boolean selectDown(final Context context) {
		select(context);
		return true;
	}

	public void select(final Context context) {
		if (context.window) {
			if (visual == null || data.visual == null) {
				context.createWindowForSelection(this, context.syntax.ellipsizeThreshold);
			}
		}
		visual.select(context);
	}

	public void addListener(final Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(final Listener listener) {
		listeners.remove(listener);
	}

	public Atom get() {
		return data;
	}

	@Override
	public MiddleSpec middle() {
		return middle;
	}
}
