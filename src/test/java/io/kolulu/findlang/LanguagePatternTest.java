package io.kolulu.findlang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liutianlu
 * <br/>Created 2023/9/13 14:41
 */
class LanguagePatternTest {

    @Test
    void japanese() {
        Pattern pattern = Pattern.compile(Languages.JAPANESE.getPattern());
        String target = "Note: 日本語には、さまざまな Unicode 範囲に含まれるひらがな、漢字、カタカナが含まれています。";
        Matcher matcher = pattern.matcher(target);
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals(6, matcher.start());
    }

    @Test
    void chinese() {
        Pattern pattern = Pattern.compile(Languages.CHINESE.getPattern());
        String target = "International(英特纳雄奈尔) Song(歌)";
        Matcher matcher = pattern.matcher(target);
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals(14, matcher.start());
    }

    @Test
    void chinese2() {
        Pattern pattern = Pattern.compile(Languages.CHINESE_LITERALS_ONLY.getPattern());
        String target = "String x = \"C\"; // Has 中文注释.";
        Matcher matcher = pattern.matcher(target);
        Assertions.assertFalse(matcher.find());
    }
}