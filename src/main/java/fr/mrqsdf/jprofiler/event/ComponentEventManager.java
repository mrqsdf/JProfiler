package fr.mrqsdf.jprofiler.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class ComponentEventManager {

    private static ComponentEventManager instance;
    private final Map<String, List<ComponentEventListener>> componentListeners;
    private final List<ComponentEventListener> globalListeners;

    private ComponentEventManager() {
        this.componentListeners = new ConcurrentHashMap<>();
        this.globalListeners = new CopyOnWriteArrayList<>();
    }

    public static ComponentEventManager getInstance() {
        if (instance == null) {
            instance = new ComponentEventManager();
        }
        return instance;
    }

    /**
     * Register a listener for a specific component
     */
    public void addEventListener(String componentId, ComponentEventListener listener) {
        componentListeners.computeIfAbsent(componentId, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    /**
     * Register a global listener for all components
     */
    public void addGlobalEventListener(ComponentEventListener listener) {
        globalListeners.add(listener);
    }

    /**
     * Remove a listener for a specific component
     */
    public void removeEventListener(String componentId, ComponentEventListener listener) {
        List<ComponentEventListener> listeners = componentListeners.get(componentId);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    /**
     * Remove a global listener
     */
    public void removeGlobalEventListener(ComponentEventListener listener) {
        globalListeners.remove(listener);
    }

    /**
     * Dispatch an event to registered listeners
     */
    public void dispatchEvent(ComponentEvent event) {
        fr.mrqsdf.jprofiler.JProfilerDebug.logEvent("Dispatching event: " + event);

        // Notify component-specific listeners
        List<ComponentEventListener> listeners = componentListeners.get(event.getComponentId());
        if (listeners != null) {
            for (ComponentEventListener listener : listeners) {
                try {
                    listener.onEvent(event);
                } catch (Exception e) {
                    System.err.println("Error in component listener: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        // Notify global listeners
        for (ComponentEventListener listener : globalListeners) {
            try {
                listener.onEvent(event);
            } catch (Exception e) {
                System.err.println("Error in global listener: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Clear all listeners
     */
    public void clearAllListeners() {
        componentListeners.clear();
        globalListeners.clear();
    }

    /**
     * Get all registered component IDs
     */
    public java.util.Set<String> getRegisteredComponents() {
        return componentListeners.keySet();
    }

    public void reset() {
        clearAllListeners();
        instance = null;
    }
}
