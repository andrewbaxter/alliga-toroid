package com.zarbosoft.alligatoroid.compiler.inout.utils.languageinout;

import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.DefaultStateInt;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.DefaultStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.Deserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.State;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateErrorSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto.AutoInfo;
import com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto.AutoTreeMeta;
import com.zarbosoft.alligatoroid.compiler.model.error.DeserializeUnknownLanguageVersion;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LanguageElement;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;

import java.io.InputStream;

import static com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.Deserializer.errorRet;

public class LanguageDeserializer {
  private static final AutoTreeMeta languageMeta;

  static {
    languageMeta = new AutoTreeMeta();
    languageMeta.infos.put(
        Location.class,
        new AutoInfo() {
          @Override
          public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath) {
            return new DefaultStateInt<ModuleId, Location>() {
              @Override
              public Location build(ModuleId module, TSList<Error> errors1) {
                if (value == null) {
                  return null;
                }
                return new Location(module, value);
              }
            };
          }

          @Override
          public void write(Writer writer, Object object) {
            throw new Assertion();
          }
        });
    languageMeta.scan(LanguageElement.class);
  }

  public static LanguageElement deserialize(
      ModuleId moduleId, TSList<Error> errors, String path, InputStream source) {
    TSList<State> stack = new TSList<>();
    State[] rootNodes = new State[1];
    stack.add(
        new DefaultStateSingle<ModuleId, Object>() {
          @Override
          public Object build(ModuleId context, TSList<Error> errors) {
            return null;
          }

          @Override
          protected BaseStateSingle innerEatType(
              ModuleId module, TSList<Error> errors, LuxemPathBuilder luxemPath, String name) {
            String expected = "alligatorus:0.0.1";
            if (!expected.equals(name)) {
              errors.add(new DeserializeUnknownLanguageVersion(luxemPath.render(), expected));
              return StateErrorSingle.state;
            }
            BaseStateSingle out =
                languageMeta.deserialize(errors, luxemPath, LanguageElement.class);
            rootNodes[0] = out;
            return out;
          }
        });
    Deserializer.deserialize(moduleId, errors, path, source, stack);
    if (rootNodes[0] == null) {
      return null;
    }
    Object out = rootNodes[0].build(moduleId, errors);
    if (out == errorRet) {
      return null;
    }
    return (LanguageElement) out;
  }
}
