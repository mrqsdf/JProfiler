package fr.mrqsdf.jprofiler.event;

import java.util.HashMap;
import java.util.Map;

public class ComponentEvent {
    
    private final String componentId;
    private final EventType type;
    private final Map<String, Object> data;
    private final long timestamp;

    public ComponentEvent(String componentId, EventType type, Map<String, Object> data) {
        this.componentId = componentId;
        this.type = type;
        this.data = data != null ? new HashMap<>(data) : new HashMap<>();
        this.timestamp = System.currentTimeMillis();
    }

    public String getComponentId() {
        return componentId;
    }

    public EventType getType() {
        return type;
    }

    public Map<String, Object> getData() {
        return new HashMap<>(data);
    }

    public Object getData(String key) {
        return data.get(key);
    }

    public String getDataAsString(String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("ComponentEvent{id='%s', type=%s, data=%s, timestamp=%d}", 
            componentId, type, data, timestamp);
    }

    public enum EventType {
        BUTTON_CLICK,
        TEXT_INPUT,
        TEXT_CHANGE,
        CHECKBOX_CHANGE,
        FORM_SUBMIT,
        CUSTOM
    }
}
