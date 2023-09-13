package io.kolulu.findlang;

import lombok.Getter;

import java.util.Locale;

/**
 * Predefined language and its regex patterns.
 *
 * @author liutianlu
 * <br/>Created 2023/9/13 14:14
 */
@Getter
public enum Languages {
    /**
     * Including simplified chinese and traditional chinese
     */
    CHINESE("[\\u4e00-\\u9fa5]+", Locale.CHINESE),

    /**
     * Only match string literals that contains Chinese character
     */
    CHINESE_LITERALS_ONLY("\\\".*[\\u4e00-\\u9fa5]+.*\\\"", Locale.CHINESE),

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

    public String getLocaleStr() {
        return locale.getISO3Language();
    }
}
