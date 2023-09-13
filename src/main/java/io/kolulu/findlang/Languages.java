package io.kolulu.findlang;

import java.util.Locale;

/**
 * @author liutianlu
 * <br/>Created 2023/9/13 14:14
 */
public enum Languages {
    /**
     * Including simplified chinese and traditional chinese
     */
    CHINESE("[\\u4e00-\\u9fa5]+", Locale.CHINESE),

    /**
     * Hiragana, Kanji, Katakana
     * <p>
     * Currently the regex is broken.
     */
    JAPANESE("(\\p{Script=Han}|\\p{Script=Katakana}|\\p{Script=Hiragana})+", Locale.JAPANESE);

    /**
     * What pattern to match this language variant
     */
    private final String pattern;

    /**
     * What locale this language maps to
     */
    private final Locale locale;

    Languages(String pattern, Locale locale) {
        this.pattern = pattern;
        this.locale = locale;
    }

    public String getPattern() {
        return pattern;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getLocaleStr() {
        return locale.getISO3Language();
    }
}
