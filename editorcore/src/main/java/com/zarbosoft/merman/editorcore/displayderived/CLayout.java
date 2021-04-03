package com.zarbosoft.merman.editorcore.displayderived;

import com.zarbosoft.merman.core.display.Display;
import com.zarbosoft.merman.core.display.DisplayNode;
import com.zarbosoft.merman.core.display.Group;

import java.util.ArrayList;
import java.util.List;

public class CLayout {
	public final Group group;
	List<DisplayNode> nodes = new ArrayList<>();

	public CLayout(final Group group) {
		this.group = group;
	}

	public CLayout(final Display display) {
		this.group = display.group();
	}

	public void add(final DisplayNode node) {
		group.add(node);
		nodes.add(node);
	}

	public void layout() {
		double converse = 0;
		for (final DisplayNode node : nodes) {
			node.setConverse(converse, false);
			converse += node.converseSpan();
		}
	}
}
