package fr.mrqsdf.jprofiler.component;

import j2html.tags.DomContent;

import static j2html.TagCreator.*;

public abstract class Component {

    protected DomContent componentContent;
    
    public Component(DomContent content){
        this.componentContent = content;
    }

    public Component(String className){
        this.componentContent = div().withClass(className);
    }


    public DomContent getContent(){
        return componentContent;
    }

}
