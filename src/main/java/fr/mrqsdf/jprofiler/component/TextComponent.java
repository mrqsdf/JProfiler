package fr.mrqsdf.jprofiler.component;

import static j2html.TagCreator.div;

import fr.mrqsdf.jprofiler.css.CSSRule;
import j2html.tags.specialized.DivTag;

public class TextComponent extends Component{

    private final String text;
    private final String className;

    public TextComponent(String text, String className){
        super(div(text).withClass("text_component " + className));
        this.text = text;
        this.className = className;
    }

    public TextComponent(String text, String className, CSSRule... styles){
        super(div(text).withClass("text_component " + className));
        this.text = text;
        this.className = className;
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

}
