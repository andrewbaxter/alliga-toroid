package com.zarbosoft.webviewsyntaxexamples;

import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.MultiError;
import com.zarbosoft.merman.core.example.DirectStylist;
import com.zarbosoft.merman.core.example.SyntaxOut;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.BackType;
import com.zarbosoft.merman.core.syntax.Direction;
import com.zarbosoft.merman.core.syntax.FreeAtomType;
import com.zarbosoft.merman.core.syntax.GapAtomType;
import com.zarbosoft.merman.core.syntax.SuffixGapAtomType;
import com.zarbosoft.merman.core.syntax.Syntax;
import com.zarbosoft.merman.core.syntax.alignments.RelativeAlignmentSpec;
import com.zarbosoft.merman.core.syntax.back.BackArraySpec;
import com.zarbosoft.merman.core.syntax.back.BackAtomSpec;
import com.zarbosoft.merman.core.syntax.back.BackFixedJSONSpecialPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.back.BackFixedPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.back.BackJSONSpecialPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.back.BackPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.back.BackSpec;
import com.zarbosoft.merman.core.syntax.back.BaseBackArraySpec;
import com.zarbosoft.merman.core.syntax.back.BaseBackPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.builder.BackFixedRecordSpecBuilder;
import com.zarbosoft.merman.core.syntax.builder.FrontArraySpecBuilder;
import com.zarbosoft.merman.core.syntax.builder.RootTypeBuilder;
import com.zarbosoft.merman.core.syntax.builder.TypeBuilder;
import com.zarbosoft.merman.core.syntax.front.FrontArraySpec;
import com.zarbosoft.merman.core.syntax.front.FrontAtomSpec;
import com.zarbosoft.merman.core.syntax.front.FrontPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.front.FrontSymbolSpec;
import com.zarbosoft.merman.core.syntax.primitivepattern.PatternCharacterClass;
import com.zarbosoft.merman.core.syntax.primitivepattern.PatternSequence;
import com.zarbosoft.merman.core.syntax.primitivepattern.Repeat1;
import com.zarbosoft.merman.core.syntax.style.ModelColor;
import com.zarbosoft.merman.core.syntax.style.ObboxStyle;
import com.zarbosoft.merman.core.syntax.style.Padding;
import com.zarbosoft.merman.core.syntax.style.SplitMode;
import com.zarbosoft.merman.core.syntax.symbol.SymbolSpaceSpec;
import com.zarbosoft.merman.core.syntax.symbol.SymbolTextSpec;
import com.zarbosoft.merman.webview.JSEnvironment;
import com.zarbosoft.merman.webview.WebView;
import com.zarbosoft.pidgoon.errors.GrammarTooUncertainAt;
import com.zarbosoft.pidgoon.errors.InvalidStreamAt;
import com.zarbosoft.pidgoon.events.Position;
import com.zarbosoft.pidgoon.model.Leaf;
import com.zarbosoft.pidgoon.model.MismatchCause;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROOrderedSetRef;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import jsinterop.annotations.JsMethod;

