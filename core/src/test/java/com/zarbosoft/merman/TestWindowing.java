package com.zarbosoft.merman;

import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.document.values.ValueArray;
import com.zarbosoft.merman.document.values.ValueAtom;
import com.zarbosoft.merman.editor.Path;
import com.zarbosoft.merman.helper.BackRecordBuilder;
import com.zarbosoft.merman.helper.FrontDataArrayBuilder;
import com.zarbosoft.merman.helper.GeneralTestWizard;
import com.zarbosoft.merman.helper.GroupBuilder;
import com.zarbosoft.merman.helper.Helper;
import com.zarbosoft.merman.helper.StyleBuilder;
import com.zarbosoft.merman.helper.SyntaxBuilder;
import com.zarbosoft.merman.helper.TreeBuilder;
import com.zarbosoft.merman.helper.TypeBuilder;
import com.zarbosoft.merman.syntax.FreeAtomType;
import com.zarbosoft.merman.syntax.Syntax;
import org.junit.Test;

public class TestWindowing {
  public static final FreeAtomType a0_0;
  public static final FreeAtomType a1_0;
  public static final FreeAtomType a2_0;
  public static final FreeAtomType a3_0;
  public static final FreeAtomType a4;
  public static final FreeAtomType a5;
  public static final FreeAtomType a0_1;
  public static final FreeAtomType a1_1;
  public static final FreeAtomType a2_1;
  public static final FreeAtomType a3_1;
  public static final FreeAtomType oneAtom;
  public static final FreeAtomType array;

  static {
    a0_0 = new TypeBuilder("a0_0").back(Helper.buildBackPrimitive("a0_0")).frontMark("0_0").build();
    a1_0 = new TypeBuilder("a1_0").back(Helper.buildBackPrimitive("a1_0")).frontMark("1_0").build();
    a2_0 = new TypeBuilder("a2_0").back(Helper.buildBackPrimitive("a2_0")).frontMark("2_0").build();
    a3_0 = new TypeBuilder("a3_0").back(Helper.buildBackPrimitive("a3_0")).frontMark("3_0").build();
    a4 = new TypeBuilder("a4").back(Helper.buildBackPrimitive("a4")).frontMark("4").build();
    a5 = new TypeBuilder("a5").back(Helper.buildBackPrimitive("a5")).frontMark("5").build();
    a0_1 = new TypeBuilder("a0_1").back(Helper.buildBackPrimitive("a0_1")).frontMark("0_1").build();
    a1_1 = new TypeBuilder("a1_1").back(Helper.buildBackPrimitive("a1_1")).frontMark("1_1").build();
    a2_1 = new TypeBuilder("a2_1").back(Helper.buildBackPrimitive("a2_1")).frontMark("2_1").build();
    a3_1 = new TypeBuilder("a3_1").back(Helper.buildBackPrimitive("a3_1")).frontMark("3_1").build();
    oneAtom =
        new TypeBuilder("oneAtom")
            .back(
                new BackRecordBuilder()
                    .add("stop", Helper.buildBackDataAtom("value", "any"))
                    .build())
            .frontDataNode("value")
            .depthScore(1)
            .build();
    array =
        new TypeBuilder("array")
            .back(Helper.buildBackDataArray("value", "any"))
            .front(new FrontDataArrayBuilder("value").build())
            .depthScore(1)
            .build();
  }

  @Test
  public void testInitialNoWindow() {
    int i = 0;
    start(false)
        .checkTextBrick(i++, 0, "0_0")
        .checkTextBrick(i++, 0, "1_0")
        .checkTextBrick(i++, 0, "2_0")
        .checkTextBrick(i++, 0, "3_0")
        .checkTextBrick(i++, 0, "4")
        .checkTextBrick(i++, 0, "5")
        .checkTextBrick(i++, 0, "3_1")
        .checkTextBrick(i++, 0, "2_1")
        .checkTextBrick(i++, 0, "1_1")
        .checkTextBrick(i++, 0, "0_1");
  }

