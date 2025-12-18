package fr.mrqsdf.jprofiler.component;

import static j2html.TagCreator.div;
import static j2html.TagCreator.input;
import static j2html.TagCreator.label;

import j2html.tags.specialized.DivTag;

public class CheckboxComponent extends Component {

    private final String id;
    private final String label;
    private final String className;
    private boolean checked;

    public CheckboxComponent(String id, String label, String className) {
        this(id, label, className, false);
    }

    public CheckboxComponent(String id, String label, String className, boolean checked) {
        super(createCheckboxDiv(id, label, className, checked));
        this.id = id;
        this.label = label;
        this.className = className;
        this.checked = checked;
        addDataAttribute("checkbox-checked", String.valueOf(checked));
    }

    private static DivTag createCheckboxDiv(String id, String label, String className, boolean checked) {
        return div(
            input()
                .withType("checkbox")
                .withId(id)
                .withName(id)
                .withClass("checkbox_element " + className)
                .attr("checked", checked ? "checked" : null)
                .attr("data-checked", checked ? "true" : "false"),
            label(label)
                .withFor(id)
                .withClass("checkbox_label " + className)
        ).withClass("checkbox_component " + className);
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getClassName() {
        return className;
    }

    public boolean isChecked() {
        return checked;
    }

    public void updateChecked(boolean newChecked) {
        this.checked = newChecked;
        this.componentContent = createCheckboxDiv(id, label, className, newChecked);
        addDataAttribute("checkbox-checked", String.valueOf(newChecked));
    }

    @Override
    public DivTag getContent() {
        return (DivTag) this.componentContent;
    }
}
