package fr.mrqsdf.jprofiler.css;

public class CSS {

    // Color utilities
    public static final String COLOR_WHITE = "#FFFFFF";
    public static final String COLOR_BLACK = "#000000";
    public static final String COLOR_GRAY = "#808080";
    public static final String COLOR_LIGHT_GRAY = "#D3D3D3";
    public static final String COLOR_DARK_GRAY = "#404040";
    public static final String COLOR_RED = "#FF0000";
    public static final String COLOR_GREEN = "#00FF00";
    public static final String COLOR_BLUE = "#0000FF";

    // Font sizes
    public static final String FONT_SIZE_SMALL = "12px";
    public static final String FONT_SIZE_NORMAL = "14px";
    public static final String FONT_SIZE_LARGE = "18px";
    public static final String FONT_SIZE_XLARGE = "24px";

    // Common spacing
    public static final String MARGIN_NONE = "0";
    public static final String MARGIN_SMALL = "8px";
    public static final String MARGIN_MEDIUM = "16px";
    public static final String MARGIN_LARGE = "32px";

    public static final String PADDING_NONE = "0";
    public static final String PADDING_SMALL = "8px";
    public static final String PADDING_MEDIUM = "16px";
    public static final String PADDING_LARGE = "32px";

    // Border styles
    public static final String BORDER_NONE = "none";
    public static final String BORDER_SOLID = "1px solid #000000";
    public static final String BORDER_LIGHT = "1px solid #D3D3D3";

    // Display properties
    public static final String DISPLAY_BLOCK = "block";
    public static final String DISPLAY_INLINE = "inline";
    public static final String DISPLAY_INLINE_BLOCK = "inline-block";
    public static final String DISPLAY_FLEX = "flex";
    public static final String DISPLAY_GRID = "grid";
    public static final String DISPLAY_NONE = "none";

    // Text alignment
    public static final String TEXT_ALIGN_LEFT = "left";
    public static final String TEXT_ALIGN_CENTER = "center";
    public static final String TEXT_ALIGN_RIGHT = "right";
    public static final String TEXT_ALIGN_JUSTIFY = "justify";

    /**
     * Setup basic CSS rules for common components
     */
    public static void setupDefaultStyles() {
        // Text component
        CSSManager.addRule(".text_component")
            .addProperty("font-size", FONT_SIZE_NORMAL)
            .addProperty("color", COLOR_BLACK)
            .addProperty("margin", MARGIN_SMALL);

        // Multi text component
        CSSManager.addRule(".multi_text_component")
            .addProperty("display", DISPLAY_FLEX)
            .addProperty("flex-direction", "column")
            .addProperty("gap", MARGIN_SMALL);

        // Image component
        CSSManager.addRule(".image_component")
            .addProperty("text-align", TEXT_ALIGN_CENTER)
            .addProperty("margin", MARGIN_MEDIUM);

        CSSManager.addRule(".image_component img")
            .addProperty("max-width", "100%")
            .addProperty("height", "auto")
            .addProperty("border", BORDER_LIGHT);

        // Chart component
        CSSManager.addRule(".chart_component")
            .addProperty("display", DISPLAY_FLEX)
            .addProperty("justify-content", "center")
            .addProperty("margin", MARGIN_MEDIUM)
            .addProperty("padding", PADDING_MEDIUM)
            .addProperty("background-color", COLOR_WHITE)
            .addProperty("border", BORDER_LIGHT);

        // Page component
        CSSManager.addRule(".page_component")
            .addProperty("display", DISPLAY_FLEX)
            .addProperty("flex-direction", "column")
            .addProperty("min-height", "100vh");

        CSSManager.addRule(".page_header")
            .addProperty("background-color", COLOR_DARK_GRAY)
            .addProperty("color", COLOR_WHITE)
            .addProperty("padding", PADDING_MEDIUM)
            .addProperty("border-bottom", BORDER_SOLID);

        CSSManager.addRule(".page_body")
            .addProperty("flex", "1")
            .addProperty("padding", PADDING_LARGE);

        CSSManager.addRule(".page_footer")
            .addProperty("background-color", COLOR_DARK_GRAY)
            .addProperty("color", COLOR_WHITE)
            .addProperty("padding", PADDING_MEDIUM)
            .addProperty("border-top", BORDER_SOLID)
            .addProperty("text-align", TEXT_ALIGN_CENTER);

        // Image error
        CSSManager.addRule(".image_component.error")
            .addProperty("color", COLOR_RED)
            .addProperty("text-align", TEXT_ALIGN_CENTER)
            .addProperty("padding", PADDING_MEDIUM);

        // Button component
        CSSManager.addRule(".button_component")
            .addProperty("display", DISPLAY_INLINE_BLOCK)
            .addProperty("margin", MARGIN_SMALL);

        CSSManager.addRule(".button_element")
            .addProperty("padding", PADDING_MEDIUM)
            .addProperty("font-size", FONT_SIZE_NORMAL)
            .addProperty("background-color", COLOR_BLUE)
            .addProperty("color", COLOR_WHITE)
            .addProperty("border", BORDER_NONE)
            .addProperty("border-radius", "4px")
            .addProperty("cursor", "pointer")
            .addProperty("transition", "background-color 0.3s");

        CSSManager.addRule(".button_element:hover")
            .addProperty("background-color", COLOR_DARK_GRAY);

        // TextFieldComponent
        CSSManager.addRule(".textfield_component")
            .addProperty("margin", MARGIN_SMALL)
            .addProperty("display", DISPLAY_BLOCK);

        CSSManager.addRule(".textfield_element")
            .addProperty("padding", PADDING_SMALL)
            .addProperty("font-size", FONT_SIZE_NORMAL)
            .addProperty("border", BORDER_LIGHT)
            .addProperty("border-radius", "4px")
            .addProperty("width", "100%")
            .addProperty("box-sizing", "border-box");

        // CheckboxComponent
        CSSManager.addRule(".checkbox_component")
            .addProperty("display", DISPLAY_INLINE_BLOCK)
            .addProperty("margin", MARGIN_SMALL);

        CSSManager.addRule(".checkbox_element")
            .addProperty("margin-right", PADDING_SMALL)
            .addProperty("cursor", "pointer");

        CSSManager.addRule(".checkbox_label")
            .addProperty("cursor", "pointer")
            .addProperty("user-select", "none");
    }
}
