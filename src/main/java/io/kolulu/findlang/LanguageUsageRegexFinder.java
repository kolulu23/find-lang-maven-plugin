package io.kolulu.findlang;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author liutianlu
 * <br/>Created 2023/9/13 14:20
 */
public class LanguageUsageRegexFinder {

    /**
     * Pattern for the target language
     */
    private final Pattern pattern;

    private boolean skipComments;

    protected LanguageUsageRegexFinder(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    protected LanguageUsageRegexFinder(Languages lang) {
        this.pattern = Pattern.compile(lang.getPattern());
    }

    public LanguageUsageRegexFinder setSkipComments(boolean skipComments) {
        this.skipComments = skipComments;
        return this;
    }

    public Stream<LanguageUsage> process(SourceFileReader reader) {
        AtomicInteger lineNumber = new AtomicInteger();
        return reader.lines().map(line -> {
            lineNumber.incrementAndGet();
            int column;
            if (skipComments && possibleComment(line)) {
                return null;
            }
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                column = matcher.start() + 1;
                LanguageUsage usage = new LanguageUsage();
                usage.setLine(line.trim());
                usage.setFilename(reader.getPath().toString());
                usage.setColumn(column);
                usage.setLineNumber(lineNumber.get());
                return usage;
            }
            return null;
        }).filter(Objects::nonNull);
    }

    /**
     * Returns true if {@code line} is likely a comment.
     * This will detect line comment and block comment when it comes first, but for something like
     *
     * <pre>String x = String.format("Target pattern"); // Also target pattern</pre>
     * <p>
     * will produce false negative.
     * <p>
     * Since I don't feel like I need parse the whole line, so I can't just use contains or
     * simply compare index of '//' and the last match of target pattern.
     *
     * @param line Source line
     * @return True is the line is a comment, false otherwise, but false doesn't mean it contains
     * absolute no comment part.
     */
    private static boolean possibleComment(String line) {
        String trimmed = line.trim();
        return trimmed.startsWith("//") || trimmed.startsWith("/*") || trimmed.startsWith("*");
    }
}