public class Main {
  public static final String rawDoc =
      "{\n"
          + "  \"type\": \"Program\",\n"
          + "  \"start\": 0,\n"
          + "  \"end\": 192,\n"
          + "  \"body\": [\n"
          + "    {\n"
          + "      \"type\": \"ForStatement\",\n"
          + "      \"start\": 0,\n"
          + "      \"end\": 192,\n"
          + "      \"init\": {\n"
          + "        \"type\": \"VariableDeclaration\",\n"
          + "        \"start\": 5,\n"
          + "        \"end\": 14,\n"
          + "        \"declarations\": [\n"
          + "          {\n"
          + "            \"type\": \"VariableDeclarator\",\n"
          + "            \"start\": 9,\n"
          + "            \"end\": 14,\n"
          + "            \"id\": {\n"
          + "              \"type\": \"Identifier\",\n"
          + "              \"start\": 9,\n"
          + "              \"end\": 10,\n"
          + "              \"name\": \"i\"\n"
          + "            },\n"
          + "            \"init\": {\n"
          + "              \"type\": \"Literal\",\n"
          + "              \"start\": 13,\n"
          + "              \"end\": 14,\n"
          + "              \"value\": 1,\n"
          + "              \"raw\": \"1\"\n"
          + "            }\n"
          + "          }\n"
          + "        ],\n"
          + "        \"kind\": \"let\"\n"
          + "      },\n"
          + "      \"test\": {\n"
          + "        \"type\": \"BinaryExpression\",\n"
          + "        \"start\": 16,\n"
          + "        \"end\": 24,\n"
          + "        \"left\": {\n"
          + "          \"type\": \"Identifier\",\n"
          + "          \"start\": 16,\n"
          + "          \"end\": 17,\n"
          + "          \"name\": \"i\"\n"
          + "        },\n"
          + "        \"operator\": \"<=\",\n"
          + "        \"right\": {\n"
          + "          \"type\": \"Literal\",\n"
          + "          \"start\": 21,\n"
          + "          \"end\": 24,\n"
          + "          \"value\": 100,\n"
          + "          \"raw\": \"100\"\n"
          + "        }\n"
          + "      },\n"
          + "      \"update\": {\n"
          + "        \"type\": \"UpdateExpression\",\n"
          + "        \"start\": 26,\n"
          + "        \"end\": 29,\n"
          + "        \"operator\": \"++\",\n"
          + "        \"prefix\": true,\n"
          + "        \"argument\": {\n"
          + "          \"type\": \"Identifier\",\n"
          + "          \"start\": 28,\n"
          + "          \"end\": 29,\n"
          + "          \"name\": \"i\"\n"
          + "        }\n"
          + "      },\n"
          + "      \"body\": {\n"
          + "        \"type\": \"BlockStatement\",\n"
          + "        \"start\": 31,\n"
          + "        \"end\": 192,\n"
          + "        \"body\": [\n"
          + "          {\n"
          + "            \"type\": \"VariableDeclaration\",\n"
          + "            \"start\": 34,\n"
          + "            \"end\": 46,\n"
          + "            \"declarations\": [\n"
          + "              {\n"
          + "                \"type\": \"VariableDeclarator\",\n"
          + "                \"start\": 38,\n"
          + "                \"end\": 46,\n"
          + "                \"id\": {\n"
          + "                  \"type\": \"Identifier\",\n"
          + "                  \"start\": 38,\n"
          + "                  \"end\": 41,\n"
          + "                  \"name\": \"out\"\n"
          + "                },\n"
          + "                \"init\": {\n"
          + "                  \"type\": \"Literal\",\n"
          + "                  \"start\": 44,\n"
          + "                  \"end\": 46,\n"
          + "                  \"value\": \"\",\n"
          + "                  \"raw\": \"\\\"\\\"\"\n"
          + "                }\n"
          + "              }\n"
          + "            ],\n"
          + "            \"kind\": \"let\"\n"
          + "          },\n"
          + "          {\n"
          + "            \"type\": \"IfStatement\",\n"
          + "            \"start\": 48,\n"
          + "            \"end\": 86,\n"
          + "            \"test\": {\n"
          + "              \"type\": \"BinaryExpression\",\n"
          + "              \"start\": 52,\n"
          + "              \"end\": 63,\n"
          + "              \"left\": {\n"
          + "                \"type\": \"BinaryExpression\",\n"
          + "                \"start\": 52,\n"
          + "                \"end\": 57,\n"
          + "                \"left\": {\n"
          + "                  \"type\": \"Identifier\",\n"
          + "                  \"start\": 52,\n"
          + "                  \"end\": 53,\n"
          + "                  \"name\": \"i\"\n"
          + "                },\n"
          + "                \"operator\": \"%\",\n"
          + "                \"right\": {\n"
          + "                  \"type\": \"Literal\",\n"
          + "                  \"start\": 56,\n"
          + "                  \"end\": 57,\n"
          + "                  \"value\": 3,\n"
          + "                  \"raw\": \"3\"\n"
          + "                }\n"
          + "              },\n"
          + "              \"operator\": \"===\",\n"
          + "              \"right\": {\n"
          + "                \"type\": \"Literal\",\n"
          + "                \"start\": 62,\n"
          + "                \"end\": 63,\n"
          + "                \"value\": 0,\n"
          + "                \"raw\": \"0\"\n"
          + "              }\n"
          + "            },\n"
          + "            \"consequent\": {\n"
          + "              \"type\": \"BlockStatement\",\n"
          + "              \"start\": 65,\n"
          + "              \"end\": 86,\n"
          + "              \"body\": [\n"
          + "                {\n"
          + "                  \"type\": \"ExpressionStatement\",\n"
          + "                  \"start\": 69,\n"
          + "                  \"end\": 83,\n"
          + "                  \"expression\": {\n"
          + "                    \"type\": \"AssignmentExpression\",\n"
          + "                    \"start\": 69,\n"
          + "                    \"end\": 82,\n"
          + "                    \"operator\": \"+=\",\n"
          + "                    \"left\": {\n"
          + "                      \"type\": \"Identifier\",\n"
          + "                      \"start\": 69,\n"
          + "                      \"end\": 72,\n"
          + "                      \"name\": \"out\"\n"
          + "                    },\n"
          + "                    \"right\": {\n"
          + "                      \"type\": \"Literal\",\n"
          + "                      \"start\": 76,\n"
          + "                      \"end\": 82,\n"
          + "                      \"value\": \"Fizz\",\n"
          + "                      \"raw\": \"\\\"Fizz\\\"\"\n"
          + "                    }\n"
          + "                  }\n"
          + "                }\n"
          + "              ]\n"
          + "            },\n"
          + "            \"alternate\": null\n"
          + "          },\n"
          + "          {\n"
          + "            \"type\": \"IfStatement\",\n"
          + "            \"start\": 88,\n"
          + "            \"end\": 126,\n"
          + "            \"test\": {\n"
          + "              \"type\": \"BinaryExpression\",\n"
          + "              \"start\": 92,\n"
          + "              \"end\": 103,\n"
          + "              \"left\": {\n"
          + "                \"type\": \"BinaryExpression\",\n"
          + "                \"start\": 92,\n"
          + "                \"end\": 97,\n"
          + "                \"left\": {\n"
          + "                  \"type\": \"Identifier\",\n"
          + "                  \"start\": 92,\n"
          + "                  \"end\": 93,\n"
          + "                  \"name\": \"i\"\n"
          + "                },\n"
          + "                \"operator\": \"%\",\n"
          + "                \"right\": {\n"
          + "                  \"type\": \"Literal\",\n"
          + "                  \"start\": 96,\n"
          + "                  \"end\": 97,\n"
          + "                  \"value\": 5,\n"
          + "                  \"raw\": \"5\"\n"
          + "                }\n"
          + "              },\n"
          + "              \"operator\": \"===\",\n"
          + "              \"right\": {\n"
          + "                \"type\": \"Literal\",\n"
          + "                \"start\": 102,\n"
          + "                \"end\": 103,\n"
          + "                \"value\": 0,\n"
          + "                \"raw\": \"0\"\n"
          + "              }\n"
          + "            },\n"
          + "            \"consequent\": {\n"
          + "              \"type\": \"BlockStatement\",\n"
          + "              \"start\": 105,\n"
          + "              \"end\": 126,\n"
          + "              \"body\": [\n"
          + "                {\n"
          + "                  \"type\": \"ExpressionStatement\",\n"
          + "                  \"start\": 109,\n"
          + "                  \"end\": 123,\n"
          + "                  \"expression\": {\n"
          + "                    \"type\": \"AssignmentExpression\",\n"
          + "                    \"start\": 109,\n"
          + "                    \"end\": 122,\n"
          + "                    \"operator\": \"+=\",\n"
          + "                    \"left\": {\n"
          + "                      \"type\": \"Identifier\",\n"
          + "                      \"start\": 109,\n"
          + "                      \"end\": 112,\n"
          + "                      \"name\": \"out\"\n"
          + "                    },\n"
          + "                    \"right\": {\n"
          + "                      \"type\": \"Literal\",\n"
          + "                      \"start\": 116,\n"
          + "                      \"end\": 122,\n"
          + "                      \"value\": \"Buzz\",\n"
          + "                      \"raw\": \"\\\"Buzz\\\"\"\n"
          + "                    }\n"
          + "                  }\n"
          + "                }\n"
          + "              ]\n"
          + "            },\n"
          + "            \"alternate\": null\n"
          + "          },\n"
          + "          {\n"
          + "            \"type\": \"IfStatement\",\n"
          + "            \"start\": 128,\n"
          + "            \"end\": 166,\n"
          + "            \"test\": {\n"
          + "              \"type\": \"BinaryExpression\",\n"
          + "              \"start\": 132,\n"
          + "              \"end\": 148,\n"
          + "              \"left\": {\n"
          + "                \"type\": \"MemberExpression\",\n"
          + "                \"start\": 132,\n"
          + "                \"end\": 142,\n"
          + "                \"object\": {\n"
          + "                  \"type\": \"Identifier\",\n"
          + "                  \"start\": 132,\n"
          + "                  \"end\": 135,\n"
          + "                  \"name\": \"out\"\n"
          + "                },\n"
          + "                \"property\": {\n"
          + "                  \"type\": \"Identifier\",\n"
          + "                  \"start\": 136,\n"
          + "                  \"end\": 142,\n"
          + "                  \"name\": \"length\"\n"
          + "                },\n"
          + "                \"computed\": false,\n"
          + "                \"optional\": false\n"
          + "              },\n"
          + "              \"operator\": \"===\",\n"
          + "              \"right\": {\n"
          + "                \"type\": \"Literal\",\n"
          + "                \"start\": 147,\n"
          + "                \"end\": 148,\n"
          + "                \"value\": 0,\n"
          + "                \"raw\": \"0\"\n"
          + "              }\n"
          + "            },\n"
          + "            \"consequent\": {\n"
          + "              \"type\": \"BlockStatement\",\n"
          + "              \"start\": 150,\n"
          + "              \"end\": 166,\n"
          + "              \"body\": [\n"
          + "                {\n"
          + "                  \"type\": \"ExpressionStatement\",\n"
          + "                  \"start\": 154,\n"
          + "                  \"end\": 163,\n"
          + "                  \"expression\": {\n"
          + "                    \"type\": \"AssignmentExpression\",\n"
          + "                    \"start\": 154,\n"
          + "                    \"end\": 162,\n"
          + "                    \"operator\": \"+=\",\n"
          + "                    \"left\": {\n"
          + "                      \"type\": \"Identifier\",\n"
          + "                      \"start\": 154,\n"
          + "                      \"end\": 157,\n"
          + "                      \"name\": \"out\"\n"
          + "                    },\n"
          + "                    \"right\": {\n"
          + "                      \"type\": \"Identifier\",\n"
          + "                      \"start\": 161,\n"
          + "                      \"end\": 162,\n"
          + "                      \"name\": \"i\"\n"
          + "                    }\n"
          + "                  }\n"
          + "                }\n"
          + "              ]\n"
          + "            },\n"
          + "            \"alternate\": null\n"
          + "          },\n"
          + "          {\n"
          + "            \"type\": \"ExpressionStatement\",\n"
          + "            \"start\": 168,\n"
          + "            \"end\": 190,\n"
          + "            \"expression\": {\n"
          + "              \"type\": \"CallExpression\",\n"
          + "              \"start\": 168,\n"
          + "              \"end\": 190,\n"
          + "              \"callee\": {\n"
          + "                \"type\": \"MemberExpression\",\n"
          + "                \"start\": 168,\n"
          + "                \"end\": 179,\n"
          + "                \"object\": {\n"
          + "                  \"type\": \"Identifier\",\n"
          + "                  \"start\": 168,\n"
          + "                  \"end\": 175,\n"
          + "                  \"name\": \"console\"\n"
          + "                },\n"
          + "                \"property\": {\n"
          + "                  \"type\": \"Identifier\",\n"
          + "                  \"start\": 176,\n"
          + "                  \"end\": 179,\n"
          + "                  \"name\": \"log\"\n"
          + "                },\n"
          + "                \"computed\": false,\n"
          + "                \"optional\": false\n"
          + "              },\n"
          + "              \"arguments\": [\n"
          + "                {\n"
          + "                  \"type\": \"BinaryExpression\",\n"
          + "                  \"start\": 180,\n"
          + "                  \"end\": 189,\n"
          + "                  \"left\": {\n"
          + "                    \"type\": \"Identifier\",\n"
          + "                    \"start\": 180,\n"
          + "                    \"end\": 183,\n"
          + "                    \"name\": \"out\"\n"
          + "                  },\n"
          + "                  \"operator\": \"+\",\n"
          + "                  \"right\": {\n"
          + "                    \"type\": \"Literal\",\n"
          + "                    \"start\": 186,\n"
          + "                    \"end\": 189,\n"
          + "                    \"value\": \" \",\n"
          + "                    \"raw\": \"\\\" \\\"\"\n"
          + "                  }\n"
          + "                }\n"
          + "              ],\n"
          + "              \"optional\": false\n"
          + "            }\n"
          + "          }\n"
          + "        ]\n"
          + "      }\n"
          + "    }\n"
          + "  ],\n"
          + "  \"sourceType\": \"script\"\n"
          + "}\n";

