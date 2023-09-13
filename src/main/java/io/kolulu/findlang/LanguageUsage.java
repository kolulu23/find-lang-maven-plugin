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
    private String filename;
    private String line;
    private Integer lineNumber;
    private Integer column;
}
