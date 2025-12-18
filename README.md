# JProfiler — Component-based HTML + Live Config Toolkit

JProfiler is a lightweight Java toolkit to render HTML pages with a component model, real-time updates (SSE), and bidirectional communication (frontend → backend events, backend → frontend updates). It is designed to quickly build interactive profiling and configuration UIs for JVM apps without a heavy web stack.

---

## Overview
- **Pages**: Structured views composed of reusable components.
- **Components**: `TextComponent`, `ButtonComponent`, `TextFieldComponent`, `CheckboxComponent`, `DynamicTextComponent`, etc.
- **CSS**: Declarative CSS via `CSSManager` + sensible defaults via `CSS.setupDefaultStyles()`.
- **Server**: Embedded Jetty serving HTML, static JS, and an SSE stream for live updates.
- **Events**: Frontend interactions are sent to the backend (`ComponentEvent`) where you can bind and react.
- **Bindings**: `BindableProperty<T>` keeps UI and backend values in sync.

Key files:
- [src/main/java/fr/mrqsdf/jprofiler/JProfilerMain.java](src/main/java/fr/mrqsdf/jprofiler/JProfilerMain.java)
- [src/main/java/fr/mrqsdf/jprofiler/page/Page.java](src/main/java/fr/mrqsdf/jprofiler/page/Page.java)
- [src/main/java/fr/mrqsdf/jprofiler/component](src/main/java/fr/mrqsdf/jprofiler/component)
- [src/main/java/fr/mrqsdf/jprofiler/css/CSSManager.java](src/main/java/fr/mrqsdf/jprofiler/css/CSSManager.java)
- [src/main/java/fr/mrqsdf/jprofiler/event](src/main/java/fr/mrqsdf/jprofiler/event)

---

## Quick Start

1. Create a main entry (see [JProfilerMain.java](src/main/java/fr/mrqsdf/jprofiler/JProfilerMain.java))
2. Set CSS defaults, build pages/components
3. Set the current page and start the server

Example:

```java
CSS.setupDefaultStyles();
CSSManager.addRule(".title").addProperty("font-size", "24px");

Page home = JProfiler.createPage("home", "JProfiler Demo");
TextComponent title = new TextComponent("<h1>Welcome</h1>", "title");
ButtonComponent goForm = new ButtonComponent("Go to Form", "button", ActionType.NAVIGATE_PAGE, "simpleForm", "goFormBtn");

home.addToBody(title);
home.addToBody(goForm);

JProfiler.setCurrentPage("home");
JProfiler.startServerAsync(8080);
```

Run:

```bash
./gradlew run --args='fr.mrqsdf.jprofiler.JProfilerMain'
```

---

## Pages

- Create a page: `Page page = JProfiler.createPage("simpleForm", "Simple Form")`.
- Add components: `page.addToBody(component)` (header/footer zones also available via `addToHeader`, `addToFooter`).
- Current page drives the initial HTML: `JProfiler.setCurrentPage("home")`.

See: [Page.java](src/main/java/fr/mrqsdf/jprofiler/page/Page.java)

---

## Components

All components render HTML using `j2html` with proper IDs and data attributes to support dynamic updates and event wiring.

- **TextComponent**: `new TextComponent(String htmlOrText, String className)` or `new TextComponent(String htmlOrText, String className, String id)`
  - Use inline HTML safely for formatting: `"<h1>Title</h1>"`

- **ButtonComponent**: `new ButtonComponent(String text, String className, ActionType actionType, String actionData, String id)`
  - Navigation: `ActionType.NAVIGATE_PAGE` with `actionData = "targetPageId"`
  - Avoid raw HTML `onclick`: use `ButtonComponent` to ensure proper client-side wiring.

- **TextFieldComponent**: `new TextFieldComponent(String id, String placeholder, String className)`
  - Emits `TEXT_CHANGE` events; value mirrored via data attributes for SSE updates.

- **CheckboxComponent**: `new CheckboxComponent(String id, String label, String className, boolean checked)`
  - Emits `CHECKBOX_CHANGE` events.

- **DynamicTextComponent**: `new DynamicTextComponent(String id, String template, String className)`
  - Template placeholders like `{username}` are updated via SSE.

See: [component/](src/main/java/fr/mrqsdf/jprofiler/component)

---

## Styling (CSS)

- Defaults: `CSS.setupDefaultStyles()` (text, page layout, image, chart, etc.)
- Custom rules: `CSSManager.addRule(".my-class").addProperty("color", "red");`
- Recommended workflow:
  - Define semantic classes (e.g., `"title"`, `"button"`, `"input-field"`)
  - Attach classes through component constructors
  - Keep layout consistent via CSS grid/flex rules

See: [CSSManager.java](src/main/java/fr/mrqsdf/jprofiler/css/CSSManager.java)

---

## Events (Frontend → Backend)

Events are automatically emitted by components and delivered to your listeners.

- Add a listener per component:

```java
JProfiler.addEventListener("messageField", event -> {
  if (event.getType() == ComponentEvent.EventType.TEXT_CHANGE) {
    String value = event.getDataAsString("value");
    messageProp.setValue(value);
  }
});
```

