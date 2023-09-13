package io.kolulu.findlang;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author liutianlu
 * <br/>Created 2023/9/13 17:41
 */
@Getter
@Setter
@ToString
public class LanguageUsage {
    public static final String CSV_HEADER = "FileName,LineNumber,Column,Line";

    private String filename;
    private Integer lineNumber;
    private Integer column;
    private String line;

    public String toCsvRow() {
        return filename + "," + lineNumber + "," + column + "," + line;
    }
}
