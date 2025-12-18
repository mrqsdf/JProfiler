package fr.mrqsdf.jprofiler.component;

import static j2html.TagCreator.div;

import fr.mrqsdf.jprofiler.css.CSSRule;
import j2html.tags.specialized.DivTag;

public class TextComponent extends Component{

    private final String text;
    private final String className;
    private final String id;

    public TextComponent(String text, String className){
        this(text, className, "");
    }

    public TextComponent(String text, String className, String id){
        super(createTextDiv(text, className, id));
        this.text = text;
        this.className = className;
        this.id = id;
        if (id != null) {
            addDataAttribute("text", text);
        }
    }

    public TextComponent(String text, String className, CSSRule... styles){
        this(text, className, "");
    }

    private static DivTag createTextDiv(String text, String className, String id) {
        DivTag div = div(text).withClass("text_component " + className);
        if (id != null) {
            div = div.withId(id).attr("data-text", text);
        }
        return div;
    }

    @Override
    public DivTag getContent(){
        return (DivTag) this.componentContent;
    }

    public String getText(){
        return this.text;
    }

    public String getClassName(){
        return this.className;
    }

    public String getId(){
        return this.id;
    }

    public void updateText(String newText) {
        // Update content dynamically
        this.componentContent = createTextDiv(newText, className, id);
        addDataAttribute("text", newText);
    }

}
