package com.xenoamess.krakatau_java_wrapper;

import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class KrakatauUtil {

    public static void main(String[] args) throws IOException {
        disassemble();
        assemble();
    }

    public static void disassemble() throws IOException {
        Properties props = new Properties();
        System.out.println("草");

//        props.put("python.home", "/Krakatau");
        props.put("python.console.encoding", "UTF-8");
        props.put("python.security.respectJavaAccessibility", "false");
        props.put("python.import.site", "false");

        Properties preprops = System.getProperties();

        String[] params = new String[]{
                "",
                "-out",
                new File(new File("").getAbsolutePath() + "/out/out.zip").getAbsolutePath().replaceAll("\\\\","/"),
                "-roundtrip",
                "C:\\workspace\\Krakatau_java_wrapper\\target\\classes\\com\\xenoamess\\krakatau_java_wrapper\\KrakatauUtil.class".replaceAll("\\\\","/")
        };

        String pythonParams = buildPythonParams(params);

        PythonInterpreter.initialize(preprops, props, params);

        PySystemState state = new PySystemState();

        Path path = Files.createTempFile("KrakatauJavaWrapper", "");
        try (
                InputStream is = KrakatauUtil.class.getResourceAsStream("/disassemble.py");
                PythonInterpreter pythonInterpreter = new PythonInterpreter(null, state)
        ) {
            pythonInterpreter.exec("import sys");
            pythonInterpreter.exec("sys.argv = " + pythonParams);
            pythonInterpreter.exec("print(sys.argv)");
            pythonInterpreter.exec("reload(sys)");
//            pythonInterpreter.exec("print(u'\\ud800')");
//            pythonInterpreter.exec("re.compile(u'\\ud800')");
//            pythonInterpreter.exec("import disassemble");
            pythonInterpreter.execfile(is);
        }

    }

    public static void assemble() throws IOException {
        Properties props = new Properties();
        System.out.println("草");

//        props.put("python.home", "/Krakatau");
        props.put("python.console.encoding", "UTF-8");
        props.put("python.security.respectJavaAccessibility", "false");
        props.put("python.import.site", "false");

        Properties preprops = System.getProperties();

        String[] params = new String[]{
                "",
                "-out",
                new File(new File("").getAbsolutePath() + "/out").getAbsolutePath().replaceAll("\\\\","/"),
                new File(new File("").getAbsolutePath() + "/out/KrakatauUtil.j").getAbsolutePath().replaceAll("\\\\","/")
        };

        String pythonParams = buildPythonParams(params);

        PythonInterpreter.initialize(preprops, props, new String[0]);

        PySystemState state = new PySystemState();

        Path path = Files.createTempFile("KrakatauJavaWrapper", "");
        try (
                InputStream is = KrakatauUtil.class.getResourceAsStream("/assemble.py");
                PythonInterpreter pythonInterpreter = new PythonInterpreter(null, state)
        ) {
            pythonInterpreter.exec("import sys");
            pythonInterpreter.exec("sys.argv = " + pythonParams);
            pythonInterpreter.exec("reload(sys)");
//            pythonInterpreter.exec("print(u'\\ud800')");
//            pythonInterpreter.exec("re.compile(u'\\ud800')");
//            pythonInterpreter.exec("import disassemble");
            pythonInterpreter.execfile(is);
        }

    }

    @NotNull
    private static String buildPythonParams(@NotNull String @NotNull [] params) {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (String string : params) {
            stringBuilder.append(" r\"").append(string).append("\" ,");
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }


//    @NotNull
//    public static String readClass(
//
//    ) {
//
//    }


}
