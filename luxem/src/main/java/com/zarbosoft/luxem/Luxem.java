package com.zarbosoft.luxem;

import com.zarbosoft.luxem.events.LArrayCloseEvent;
import com.zarbosoft.luxem.events.LArrayOpenEvent;
import com.zarbosoft.luxem.events.LPrimitiveEvent;
import com.zarbosoft.luxem.events.LRecordCloseEvent;
import com.zarbosoft.luxem.events.LRecordOpenEvent;
import com.zarbosoft.luxem.events.LTypeEvent;
import com.zarbosoft.luxem.read.BufferedReader;
import com.zarbosoft.luxem.read.Reader;
import com.zarbosoft.luxem.read.path.LuxemArrayPathBuilder;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.luxem.read.path.LuxemRecordPathBuilder;
import com.zarbosoft.pidgoon.events.Event;
import com.zarbosoft.pidgoon.events.Position;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

import java.io.InputStream;

/** Methods for common use cases. */
public class Luxem {
  public static ROList<? extends ROPair> streamEvents(
      final InputStream source, final Reader.EventFactory factory) {
    class State {
      LuxemPathBuilder path = new LuxemArrayPathBuilder(null);
      TSList<ROPair<Event, Position>> events = new TSList<>();
    }
    final State state = new State();
    final BufferedReader reader =
        new BufferedReader() {
          @Override
          protected void eatRecordBegin() {
            state.path = new LuxemRecordPathBuilder(state.path.value());
            state.events.add(
                new ROPair<>(
                    factory.objectOpen(), new Position(LRecordOpenEvent.instance, state.path)));
          }

          @Override
          protected void eatArrayBegin() {
            state.path = new LuxemArrayPathBuilder(state.path.value());
            state.events.add(
                new ROPair<>(
                    factory.arrayOpen(), new Position(LArrayOpenEvent.instance, state.path)));
          }

          @Override
          protected void eatArrayEnd() {
            state.path = state.path.pop();
            state.events.add(
                new ROPair<>(
                    factory.arrayClose(), new Position(LArrayCloseEvent.instance, state.path)));
          }

          @Override
          protected void eatRecordEnd() {
            state.path = state.path.pop();
            state.events.add(
                new ROPair<>(
                    factory.objectClose(), new Position(LRecordCloseEvent.instance, state.path)));
          }

          @Override
          protected void eatType(String text) {
            state.path = state.path.type();
            state.events.add(
                new ROPair<>(factory.type(text), new Position(new LTypeEvent(text), state.path)));
          }

          @Override
          protected void eatPrimitive(String string) {
            state.path = state.path.value();
            state.events.add(
                new ROPair<>(
                    factory.primitive(string),
                    new Position(new LPrimitiveEvent(string), state.path)));
          }
        };
    reader.feed(source);
    reader.finish();
    return state.events;
  }
}
