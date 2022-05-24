package com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.IdentityArtifact;
import com.zarbosoft.alligatoroid.compiler.inout.graph.IdentityArtifactType;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRecord;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialString;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto.TypeInfo;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.SingletonBuiltinArtifact;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class AutoBuiltinArtifactType
    implements IdentityArtifactType, SingletonBuiltinArtifact {
  private final Constructor constructor;

  public AutoBuiltinArtifactType(Class klass) {
    constructor = uncheck(() -> klass.getConstructors()[0]);
  }

  @Override
  public IdentityArtifact graphDesemiserializeBody(
      ModuleCompileContext context,
      Desemiserializer typeDesemiserializer,
      SemiserialSubvalue data) {
    Class klass = constructor.getDeclaringClass();
    IdentityArtifact out =
        (IdentityArtifact) uncheck(() -> klass.getConstructors()[0].newInstance());
    typeDesemiserializer.finishTasks.add(
        () -> {
          data.dispatch(
              new SemiserialSubvalue.DefaultDispatcher<>() {
                @Override
                public Object handleRecord(SemiserialRecord s) {
                  for (Field field : klass.getFields()) {
                    if (field.getAnnotation(Param.class) == null) continue;
                    uncheck(
                        () ->
                            field.set(
                                out,
                                AutoGraphUtils.autoDesemiAnyViaReflect(
                                    context,
                                    TypeInfo.fromField(field),
                                    s.data.get(SemiserialString.create(field.getName())))));
                  }
                  return null;
                }
              });
        });
    typeDesemiserializer.exportables.add(out);
    return out;
  }
}
