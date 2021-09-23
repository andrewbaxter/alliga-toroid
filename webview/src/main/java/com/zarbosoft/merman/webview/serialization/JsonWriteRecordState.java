package com.zarbosoft.merman.webview.serialization;

import elemental2.core.JsObject;
import jsinterop.base.JsPropertyMap;

public class JsonWriteRecordState implements JsonWriteState {
  public final JsPropertyMap value = (JsPropertyMap) new JsObject();
  private String key;

  @Override
  public void value(Object value) {
    if (key == null) {
      this.key = (String) key;
    } else {
      this.value.set(key, value);
      key = null;
    }
  }
}
