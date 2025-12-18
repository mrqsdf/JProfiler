package fr.mrqsdf.jprofiler.event;

@FunctionalInterface
public interface ComponentEventListener {
    void onEvent(ComponentEvent event);
}
