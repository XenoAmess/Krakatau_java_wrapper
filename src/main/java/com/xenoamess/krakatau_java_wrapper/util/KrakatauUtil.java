package com.xenoamess.krakatau_java_wrapper.util;

import com.xenoamess.krakatau_java_wrapper.exception.CannotFindOutputException;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

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

    @NotNull
    public static synchronized File assureKrak2File(@Nullable Supplier<String> tempFolderPathSupplier) throws IOException {
        String krak2Path;
        if (tempFolderPathSupplier != null) {
            krak2Path = tempFolderPathSupplier.get() + "/krak2.exe";
        } else {
            krak2Path = SystemUtils.getJavaIoTmpDir() + "/krakatau_java_wrapper/krak2.exe";
        }
        File krak2File = new File(krak2Path);
        if (krak2File.exists()) {
            return krak2File;
        }
        Files.createDirectories(krak2File.getParentFile().toPath());
        try (InputStream inputStream = KrakatauUtil.class.getResourceAsStream("/krak2.exe")) {
            Files.copy(Objects.requireNonNull(inputStream), krak2File.toPath());
        }
        krak2File.deleteOnExit();
        return krak2File;
    }

    public static String disassemble(
            byte @NotNull [] inputClassBytes,
            @Nullable Supplier<String> tempFolderPathSupplier
    ) throws IOException {
        String tempFilePath;
        if (tempFolderPathSupplier != null) {
            tempFilePath = tempFolderPathSupplier.get() + "/" + UUID.randomUUID() + ".class";
        } else {
            tempFilePath = Files
                    .createTempFile("KrakatauJavaWrapperTempFileDisassemble", ".class")
                    .toAbsolutePath()
                    .toFile()
                    .getAbsolutePath()
                    .replaceAll("\\\\", "/");
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
            outputFilePath = tempFolderPathSupplier.get() + "/" + UUID.randomUUID() + ".j";
        } else {
            outputFilePath = Files
                    .createTempFile("KrakatauJavaWrapperTempFileDisassemble", ".j")
                    .toAbsolutePath()
                    .toFile()
                    .getAbsolutePath()
                    .replaceAll("\\\\", "/");
        }

        FileObject fileObject = VFS.getManager().toFileObject(new File(outputFilePath));

        fileObject.createFile();

        File krak2File = assureKrak2File(tempFolderPathSupplier);

        String[] command = new String[]{
                krak2File.getAbsolutePath(),
                "dis",
                "--roundtrip",
                "--out",
                outputFilePath,
                inputFilePath
        };

        try {
            ProcessBuilder builder = new ProcessBuilder(
                    command
            );
            builder.redirectErrorStream(true);
            final Process process = builder.start();
            process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        String res = findOnlyChild(
                fileObject,
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

        FileObject outputFileObject = VFS.getManager().toFileObject(new File(tempFilePath));
        try (
                OutputStream outputStream = outputFileObject.getContent().getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)
        ) {
            outputStreamWriter.write(inputKrakatauString);
        }

        byte[] res = assembleFromPath(
                tempFilePath,
                tempFolderPathSupplier
        );

        try {
            outputFileObject.delete();
        } catch (Exception ignored) {
        }

        return res;
    }

    public static byte[] assembleFromPath(
            @NotNull String inputFilePath,
            @Nullable Supplier<String> tempFolderPathSupplier
    ) throws IOException {
        final String outputFilePath;
        if (tempFolderPathSupplier != null) {
            outputFilePath = tempFolderPathSupplier.get() + "/" + UUID.randomUUID() + ".class";
        } else {
            outputFilePath =
                    Files.createTempFile("KrakatauJavaWrapperTempFileAssemble", ".class").toAbsolutePath().toFile().getAbsolutePath().replaceAll("\\\\", "/");
        }

        FileObject outputFolderFileObject = VFS.getManager().toFileObject(new File(outputFilePath));

        File krak2File = assureKrak2File(tempFolderPathSupplier);

        String[] command = new String[]{
                krak2File.getAbsolutePath(),
                "asm",
                "--out",
                outputFilePath,
                inputFilePath
        };

        try {
            ProcessBuilder builder = new ProcessBuilder(
                    command
            );
            builder.redirectErrorStream(true);
            final Process process = builder.start();
            process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