  public static final int indentPx = 32;
  public static final int fontSize = 15;
  public static final int lineSpace = 4;

  public static final String baseAlign = "base";
  public static final String indentAlign = "indent";

  public static final double bright = 0.7;
  public static final double dark = 0.25;
  public static final double semidark = 0.35;
  public static final double subbright = 0.6;
  public static final double sat = 0.4;
  public static final double unsat = 0.2;
  public static final ModelColor.RGB stringColor =
      ModelColor.RGB.polarOKLab(bright, unsat, 100 + 60);
  public static final ModelColor.RGB stringQuoteColor =
      ModelColor.RGB.polarOKLab(subbright, unsat, 100 + 70);
  public static final ModelColor.RGB nonstringColor =
      ModelColor.RGB.polarOKLab(bright, unsat, 100 + 240);
  public static final ModelColor.RGB identifierColor =
      ModelColor.RGB.polarOKLab(bright, unsat, 100 + 120);
  public static final ModelColor.RGB declareColor =
      ModelColor.RGB.polarOKLab(semidark, unsat, 100 + 150);
  public static final ModelColor.RGB keywordColor = ModelColor.RGB.polarOKLab(dark, sat, 0);
  public static final ModelColor.RGB blockColor = ModelColor.RGB.polarOKLab(dark, sat, 30);
  public static final ModelColor.RGB operatorColor = ModelColor.RGB.polarOKLab(dark, sat, 60);
  public static final ModelColor.RGB callColor = ModelColor.RGB.polarOKLab(dark, sat, 90);
  public static final String statementGroupType = "statement";
  public static final String accessGroupType = "access";
  public static final String expresisonGroupType = "expression";
  public static final String literalGroupType = "literal";
  public static final String declareGroupType = "declare";
  public static final String letType = "let";
  public static final String declareInner = "declare_inner";
  public static final String stringLiteralType = "string_literal";
  public static final String symbolLiteralType = "symbol_literal";
  public static final String identifierType = "identifier";
  public static final String memberType = "member";
  public static final String callType = "call";
  public static final String forType = "for";
  public static final String ifType = "if";
  public static final String blockType = "block";
  public static final String preincrementOperatorType = "preincrement";
  public static final String addOperatorType = "add";
  public static final String lessThanEqualOperatorType = "less_than_equal";
  public static final String moduloOperatorType = "modulo";
  public static final String addEqualOperatorType = "add_equal";
  public static final String tripleEqualOperatorType = "triple_equal";
  public static final FrontSymbolSpec prefixCompactIndent =
      new FrontSymbolSpec(
          new FrontSymbolSpec.Config(
              new SymbolSpaceSpec(
                  new SymbolSpaceSpec.Config()
                      .splitMode(SplitMode.COMPACT)
                      .splitAlignmentId(indentAlign))));
  public static final FrontSymbolSpec prefixIndent =
      new FrontSymbolSpec(
          new FrontSymbolSpec.Config(
              new SymbolSpaceSpec(
                  new SymbolSpaceSpec.Config()
                      .splitMode(SplitMode.ALWAYS)
                      .splitAlignmentId(indentAlign))));
  public static final FrontSymbolSpec space = text(" ", styleBase());

