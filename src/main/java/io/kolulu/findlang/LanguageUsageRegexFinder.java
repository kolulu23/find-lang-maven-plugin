package io.kolulu.findlang;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author liutianlu
 * <br/>Created 2023/9/13 14:20
 */
public class LanguageUsageRegexFinder {

    /**
     * Pattern for the target language
     */
    private Pattern pattern;

    private boolean skipComments;

    private List<Pattern> filterPatterns;

    public LanguageUsageRegexFinder usePattern(Languages lang) {
        this.pattern = Pattern.compile(lang.getPattern());
        return this;
    }

    public LanguageUsageRegexFinder usePattern(String pattern) {
        this.pattern = Pattern.compile(pattern);
        return this;
    }

    public LanguageUsageRegexFinder skipComments(boolean skipComments) {
        this.skipComments = skipComments;
        return this;
    }

    public LanguageUsageRegexFinder filterPatterns(List<String> patterns) {
        if (patterns == null) {
            this.filterPatterns = new ArrayList<>();
        } else {
            this.filterPatterns = patterns.stream().map(Pattern::compile).collect(Collectors.toList());
        }
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
            if (matcher.find() && !matchFilterPattern(line)) {
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
     * <pre>String x = String.format("Target usePattern"); // Also target usePattern</pre>
     * <p>
     * will produce false negative.
     * <p>
     * Since I don't feel like I need parse the whole line, so I can't just use contains or
     * simply compare index of '//' and the last match of target usePattern.
     *
     * @param line Source line
     * @return True is the line is a comment, false otherwise, but false doesn't mean it contains
     * absolute no comment part.
     */
    private static boolean possibleComment(String line) {
        String trimmed = line.trim();
        return trimmed.startsWith("//") || trimmed.startsWith("/*") || trimmed.startsWith("*");
    }

    private boolean matchFilterPattern(String line) {
        return filterPatterns.stream().anyMatch(p -> p.matcher(line).find());
    }

    public String debug() {
        StringBuilder sb = new StringBuilder();
        String searched = pattern.toString();
        String filtered = filterPatterns.stream()
                .map(Pattern::toString)
                .collect(Collectors.joining("\t"));
        sb.append("Using search usePattern ")
                .append(searched)
                .append("\n")
                .append("while filter out ")
                .append(filtered)
                .append("\n")
                .append("skip comments: ")
                .append(skipComments);
        return sb.toString();
    }
}
