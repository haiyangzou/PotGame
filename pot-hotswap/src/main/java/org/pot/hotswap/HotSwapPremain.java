package org.pot.hotswap;

import java.lang.instrument.Instrumentation;

public class HotSwapPremain {
    private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) {
        HotSwapPremain.instrumentation = inst;
    }

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }
}