  public GeneralTestWizard start(final boolean startWindowed) {
    final Syntax out =
        new SyntaxBuilder("any")
            .type(a0_0)
            .type(a1_0)
            .type(a2_0)
            .type(a3_0)
            .type(a4)
            .type(a5)
            .type(a0_1)
            .type(a1_1)
            .type(a2_1)
            .type(a3_1)
            .type(oneAtom)
            .type(array)
            .group(
                "any",
                new GroupBuilder()
                    .type(a0_0)
                    .type(a1_0)
                    .type(a2_0)
                    .type(a3_0)
                    .type(a4)
                    .type(a5)
                    .type(a0_1)
                    .type(a1_1)
                    .type(a2_1)
                    .type(a3_1)
                    .type(oneAtom)
                    .type(array)
                    .build())
            .style(new StyleBuilder().split(true).build())
            .build();
    final Syntax syntax = out;
    GeneralTestWizard generalTestWizard =
        new GeneralTestWizard(
            syntax,
            startWindowed,
            new TreeBuilder(oneAtom)
                .add(
                    "value",
                    new TreeBuilder(oneAtom).add("value", new TreeBuilder(a0_0).build()).build())
                .build(),
            new TreeBuilder(array)
                .addArray(
                    "value",
                    new TreeBuilder(a1_0).build(),
                    new TreeBuilder(array)
                        .addArray(
                            "value",
                            new TreeBuilder(a2_0).build(),
                            new TreeBuilder(array)
                                .addArray(
                                    "value",
                                    new TreeBuilder(a3_0).build(),
                                    new TreeBuilder(array)
                                        .addArray("value", new TreeBuilder(a4).build())
                                        .build(),
                                    new TreeBuilder(oneAtom)
                                        .add("value", new TreeBuilder(a5).build())
                                        .build(),
                                    new TreeBuilder(a3_1).build())
                                .build(),
                            new TreeBuilder(a2_1).build())
                        .build(),
                    new TreeBuilder(a1_1).build())
                .build(),
            new TreeBuilder(a0_1).build());
    generalTestWizard.inner.context.ellipsizeThreshold = 3;
    return generalTestWizard;
  }

  @Test
  public void testInitialWindow() {
    int i = 0;
    start(true)
        .checkTextBrick(i++, 0, "0_0")
        .checkTextBrick(i++, 0, "1_0")
        .checkTextBrick(i++, 0, "2_0")
        .checkTextBrick(i++, 0, "...")
        .checkTextBrick(i++, 0, "2_1")
        .checkTextBrick(i++, 0, "1_1")
        .checkTextBrick(i++, 0, "0_1");
  }

  @Test
  public void testWindowArray() {
    int i = 0;
    start(false)
        .run(
            context ->
                ((Atom) context.syntaxLocate(new Path("value", "1"))).valueParentRef.selectValue(context))
        .act("window")
        .checkTextBrick(i++, 0, "1_0")
        .checkTextBrick(i++, 0, "2_0")
        .checkTextBrick(i++, 0, "3_0")
        .checkTextBrick(i++, 0, "...")
        .checkTextBrick(i++, 0, "...")
        .checkTextBrick(i++, 0, "3_1")
        .checkTextBrick(i++, 0, "2_1")
        .checkTextBrick(i++, 0, "1_1");
  }

  @Test
  public void testWindowArrayUnselectable() {
    int i = 0;
    start(false)
        .run(
            context ->
                ((Atom) context.syntaxLocate(new Path("value", "2"))).valueParentRef.selectValue(context))
        .checkTextBrick(0, 0, "0_0")
        .act("window")
        .checkTextBrick(i++, 0, "0_0")
        .checkTextBrick(i++, 0, "1_0")
        .checkTextBrick(i++, 0, "2_0")
        .checkTextBrick(i++, 0, "3_0")
        .checkTextBrick(i++, 0, "4")
        .checkTextBrick(i++, 0, "5")
        .checkTextBrick(i++, 0, "3_1")
        .checkTextBrick(i++, 0, "2_1")
        .checkTextBrick(i++, 0, "1_1")
        .checkTextBrick(i++, 0, "0_1");
  }

  @Test
  public void testRewindowArray() {
    int i = 0;
    start(false)
        .run(
            context ->
                ((Atom) context.syntaxLocate(new Path("value", "1"))).valueParentRef.selectValue(context))
        .act("window")
        .run(
            context ->
                ((Atom) context.syntaxLocate(new Path("value", "1", "value", "1")))
                    .valueParentRef.selectValue(context))
        .act("window")
        .checkTextBrick(i++, 0, "2_0")
        .checkTextBrick(i++, 0, "3_0")
        .checkTextBrick(i++, 0, "4")
        .checkTextBrick(i++, 0, "5")
        .checkTextBrick(i++, 0, "3_1")
        .checkTextBrick(i++, 0, "2_1");
  }

