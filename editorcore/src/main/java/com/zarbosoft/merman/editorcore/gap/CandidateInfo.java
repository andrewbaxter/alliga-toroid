package com.zarbosoft.merman.editorcore.gap;

import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.document.fields.FieldPrimitive;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.front.FrontArraySpecBase;
import com.zarbosoft.merman.core.syntax.front.FrontAtomSpec;
import com.zarbosoft.merman.core.syntax.front.FrontPrimitiveSpec;
import com.zarbosoft.merman.core.syntax.front.FrontSpec;
import com.zarbosoft.merman.core.syntax.front.FrontSymbolSpec;
import com.zarbosoft.merman.core.syntax.primitivepattern.Any;
import com.zarbosoft.merman.core.syntax.primitivepattern.PatternString;
import com.zarbosoft.merman.core.syntax.symbol.SymbolTextSpec;
import com.zarbosoft.pidgoon.events.EscapableResult;
import com.zarbosoft.pidgoon.model.Node;
import com.zarbosoft.pidgoon.nodes.HomogenousEscapableSequence;
import com.zarbosoft.pidgoon.nodes.Operator;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

public class CandidateInfo {
  /** Atom/array fronts preceding the concrete key text */
  public final ROList<FrontSpec> preceding;
  /**
   * Matches the key text and puts a var double list of (string key, string primitive contents) for
   * parsed primitive front
   */
  public final Node<EscapableResult<ROList<FieldPrimitive>>> keyGrammar;

  public final ROList<FrontSpec> keySpecs;
  /** May be null */
  public final FrontSpec following;

  public CandidateInfo(
      ROList<FrontSpec> preceding,
      Node<EscapableResult<ROList<FieldPrimitive>>> keyGrammar,
      ROList<FrontSpec> keySpecs,
      FrontSpec following) {
    this.preceding = preceding;
    this.keyGrammar = keyGrammar;
    this.keySpecs = keySpecs;
    this.following = following;
  }

  public static CandidateInfo inspect(Environment env, AtomType candidate) {
    ROList<FrontSpec> front = candidate.front();
    TSList<FrontSpec> preceding = new TSList<>();
    HomogenousEscapableSequence<FieldPrimitive> keyGrammar =
        new HomogenousEscapableSequence<FieldPrimitive>();
    TSList<FrontSpec> keySpecs = new TSList<>();
    FrontSpec[] following = {null};
    new Object() {
      {
        for (int i = 0; i < front.size(); ++i) {
          FrontSpec f = front.get(i);
          if (f instanceof FrontSymbolSpec) {
            processSymbol((FrontSymbolSpec) f);

          } else if (f instanceof FrontArraySpecBase) {
            if (((FrontArraySpecBase) f).prefix.isEmpty()) {
              if (!keyGrammar.isEmpty()) {
                following[0] = f;
                break;
              }
              preceding.add(f);
            } else {
              for (FrontSymbolSpec prefix : ((FrontArraySpecBase) f).prefix) {
                processSymbol(prefix);
              }
              following[0] = f;
              break;
            }

          } else if (f instanceof FrontAtomSpec) {
            if (!keyGrammar.isEmpty()) {
              following[0] = f;
              break;
            }
            preceding.add(f);

          } else if (f instanceof FrontPrimitiveSpec) {
            keySpecs.add(f);
            keyGrammar.add(
                new Operator<EscapableResult<ROList<String>>, EscapableResult<FieldPrimitive>>(
                    ((FrontPrimitiveSpec) f).field.pattern != null
                        ? ((FrontPrimitiveSpec) f).field.pattern.build(true)
                        : Any.repeatedAny.build(true)) {
                  @Override
                  protected EscapableResult<FieldPrimitive> process(
                      EscapableResult<ROList<String>> value) {
                    return new EscapableResult<>(
                        value.completed,
                        new FieldPrimitive(
                            ((FrontPrimitiveSpec) f).field, Environment.joinGlyphs(value.value)));
                  }
                });
          }
        }
      }

      void processSymbol(FrontSymbolSpec f) {
        keySpecs.add(f);
        if (f.gapKey != null) {
          keyGrammar.addIgnored(new PatternString(env, f.gapKey).build(false));
        } else if (f.type instanceof SymbolTextSpec) {
          keyGrammar.addIgnored(
              new PatternString(env, ((SymbolTextSpec) f.type).text).build(false));
        }
      }
    };

    return new CandidateInfo(preceding, keyGrammar, keySpecs, following[0]);
  }
}
