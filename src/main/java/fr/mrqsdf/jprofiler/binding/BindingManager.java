package fr.mrqsdf.jprofiler.binding;

import fr.mrqsdf.jprofiler.JProfiler;
import fr.mrqsdf.jprofiler.event.ComponentEvent;
import fr.mrqsdf.jprofiler.event.ComponentEventManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages bindings between UI components and backend properties
 */
public class BindingManager {

    private static BindingManager instance;
    private final Map<String, BindableProperty<?>> bindings;

    private BindingManager() {
        this.bindings = new ConcurrentHashMap<>();
    }

    public static BindingManager getInstance() {
        if (instance == null) {
            instance = new BindingManager();
        }
        return instance;
    }

    /**
     * Bind a component to a property with automatic sync
     */
    public <T> void bind(String componentId, BindableProperty<T> property) {
        bindings.put(componentId, property);

        // Listen for events from this component and update the property
        ComponentEventManager.getInstance().addEventListener(componentId, event -> {
            updatePropertyFromEvent(componentId, event);
        });

        // Initialize UI with current property value
        updateComponentFromProperty(componentId, property);
    }

    /**
     * Bind a simple value property
     */
    public <T> BindableProperty<T> bindValue(String componentId, String propertyName, T initialValue, Class<T> type) {
        BindableProperty<T> property = new BindableProperty<>(propertyName, initialValue, type);
        bind(componentId, property);
        return property;
    }

    /**
     * Get a bound property
     */
    @SuppressWarnings("unchecked")
    public <T> BindableProperty<T> getProperty(String componentId) {
        return (BindableProperty<T>) bindings.get(componentId);
    }

    /**
     * Update a property value and sync to UI
     */
    public <T> void updateProperty(String componentId, T value) {
        BindableProperty<?> property = bindings.get(componentId);
        if (property != null) {
            @SuppressWarnings("unchecked")
            BindableProperty<T> typedProperty = (BindableProperty<T>) property;
            typedProperty.setValue(value);
            updateComponentFromProperty(componentId, typedProperty);
        }
    }

    /**
     * Update property from component event
     */
    private void updatePropertyFromEvent(String componentId, ComponentEvent event) {
        BindableProperty<?> property = bindings.get(componentId);
        if (property == null) {
            return;
        }

        String newValue = null;

        switch (event.getType()) {
            case TEXT_INPUT:
            case TEXT_CHANGE:
                newValue = event.getDataAsString("value");
                break;
            case CHECKBOX_CHANGE:
                newValue = event.getDataAsString("checked");
                break;
            default:
                break;
        }

        if (newValue != null) {
            try {
                property.setValueFromString(newValue);
                fr.mrqsdf.jprofiler.JProfilerDebug.logBinding("Updated property: " + property);
            } catch (Exception e) {
                System.err.println("Error updating property from event: " + e.getMessage());
            }
        }
    }

    /**
     * Update component UI from property value
     */
    private <T> void updateComponentFromProperty(String componentId, BindableProperty<T> property) {
        T value = property.getValue();
        String stringValue = value != null ? value.toString() : "";

        // Determine component type and update accordingly
        if (property.getType() == Boolean.class || property.getType() == boolean.class) {
            JProfiler.updateCheckboxState(componentId, Boolean.parseBoolean(stringValue));
        } else {
            // Default: update as text field or text component
            JProfiler.updateFieldValue(componentId, stringValue);
        }
    }

    /**
     * Remove a binding
     */
    public void unbind(String componentId) {
        bindings.remove(componentId);
    }

    /**
     * Get all bindings
     */
    public Map<String, BindableProperty<?>> getAllBindings() {
        return new ConcurrentHashMap<>(bindings);
    }

    /**
     * Clear all bindings
     */
    public void clearAllBindings() {
        bindings.clear();
    }

    public void reset() {
        clearAllBindings();
        instance = null;
    }
}
