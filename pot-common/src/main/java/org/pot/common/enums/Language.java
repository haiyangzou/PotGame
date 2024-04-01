package org.pot.common.enums;

import lombok.Getter;

import java.util.function.Function;

@Getter
public enum Language implements IntEnum {
    Unknown(0, "", "", ""),

    English(1, "en", "en", "en"),

    ChineseSimplified(2, "zh-cn", "zh_cn", "zh-Hans"),

    ChineseTraditional(3, "zh-tw", "zh_tw", "zh-Hant"),

    Arabic(4, "ar", "ar", "ar"),

    French(5, "fr", "fr", "fr"),

    German(6, "de", "de", "de"),

    Hindi(7, "hi", "hi", "hi"),

    Italian(8, "it", "it", "it"),

    Portuguese(9, "pt", "pt", "pt"),

    Russian(10, "ru", "ru", "ru"),

    Spanish(11, "es", "es", "es"),

    Thai(12, "th", "th", "th"),

    Japanese(13, "ja", "ja", "ja");

    private final int id;

    private final String isoCode;

    private final String configCode;

    private final String microsoftCode;

    Language(int id, String isoCode, String configCode, String microsoftCode) {
        this.id = id;
        this.isoCode = isoCode;
        this.configCode = configCode;
        this.microsoftCode = microsoftCode;
    }

    @Override
    public int getId() {
        return id;
    }

    public static Language ofName(String lang, Language defaultLanguage) {
        return of(lang, Enum::name, defaultLanguage);
    }

    public static Language ofIsoCode(String lang, Language defaultLanguage) {
        return of(lang, language -> language.isoCode, defaultLanguage);
    }

    public static Language ofConfigCode(String lang, Language defaultLanguage) {
        return of(lang, language -> language.configCode, defaultLanguage);
    }

    public static Language ofMicrosoftCode(String lang, Language defaultLanguage) {
        return of(lang, language -> language.microsoftCode, defaultLanguage);
    }

    private static Language of(String lang, Function<Language, String> function, Language defaultLanguage) {
        for (Language language : values()) {
            if (lang.equals(function.apply(language)))
                return language;
        }
        return defaultLanguage;
    }

    public static boolean isValidName(String lang) {
        return isValid(lang, language -> language.name());
    }

    public static boolean isValidIsoCode(String lang) {
        return isValid(lang, language -> language.isoCode);
    }

    public static boolean isValidConfigCode(String lang) {
        return isValid(lang, language -> language.configCode);
    }

    public static boolean isValidMicrosoftCode(String lang) {
        return isValid(lang, language -> language.microsoftCode);
    }

    private static boolean isValid(String lang, Function<Language, String> function) {
        for (Language language : values()) {
            if (lang.equals(function.apply(language)))
                return true;
        }
        return false;
    }
}
