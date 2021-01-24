package com.zarbosoft.merman;

import com.zarbosoft.merman.editor.display.Group;
import com.zarbosoft.merman.editor.display.MockeryDisplay;
import com.zarbosoft.merman.editor.display.MockeryGroup;
import com.zarbosoft.merman.editor.display.Text;
import com.zarbosoft.merman.editor.display.derived.CLayout;
import com.zarbosoft.merman.editor.display.derived.ColumnarTableLayout;
import com.zarbosoft.merman.editor.display.derived.RowLayout;
import com.zarbosoft.rendaw.common.Pair;
import com.zarbosoft.rendaw.common.TSList;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class TestDisplayDerived {

	@Test
	public void testColumnarTableLayout() {
		final MockeryDisplay display = new MockeryDisplay();
		final ColumnarTableLayout layout = new ColumnarTableLayout(display, 35);
		{
			final Group leftGroup = display.group();
			final Text left = display.text();
			left.setText(null, "1");
			left.setTransverse(null, 8);
			leftGroup.add(left);
			final Text right = display.text();
			right.setText(null, "aaa");
			layout.add(TSList.of(leftGroup, right));
		}
		{
			final Text left = display.text();
			left.setText(null, "333");
			final Text right = display.text();
			right.setText(null, "bb");
			layout.add(TSList.of(left, right));
		}
		{
			final Text left = display.text();
			left.setText(null, "22");
			final Text right = display.text();
			right.setText(null, "c");
			layout.add(TSList.of(left, right));
		}
		{
			final Text left = display.text();
			left.setText(null, "4444");
			final Text right = display.text();
			right.setText(null, "dddd");
			layout.add(TSList.of(left, right));
		}
		layout.layout(null);
		int index = 0;
		for (final Pair<Integer, Integer> pair : TSList.of(new Pair<>(0, 0),
				new Pair<>(30, 10),
				new Pair<>(0, 20),
				new Pair<>(30, 20),
				new Pair<>(0, 30),
				new Pair<>(30, 30),
				new Pair<>(60, 8),
				new Pair<>(100, 8)
		)) {
			final int index2 = index++;
			assertThat(String.format("for index %s, converse", index2),
					((MockeryGroup) layout.group).get(index2).converse(null),
					equalTo(pair.first)
			);
			assertThat(String.format("for index %s, transverse", index2),
					((MockeryGroup) layout.group).get(index2).transverse(null),
					equalTo(pair.second)
			);
		}
	}

	@Test
	public void testCLayout() {
		final MockeryDisplay display = new MockeryDisplay();
		final CLayout layout = new CLayout(display);
		{
			final Group itemGroup = display.group();
			final Text item = display.text();
			item.setText(null, "dog");
			item.setTransverse(null, 8);
			itemGroup.add(item);
			layout.add(itemGroup);
		}
		{
			final Text item = display.text();
			item.setText(null, "donut");
			layout.add(item);
		}
		{
			final Group itemGroup = display.group();
			final Text item = display.text();
			item.setText(null, "9");
			item.setTransverse(null, 8);
			itemGroup.add(item);
			layout.add(itemGroup);
		}
		{
			final Text item = display.text();
			item.setText(null, "apple");
			layout.add(item);
		}
		layout.layout(null);
		int index = 0;
		for (final Pair<Integer, Integer> pair : TSList.of(new Pair<>(0, 0),
				new Pair<>(30, 0),
				new Pair<>(80, 0),
				new Pair<>(90, 0)
		)) {
			final int index2 = index++;
			assertThat(String.format("for index %s, converse", index2),
					((MockeryGroup) layout.group).get(index2).converse(null),
					equalTo(pair.first)
			);
			assertThat(String.format("for index %s, transverse", index2),
					((MockeryGroup) layout.group).get(index2).transverse(null),
					equalTo(pair.second)
			);
		}
	}

	@Test
	public void testRowLayout() {
		final MockeryDisplay display = new MockeryDisplay();
		final RowLayout layout = new RowLayout(display);
		{
			final Group itemGroup = display.group();
			final Text item = display.text();
			item.setText(null, "dog");
			item.setTransverse(null, 8);
			itemGroup.add(item);
			layout.add(itemGroup);
		}
		{
			final Text item = display.text();
			item.setText(null, "donut");
			layout.add(item);
		}
		{
			final Group itemGroup = display.group();
			final Text item = display.text();
			item.setText(null, "9");
			item.setTransverse(null, 8);
			itemGroup.add(item);
			layout.add(itemGroup);
		}
		{
			final Text item = display.text();
			item.setText(null, "apple");
			layout.add(item);
		}
		layout.layout(null);
		int index = 0;
		for (final Pair<Integer, Integer> pair : TSList.of(new Pair<>(0, 0),
				new Pair<>(30, 10),
				new Pair<>(80, 0),
				new Pair<>(90, 10)
		)) {
			final int index2 = index++;
			assertThat(String.format("for index %s, converse", index2),
					((MockeryGroup) layout.group).get(index2).converse(null),
					equalTo(pair.first)
			);
			assertThat(String.format("for index %s, transverse", index2),
					((MockeryGroup) layout.group).get(index2).transverse(null),
					equalTo(pair.second)
			);
		}
	}
}
