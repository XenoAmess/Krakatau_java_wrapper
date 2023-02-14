package com.xenoamess.krakatau_java_wrapper.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

public class KrakatauUtilTest {

    @Test
    public void test() throws IOException {
        if (!SystemUtils.IS_OS_WINDOWS) {
            return;
        }
        String inputFilePath1 = new File(new File("").getAbsolutePath() + "/target/classes/com/xenoamess" +
                "/krakatau_java_wrapper/util/KrakatauUtil.class").getAbsolutePath().replaceAll("\\\\", "/");
        String disassembleResult = KrakatauUtil.disassembleFromPath(inputFilePath1);
        System.out.println("disassembleResult : " + disassembleResult);
        byte[] assembleResult = KrakatauUtil.assemble(disassembleResult);
        System.out.println("assembleResult : " + Arrays.toString(assembleResult));
    }

}
