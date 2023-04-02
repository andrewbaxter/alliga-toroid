package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Map;

public class MortarObjectImplField implements MortarObjectField {
    private final MortarObjectInnerType parentInfo;
    private final String name;
    public final MortarObjectInnerType info;
    public final ROMap<Object, MortarObjectField> fields;

    public MortarObjectImplField(
            MortarObjectInnerType parentInfo, String name, MortarObjectInnerType info, ROMap<Object, MortarObjectField> fields) {
        this.parentInfo = parentInfo;
        this.name = name;
        this.info = info;
        this.fields = fields;
    }

    @Override
    public MortarObjectFieldstate field_newFieldstate() {
        TSMap<Object, MortarObjectFieldstate> fields = new TSMap<>();
        for (Map.Entry<Object, MortarObjectField> field : this.fields) {
            fields.put(field.getKey(), field.getValue().field_newFieldstate());
        }
    return new MortarObjectImplFieldstate(parentInfo, name, info, fields);
    }
}
