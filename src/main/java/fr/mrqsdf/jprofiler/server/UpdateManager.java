package fr.mrqsdf.jprofiler.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class UpdateManager {

    private static UpdateManager instance;
    private final List<UpdateListener> listeners;
    private final Map<String, Object> componentStates;
    private final Gson gson;

    private UpdateManager() {
        this.listeners = new CopyOnWriteArrayList<>();
        this.componentStates = new ConcurrentHashMap<>();
        this.gson = new Gson();
    }

    public static UpdateManager getInstance() {
        if (instance == null) {
            instance = new UpdateManager();
        }
        return instance;
    }

    public interface UpdateListener {
        void onUpdate(String json);
    }

    public void addListener(UpdateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(UpdateListener listener) {
        listeners.remove(listener);
    }

    public void updateTextComponent(String id, String text) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "text");
        json.addProperty("id", id);
        json.addProperty("value", text);

        componentStates.put(id, text);
        broadcast(gson.toJson(json));
    }

    public void updatePlaceholder(String componentId, String placeholder, String value) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "placeholder");
        json.addProperty("id", componentId);
        json.addProperty("placeholder", placeholder);
        json.addProperty("value", value);

        String key = componentId + ":" + placeholder;
        componentStates.put(key, value);
        broadcast(gson.toJson(json));
    }

    public void updateHTML(String id, String html) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "html");
        json.addProperty("id", id);
        json.addProperty("html", html);

        componentStates.put(id, html);
        broadcast(gson.toJson(json));
    }

    private void broadcast(String json) {
        for (UpdateListener listener : listeners) {
            try {
                listener.onUpdate(json);
            } catch (Exception e) {
                System.err.println("Error broadcasting update: " + e.getMessage());
            }
        }
    }

    public Map<String, Object> getComponentStates() {
        return new ConcurrentHashMap<>(componentStates);
    }

    public void clearStates() {
        componentStates.clear();
    }

    public void reset() {
        listeners.clear();
        componentStates.clear();
    }
}
