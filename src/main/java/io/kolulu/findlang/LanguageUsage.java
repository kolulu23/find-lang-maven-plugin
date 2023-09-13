package io.kolulu.findlang;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
@JsonPropertyOrder(value = {"filename", "lineNumber", "column", "line"})
public class LanguageUsage {

    /**
     * Absolute path of the file being scanned
     */
    private String filename;

    /**
     * Line number of target language appearance
     */
    private Integer lineNumber;

    /**
     * Column index of the first match
     */
    private Integer column;

    /**
     * The whole line being processed as a general reference
     */
    private String line;
}
