package fr.mrqsdf.jprofiler.component;

public enum ActionType {
    NAVIGATE_PAGE("navigate_page"),
    SUBMIT("submit"),
    CUSTOM("custom");

    private final String value;

    ActionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
