package com.xenoamess.krakatau_java_wrapper.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Supplier;

import com.xenoamess.krakatau_java_wrapper.exception.CannotFindOutputException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

/**
 * @author XenoAmess
 */
public class KrakatauUtil {

    public static String disassemble(
            byte @NotNull [] inputClassBytes
    ) throws IOException {
        return disassemble(
                inputClassBytes,
                null
        );
    }

    public static String disassemble(
            byte @NotNull [] inputClassBytes,
            @Nullable Supplier<String> tempFolderPathSupplier
    ) throws IOException {
        String tempFilePath;
        if (tempFolderPathSupplier != null) {
            tempFilePath = tempFolderPathSupplier.get() + "/" + UUID.randomUUID() + ".class";
        } else {
            tempFilePath =
                    Files.createTempFile("KrakatauJavaWrapperTempFileDisassemble", ".class").toAbsolutePath().toFile().getAbsolutePath().replaceAll("\\\\", "/");
        }

        FileObject outputFolderFileObject = VFS.getManager().toFileObject(new File(tempFilePath));
        try (
                OutputStream outputStream = outputFolderFileObject.getContent().getOutputStream()
        ) {
            outputStream.write(inputClassBytes);
        }

        String res = disassembleFromPath(
                tempFilePath,
                tempFolderPathSupplier
        );

        try {
            outputFolderFileObject.delete();
        } catch (Exception ignored) {


        }

        return res;

    }

    public static String disassembleFromPath(
            @NotNull String inputFilePath
    ) throws IOException {
        return disassembleFromPath(
                inputFilePath,
                null
        );
    }

    public static String disassembleFromPath(
            @NotNull String inputFilePath,
            @Nullable Supplier<String> tempFolderPathSupplier
    ) throws IOException {
        final String outputFilePath;
        if (tempFolderPathSupplier != null) {
            outputFilePath = tempFolderPathSupplier.get() + "/" + UUID.randomUUID() + ".zip";
        } else {
            outputFilePath =
                    Files.createTempFile("KrakatauJavaWrapperTempFileDisassemble", ".zip").toAbsolutePath().toFile().getAbsolutePath().replaceAll("\\\\", "/");
        }
        Properties props = new Properties();

        props.put("python.console.encoding", "UTF-8");
        props.put("python.security.respectJavaAccessibility", "false");
        props.put("python.import.site", "false");

        Properties preprops = System.getProperties();

        String[] params = new String[]{
                "",
                "-out",
                outputFilePath,
                "-roundtrip",
                inputFilePath
        };

        String pythonParams = buildPythonParams(params);

        PythonInterpreter.initialize(preprops, props, params);

        PySystemState state = new PySystemState();

        try (
                InputStream inputStream = KrakatauUtil.class.getResourceAsStream("/disassemble.py");
                BufferedInputStream bufferedInputStream = new BufferedInputStream(Objects.requireNonNull(inputStream));
                PythonInterpreter pythonInterpreter = new PythonInterpreter(null, state)
        ) {
            pythonInterpreter.exec("import sys");
            pythonInterpreter.exec("sys.argv = " + pythonParams);
            pythonInterpreter.exec("reload(sys)");
            pythonInterpreter.execfile(bufferedInputStream);
        }

        FileObject fileObject = VFS.getManager().toFileObject(new File(outputFilePath));
        String zipFileUrl = "zip:" + fileObject.getURL().toString();
        FileObject currentFileObject = VFS.getManager().resolveFile(zipFileUrl);

        String res = findOnlyChild(
                currentFileObject,
                ".j"
        ).getContent().getString(StandardCharsets.UTF_8);

        try {
            fileObject.delete();
        } catch (Exception ignored) {

        }

        return res;
    }

    public static byte[] assemble(
            @NotNull String inputKrakatauString
    ) throws IOException {
        return assemble(
                inputKrakatauString,
                null
        );
    }

    public static byte[] assemble(
            @NotNull String inputKrakatauString,
            @Nullable Supplier<String> tempFolderPathSupplier
    ) throws IOException {
        String tempFilePath;
        if (tempFolderPathSupplier != null) {
            tempFilePath = tempFolderPathSupplier.get() + "/" + UUID.randomUUID() + ".j";
        } else {
            tempFilePath =
                    Files.createTempFile("KrakatauJavaWrapperTempFileDisassemble", ".j").toAbsolutePath().toFile().getAbsolutePath().replaceAll("\\\\", "/");
        }

        FileObject outputFolderFileObject = VFS.getManager().toFileObject(new File(tempFilePath));
        try (
                OutputStream outputStream = outputFolderFileObject.getContent().getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)
        ) {
            outputStreamWriter.write(inputKrakatauString);
        }

        byte[] res = assembleFromPath(
                tempFilePath,
                tempFolderPathSupplier
        );

        try {
            outputFolderFileObject.delete();
        } catch (Exception ignored) {

        }

        return res;
    }

    public static byte[] assembleFromPath(
            @NotNull String inputFilePath,
            @Nullable Supplier<String> tempFolderPathSupplier
    ) throws IOException {
        final String outputFolderPath;
        if (tempFolderPathSupplier != null) {
            outputFolderPath = tempFolderPathSupplier.get();
        } else {
            outputFolderPath =
                    Files.createTempDirectory("KrakatauJavaWrapperTempFileAssemble").toAbsolutePath().toFile().getAbsolutePath().replaceAll("\\\\", "/");
        }

        FileObject outputFolderFileObject = VFS.getManager().toFileObject(new File(outputFolderPath));
        if (!outputFolderFileObject.isFolder()) {
            throw new IllegalArgumentException("tempFolderPathSupplier illegal : folder must be folder ! " +
                    "outputFolderPath : " + outputFolderPath);
        }

        Properties props = new Properties();

        props.put("python.console.encoding", "UTF-8");
        props.put("python.security.respectJavaAccessibility", "false");
        props.put("python.import.site", "false");

        Properties preprops = System.getProperties();

        String[] params = new String[]{
                "",
                "-out",
                outputFolderPath,
                inputFilePath
        };

        String pythonParams = buildPythonParams(params);

        PythonInterpreter.initialize(preprops, props, new String[0]);

        PySystemState state = new PySystemState();

        try (
                InputStream inputStream = KrakatauUtil.class.getResourceAsStream("/assemble.py");
                BufferedInputStream bufferedInputStream = new BufferedInputStream(Objects.requireNonNull(inputStream));
                PythonInterpreter pythonInterpreter = new PythonInterpreter(null, state)
        ) {
            pythonInterpreter.exec("import sys");
            pythonInterpreter.exec("sys.argv = " + pythonParams);
            pythonInterpreter.exec("reload(sys)");
            pythonInterpreter.execfile(bufferedInputStream);
        }

        byte[] res = findOnlyChild(
                outputFolderFileObject,
                ".class"
        ).getContent().getByteArray();

        try {
            outputFolderFileObject.delete();
        } catch (Exception ignored) {
        }

        return res;
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

    @NotNull
    private static FileObject findOnlyChild(
            @NotNull FileObject currentFileObject,
            @NotNull String suffix
    ) throws FileSystemException {
        while (true) {
            if (!currentFileObject.isFolder() && currentFileObject.isFile() && currentFileObject.getURL().toString().endsWith(suffix)) {
                return currentFileObject;
            }
            if (currentFileObject.isFolder()) {
                FileObject[] children = currentFileObject.getChildren();
                if (children == null || children.length == 0) {
                    throw new CannotFindOutputException("cannot find result! " + currentFileObject);
                }
                currentFileObject = children[0];
            }
        }
    }

}