  @Test
  public void testWindowAtom() {
    int i = 0;
    start(false)
        .run(
            context ->
                ((ValueAtom) context.syntaxLocate(new Path("value", "0", "value")))
                    .selectInto(context))
        .act("window")
        .checkTextBrick(i++, 0, "0_0");
  }

  @Test
  public void testWindowAtomUnselectable() {
    int i = 0;
    start(false)
        .run(
            context ->
                ((ValueAtom) context.syntaxLocate(new Path("value", "0", "value", "atom", "value")))
                    .selectInto(context))
        .act("window")
        .checkTextBrick(i++, 0, "0_0")
        .checkTextBrick(i++, 0, "1_0")
        .checkTextBrick(i++, 0, "2_0")
        .checkTextBrick(i++, 0, "3_0")
        .checkTextBrick(i++, 0, "4")
        .checkTextBrick(i++, 0, "5")
        .checkTextBrick(i++, 0, "3_1")
        .checkTextBrick(i++, 0, "2_1")
        .checkTextBrick(i++, 0, "1_1")
        .checkTextBrick(i++, 0, "0_1");
  }

  @Test
  public void testWindowMaxDepth() {
    int i = 0;
    start(false)
        .run(
            context ->
                ((Atom)
                        context.syntaxLocate(
                            new Path("value", "1", "value", "1", "value", "1", "value", "1")))
                    .valueParentRef.selectValue(context))
        .act("window")
        .checkTextBrick(i++, 0, "4");
  }

  @Test
  public void testWindowDown() {
    int i = 0;
    start(false)
        .run(
            context ->
                ((Atom) context.syntaxLocate(new Path("value", "1"))).valueParentRef.selectValue(context))
        .act("window")
        .run(
            context ->
                ((Atom) context.syntaxLocate(new Path("value", "1", "value", "1", "value", "1")))
                    .valueParentRef.selectValue(context))
        .act("window_down")
        .checkTextBrick(i++, 0, "2_0")
        .checkTextBrick(i++, 0, "3_0")
        .checkTextBrick(i++, 0, "4")
        .checkTextBrick(i++, 0, "5")
        .checkTextBrick(i++, 0, "3_1")
        .checkTextBrick(i++, 0, "2_1");
  }

  @Test
  public void testWindowDownMaxDepth() {
    int i = 0;
    start(false)
        .run(
            context ->
                ((Atom)
                        context.syntaxLocate(
                            new Path("value", "1", "value", "1", "value", "1", "value", "1")))
                    .valueParentRef.selectValue(context))
        .act("window")
        .act("window_down")
        .checkTextBrick(i++, 0, "4");
  }

  @Test
  public void testWindowUp() {
    int i = 0;
    start(false)
        .run(
            context ->
                ((Atom)
                        context.syntaxLocate(
                            new Path("value", "1", "value", "1", "value", "1", "value", "1")))
                    .valueParentRef.selectValue(context))
        .act("window")
        .checkCourseCount(1)
        .checkTextBrick(0, 0, "4")
        .act("window_up")
        .checkTextBrick(i++, 0, "3_0")
        .checkTextBrick(i++, 0, "4")
        .checkTextBrick(i++, 0, "5")
        .checkTextBrick(i++, 0, "3_1");
  }

  /**
   * Moving the window up to the root node shows all root level items
   */
  @Test
  public void testWindowUpRoot() {
    int i = 0;
    start(true)
        .run(
            context ->
                ((ValueArray) context.syntaxLocate(new Path("value"))).select(context,true,1,1))
        .act("window")
        .checkTextBrick(0, 0, "1_0")
        .act("window_up")
            .dumpCourses()
        .checkTextBrick(i++, 0, "0_0")
        .checkTextBrick(i++, 0, "1_0")
        .checkTextBrick(i++, 0, "2_0")
        .checkTextBrick(i++, 0, "...")
        .checkTextBrick(i++, 0, "2_1")
        .checkTextBrick(i++, 0, "1_1")
        .checkTextBrick(i++, 0, "0_1");
  }

  @Test
  public void testWindowClear() {
    int i = 0;
    start(false)
        .run(
            context ->
                ((Atom) context.syntaxLocate(new Path("value", "1"))).valueParentRef.selectValue(context))
        .act("window")
        .act("window_clear")
        .checkTextBrick(i++, 0, "0_0")
        .checkTextBrick(i++, 0, "1_0")
        .checkTextBrick(i++, 0, "2_0")
        .checkTextBrick(i++, 0, "3_0")
        .checkTextBrick(i++, 0, "4")
        .checkTextBrick(i++, 0, "5")
        .checkTextBrick(i++, 0, "3_1")
        .checkTextBrick(i++, 0, "2_1")
        .checkTextBrick(i++, 0, "1_1")
        .checkTextBrick(i++, 0, "0_1");
  }

