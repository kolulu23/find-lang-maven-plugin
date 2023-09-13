package io.kolulu.findlang;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

/**
 * @author liutianlu
 * <br/>Created 2023/9/13 20:15
 */
@Getter
public class LanguageUsageCsvWriter implements Closeable {

    private final BufferedWriter writer;

    public LanguageUsageCsvWriter(File file) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(file, false));
    }

    @SneakyThrows
    public void write(Collection<LanguageUsage> usageStream) {
        for (LanguageUsage usage : usageStream) {
            writer.write(usage.toCsvRow() + "\n");
        }
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        writer.flush();
        writer.close();
    }
}
