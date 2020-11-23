package com.zarbosoft.merman.syntax.back;

import com.zarbosoft.merman.editor.backevents.EArrayCloseEvent;
import com.zarbosoft.merman.editor.backevents.EArrayOpenEvent;
import com.zarbosoft.merman.syntax.AtomType;
import com.zarbosoft.merman.syntax.Syntax;
import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.events.nodes.MatchingEventTerminal;
import com.zarbosoft.pidgoon.nodes.Sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.zarbosoft.rendaw.common.Common.enumerate;

public class BackFixedArraySpec extends BackSpec {

  public List<BackSpec> elements = new ArrayList<>();

  @Override
  public Node buildBackRule(final Syntax syntax, final AtomType atomType) {
    final Sequence sequence;
    sequence = new Sequence();
    sequence.add(new MatchingEventTerminal(new EArrayOpenEvent()));
    for (final BackSpec element : elements) {
      sequence.add(element.buildBackRule(syntax, atomType));
    }
    sequence.add(new MatchingEventTerminal(new EArrayCloseEvent()));
    return sequence;
  }

  @Override
  public void finish(final Syntax syntax, final AtomType atomType, final Set<String> middleUsed) {
    enumerate(elements.stream())
        .forEach(
            pair -> {
              pair.second.finish(syntax, atomType, middleUsed);
              pair.second.parent =
                  new PartParent() {
                    @Override
                    public BackSpec part() {
                      return BackFixedArraySpec.this;
                    }

                    @Override
                    public String pathSection() {
                      return pair.first.toString();
                    }
                  };
            });
  }
}
