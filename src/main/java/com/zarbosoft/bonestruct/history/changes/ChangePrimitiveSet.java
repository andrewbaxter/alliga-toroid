package com.zarbosoft.bonestruct.history.changes;

import com.zarbosoft.bonestruct.document.values.ValuePrimitive;
import com.zarbosoft.bonestruct.editor.Context;
import com.zarbosoft.bonestruct.history.Change;

public class ChangePrimitiveSet extends Change {
	private final ValuePrimitive data;
	private String value;

	public ChangePrimitiveSet(final ValuePrimitive data, final String value) {
		this.data = data;
		this.value = value;
	}

	@Override
	public boolean merge(final Change other) {
		final ChangePrimitiveSet other2;
		try {
			other2 = (ChangePrimitiveSet) other;
		} catch (final ClassCastException e) {
			return false;
		}
		if (other2.data != data)
			return false;
		value = other2.value;
		return true;
	}

	@Override
	public Change apply(final Context context) {
		final Change reverse = new ChangePrimitiveSet(data, data.value.toString());
		data.value = new StringBuilder(value);
		for (final ValuePrimitive.Listener listener : data.listeners)
			listener.set(context, value);
		return reverse;
	}

	@Override
	public com.zarbosoft.bonestruct.document.values.Value getValue() {
		return data;
	}
}