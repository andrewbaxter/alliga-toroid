package com.zarbosoft.alligatoroid.compiler.jvmshared;

public interface JavaBytecode {
  <T> T dispatch(Dispatcher<T> dispatcher);

  public interface Dispatcher<T> {
    T handleInstruction(JavaBytecodeInstruction n);

    T handleLineNumber(JavaBytecodeLineNumber n);

    T handleSequence(JavaBytecodeSequence n);

    T handleStoreLoad(JavaBytecodeStoreLoad n);

    T handleCatch(JavaBytecodeCatch n);

    T handleCatchStart(JavaBytecodeCatchStart n);
  }

  public interface DefaultDispatcher<T> extends Dispatcher<T> {

    @Override
    default T handleCatch(JavaBytecodeCatch n) {
      return handleDefault(n);
    }

    @Override
    default T handleCatchStart(JavaBytecodeCatchStart n) {
      return handleDefault(n);
    }

    default T handleDefault(JavaBytecode n) {
      return null;
    }

    @Override
    default T handleInstruction(JavaBytecodeInstruction n) {
      return handleDefault(n);
    }

    @Override
    default T handleStoreLoad(JavaBytecodeStoreLoad n) {
      return handleDefault(n);
    }

    @Override
    default T handleLineNumber(JavaBytecodeLineNumber n) {
      return handleDefault(n);
    }

    @Override
    default T handleSequence(JavaBytecodeSequence n) {
      return handleDefault(n);
    }
  }
}
