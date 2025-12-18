# JProfiler - Système de Communication Bidirectionnelle 🔄

## Vue d'ensemble

JProfiler intègre maintenant un système complet de communication bidirectionnelle entre le frontend et le backend, permettant :

- ✅ **Frontend → Backend** : Capturer les événements utilisateur (clics, saisies, changements)
- ✅ **Backend → Frontend** : Mises à jour en temps réel via SSE
- ✅ **Data Binding** : Liaison automatique entre composants UI et variables backend
- ✅ **Event System** : Système d'événements flexible avec listeners

## Architecture

### 1. Système d'Événements

#### Classes principales

```java
// Événement component
ComponentEvent {
    String componentId;
    EventType type; // BUTTON_CLICK, TEXT_INPUT, TEXT_CHANGE, CHECKBOX_CHANGE
    Map<String, Object> data;
    long timestamp;
}

// Listener d'événements
@FunctionalInterface
ComponentEventListener {
    void onEvent(ComponentEvent event);
}

// Gestionnaire d'événements
ComponentEventManager {
    addEventListener(componentId, listener)     // Écouter un composant spécifique
    addGlobalEventListener(listener)            // Écouter tous les événements
    dispatchEvent(event)                        // Dispatcher un événement
}
```

#### Utilisation

```java
// Écouter un bouton spécifique
JProfiler.addEventListener("my-button", event -> {
    System.out.println("Button clicked: " + event.getData("text"));
});

// Écouter tous les événements
JProfiler.addGlobalEventListener(event -> {
    System.out.println("Event: " + event);
});
```

### 2. Data Binding

#### Classes principales

```java
// Propriété bindable
BindableProperty<T> {
    T getValue()
    void setValue(T value)
    void setValueFromString(String value)  // Conversion automatique
}

// Gestionnaire de bindings
BindingManager {
    bind(componentId, property)                    // Lier un composant à une propriété
    bindValue(componentId, name, initial, type)    // Créer et lier une propriété simple
    updateProperty(componentId, value)             // Mettre à jour et synchroniser
}
```

#### Utilisation

```java
// Binding simple
BindableProperty<String> username = JProfiler.bindProperty(
    "username-field", 
    "username", 
    "Guest", 
    String.class
);

// Accéder à la valeur
String currentUsername = username.getValue();

// Modifier la valeur (synchronise automatiquement l'UI)
username.setValue("NewUser");

// Binding avec getter/setter personnalisés
BindableProperty<Integer> counter = new BindableProperty<>(
    "counter",
    () -> config.counter,              // getter
    val -> config.counter = val,       // setter
    Integer.class
);
JProfiler.bindComponent("counter-field", counter);
```

### 3. Communication Frontend ↔ Backend

#### Backend → Frontend (SSE)

```java
// Mettre à jour un composant
JProfiler.updateTextContent("text-id", "New text");
JProfiler.updateFieldValue("field-id", "New value");
JProfiler.updateCheckboxState("checkbox-id", true);
JProfiler.updateButtonText("button-id", "New label");
JProfiler.updateImage("image-id", base64String);
JProfiler.updateChart("chart-id", svgString);
JProfiler.updatePlaceholder("dynamic-id", "key", "value");
```

#### Frontend → Backend (HTTP POST)

Le JavaScript envoie automatiquement les événements :

```javascript
// Automatic event sending
- Text field: Enter key or blur → TEXT_INPUT / TEXT_CHANGE
- Checkbox: change → CHECKBOX_CHANGE  
- Button: click → BUTTON_CLICK

// Manual event sending
window.JProfiler.sendEvent(componentId, eventType, data);
```

### 4. Endpoints HTTP

- **`GET /`** - Page HTML principale
- **`GET /jprofiler-dynamic.js`** - Script JavaScript
- **`GET /updates`** - Stream SSE pour mises à jour temps réel
- **`POST /event`** - Réception des événements frontend

Format POST `/event`:
```json
{
    "componentId": "my-component",
    "type": "TEXT_INPUT",
    "data": {
        "value": "user input"
    }
}
```

