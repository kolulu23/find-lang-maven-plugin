package io.kolulu.findlang;

import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * @author liutianlu
 * <br/>Created 2023/9/13 17:04
 */
@Getter
@Setter
public class SourceFileReader implements Closeable {
    /**
     * Absolute path of source file
     */
    private Path path;

    /**
     * Underlying reader
     */
    private BufferedReader reader;

    public static SourceFileReader from(Path path) {
        SourceFileReader sourceFileReader = new SourceFileReader();
        sourceFileReader.setPath(path);
        try {
            sourceFileReader.setReader(Files.newBufferedReader(path, StandardCharsets.UTF_8));
        } catch (IOException ignored) {
        }
        return sourceFileReader;
    }

    public Stream<String> lines() {
        return reader.lines();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