Common types:
- `BUTTON_CLICK`
- `TEXT_CHANGE`
- `CHECKBOX_CHANGE`
- `CUSTOM`

See: [event/ComponentEvent.java](src/main/java/fr/mrqsdf/jprofiler/event/ComponentEvent.java)

---

## Data Binding

Link UI components to backend values with `BindableProperty<T>`:

```java
BindableProperty<String> usernameProp = new BindableProperty<>("usernameProp", "User", String.class);
JProfiler.bindComponent("username", usernameProp);
```

- Update the property in listeners so `getValue()` always reflects the current UI state.
- Subscribe to changes via `addListener((oldVal, newVal) -> { ... })` when needed.

See: [binding/](src/main/java/fr/mrqsdf/jprofiler/binding)

---

## Live Updates (Backend → Frontend)

Use SSE updates to modify rendered content without page reload:

- Update a single placeholder in `DynamicTextComponent`:

```java
JProfiler.updatePlaceholder("display", "counter", String.valueOf(counterProp.getValue()));
JProfiler.updatePlaceholder("display", "username", usernameProp.getValue());
```

- Other update helpers exist in `JProfiler`: text content, images, buttons, fields, checkboxes.

See: [server/UpdateManager.java](src/main/java/fr/mrqsdf/jprofiler/server/UpdateManager.java)

---

## Debugging

Debug logs are disabled by default. Enable selectively:

```java
JProfiler.enableDebug();           // enable all debug
JProfiler.setLogEvents(true);      // log frontend events
JProfiler.setLogBindings(true);    // log binding updates
JProfiler.setLogUpdates(true);     // log SSE updates
```

See: [JProfilerDebug.java](src/main/java/fr/mrqsdf/jprofiler/JProfilerDebug.java)

---

## Navigation — Do This, Not That

- ✅ Use `ButtonComponent` with `ActionType.NAVIGATE_PAGE`:

```java
ButtonComponent goToForm = new ButtonComponent(
  "Go to Form", "button", ActionType.NAVIGATE_PAGE, "simpleForm", "goFormBtn"
);
```

- ❌ Avoid raw HTML strings with inline `onclick`:

```html
<!-- This will not be wired to JProfiler's event system and may break -->
<div class='demo-card' onclick="navigateToPage('simpleForm')">...</div>
```

JProfiler’s client-side script and data attributes expect components to be created through the Java component API to ensure proper IDs, attributes, and event wiring.

---

## Common Pitfalls

- Missing IDs on components → events and updates won’t route
- Using raw HTML for interactive elements → no automatic wiring
- Forgetting `CSS.setupDefaultStyles()` → layout looks off
- Not calling `JProfiler.setCurrentPage("...")` → blank page on start
- Blocking the main thread → prefer `JProfiler.startServerAsync(port)`

---

## Project Structure

- [src/main/java/fr/mrqsdf/jprofiler/JProfilerMain.java](src/main/java/fr/mrqsdf/jprofiler/JProfilerMain.java)
- [src/main/java/fr/mrqsdf/jprofiler/component](src/main/java/fr/mrqsdf/jprofiler/component): Components
- [src/main/java/fr/mrqsdf/jprofiler/page](src/main/java/fr/mrqsdf/jprofiler/page): Page system
- [src/main/java/fr/mrqsdf/jprofiler/css](src/main/java/fr/mrqsdf/jprofiler/css): CSS utilities
- [src/main/java/fr/mrqsdf/jprofiler/event](src/main/java/fr/mrqsdf/jprofiler/event): Event system
- [src/main/java/fr/mrqsdf/jprofiler/server](src/main/java/fr/mrqsdf/jprofiler/server): Jetty + SSE

---

## Example: Simple Form

```java
Page page = JProfiler.createPage("simpleForm", "Simple Form Demo");

BindableProperty<String> messageProp = new BindableProperty<>("messageProp", "", String.class);
BindableProperty<Integer> counterProp = new BindableProperty<>("counterProp", 0, Integer.class);

TextFieldComponent messageField = new TextFieldComponent("messageField", "Enter your message", "input-field");
ButtonComponent sendButton = new ButtonComponent("Send Message", "button", null, null, "sendButton");
DynamicTextComponent counterDisplay = new DynamicTextComponent("counterDisplay", "Message sent {counter} times: {message}", "result");

JProfiler.bindComponent("messageField", messageProp);

JProfiler.addEventListener("messageField", event -> {
  if (event.getType() == ComponentEvent.EventType.TEXT_CHANGE) {
    messageProp.setValue(event.getDataAsString("value"));
  }
});

JProfiler.addEventListener("sendButton", event -> {
  if (event.getType() == ComponentEvent.EventType.BUTTON_CLICK) {
    String msg = messageProp.getValue();
    if (msg != null && !msg.trim().isEmpty()) {
      counterProp.setValue(counterProp.getValue() + 1);
      JProfiler.updatePlaceholder("counterDisplay", "counter", String.valueOf(counterProp.getValue()));
      JProfiler.updatePlaceholder("counterDisplay", "message", msg);
    }
  }
});

page.addToBody(messageField);
page.addToBody(sendButton);
page.addToBody(counterDisplay);
```

---

## License

This repository is for demonstration purposes. Add your license if applicable.