## Exemple Complet

```java
public class MyInteractiveApp {
    
    private static String username = "Guest";
    private static int counter = 0;
    
    public static void main(String[] args) throws Exception {
        // Créer page
        Page page = JProfiler.createPage("app", "My App");
        
        // Composants
        TextFieldComponent usernameField = new TextFieldComponent(
            "username-field", "Enter name", "input", "text", username
        );
        ButtonComponent incrementBtn = new ButtonComponent(
            "Increment", "btn", null, null, "increment-btn"
        );
        DynamicTextComponent status = new DynamicTextComponent(
            "status", "User: {user}, Count: {count}", "status"
        );
        
        page.addToBody(usernameField);
        page.addToBody(incrementBtn);
        page.addToBody(status);
        
        // Binding
        BindableProperty<String> userProp = JProfiler.bindProperty(
            "username-field", "username", username, String.class
        );
        BindableProperty<Integer> counterProp = JProfiler.bindProperty(
            "counter-field", "counter", counter, Integer.class
        );
        
        // Event listeners
        JProfiler.addEventListener("increment-btn", event -> {
            int newCount = counterProp.getValue() + 1;
            counterProp.setValue(newCount);
            JProfiler.updateFieldValue("counter-field", String.valueOf(newCount));
        });
        
        // Démarrer serveur
        JProfiler.startServerAsync(8080);
        
        // Boucle de mise à jour
        while (true) {
            JProfiler.updatePlaceholder("status", "user", userProp.getValue());
            JProfiler.updatePlaceholder("status", "count", 
                String.valueOf(counterProp.getValue()));
            Thread.sleep(500);
        }
    }
}
```

## Démonstration

Lancer la démo interactive :

```bash
java -cp build/classes/java/main:... fr.mrqsdf.jprofiler.JProfilerInteractiveDemo
```

Puis visiter `http://localhost:8080`

### Fonctionnalités de la démo :

1. **Champ Username** - Modifier et voir la mise à jour en temps réel
2. **Counter** - Saisir une valeur ou utiliser le bouton
3. **Multiplier** - Changer le multiplicateur
4. **Auto-increment** - Activer/désactiver l'incrémentation automatique
5. **Status Display** - Affichage dynamique calculé en temps réel

## Types Supportés pour le Binding

- `String`
- `Integer` / `int`
- `Long` / `long`
- `Double` / `double`
- `Float` / `float`
- `Boolean` / `boolean`

Les conversions sont automatiques depuis les valeurs string du frontend.

## Bonnes Pratiques

1. **Toujours donner un ID aux composants interactifs**
   ```java
   new TextFieldComponent("my-field", ..., id)  // ✅ Bon
   new TextFieldComponent("my-field", ...)      // ❌ Pas d'events
   ```

2. **Utiliser le binding pour la synchronisation automatique**
   ```java
   // ✅ Bon - synchronisation automatique
   BindableProperty<String> prop = JProfiler.bindProperty(...);
   
   // ❌ Manuel - plus de code
   JProfiler.addEventListener(...);
   JProfiler.updateFieldValue(...);
   ```

3. **Nettoyer les listeners si nécessaire**
   ```java
   ComponentEventManager.getInstance().clearAllListeners();
   BindingManager.getInstance().clearAllBindings();
   ```

## Prochaines Étapes

- [ ] Validation des données côté backend
- [ ] Gestion des erreurs avec feedback UI
- [ ] Binding bidirectionnel avec synchronisation automatique complète
- [ ] Support des formulaires complexes avec validation
- [ ] WebSocket en option pour réduire la latence

## Conclusion

JProfiler offre maintenant une solution complète pour créer des interfaces web interactives avec communication bidirectionnelle en temps réel, parfait pour :

- 🎮 Dashboards interactifs
- ⚙️ Panels de configuration
- 📊 Outils de monitoring avec contrôle
- 🔧 Interfaces d'administration
- 🎯 Applications de profiling avec ajustements live
