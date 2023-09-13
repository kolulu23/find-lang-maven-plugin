package io.kolulu.findlang;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.Getter;

import java.io.*;
import java.util.Collection;

/**
 * @author liutianlu
 * <br/>Created 2023/9/13 20:15
 */
@Getter
public class LanguageUsageCsvWriter implements Closeable {

    /**
     * The underlying writer reference
     */
    private final BufferedWriter writer;

    /**
     * The directly managed csv writer.
     */
    private final SequenceWriter csvWriter;


    public LanguageUsageCsvWriter(File file) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(file, false));
        final CsvMapper csvMapper = new CsvMapper();
        final CsvSchema schema = csvMapper.schemaFor(LanguageUsage.class)
                .withHeader()
                .withAnyPropertyName("_extra");
        final ObjectWriter objectWriter = csvMapper.writer(schema);
        this.csvWriter = objectWriter.writeValues(this.writer);
    }

    public void write(Collection<LanguageUsage> usages) throws IOException {
        csvWriter.writeAll(usages);
    }

    @Override
    public void close() throws IOException {
        csvWriter.close();
    }
}
