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
public class RegexLanguageMatcher {
    private final Pattern pattern;

    protected RegexLanguageMatcher(Languages lang) {
        this.pattern = Pattern.compile(lang.getPattern());
    }

    public Stream<LanguageUsage> process(SourceFileReader reader) {
        AtomicInteger lineNumer = new AtomicInteger();
        return reader.lines().map(line -> {
            lineNumer.incrementAndGet();
            Integer column = null;
            if (possibleComment(line)) {
                return null;
            }
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                column = matcher.start() + 1;
                LanguageUsage usage = new LanguageUsage();
                usage.setLine(line.trim());
                usage.setFilename(reader.getPath().toString());
                usage.setColumn(column);
                usage.setLineNumber(lineNumer.get());
                return usage;
            }
            return null;
        }).filter(Objects::nonNull);
    }

    public static boolean possibleComment(String line) {
        String trimmed = line.trim();
        return trimmed.startsWith("//") || trimmed.startsWith("*");
    }
}
