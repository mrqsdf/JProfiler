package fr.mrqsdf.jprofiler.component;

import static j2html.TagCreator.div;
import static j2html.TagCreator.input;
import j2html.tags.specialized.DivTag;

public class TextFieldComponent extends Component {

    private final String id;
    private final String placeholder;
    private final String className;
    private final String type;
    private final String value;

    public TextFieldComponent(String id, String placeholder, String className) {
        this(id, placeholder, className, "text", "");
    }

    public TextFieldComponent(String id, String placeholder, String className, String type) {
        this(id, placeholder, className, type, "");
    }

    public TextFieldComponent(String id, String placeholder, String className, String type, String value) {
        super(createTextFieldDiv(id, placeholder, className, type, value));
        this.id = id;
        this.placeholder = placeholder;
        this.className = className;
        this.type = type;
        this.value = value;
    }

    private static DivTag createTextFieldDiv(String id, String placeholder, String className, String type, String value) {
        return div(
            input()
                .withType(type)
                .withId(id)
                .withName(id)
                .withPlaceholder(placeholder)
                .withClass("textfield_element " + className)
                .attr("value", value.isEmpty() ? null : value)
        ).withClass("textfield_component " + className);
    }

    public String getId() {
        return id;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public String getClassName() {
        return className;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public DivTag getContent() {
        return (DivTag) this.componentContent;
    }
}
