package fr.mrqsdf.jprofiler.component;

import static j2html.TagCreator.button;
import static j2html.TagCreator.div;

import j2html.tags.specialized.DivTag;

public class ButtonComponent extends Component {

    private String text;
    private final String className;
    private final ActionType actionType;
    private final String actionData;
    private final String id;

    public ButtonComponent(String text, String className) {
        this(text, className, null, null, null);
    }

    public ButtonComponent(String text, String className, ActionType actionType, String actionData) {
        this(text, className, actionType, actionData, null);
    }

    public ButtonComponent(String text, String className, ActionType actionType, String actionData, String id) {
        super(createButtonDiv(text, className, actionType, actionData, id));
        this.text = text;
        this.className = className;
        this.actionType = actionType;
        this.actionData = actionData;
        this.id = id;
        if (id != null) {
            addDataAttribute("button-text", text);
        }
    }

    private static DivTag createButtonDiv(String text, String className, ActionType actionType, String actionData, String id) {
        String onClickAttr = "";

        if (actionType != null && actionData != null) {
            switch (actionType) {
                case NAVIGATE_PAGE:
                    onClickAttr = "navigateToPage('" + actionData + "')";
                    break;
                case SUBMIT:
                    onClickAttr = "submitForm('" + actionData + "')";
                    break;
                case CUSTOM:
                    onClickAttr = actionData;
                    break;
                default:
                    break;
            }
        }

        String finalOnClickAttr = onClickAttr;
        DivTag div = div(
            button(text)
                .withClass("button_element " + className)
                .attr("onclick", finalOnClickAttr.isEmpty() ? null : finalOnClickAttr)
        ).withClass("button_component " + className);
        
        if (id != null) {
            div = div.withId(id).attr("data-button-text", text);
        }
        
        return div;
    }

    public String getText() {
        return text;
    }

    public String getClassName() {
        return className;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public String getActionData() {
        return actionData;
    }

    public String getId() {
        return id;
    }

    public void updateButtonText(String newText) {
        this.text = newText;
        this.componentContent = createButtonDiv(newText, className, actionType, actionData, id);
        addDataAttribute("button-text", newText);
    }

    @Override
    public DivTag getContent() {
        return (DivTag) this.componentContent;
    }
}