  @Test
  public void testWindowSelectArrayNoChange() {
    int i = 0;
    start(false)
        .run(
            context ->
                ((Atom) context.syntaxLocate(new Path("value", "1"))).valueParentRef.selectValue(context))
        .act("window")
        .checkTextBrick(0, 0, "1_0")
        .run(
            context ->
                ((Atom) context.syntaxLocate(new Path("value", "1", "value", "1")))
                    .valueParentRef.selectValue(context))
        .checkTextBrick(i++, 0, "1_0")
        .checkTextBrick(i++, 0, "2_0")
        .checkTextBrick(i++, 0, "3_0")
        .checkTextBrick(i++, 0, "...")
        .checkTextBrick(i++, 0, "...")
        .checkTextBrick(i++, 0, "3_1")
        .checkTextBrick(i++, 0, "2_1")
        .checkTextBrick(i++, 0, "1_1");
  }

  @Test
  public void testWindowSelectArrayEllipsis() {
    int i = 0;
    start(false)
        .run(
            context ->
                ((Atom) context.syntaxLocate(new Path("value", "1"))).valueParentRef.selectValue(context))
        .act("window")
        .run(
            context ->
                ((Atom) context.syntaxLocate(new Path("value", "1", "value", "1", "value", "1")))
                    .valueParentRef.selectValue(context))
        .act("enter")
        .checkTextBrick(i++, 0, "1_0")
        .checkTextBrick(i++, 0, "2_0")
        .checkTextBrick(i++, 0, "3_0")
        .checkTextBrick(i++, 0, "...")
        .checkTextBrick(i++, 0, "...")
        .checkTextBrick(i++, 0, "3_1")
        .checkTextBrick(i++, 0, "2_1")
        .checkTextBrick(i++, 0, "1_1");
  }

  @Test
  public void testWindowSelectArrayOutside() {
    int i = 0;
    start(true)
        .run(
            context ->
                ((Atom) context.syntaxLocate(new Path("value", "0"))).valueParentRef.selectValue(context))
        .act("window")
        .checkTextBrick(0, 0, "0_0")
        .checkCourseCount(1)
        .run(
            context ->
                ((Atom)
                        context.syntaxLocate(
                            new Path("value", "1", "value", "1", "value", "1", "value", "1")))
                    .valueParentRef.selectValue(context))
        .checkTextBrick(i++, 0, "1_0")
        .checkTextBrick(i++, 0, "2_0")
        .checkTextBrick(i++, 0, "3_0")
        .checkTextBrick(i++, 0, "...")
        .checkTextBrick(i++, 0, "...")
        .checkTextBrick(i++, 0, "3_1")
        .checkTextBrick(i++, 0, "2_1")
        .checkTextBrick(i++, 0, "1_1");
  }

  @Test
  public void testWindowSelectArrayOutsideRoot() {
    int i = 0;
    start(true)
        .run(
            context ->
                ((Atom) context.syntaxLocate(new Path("value", "0"))).valueParentRef.selectValue(context))
        .act("window")
        .checkTextBrick(0, 0, "0_0")
        .checkCourseCount(1)
        .run(
            context ->
                ((Atom) context.syntaxLocate(new Path("value", "1", "value", "1")))
                    .valueParentRef.selectValue(context))
        .checkTextBrick(i++, 0, "0_0")
        .checkTextBrick(i++, 0, "1_0")
        .checkTextBrick(i++, 0, "2_0")
        .checkTextBrick(i++, 0, "...")
        .checkTextBrick(i++, 0, "2_1")
        .checkTextBrick(i++, 0, "1_1")
        .checkTextBrick(i++, 0, "0_1");
  }

  @Test
  public void testWindowSelectArrayAbove() {
    int i = 0;
    start(true)
        .run(
            context ->
                ((Atom)
                        context.syntaxLocate(
                            new Path("value", "1", "value", "1", "value", "1", "value", "1")))
                    .valueParentRef.selectValue(context))
        .act("window")
        .checkTextBrick(0, 0, "4")
        .checkCourseCount(1)
        .act("exit")
        .checkTextBrick(i++, 0, "3_0")
        .checkTextBrick(i++, 0, "4")
        .checkTextBrick(i++, 0, "5")
        .checkTextBrick(i++, 0, "3_1");
  }
}
