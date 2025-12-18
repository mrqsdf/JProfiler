package fr.mrqsdf.jprofiler.component;

import static j2html.TagCreator.div;
import static j2html.TagCreator.header;
import static j2html.TagCreator.main;
import static j2html.TagCreator.footer;

import java.util.ArrayList;
import java.util.List;

import j2html.tags.DomContent;

public class PageComponent extends Component {

    private final List<Component> headerComponents;
    private final List<Component> bodyComponents;
    private final List<Component> footerComponents;
    private final String className;

    public PageComponent(String className) {
        super(div().withClass("page_component " + className));
        this.headerComponents = new ArrayList<>();
        this.bodyComponents = new ArrayList<>();
        this.footerComponents = new ArrayList<>();
        this.className = className;
        updateContent();
    }

    public PageComponent addToHeader(Component component) {
        this.headerComponents.add(component);
        updateContent();
        return this;
    }

    public PageComponent addToBody(Component component) {
        this.bodyComponents.add(component);
        updateContent();
        return this;
    }

    public PageComponent addToFooter(Component component) {
        this.footerComponents.add(component);
        updateContent();
        return this;
    }

    public PageComponent addAllToHeader(List<Component> components) {
        this.headerComponents.addAll(components);
        updateContent();
        return this;
    }

    public PageComponent addAllToBody(List<Component> components) {
        this.bodyComponents.addAll(components);
        updateContent();
        return this;
    }

    public PageComponent addAllToFooter(List<Component> components) {
        this.footerComponents.addAll(components);
        updateContent();
        return this;
    }

    private void updateContent() {
        DomContent[] headerContent = headerComponents.stream()
            .map(Component::getContent)
            .toArray(DomContent[]::new);

        DomContent[] bodyContent = bodyComponents.stream()
            .map(Component::getContent)
            .toArray(DomContent[]::new);

        DomContent[] footerContent = footerComponents.stream()
            .map(Component::getContent)
            .toArray(DomContent[]::new);

        this.componentContent = div(
            headerComponents.isEmpty() ? null : header(headerContent).withClass("page_header"),
            bodyComponents.isEmpty() ? null : main(bodyContent).withClass("page_body"),
            footerComponents.isEmpty() ? null : footer(footerContent).withClass("page_footer")
        ).withClass("page_component " + className);
    }

    public List<Component> getHeaderComponents() {
        return new ArrayList<>(headerComponents);
    }

    public List<Component> getBodyComponents() {
        return new ArrayList<>(bodyComponents);
    }

    public List<Component> getFooterComponents() {
        return new ArrayList<>(footerComponents);
    }

    public String getClassName() {
        return className;
    }

    @Override
    public DomContent getContent() {
        return this.componentContent;
    }
}
