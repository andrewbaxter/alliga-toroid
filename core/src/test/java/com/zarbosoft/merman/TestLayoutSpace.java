package com.zarbosoft.merman;

import com.zarbosoft.merman.core.syntax.FreeAtomType;
import com.zarbosoft.merman.core.syntax.Syntax;
import com.zarbosoft.merman.core.syntax.style.SplitMode;
import com.zarbosoft.merman.helper.FrontMarkBuilder;
import com.zarbosoft.merman.helper.GeneralTestWizard;
import com.zarbosoft.merman.helper.GroupBuilder;
import com.zarbosoft.merman.helper.Helper;
import com.zarbosoft.merman.helper.SyntaxBuilder;
import com.zarbosoft.merman.helper.TreeBuilder;
import com.zarbosoft.merman.helper.TypeBuilder;
import org.junit.Test;

public class TestLayoutSpace {
  @Test
  public void testLayoutInitial() {
    final FreeAtomType one =
        new TypeBuilder("one")
            .back(Helper.buildBackPrimitive("one"))
            .frontSpace(SplitMode.ALWAYS)
            .front(new FrontMarkBuilder("one").build())
            .frontSpace(SplitMode.ALWAYS)
            .build();
    final Syntax syntax =
        new SyntaxBuilder("any")
            .type(one)
            .group("any", new GroupBuilder().type(one).build())
            .build();
    new GeneralTestWizard(syntax, new TreeBuilder(one).build())
        .checkSpaceBrick(0, 0)
        .checkTextBrick(0, 1, "one")
        .checkSpaceBrick(1, 0);
  }
}
