package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.inout.utils.classstate.PrototypeAuto;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateRecordBody;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.DefaultStateArrayBody;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.DefaultStateBool;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.DefaultStateInt;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.DefaultStatePrimitive;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.DefaultStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateArray;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateErrorSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateRecord;
import com.zarbosoft.alligatoroid.compiler.model.error.DeserializeUnknownType;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSOrderedMap;

public class SemideserializeSubvalueState extends DefaultStateSingle<Void, SemiserialSubvalue> {
  public static final PrototypeAuto protoRefArtifact =
      new PrototypeAuto(SemiserialRefArtifact.class);
  public static final PrototypeAuto protoRefBuiltin = new PrototypeAuto(SemiserialRefBuiltin.class);
  private BaseStateSingle<Void, ? extends SemiserialSubvalue> inner = null;

  public SemideserializeSubvalueState() {}

  @Override
  protected BaseStateSingle innerEatType(
      Void context, TSList<Error> errors, LuxemPathBuilder luxemPath, String name) {
    switch (name) {
      case SemiserialInt.SERIAL_TYPE:
        return inner =
            new DefaultStateInt<Void, SemiserialSubvalue>() {
              @Override
              public SemiserialSubvalue build(Void context, TSList<Error> errors) {
                return new SemiserialInt(value);
              }
            };
      case SemiserialString.SERIAL_TYPE:
        return inner =
            new DefaultStatePrimitive<Void, SemiserialSubvalue>() {
              @Override
              public SemiserialSubvalue build(Void context, TSList<Error> errors) {
                return new SemiserialString(out);
              }
            };
      case SemiserialBool.SERIAL_TYPE:
        return inner =
            new DefaultStateBool<Void, SemiserialSubvalue>() {
              @Override
              public SemiserialSubvalue build(Void context, TSList<Error> errors) {
                return new SemiserialBool(value);
              }
            };
      case SemiserialRecord.SERIAL_TYPE:
        return inner =
            new StateRecord<Void, SemiserialRecord>(
                new BaseStateRecordBody<Void, SemiserialRecord>() {
                  private final TSList<
                          ROPair<SemideserializeSubvalueState, SemideserializeSubvalueState>>
                      data = new TSList<>();
                  private SemideserializeSubvalueState key;

                  @Override
                  public SemiserialRecord build(Void context, TSList<Error> errors) {
                    boolean[] ok = new boolean[] {true};
                    return new SemiserialRecord(
                        new TSOrderedMap<SemiserialSubvalue, SemiserialSubvalue>(
                            m -> {
                              for (ROPair<
                                      SemideserializeSubvalueState, SemideserializeSubvalueState>
                                  kv : data) {
                                final SemiserialSubvalue key = kv.first.build(context, errors);
                                if (key == null) {
                                  ok[0] = false;
                                  return;
                                }
                                final SemiserialSubvalue value = kv.second.build(context, errors);
                                if (value == null) {
                                  ok[0] = false;
                                  return;
                                }
                                m.put(key, value);
                              }
                            }));
                  }

                  @Override
                  public BaseStateSingle createKeyState(
                      Void context, TSList<Error> errors, LuxemPathBuilder luxemPath) {
                    return key = new SemideserializeSubvalueState();
                  }

                  @Override
                  public BaseStateSingle createValueState(
                      Void context, TSList<Error> errors, LuxemPathBuilder luxemPath, Object key) {
                    final SemideserializeSubvalueState value = new SemideserializeSubvalueState();
                    data.add(new ROPair<>(this.key, value));
                    return value;
                  }
                });
      case SemiserialTuple.SERIAL_TYPE:
        return new StateArray<>(
            new DefaultStateArrayBody<Void, SemiserialTuple>() {
              private final TSList<SemideserializeSubvalueState> elements = new TSList<>();

              @Override
              public SemiserialTuple build(Void context, TSList<Error> errors) {
                final TSList<SemiserialSubvalue> data = new TSList<>();
                for (SemideserializeSubvalueState element : elements) {
                  final SemiserialSubvalue built = element.build(context, errors);
                  if (built == null) return null;
                  data.add(built);
                }
                return new SemiserialTuple(data);
              }

              @Override
              public BaseStateSingle createElementState(
                  Void context, TSList<Error> errors, LuxemPathBuilder luxemPath) {
                final SemideserializeSubvalueState element = new SemideserializeSubvalueState();
                elements.add(element);
                return element;
              }
            });
      case SemiserialRefArtifact.SERIAL_TYPE:
        return protoRefArtifact.create(errors, luxemPath);
      case SemiserialRefBuiltin.SERIAL_TYPE:
        return protoRefBuiltin.create(errors, luxemPath);
      default:
        {
          errors.add(new DeserializeUnknownType(luxemPath.render(), name, ROList.empty));
          return StateErrorSingle.state;
        }
    }
  }

  @Override
  public SemiserialSubvalue build(Void context, TSList<Error> errors) {
    return inner.build(context, errors);
  }
}