  private static DirectStylist.TextStyle styleBase() {
    return new DirectStylist.TextStyle().padding(Padding.ct(0, lineSpace)).fontSize(fontSize);
  }

  private static DirectStylist.TextStyle styleString() {
    return styleBase().color(stringColor);
  }

  private static DirectStylist.TextStyle styleNonstring() {
    return styleBase().color(nonstringColor);
  }

  private static DirectStylist.TextStyle styleStringQuote() {
    return styleBase().color(stringQuoteColor);
  }

  private static DirectStylist.TextStyle styleIdentifier() {
    return styleBase().color(identifierColor);
  }

  private static DirectStylist.TextStyle styleOperator() {
    return styleBase().color(operatorColor);
  }

  private static DirectStylist.TextStyle styleCall() {
    return styleBase().color(callColor);
  }

  private static DirectStylist.TextStyle styleKeyword() {
    return styleBase().color(keywordColor);
  }

  private static DirectStylist.TextStyle styleDeclare() {
    return styleBase().color(declareColor);
  }

  private static DirectStylist.TextStyle styleBlock() {
    return styleBase().color(blockColor);
  }

  @JsMethod
  public static void main() {
    try {
      {
        Element ast = DomGlobal.document.createElement("code");
        ast.classList.add("block");
        ast.textContent = rawDoc;
        DomGlobal.document.getElementById("replace-javascript-ast").replaceWith(ast);
      }
      WebView webView = new WebView();
      Environment env = new JSEnvironment("en");
      for (ROPair<String, SyntaxFrontFactory> p :
          new ROPair[] {
            new ROPair<>("javascript", new JavascriptSyntaxFrontFactory()),
            new ROPair<>("python", new PythonSyntaxFrontFactory()),
            new ROPair<>("lisp", new LispSyntaxFrontFactory()),
            new ROPair<>("prodel", new ProdelSyntaxFrontFactory())
          }) {
        Element e = DomGlobal.document.getElementById("replace-" + p.first);
        if (e == null) {
            continue;
        }
        e.replaceWith(
            webView.block(
                buildSyntax(env, p.second), env, rawDoc, TSList.of("type", "operator", "kind")));
      }
    } catch (GrammarTooUncertainAt e) {
      StringBuilder message = new StringBuilder();
      for (Leaf leaf : (TSList<Leaf>) e.e.step.leaves) {
        message.append(Format.format(" * %s (%s)\n", leaf, leaf.color()));
      }
      throw new RuntimeException(
          Format.format(
              "Too much uncertainty while parsing!\nat %s %s\n%s branches:\n%s",
              ((Position) e.at).at, ((Position) e.at).event, message.toString()));
    } catch (InvalidStreamAt e) {
      StringBuilder message = new StringBuilder();
      for (MismatchCause error : (TSList<MismatchCause>) e.step.errors) {
        message.append(Format.format(" * %s\n", error));
      }
      throw new RuntimeException(
          Format.format(
              "Document doesn't conform to syntax tree\nat %s %s\nexpected:\n%s",
              ((Position) e.at).at, ((Position) e.at).event, message.toString()));
    } catch (RuntimeException e) {
      throw new RuntimeException("\n" + e);
    }
  }

