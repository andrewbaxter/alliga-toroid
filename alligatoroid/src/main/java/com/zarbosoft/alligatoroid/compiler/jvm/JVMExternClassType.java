package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.model.Value;
import com.zarbosoft.alligatoroid.compiler.model.language.Builtin;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarClass;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.Tuple;
import com.zarbosoft.alligatoroid.compiler.mortar.WholeString;
import com.zarbosoft.alligatoroid.compiler.mortar.WholeValue;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSOrderedMap;

public class JVMExternClassType extends JVMBaseClassType {
  public static final String SERIAL_NAME = "jvmName";
  public static final String SERIAL_FIELDS_METHODS = "methods";
  public static final String SERIAL_FIELDS_DATA = "data";
  public static final String SERIAL_FIELDS_STATIC_METHODS = "staticMethods";
  public static final String SERIAL_FIELDS_STATIC_DATA = "staticData";
  public static final String SERIAL_INHERITS = "inherits";
  public static final String SERIAL_SETUP = "setup";
  public final TSMap<ROTuple, JVMShallowMethodFieldType.MethodSpecDetails> constructorSigs =
      new TSMap<>();
  private final Value setup;
  boolean finished;
  private boolean setupDone = false;

  public JVMExternClassType(String jvmExternalClass, Value setup) {
    super(
        jvmExternalClass,
        new TSMap<>(),
        new TSMap<>(),
        new TSMap<>(),
        new TSMap<>(),
        new TSList<>());
    finished = false;
    this.setup = setup;
  }

  private JVMExternClassType(
      String jvmExternalClass,
      TSMap<String, JVMDataType> dataFields,
      TSMap<ROTuple, JVMShallowMethodFieldType> methodFields,
      TSMap<String, JVMDataType> staticDataFields,
      TSMap<ROTuple, JVMShallowMethodFieldType> staticMethodFields,
      TSList<JVMBaseClassType> inherits,
      Value setup) {
    super(
        jvmExternalClass, dataFields, methodFields, staticDataFields, staticMethodFields, inherits);
    finished = true;
    this.setup = setup;
  }

  public static JVMExternClassType graphDeserialize(Record record) {
    JVMExternClassType out =
        new JVMExternClassType(
            (String) record.data.get(SERIAL_NAME),
            (TSMap) ((Record) record.data.get(SERIAL_FIELDS_DATA)).data,
            new TSMap<>(),
            (TSMap) ((Record) record.data.get(SERIAL_FIELDS_STATIC_DATA)).data,
            new TSMap<>(),
            (TSList) ((Tuple) record.data.get(SERIAL_INHERITS)).data,
            (Value) record.data.get(SERIAL_SETUP));
    return out;
  }

  @Override
  public void resolveMethods(ModuleCompileContext module) {
    if (setupDone) return;
    MortarClass classValueType = Builtin.wrappedClasses.get(JVMExternClassBuilder.class);
    module.compileContext.evaluate(
        module,
        new TSList<>(setup),
        new TSOrderedMap<WholeValue, Value>()
            .put(
                new WholeString("class"), classValueType.unlower(new JVMExternClassBuilder(this))));
    setupDone = true;
  }

  @Override
  public Record graphSerialize() {
    return new Record(
        new TSMap<Object, Object>()
            .put(SERIAL_NAME, jvmName)
            .put(SERIAL_FIELDS_DATA, new Record((TSMap) dataFields))
            .put(SERIAL_FIELDS_STATIC_DATA, new Record((TSMap) staticDataFields))
            .put(SERIAL_INHERITS, new Tuple((TSList) inherits))
            .put(SERIAL_SETUP, setup));
  }
}
