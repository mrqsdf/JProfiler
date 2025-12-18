package fr.mrqsdf.jprofiler.css;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSSManager {

    private static CSSManager instance;
    private final Map<String, CSSRule> rules;
    private final List<String> globalStyles;

    private CSSManager() {
        this.rules = new HashMap<>();
        this.globalStyles = new ArrayList<>();
    }

    public static CSSManager getInstance() {
        if (instance == null) {
            instance = new CSSManager();
        }
        return instance;
    }

    public static CSSRule addRule(String selector) {
        return getInstance()._addRule(selector);
    }

    public static void addGlobalStyle(String style) {
        getInstance().globalStyles.add(style);
    }

    public static String generateCSS() {
        return getInstance()._generateCSS();
    }

    public static void clearAll() {
        getInstance().rules.clear();
        getInstance().globalStyles.clear();
    }

    public static void reset() {
        instance = null;
    }

    // Private methods
    private CSSRule _addRule(String selector) {
        CSSRule rule = rules.computeIfAbsent(selector, CSSRule::new);
        return rule;
    }

    private String _generateCSS() {
        StringBuilder css = new StringBuilder();

        // Add global styles
        for (String style : globalStyles) {
            css.append(style).append("\n");
        }

        // Add rules
        for (CSSRule rule : rules.values()) {
            css.append(rule);
        }

        return css.toString();
    }

    public Map<String, CSSRule> getRules() {
        return new HashMap<>(rules);
    }

    public List<String> getGlobalStyles() {
        return new ArrayList<>(globalStyles);
    }
}
