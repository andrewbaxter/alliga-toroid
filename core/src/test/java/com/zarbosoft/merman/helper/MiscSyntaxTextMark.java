package com.zarbosoft.merman.helper;

import com.zarbosoft.merman.syntax.FreeAtomType;
import com.zarbosoft.merman.syntax.Syntax;

public class MiscSyntaxTextMark {
  {
    FreeAtomType one =
      new TypeBuilder("one").back(Helper.buildBackPrimitive("one")).front(new FrontMarkBuilder("one").build()).build();
    FreeAtomType two =
      new TypeBuilder("two").back(Helper.buildBackPrimitive("two")).front(new FrontMarkBuilder("two").build()).build();
    FreeAtomType three = new TypeBuilder("three")
      .back(Helper.buildBackPrimitive("three"))
      .front(new FrontMarkBuilder("three").build())
      .build();
    FreeAtomType textMark1 = new TypeBuilder("textmark1")
      .middlePrimitiveLetters("text")
      .middleAtom("atom", "any")
      .back(new BackRecordBuilder()
        .add("text", Helper.buildBackDataPrimitive("text"))
        .add("atom", Helper.buildBackDataAtom("atom"))
        .build())
      .frontDataPrimitive("text")
      .frontMark("$1")
      .frontDataNode("atom")
      .autoComplete(1)
      .build();
    FreeAtomType textMark2 = new TypeBuilder("textmark2")
      .middlePrimitiveLetters("text")
      .middleAtom("atom", "any")
      .middleAtom("atom2", "any")
      .back(new BackRecordBuilder()
        .add("text", Helper.buildBackDataPrimitive("text"))
        .add("atom", Helper.buildBackDataAtom("atom"))
        .add("atom2", Helper.buildBackDataAtom("atom2"))
        .build())
      .frontDataPrimitive("text")
      .frontMark("$2")
      .frontDataNode("atom")
      .frontDataNode("atom2")
      .autoComplete(1)
      .build();
    FreeAtomType textMark3 = new TypeBuilder("textmark3")
      .middlePrimitiveLetters("text")
      .middleAtom("atom", "any")
      .middleAtom("atom2", "any")
      .back(new BackRecordBuilder()
        .add("text", Helper.buildBackDataPrimitive("text"))
        .add("atom", Helper.buildBackDataAtom("atom"))
        .add("atom2", Helper.buildBackDataAtom("atom2"))
        .build())
      .frontDataNode("atom2")
      .frontDataPrimitive("text")
      .frontMark("$3")
      .frontDataNode("atom")
      .autoComplete(1)
      .build();
    FreeAtomType ambiguateTextMark3 = new TypeBuilder("atextmark3")
      .middleAtom("atom", "any")
      .back(new BackRecordBuilder().add("atom", Helper.buildBackDataAtom("atom")).build())
      .frontDataNode("atom")
      .frontMark("t")
      .autoComplete(1)
      .build();
    Syntax syntax = new SyntaxBuilder("any")
      .type(one)
      .type(two)
      .type(three)
      .type(textMark1)
      .type(textMark2)
      .type(textMark3)
      .type(ambiguateTextMark3)
      .group(
        "any",
        new GroupBuilder()
          .type(one)
          .type(two)
          .type(three)
          .type(textMark1)
          .type(textMark2)
          .type(textMark3)
          .type(ambiguateTextMark3)
          .build()
      )
      .build();


  }
}
