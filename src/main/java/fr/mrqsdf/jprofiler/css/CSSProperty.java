package fr.mrqsdf.jprofiler.css;

public class CSSProperty {

    private final String property;
    private final String value;

    public CSSProperty(String property, String value) {
        this.property = property;
        this.value = value;
    }

    public CSSProperty(CSSPropertyName property, String value) {
        this.property = property.getValue();
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return property + ": " + value + ";";
    }
}
