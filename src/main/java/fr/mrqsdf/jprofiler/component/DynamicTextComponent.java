package fr.mrqsdf.jprofiler.component;

import static j2html.TagCreator.div;

import fr.mrqsdf.jprofiler.css.CSSRule;
import j2html.tags.specialized.DivTag;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicTextComponent extends Component {

    private String text;
    private final String className;
    private final String id;
    private final Map<String, String> placeholders;
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)\\}");

    public DynamicTextComponent(String id, String text, String className) {
        super(div(text).withClass("text_component dynamic " + className).withId(id).attr("data-template", text));
        this.id = id;
        this.text = text;
        this.className = className;
        this.placeholders = new HashMap<>();
        extractPlaceholders();
    }

    private void extractPlaceholders() {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            placeholders.put(placeholder, "");
        }
    }

    public void updatePlaceholder(String placeholder, String value) {
        placeholders.put(placeholder, value);
        updateContent();
    }

    public void updatePlaceholders(Map<String, String> values) {
        placeholders.putAll(values);
        updateContent();
    }

    private void updateContent() {
        String renderedText = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            renderedText = renderedText.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        this.componentContent = div(renderedText)
            .withClass("text_component dynamic " + className)
            .withId(id)
            .attr("data-template", text);
    }

    public String getText() {
        return text;
    }

    public String getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public Map<String, String> getPlaceholders() {
        return new HashMap<>(placeholders);
    }

    public String getRenderedText() {
        String renderedText = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            renderedText = renderedText.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return renderedText;
    }

    @Override
    public DivTag getContent() {
        return (DivTag) this.componentContent;
    }
}