  private static SyntaxOut buildSyntax(Environment env, SyntaxFrontFactory frontFactory) {
    FrontArraySpec statementsFront =
        new FrontArraySpecBuilder("statements")
            .prefix(
                new FrontSymbolSpec(
                    new FrontSymbolSpec.Config(
                        new SymbolSpaceSpec(
                            new SymbolSpaceSpec.Config().splitMode(SplitMode.ALWAYS)))))
            .build();
    BackArraySpec statementBackArray =
        new BackArraySpec(
            new BaseBackArraySpec.Config(
                "statements",
                statementGroupType,
                new TSList<>(
                    estreeBackBuilder()
                        .field("type", new BackFixedPrimitiveSpec("ExpressionStatement"))
                        .field(
                            "expression",
                            new BackAtomSpec(new BackAtomSpec.Config(null, expresisonGroupType)))
                        .build())));
    TSList<AtomType> types =
        TSList.of(
            frontFactory
                .block(
                    estreeTypeBuilder(blockType, "Block")
                        .back(
                            estreeBackBuilder()
                                .field("type", new BackFixedPrimitiveSpec("BlockStatement"))
                                .field("body", statementBackArray)
                                .build()),
                    "statements")
                .precedence(0)
                .build(),
            //
            /// Declaration
            frontFactory
                .declareOuter(
                    estreeTypeBuilder(letType, "Declare - let (outer)")
                        .back(
                            estreeBackBuilder()
                                .field("type", new BackFixedPrimitiveSpec("VariableDeclaration"))
                                .field("kind", new BackFixedPrimitiveSpec("let"))
                                .field(
                                    "declarations",
                                    new BackArraySpec(
                                        new BaseBackArraySpec.Config(
                                            "declarations", declareInner, new TSList<>())))
                                .build()),
                    "declarations")
                .precedence(10)
                .build(),
            frontFactory
                .declareInner(
                    estreeTypeBuilder(declareInner, "Declare (inner)")
                        .back(
                            estreeBackBuilder()
                                .field("type", new BackFixedPrimitiveSpec("VariableDeclarator"))
                                .field("id", identifierBack("id"))
                                .field(
                                    "init",
                                    new BackAtomSpec(
                                        new BackAtomSpec.Config("init", expresisonGroupType)))
                                .build()),
                    "id",
                    "init")
                .build(),
            //
            /// Access
            estreeTypeBuilder(identifierType, "Identifier")
                .back(identifierBack("name"))
                .front(
                    new FrontPrimitiveSpec(
                        new FrontPrimitiveSpec.Config("name")
                            .meta(DirectStylist.meta(styleIdentifier()))))
                .build(),
            frontFactory
                .memberExpr(
                    estreeTypeBuilder(memberType, "Member")
                        .back(
                            estreeBackBuilder()
                                .field("type", new BackFixedPrimitiveSpec("MemberExpression"))
                                .field(
                                    "object",
                                    new BackAtomSpec(
                                        new BackAtomSpec.Config("object", expresisonGroupType)))
                                .field("property", identifierBack("property"))
                                .field("computed", new BackFixedJSONSpecialPrimitiveSpec("false"))
                                .field("optional", new BackFixedJSONSpecialPrimitiveSpec("false"))
                                .build()),
                    "object",
                    "property")
                .precedence(200)
                .build(),
            //
            /// Literal
            estreeTypeBuilder(symbolLiteralType, "True literal")
                .back(
                    estreeBackBuilder()
                        .field("type", new BackFixedPrimitiveSpec("Literal"))
                        .field("value", new BackFixedJSONSpecialPrimitiveSpec("true"))
                        .build())
                .front(
                    new FrontPrimitiveSpec(
                        new FrontPrimitiveSpec.Config("value")
                            .meta(DirectStylist.meta(styleNonstring()))))
                .build(),
            estreeTypeBuilder(symbolLiteralType, "False literal")
                .back(
                    estreeBackBuilder()
                        .field("type", new BackFixedPrimitiveSpec("Literal"))
                        .field("value", new BackFixedJSONSpecialPrimitiveSpec("false"))
                        .build())
                .front(
                    new FrontPrimitiveSpec(
                        new FrontPrimitiveSpec.Config("value")
                            .meta(DirectStylist.meta(styleNonstring()))))
                .build(),
            estreeTypeBuilder(symbolLiteralType, "Null literal")
                .back(
                    estreeBackBuilder()
                        .field("type", new BackFixedPrimitiveSpec("Literal"))
                        .field("value", new BackFixedJSONSpecialPrimitiveSpec("null"))
                        .build())
                .front(
                    new FrontPrimitiveSpec(
                        new FrontPrimitiveSpec.Config("value")
                            .meta(DirectStylist.meta(styleNonstring()))))
                .build(),
            estreeTypeBuilder(symbolLiteralType, "Decimal literal")
                .back(
                    estreeBackBuilder()
                        .field("type", new BackFixedPrimitiveSpec("Literal"))
                        .field(
                            "value",
                            new BackJSONSpecialPrimitiveSpec(
                                BackJSONSpecialPrimitiveSpec.decimalConfig("value")))
                        .build())
                .front(
                    new FrontPrimitiveSpec(
                        new FrontPrimitiveSpec.Config("value")
                            .meta(DirectStylist.meta(styleNonstring()))))
                .build(),
            frontFactory
                .stringLit(
                    estreeTypeBuilder(stringLiteralType, "String literal")
                        .back(
                            estreeBackBuilder()
                                .field("type", new BackFixedPrimitiveSpec("Literal"))
                                .field(
                                    "value",
                                    new BackPrimitiveSpec(
                                        new BaseBackPrimitiveSpec.Config("value")))
                                .build()),
                    "value")
                .build(),
            //
            /// Control
            frontFactory
                .forStatement(
                    estreeTypeBuilder(forType, "For")
                        .back(
                            estreeBackBuilder()
                                .field("type", new BackFixedPrimitiveSpec("ForStatement"))
                                .field(
                                    "init",
                                    new BackAtomSpec(
                                        new BackAtomSpec.Config("init", declareGroupType)))
                                .field(
                                    "test",
                                    new BackAtomSpec(
                                        new BackAtomSpec.Config("test", expresisonGroupType)))
                                .field(
                                    "update",
                                    new BackAtomSpec(
                                        new BackAtomSpec.Config("update", expresisonGroupType)))
                                .field(
                                    "body",
                                    new BackAtomSpec(
                                        new BackAtomSpec.Config("body", statementGroupType)))
                                .build()),
                    "init",
                    "test",
                    "update",
                    "body")
                .build(),
            frontFactory
                .ifStatement(
                    estreeTypeBuilder(ifType, "If")
                        .back(
                            estreeBackBuilder()
                                .field("type", new BackFixedPrimitiveSpec("IfStatement"))
                                .field(
                                    "test",
                                    new BackAtomSpec(
                                        new BackAtomSpec.Config("test", expresisonGroupType)))
                                .field(
                                    "consequent",
                                    new BackAtomSpec(
                                        new BackAtomSpec.Config("consequent", statementGroupType)))
                                .field("alternate", new BackFixedJSONSpecialPrimitiveSpec("null"))
                                .build()),
                    "test",
                    "consequent")
                .build(),
            //
            /// Operators
            prefixOperator(
                frontFactory,
                preincrementOperatorType,
                "++",
                170,
                "UpdateExpression",
                accessGroupType),
            binaryOperator(
                frontFactory,
                addOperatorType,
                "+",
                140,
                "BinaryExpression",
                expresisonGroupType,
                expresisonGroupType),
            binaryOperator(
                frontFactory,
                lessThanEqualOperatorType,
                "<=",
                120,
                "BinaryExpression",
                expresisonGroupType,
                expresisonGroupType),
            binaryOperator(
                frontFactory,
                moduloOperatorType,
                "%",
                150,
                "BinaryExpression",
                expresisonGroupType,
                expresisonGroupType),
            binaryOperator(
                frontFactory,
                addEqualOperatorType,
                "+=",
                30,
                "AssignmentExpression",
                accessGroupType,
                expresisonGroupType),
            binaryOperator(
                frontFactory,
                tripleEqualOperatorType,
                "===",
                110,
                "BinaryExpression",
                expresisonGroupType,
                expresisonGroupType),
            //
            /// Other expressions
            frontFactory
                .callExpr(
                    estreeTypeBuilder(callType, "Call")
                        .back(
                            estreeBackBuilder()
                                .field("type", new BackFixedPrimitiveSpec("CallExpression"))
                                .field(
                                    "callee",
                                    new BackAtomSpec(
                                        new BackAtomSpec.Config("callee", expresisonGroupType)))
                                .field(
                                    "arguments",
                                    new BackArraySpec(
                                        new BaseBackArraySpec.Config(
                                            "arguments", expresisonGroupType, new TSList<>())))
                                .field("optional", new BackFixedJSONSpecialPrimitiveSpec("false"))
                                .build()),
                    "callee",
                    "arguments")
                .precedence(0)
                .build());
    TSOrderedMap<String, ROList<String>> groups =
        new TSOrderedMap<String, ROList<String>>()
            .put(
                statementGroupType,
                TSList.of(declareGroupType, forType, ifType, blockType, expresisonGroupType))
            .put(accessGroupType, TSList.of(identifierType, memberType))
            .put(declareGroupType, TSList.of(letType))
            .put(
                expresisonGroupType,
                TSList.of(
                    literalGroupType,
                    accessGroupType,
                    preincrementOperatorType,
                    addOperatorType,
                    lessThanEqualOperatorType,
                    moduloOperatorType,
                    addEqualOperatorType,
                    tripleEqualOperatorType,
                    callType))
            .put(literalGroupType, TSList.of(symbolLiteralType, stringLiteralType));
    MultiError errors = new MultiError();
    GapAtomType gap = new GapAtomType(new GapAtomType.Config());
    SuffixGapAtomType suffixGap = new SuffixGapAtomType(new SuffixGapAtomType.Config());
    TSMap<String, ROOrderedSetRef<AtomType>> splayedTypes =
        Syntax.splayGroups(errors, types, gap, suffixGap, groups);
    errors.raise();
    Syntax.Config syntaxConfig =
        new Syntax.Config(
            splayedTypes,
            new RootTypeBuilder()
                .back(
                    estreeBackBuilder()
                        .field("type", new BackFixedPrimitiveSpec("Program"))
                        .field("sourceType", new BackFixedPrimitiveSpec("script"))
                        .field("body", statementBackArray)
                        .build())
                .front(statementsFront)
                .build(),
            gap,
            suffixGap);
    syntaxConfig.converseDirection = frontFactory.converseDirection();
    syntaxConfig.transverseDirection = frontFactory.transverseDirection();
    syntaxConfig.backType = BackType.JSON;
    return new SyntaxOut(
        new DirectStylist(
            new ObboxStyle(
                new ObboxStyle.Config()
                    .padding(Padding.same(1))
                    .roundStart(true)
                    .roundEnd(true)
                    .lineThickness(1.5)
                    .roundRadius(8)
                    .lineColor(ModelColor.RGBA.polarOKLab(0.3, 0.5, 180, 0.8))),
            new ObboxStyle(
                new ObboxStyle.Config()
                    .padding(Padding.same(1))
                    .roundEnd(true)
                    .roundStart(true)
                    .roundRadius(8)
                    .lineThickness(1.5)
                    .lineColor(ModelColor.RGBA.polarOKLab(0.3, 0, 0, 0.4))),
            null,
            null,
            null,
            null,
            null,
            null),
        null,
            colorError, colorInfo, new Syntax(env, syntaxConfig),
        null,
        fontSize * 0.7);
  }

