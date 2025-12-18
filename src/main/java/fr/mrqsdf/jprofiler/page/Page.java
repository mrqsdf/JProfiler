package fr.mrqsdf.jprofiler.page;

import fr.mrqsdf.jprofiler.component.Component;
import j2html.tags.DomContent;

import java.util.ArrayList;
import java.util.List;

import static j2html.TagCreator.*;

public class Page {

    private final String id;
    private final String title;
    private final List<Component> headerComponents;
    private final List<Component> bodyComponents;
    private final List<Component> footerComponents;

    public Page(String id, String title) {
        this.id = id;
        this.title = title;
        this.headerComponents = new ArrayList<>();
        this.bodyComponents = new ArrayList<>();
        this.footerComponents = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Page addToHeader(Component component) {
        this.headerComponents.add(component);
        return this;
    }

    public Page addToBody(Component component) {
        this.bodyComponents.add(component);
        return this;
    }

    public Page addToFooter(Component component) {
        this.footerComponents.add(component);
        return this;
    }

    public Page addAllToHeader(List<Component> components) {
        this.headerComponents.addAll(components);
        return this;
    }

    public Page addAllToBody(List<Component> components) {
        this.bodyComponents.addAll(components);
        return this;
    }

    public Page addAllToFooter(List<Component> components) {
        this.footerComponents.addAll(components);
        return this;
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

    public DomContent render() {
        DomContent[] headerContent = headerComponents.stream()
            .map(Component::getContent)
            .toArray(DomContent[]::new);

        DomContent[] bodyContent = bodyComponents.stream()
            .map(Component::getContent)
            .toArray(DomContent[]::new);

        DomContent[] footerContent = footerComponents.stream()
            .map(Component::getContent)
            .toArray(DomContent[]::new);

        return div(
            !headerComponents.isEmpty() ? header(headerContent).withClass("page_header") : null,
            !bodyComponents.isEmpty() ? main(bodyContent).withClass("page_body") : null,
            !footerComponents.isEmpty() ? footer(footerContent).withClass("page_footer") : null
        ).withClass("page").withId(id);
    }

    public void clear() {
        headerComponents.clear();
        bodyComponents.clear();
        footerComponents.clear();
    }
}
