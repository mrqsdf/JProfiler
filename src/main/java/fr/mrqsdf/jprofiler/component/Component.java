package fr.mrqsdf.jprofiler.component;

import j2html.tags.DomContent;
import java.util.HashMap;
import java.util.Map;

import static j2html.TagCreator.*;

public abstract class Component {

    protected DomContent componentContent;
    protected String id;
    protected Map<String, String> dataAttributes;
    
    public Component(DomContent content){
        this.componentContent = content;
        this.dataAttributes = new HashMap<>();
    }

    public Component(String className){
        this.componentContent = div().withClass(className);
        this.dataAttributes = new HashMap<>();
    }

    public DomContent getContent(){
        return componentContent;
    }

    public String render(){
        return componentContent.render();
    }

    /**
     * Set the component ID for dynamic updates
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the component ID
     */
    public String getId() {
        return id;
    }

    /**
     * Add a data attribute for dynamic updates
     */
    public void addDataAttribute(String name, String value) {
        dataAttributes.put(name, value);
    }

    /**
     * Get a data attribute
     */
    public String getDataAttribute(String name) {
        return dataAttributes.get(name);
    }

    /**
     * Get all data attributes
     */
    public Map<String, String> getDataAttributes() {
        return new HashMap<>(dataAttributes);
    }

    /**
     * Update the component dynamically
     * Subclasses should override this method to implement their own update logic
     */
    public void updateDynamically(String key, Object value) {
        // Default implementation - subclasses should override
        addDataAttribute(key, String.valueOf(value));
    }

}