  public static TypeBuilder estreeTypeBuilder(String callType, String call) {
    return new TypeBuilder(callType, call)
        .alignment(
            baseAlign,
            new RelativeAlignmentSpec(new RelativeAlignmentSpec.Config(indentAlign, 0, false)))
        .alignment(
            indentAlign,
            new RelativeAlignmentSpec(
                new RelativeAlignmentSpec.Config(indentAlign, indentPx, true)));
  }

  private static FreeAtomType binaryOperator(
      SyntaxFrontFactory frontFactory,
      String id,
      String symbol,
      int precedence,
      String esType,
      String leftChildType,
      String rightChildType) {
    return frontFactory
        .binaryOp(
            estreeTypeBuilder(id, symbol + " operator")
                .precedence(precedence)
                .back(
                    estreeBackBuilder()
                        .field("type", new BackFixedPrimitiveSpec(esType))
                        .field(
                            "left",
                            new BackAtomSpec(new BackAtomSpec.Config("left", leftChildType)))
                        .field("operator", new BackFixedPrimitiveSpec(symbol))
                        .field(
                            "right",
                            new BackAtomSpec(new BackAtomSpec.Config("right", rightChildType)))
                        .build()),
            symbol,
            "left",
            "right")
        .build();
  }

  private static FreeAtomType prefixOperator(
      SyntaxFrontFactory frontFactory,
      String id,
      String symbol,
      int precedence,
      String esType,
      String childType) {
    return frontFactory
        .prefixOp(
            estreeTypeBuilder(id, symbol + " operator")
                .precedence(precedence)
                .associateForward()
                .back(
                    estreeBackBuilder()
                        .field("type", new BackFixedPrimitiveSpec(esType))
                        .field("operator", new BackFixedPrimitiveSpec(symbol))
                        .field("prefix", new BackFixedJSONSpecialPrimitiveSpec("true"))
                        .field(
                            "argument",
                            new BackAtomSpec(new BackAtomSpec.Config("argument", childType)))
                        .build()),
            symbol,
            "argument")
        .build();
  }

  private static BackSpec identifierBack(String id) {
    return estreeBackBuilder()
        .field("type", new BackFixedPrimitiveSpec("Identifier"))
        .field(
            "name",
            new BackPrimitiveSpec(
                new BaseBackPrimitiveSpec.Config(id)
                    .pattern(
                        new PatternSequence(
                            TSList.of(
                                new PatternCharacterClass(
                                    TSList.of(
                                        new ROPair<>("$", "$"),
                                        new ROPair<>("_", "_"),
                                        new ROPair<>("a", "z"),
                                        new ROPair<>("A", "Z"))),
                                new Repeat1(
                                    new PatternCharacterClass(
                                        TSList.of(
                                            new ROPair<>("$", "$"),
                                            new ROPair<>("_", "_"),
                                            new ROPair<>("a", "z"),
                                            new ROPair<>("A", "Z"),
                                            new ROPair<>("0", "9")))))),
                        "valid identifiers")))
        .build();
  }

  private static FrontSymbolSpec text(String text, DirectStylist.TextStyle style) {
    return new FrontSymbolSpec(
        new FrontSymbolSpec.Config(
            new SymbolTextSpec(new SymbolTextSpec.Config(text).meta(DirectStylist.meta(style)))));
  }

  private static FrontSymbolSpec textBase(String text, DirectStylist.TextStyle style) {
    return new FrontSymbolSpec(
        new FrontSymbolSpec.Config(
            new SymbolTextSpec(
                new SymbolTextSpec.Config(text)
                    .splitMode(SplitMode.ALWAYS)
                    .splitAlignmentId(baseAlign)
                    .meta(DirectStylist.meta(style)))));
  }

  private static FrontSymbolSpec textCompactBase(String text, DirectStylist.TextStyle style) {
    return new FrontSymbolSpec(
        new FrontSymbolSpec.Config(
            new SymbolTextSpec(
                new SymbolTextSpec.Config(text)
                    .splitMode(SplitMode.COMPACT)
                    .splitAlignmentId(baseAlign)
                    .meta(DirectStylist.meta(style)))));
  }

  private static FrontSymbolSpec textCompactIndent(String text, DirectStylist.TextStyle style) {
    return new FrontSymbolSpec(
        new FrontSymbolSpec.Config(
            new SymbolTextSpec(
                new SymbolTextSpec.Config(text)
                    .splitMode(SplitMode.COMPACT)
                    .splitAlignmentId(indentAlign)
                    .meta(DirectStylist.meta(style)))));
  }

  private static BackFixedRecordSpecBuilder estreeBackBuilder() {
    return new BackFixedRecordSpecBuilder()
        .discardField("start")
        .discardField("end")
        .discardField("raw");
  }

  public interface SyntaxFrontFactory {
    TypeBuilder block(TypeBuilder type, String statementsKey);

    TypeBuilder forStatement(
        TypeBuilder type, String initKey, String testKey, String updateKey, String bodyKey);

    TypeBuilder ifStatement(TypeBuilder type, String testKey, String consequentKey);

    TypeBuilder callExpr(TypeBuilder type, String calleeKey, String argumentsKey);

    TypeBuilder memberExpr(TypeBuilder type, String objectKey, String propertyKey);

