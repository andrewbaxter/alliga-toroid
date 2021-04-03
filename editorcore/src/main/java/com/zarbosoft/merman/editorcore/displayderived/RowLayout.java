package com.zarbosoft.merman.editorcore.displayderived;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.display.CourseDisplayNode;
import com.zarbosoft.merman.core.display.Display;
import com.zarbosoft.merman.core.display.DisplayNode;
import com.zarbosoft.merman.core.display.Group;
import com.zarbosoft.merman.core.display.Text;

import java.util.ArrayList;
import java.util.List;

public class RowLayout {
	public final Group group;
	List<DisplayNode> nodes = new ArrayList<>();

	public RowLayout(final Context context) {
		this(context.display);
	}

	public RowLayout(final Display display) {
		this.group = display.group();
	}

	public void add(final DisplayNode node) {
		group.add(node);
		nodes.add(node);
	}

	public void layout() {
		double converse = 0;
		double maxAscent = 0;
		for (final DisplayNode node : nodes) {
			if (node instanceof CourseDisplayNode)
				maxAscent = Math.max(maxAscent, ((Text) node).ascent());
			else
				maxAscent = Math.max(maxAscent, node.transverseSpan());
		}
		for (final DisplayNode node : nodes) {
			if (node instanceof CourseDisplayNode) {
				((CourseDisplayNode) node).setBaselineTransverse(maxAscent);
			}
			node.setConverse(converse, false);
			converse += node.converseSpan();
		}
	}
}
