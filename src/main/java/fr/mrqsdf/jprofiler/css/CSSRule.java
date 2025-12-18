package fr.mrqsdf.jprofiler.css;

import java.util.ArrayList;
import java.util.List;

public class CSSRule {

    private final String selector;
    private final List<CSSProperty> properties;

    public CSSRule(String selector) {
        this.selector = selector;
        this.properties = new ArrayList<>();
    }

    public CSSRule addProperty(String property, String value) {
        this.properties.add(new CSSProperty(property, value));
        return this;
    }

    public CSSRule addProperty(CSSPropertyName property, String value) {
        this.properties.add(new CSSProperty(property, value));
        return this;
    }

    public CSSRule addProperty(CSSProperty property) {
        this.properties.add(property);
        return this;
    }

    public String getSelector() {
        return selector;
    }

    public List<CSSProperty> getProperties() {
        return new ArrayList<>(properties);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(selector).append(" {\n");
        for (CSSProperty prop : properties) {
            sb.append("  ").append(prop).append("\n");
        }
        sb.append("}\n");
        return sb.toString();
    }
}