    TypeBuilder binaryOp(TypeBuilder type, String symbol, String leftKey, String rightKey);

    TypeBuilder prefixOp(TypeBuilder type, String symbol, String argumentKey);

    TypeBuilder declareInner(TypeBuilder type, String idKey, String initKey);

    TypeBuilder declareOuter(TypeBuilder type, String declarationsKey);

    TypeBuilder stringLit(TypeBuilder type, String valueKey);

    Direction converseDirection();

    Direction transverseDirection();
  }

  public abstract static class EnglishBaseFrontFactory implements SyntaxFrontFactory {
    @Override
    public Direction converseDirection() {
      return Direction.RIGHT;
    }

    @Override
    public Direction transverseDirection() {
      return Direction.DOWN;
    }

    @Override
    public TypeBuilder stringLit(TypeBuilder type, String valueKey) {
      return type.front(text("\"", styleStringQuote()))
          .front(
              new FrontPrimitiveSpec(
                  new FrontPrimitiveSpec.Config("value").meta(DirectStylist.meta(styleString()))))
          .front(text("\"", styleStringQuote()));
    }
  }

  public abstract static class NonLispSyntaxFrontFactory extends EnglishBaseFrontFactory {
    @Override
    public TypeBuilder callExpr(TypeBuilder type, String calleeKey, String argumentsKey) {
      return type.front(new FrontAtomSpec(new FrontAtomSpec.Config(calleeKey)))
          .front(text("(", styleCall()))
          .front(
              new FrontArraySpecBuilder(argumentsKey)
                  .prefix(prefixCompactIndent)
                  .separator(text(", ", styleCall()))
                  .build())
          .front(textCompactBase(")", styleCall()));
    }

    @Override
    public TypeBuilder memberExpr(TypeBuilder type, String objectKey, String propertyKey) {
      return type.front(new FrontAtomSpec(new FrontAtomSpec.Config(objectKey)))
          .front(textCompactIndent(".", styleOperator()))
          .front(
              new FrontPrimitiveSpec(
                  new FrontPrimitiveSpec.Config(propertyKey)
                      .meta(DirectStylist.meta(styleIdentifier()))));
    }

