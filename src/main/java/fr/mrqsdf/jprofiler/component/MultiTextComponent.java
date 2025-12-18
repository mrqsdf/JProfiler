package fr.mrqsdf.jprofiler.component;

import static j2html.TagCreator.div;

import java.util.List;

import j2html.tags.specialized.DivTag;

public class MultiTextComponent extends Component {

    private final List<TextComponent> components;

    public MultiTextComponent(List<TextComponent> components, String className) {
        super(div(components.stream().map(TextComponent::getContent).toArray(DivTag[]::new)).withClass("multi_text_component " + className));
        this.components = components;
    
    }

    public List<TextComponent> getComponents() {
        return components;
    }

}
