package io.kolulu.findlang;

import lombok.Getter;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.Scanner;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author liutianlu
 * <br/>Created 2023/9/13 17:03
 */
public class SourceRootReader implements Closeable {
    @Getter
    private final List<String> sourceRoots;

    private final Log log;

    /**
     * Each source file gets a reader
     */
    @Getter
    private final List<SourceFileReader> readers;

    public SourceRootReader(List<String> sourceRoots, Log log) {
        this.sourceRoots = sourceRoots;
        this.log = log;
        readers = sourceReaders(sourceRoots);
    }

    private static Scanner scanner(String srcRoot) {
        if (!new File(srcRoot).exists()) {
            return null;
        }
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(srcRoot);
        scanner.setFollowSymlinks(false);
        scanner.scan();
        return scanner;
    }

    private static Stream<Scanner> scanners(List<String> sourceRoots) {
        return sourceRoots
                .stream()
                .map(SourceRootReader::scanner)
                .filter(Objects::nonNull);
    }

    private Stream<Path> sources(List<String> sourceRoots) {
        return scanners(sourceRoots)
                .flatMap(scanner -> Arrays.stream(scanner.getIncludedFiles())
                        .map(filename -> Paths.get(scanner.getBasedir().getAbsolutePath(), filename)));
    }

    private List<SourceFileReader> sourceReaders(List<String> sourceRoots) {
        return sources(sourceRoots)
                .map(SourceFileReader::from)
                .filter(reader -> {
                    if (reader.getReader() != null) {
                        return true;
                    } else {
                        log.warn(String.format("Skip reading %s", reader.getPath()));
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    public void close() throws IOException {
        for (SourceFileReader reader : this.readers) {
            reader.getReader().close();
        }
    }
}
