package fr.mrqsdf.jprofiler.component;

import static j2html.TagCreator.button;
import static j2html.TagCreator.div;

import j2html.tags.specialized.DivTag;

public class ButtonComponent extends Component {

    private final String text;
    private final String className;
    private final ActionType actionType;
    private final String actionData;

    public ButtonComponent(String text, String className) {
        this(text, className, null, null);
    }

    public ButtonComponent(String text, String className, ActionType actionType, String actionData) {
        super(createButtonDiv(text, className, actionType, actionData));
        this.text = text;
        this.className = className;
        this.actionType = actionType;
        this.actionData = actionData;
    }

    private static DivTag createButtonDiv(String text, String className, ActionType actionType, String actionData) {
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
        return div(
            button(text)
                .withClass("button_element " + className)
                .attr("onclick", finalOnClickAttr.isEmpty() ? null : finalOnClickAttr)
        ).withClass("button_component " + className);
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

    @Override
    public DivTag getContent() {
        return (DivTag) this.componentContent;
    }
}