    @Override
    public TypeBuilder binaryOp(TypeBuilder type, String symbol, String leftKey, String rightKey) {
      return type.front(new FrontAtomSpec(new FrontAtomSpec.Config(leftKey)))
          .front(space)
          .front(
              new FrontSymbolSpec(
                  new FrontSymbolSpec.Config(
                      new SymbolTextSpec(
                          new SymbolTextSpec.Config(symbol + " ")
                              .splitMode(SplitMode.COMPACT)
                              .splitAlignmentId(indentAlign)
                              .meta(DirectStylist.meta(styleOperator()))))))
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(rightKey)));
    }

    @Override
    public TypeBuilder prefixOp(TypeBuilder type, String symbol, String argumentKey) {
      return type.front(text(symbol, styleOperator()))
          .front(new FrontAtomSpec(new FrontAtomSpec.Config("argument")));
    }

    @Override
    public TypeBuilder declareInner(TypeBuilder type, String idKey, String initKey) {
      return type.front(
              new FrontPrimitiveSpec(
                  new FrontPrimitiveSpec.Config("id").meta(DirectStylist.meta(styleIdentifier()))))
          .front(text(" = ", styleKeyword()))
          .front(new FrontAtomSpec(new FrontAtomSpec.Config("init")));
    }

    @Override
    public TypeBuilder declareOuter(TypeBuilder type, String declarationsKey) {
      return type.front(text("let ", styleDeclare()))
          .front(
              new FrontArraySpecBuilder("declarations")
                  .prefix(prefixCompactIndent)
                  .separator(text(", ", styleDeclare()))
                  .build());
    }
  }

  public static class ProdelSyntaxFrontFactory implements SyntaxFrontFactory {
    @Override
    public Direction converseDirection() {
      return Direction.RIGHT;
    }

    @Override
    public Direction transverseDirection() {
      return Direction.DOWN;
    }
    /*
    @Override
    public Direction converseDirection() {
      return Direction.DOWN;
    }

    @Override
    public Direction transverseDirection() {
      return Direction.LEFT;
    }
    */

    @Override
    public TypeBuilder stringLit(TypeBuilder type, String valueKey) {
      return type.front(text("「", styleStringQuote()))
          .front(
              new FrontPrimitiveSpec(
                  new FrontPrimitiveSpec.Config("value").meta(DirectStylist.meta(styleString()))))
          .front(text("」", styleStringQuote()));
    }

    @Override
    public TypeBuilder binaryOp(TypeBuilder type, String symbol, String leftKey, String rightKey) {
      return type.front(new FrontAtomSpec(new FrontAtomSpec.Config(leftKey)))
          .front(space)
          .front(prefixCompactIndent)
          .front(text(symbol, styleOperator()))
          .front(prefixCompactIndent)
          .front(space)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(rightKey)));
    }

    @Override
    public TypeBuilder prefixOp(TypeBuilder type, String symbol, String argumentKey) {
      return type.front(text(symbol, styleOperator()))
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config("argument")));
    }

    @Override
    public TypeBuilder callExpr(TypeBuilder type, String calleeKey, String argumentsKey) {
      return type.front(text("（", styleCall()))
          .front(
              new FrontArraySpecBuilder(argumentsKey)
                  .prefix(prefixCompactIndent)
                  .separator(text(",", styleCall()))
                  .build())
          .front(textCompactBase("）を", styleCall()))
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(calleeKey)))
          .front(textCompactIndent("する", styleCall()));
    }

    @Override
    public TypeBuilder memberExpr(TypeBuilder type, String objectKey, String propertyKey) {
      return type.front(new FrontAtomSpec(new FrontAtomSpec.Config(objectKey)))
          .front(textCompactIndent("の", styleOperator()))
          .front(
              new FrontPrimitiveSpec(
                  new FrontPrimitiveSpec.Config(propertyKey)
                      .meta(DirectStylist.meta(styleIdentifier()))));
    }

    @Override
    public TypeBuilder block(TypeBuilder type, String statementsKey) {
      return type.front(new FrontArraySpecBuilder("statements").prefix(prefixIndent).build())
          .front(textBase("終わり", styleKeyword()));
    }

    @Override
    public TypeBuilder forStatement(
        TypeBuilder type, String initKey, String testKey, String updateKey, String bodyKey) {
      return type.front(new FrontAtomSpec(new FrontAtomSpec.Config(initKey)))
          .front(textCompactIndent("から", styleKeyword()))
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(testKey)))
          .front(textCompactIndent("まで", styleKeyword()))
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(updateKey)))
          .front(textCompactBase("しながら", styleKeyword()))
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(bodyKey)));
    }

    @Override
    public TypeBuilder ifStatement(TypeBuilder type, String testKey, String consequentKey) {
      return type.front(text("もし", styleKeyword()))
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(testKey)))
          .front(textCompactBase("なら", styleKeyword()))
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(consequentKey)));
    }

    @Override
    public TypeBuilder declareInner(TypeBuilder type, String idKey, String initKey) {
      return type.front(prefixCompactIndent)
          .front(
              new FrontPrimitiveSpec(
                  new FrontPrimitiveSpec.Config("id").meta(DirectStylist.meta(styleIdentifier()))))
          .front(text("を", styleBase()))
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config("init")));
    }

    @Override
    public TypeBuilder declareOuter(TypeBuilder type, String declarationsKey) {
      return type.front(
              new FrontArraySpecBuilder("declarations")
                  .prefix(prefixCompactIndent)
                  .separator(text("、", styleDeclare()))
                  .build())
          .front(text("とする", styleDeclare()));
    }
  }

  public static class LispSyntaxFrontFactory extends EnglishBaseFrontFactory {
    @Override
    public TypeBuilder binaryOp(TypeBuilder type, String symbol, String leftKey, String rightKey) {
      String lispSymbol;
      switch (symbol) {
        case "+=":
          {
            lispSymbol = "incf";
            break;
          }
        case "%":
          {
            lispSymbol = "mod";
            break;
          }
        default:
          {
            lispSymbol = symbol;
            break;
          }
      }
      return type.front(text("(" + lispSymbol, styleOperator()))
          .front(space)
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(leftKey)))
          .front(space)
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(rightKey)))
          .front(text(")", styleOperator()));
    }

    @Override
    public TypeBuilder prefixOp(TypeBuilder type, String symbol, String argumentKey) {
      String lispSymbol;
      switch (symbol) {
        case "++":
          {
            lispSymbol = "incf";
            break;
          }
        default:
          {
            lispSymbol = symbol;
            break;
          }
      }
      return type.front(text("(" + lispSymbol, styleOperator()))
          .front(space)
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config("argument")))
          .front(text(")", styleOperator()));
    }

    @Override
    public TypeBuilder callExpr(TypeBuilder type, String calleeKey, String argumentsKey) {
      return type.front(text("(", styleCall()))
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(calleeKey)))
          .front(space)
          .front(
              new FrontArraySpecBuilder(argumentsKey)
                  .prefix(prefixCompactIndent)
                  .separator(text(" ", styleBase()))
                  .build())
          .front(text(")", styleCall()));
    }

    @Override
    public TypeBuilder memberExpr(TypeBuilder type, String objectKey, String propertyKey) {
      return type.front(new FrontAtomSpec(new FrontAtomSpec.Config(objectKey)))
          .front(textCompactIndent(".", styleOperator()))
          .front(
              new FrontPrimitiveSpec(
                  new FrontPrimitiveSpec.Config(propertyKey)
                      .meta(DirectStylist.meta(styleIdentifier()))));
    }

    @Override
    public TypeBuilder block(TypeBuilder type, String statementsKey) {
      return type.front(text("(block ", styleKeyword()))
          .front(
              new FrontArraySpecBuilder("statements")
                  .prefix(prefixCompactIndent)
                  .separator(space)
                  .build())
          .front(text(")", styleKeyword()));
    }

    @Override
    public TypeBuilder forStatement(
        TypeBuilder type, String initKey, String testKey, String updateKey, String bodyKey) {
      return type.front(text("(for ", styleKeyword()))
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(initKey)))
          .front(space)
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(testKey)))
          .front(space)
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(updateKey)))
          .front(space)
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(bodyKey)))
          .front(text(")", styleKeyword()));
    }

    @Override
    public TypeBuilder ifStatement(TypeBuilder type, String testKey, String consequentKey) {
      return type.front(text("(if ", styleKeyword()))
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(testKey)))
          .front(space)
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(consequentKey)))
          .front(text(")", styleKeyword()));
    }

    @Override
    public TypeBuilder declareInner(TypeBuilder type, String idKey, String initKey) {
      return type.front(prefixCompactIndent)
          .front(
              new FrontPrimitiveSpec(
                  new FrontPrimitiveSpec.Config("id").meta(DirectStylist.meta(styleIdentifier()))))
          .front(text(" ", styleBase()))
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config("init")));
    }

    @Override
    public TypeBuilder declareOuter(TypeBuilder type, String declarationsKey) {
      return type.front(text("(let ", styleDeclare()))
          .front(
              new FrontArraySpecBuilder("declarations")
                  .prefix(prefixCompactIndent)
                  .separator(space)
                  .build())
          .front(text(")", styleDeclare()));
    }
  }

  public static class JavascriptSyntaxFrontFactory extends NonLispSyntaxFrontFactory {
    @Override
    public TypeBuilder block(TypeBuilder type, String statementsKey) {
      return type.front(text("{ ", styleBlock()))
          .front(
              new FrontArraySpecBuilder("statements")
                  .prefix(prefixCompactIndent)
                  .suffix(text("; ", styleBlock()))
                  .build())
          .front(textCompactBase("}", styleBlock()));
    }

    @Override
    public TypeBuilder forStatement(
        TypeBuilder type, String initKey, String testKey, String updateKey, String bodyKey) {
      return type.front(text("for (", styleKeyword()))
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(initKey)))
          .front(text("; ", styleKeyword()))
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(testKey)))
          .front(text("; ", styleKeyword()))
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(updateKey)))
          .front(textCompactBase(") ", styleKeyword()))
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(bodyKey)));
    }

    @Override
    public TypeBuilder ifStatement(TypeBuilder type, String testKey, String consequentKey) {
      return type.front(text("if (", styleKeyword()))
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(testKey)))
          .front(text(") ", styleKeyword()))
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(consequentKey)));
    }
  }

  public static class PythonSyntaxFrontFactory extends NonLispSyntaxFrontFactory {
    @Override
    public TypeBuilder block(TypeBuilder type, String statementsKey) {
      return type.front(text(":", styleBlock()))
          .front(new FrontArraySpecBuilder("statements").prefix(prefixIndent).build());
    }

    @Override
    public TypeBuilder forStatement(
        TypeBuilder type, String initKey, String testKey, String updateKey, String bodyKey) {
      return type.front(text("for ", styleKeyword()))
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(initKey)))
          .front(text("; ", styleKeyword()))
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(testKey)))
          .front(text("; ", styleKeyword()))
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(updateKey)))
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(bodyKey)));
    }

    @Override
    public TypeBuilder ifStatement(TypeBuilder type, String testKey, String consequentKey) {
      return type.front(text("if ", styleKeyword()))
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(testKey)))
          .front(prefixCompactIndent)
          .front(new FrontAtomSpec(new FrontAtomSpec.Config(consequentKey)));
    }
  }
}
