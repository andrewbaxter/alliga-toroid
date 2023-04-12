package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.JumpKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;

public class JavaBytecodeJump implements JavaBytecode {
    public final JumpKey jumpKey;

    public JavaBytecodeJump(JumpKey jumpKey) {
        this.jumpKey = jumpKey;
    }

    @Override
    public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleJump(this);
    }
}
