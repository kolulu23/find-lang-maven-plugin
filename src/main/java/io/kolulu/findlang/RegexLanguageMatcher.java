package io.kolulu.findlang;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author liutianlu
 * <br/>Created 2023/9/13 14:20
 */
public class RegexLanguageMatcher {
    private final Pattern pattern;

    protected RegexLanguageMatcher(Languages lang) {
        this.pattern = Pattern.compile(lang.getPattern());
    }

    public List<LanguageUsage> process(SourceFileReader reader) {
        AtomicInteger lineNumer = new AtomicInteger();
        return reader.lines().map(line -> {
            lineNumer.incrementAndGet();
            Integer column = null;
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                column = matcher.start() + 1;
                LanguageUsage usage = new LanguageUsage();
                usage.setLine(line);
                usage.setFilename(reader.getPath().toString());
                usage.setColumn(column);
                usage.setLineNumber(lineNumer.get());
                return usage;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
