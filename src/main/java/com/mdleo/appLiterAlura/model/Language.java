package com.mdleo.appLiterAlura.model;

public enum Language {
    ESPAÑOL("es","español"),
    INGLES ("en","ingles"),
    FRANCES("fr", "frances"),
    PORTUGUES("pt","portugues"),
    LATIN("la", "latin"),
    ALEMAN("de", "aleman"),
    ITALIANO("it", "italiano");

    private String langLiter;
    private String languageLiter;

    Language(String langLiter, String languageLiter) {
        this.langLiter = langLiter;
        this.languageLiter = languageLiter;
    }
    public static Language fromString(String text) {
        for (Language language: Language.values()) {
            if (language.langLiter.equalsIgnoreCase(text)) {
                return language;
            }
        }
        throw new IllegalArgumentException("Ninguna lenguaje encontrado: " + text);
    }
    public static Language fromTotalString(String text) {
        for (Language language: Language.values()) {
            if (language.languageLiter.equalsIgnoreCase(text)) {
                return language;
            }
        }
        throw new IllegalArgumentException("Ninguna lenguaje encontrado: " + text);
    }
}
