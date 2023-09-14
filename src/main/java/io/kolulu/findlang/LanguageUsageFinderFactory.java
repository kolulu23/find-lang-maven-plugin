package io.kolulu.findlang;

/**
 * @author liutianlu
 * <br/>Created 2023/9/14 10:32
 */
public class LanguageUsageFinderFactory {

    private LanguageUsageFinderFactory() {
    }

    public static LanguageUsageRegexFinder regexFinder(LanguagePatternConfig config) {
        LanguageUsageRegexFinder finder = new LanguageUsageRegexFinder()
                .usePattern(Languages.CHINESE_LITERALS_ONLY)
                .filterPatterns(null)
                .skipComments(true);
        if (config == null) {
            return finder;
        }
        if (config.getSearchPattern() != null && !config.getSearchPattern().isEmpty()) {
            finder = finder.usePattern(config.getSearchPattern());
        }
        return finder.filterPatterns(config.getFilterPatterns())
                .skipComments(config.getSkipComments() == null || config.getSkipComments());
    }
}
