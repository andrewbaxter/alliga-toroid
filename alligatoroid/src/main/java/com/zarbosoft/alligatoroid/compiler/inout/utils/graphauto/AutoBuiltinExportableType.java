package com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto;

import com.zarbosoft.alligatoroid.compiler.Builtin;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRef;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRefArtifact;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRefBuiltin;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialTuple;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import static com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.Pregen.graphAuxConverters;
import static com.zarbosoft.rendaw.common.Common.uncheck;

public class AutoBuiltinExportableType implements Exportable {
  private final Constructor constructor;

  public AutoBuiltinExportableType(Class klass) {
    constructor = uncheck(() -> klass.getConstructors()[0]);
  }

  @Override
  public SemiserialSubvalue graphSemiserialize(
      ImportId spec,
      Semiserializer semiserializer,
      ROList<Exportable> path,
      ROList<String> accessPath) {
    throw new Assertion();
  }

  @Override
  public Value type() {
    throw new Assertion();
  }

  public Object prepNonCollectionArg(
      ModuleCompileContext context, Class t, SemiserialSubvalue data) {
    GraphAuxConverter graphAuxConverter;
    if ((graphAuxConverter = graphAuxConverters.getOpt(t)) != null) {
      return graphAuxConverter.desemiserialize(data);
    } else if (Value.class.isAssignableFrom(t)) {
      return data.dispatch(
          new SemiserialSubvalue.DefaultDispatcher<>() {
            @Override
            public Object handleRef(SemiserialRef s) {
              return s.dispatchRef(
                  new SemiserialRef.Dispatcher<Object>() {
                    @Override
                    public Object handleArtifact(SemiserialRefArtifact s) {
                      return context.artifactLookup.getOpt(s.id);
                    }

                    @Override
                    public Object handleBuiltin(SemiserialRefBuiltin s) {
                      return Builtin.semiKeyToBuiltin.get(s.key);
                    }
                  });
            }
          });
    } else return null;
  }

  @Override
  public Exportable graphDesemiserializeChild(
      ModuleCompileContext context,
      Desemiserializer typeDesemiserializer,
      SemiserialSubvalue data) {
    TSList<ROPair<Field, SemiserialRefArtifact>> valueFields = new TSList<>();
    TSList<ROPair<ROPair<TSList, Integer>, SemiserialRefArtifact>> valueListFields = new TSList<>();
    final int paramCount = constructor.getParameterCount();
    Object[] args = new Object[paramCount];
    data.dispatch(
        new SemiserialSubvalue.DefaultDispatcher<>() {
          @Override
          public Object handleTuple(SemiserialTuple s) {
            if (s.values.size() != paramCount)
              throw new RuntimeException(
                  "semiserial root tuple element count doesn't match constructor param count");
            for (int i = 0; i < paramCount; i++) {
              final SemiserialSubvalue subdata = s.values.get(i);
              final Parameter parameter = constructor.getParameters()[i];
              final Class<?> t = parameter.getType();
              Object primitiveArg = prepNonCollectionArg(context, t, subdata);
              if (primitiveArg != null) {
                args[i] = primitiveArg;
              } else if (Value.class.isAssignableFrom(t)) {
                return data.dispatch(
                    new SemiserialSubvalue.DefaultDispatcher<>() {
                      @Override
                      public Object handleRef(SemiserialRef s) {
                        return s.dispatchRef(
                            new SemiserialRef.Dispatcher<Object>() {
                              @Override
                              public Object handleArtifact(SemiserialRefArtifact s) {
                                Exportable found = context.artifactLookup.getOpt(s.id);
                                if (found != null) return found;
                                valueFields.add(
                                    new ROPair<>(
                                        uncheck(
                                            () ->
                                                constructor
                                                    .getDeclaringClass()
                                                    .getField(parameter.getName())),
                                        s));
                                return null;
                              }

                              @Override
                              public Object handleBuiltin(SemiserialRefBuiltin s) {
                                return Builtin.semiKeyToBuiltin.get(s.key);
                              }
                            });
                      }
                    });
              } else if (ROList.class.isAssignableFrom(t)) {
                args[i] =
                    subdata.dispatch(
                        new SemiserialSubvalue.DefaultDispatcher<>() {
                          @Override
                          public Object handleTuple(SemiserialTuple s) {
                            TSList out = new TSList();
                            for (SemiserialSubvalue subdata : s.values) {
                              final Class t1 = t.getTypeParameters()[0].getGenericDeclaration();
                              final Object primitiveArg =
                                  prepNonCollectionArg(context, t1, subdata);
                              if (primitiveArg == null) {
                                if (Value.class.isAssignableFrom(t1)) {
                                  valueListFields.add(
                                      new ROPair<ROPair<TSList, Integer>, SemiserialRefArtifact>(
                                          new ROPair<TSList, Integer>(out, out.size()),
                                          /** Must be artifact if prep func returns null * */
                                          (SemiserialRefArtifact) subdata));
                                } else throw new Assertion();
                              }
                              out.add(primitiveArg);
                            }
                            return out;
                          }
                        });
              } else {
                throw new Assertion();
              }
            }
            return null;
          }
        });
    Value out = (Value) uncheck(() -> constructor.newInstance(args));
    typeDesemiserializer.finishTasks.add(
        () -> {
          for (ROPair<Field, SemiserialRefArtifact> pair : valueFields) {
            uncheck(() -> pair.first.set(out, context.artifactLookup.get(pair.second.id)));
          }
          for (ROPair<ROPair<TSList, Integer>, SemiserialRefArtifact> pair : valueListFields) {
            pair.first.first.set(pair.first.second, context.artifactLookup.get(pair.second.id));
          }
        });
    return out;
  }

  @Override
  public void postDesemiserialize() {
    throw new Assertion();
  }

  public interface GraphAuxConverter {
    Object desemiserialize(SemiserialSubvalue data);

    SemiserialSubvalue semiserialize(Object data);
  }
}
