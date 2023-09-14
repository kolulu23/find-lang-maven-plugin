package io.kolulu.findlang;

import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

/**
 * Configuration of language patterns.
 * <p>
 * This configuration is only effective when a RegEx based implementation is chosen.
 *
 * @author liutianlu
 * <br/>Created 2023/9/14 10:25
 */
@Getter
@Setter
public class LanguagePatternConfig {
    /**
     * The main search usePattern, default to a built-in language setting
     */
    @Parameter
    private String searchPattern;

    /**
     * Patterns used to further filter out language usages in source code
     */
    @Parameter
    private List<String> filterPatterns;

    /**
     * Skip comments in source code
     */
    @Parameter(defaultValue = "true")
    private Boolean skipComments;
}
