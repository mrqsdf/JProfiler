package fr.mrqsdf.jprofiler.css;

public enum CSSPropertyName {

    // Colors
    COLOR("color"),
    BACKGROUND_COLOR("background-color"),
    BORDER_COLOR("border-color"),

    // Fonts
    FONT_SIZE("font-size"),
    FONT_WEIGHT("font-weight"),
    FONT_FAMILY("font-family"),
    FONT_STYLE("font-style"),
    LINE_HEIGHT("line-height"),

    // Spacing
    MARGIN("margin"),
    MARGIN_TOP("margin-top"),
    MARGIN_RIGHT("margin-right"),
    MARGIN_BOTTOM("margin-bottom"),
    MARGIN_LEFT("margin-left"),
    
    PADDING("padding"),
    PADDING_TOP("padding-top"),
    PADDING_RIGHT("padding-right"),
    PADDING_BOTTOM("padding-bottom"),
    PADDING_LEFT("padding-left"),

    // Borders
    BORDER("border"),
    BORDER_TOP("border-top"),
    BORDER_RIGHT("border-right"),
    BORDER_BOTTOM("border-bottom"),
    BORDER_LEFT("border-left"),
    BORDER_WIDTH("border-width"),
    BORDER_STYLE("border-style"),
    BORDER_RADIUS("border-radius"),

    // Display
    DISPLAY("display"),
    VISIBILITY("visibility"),
    OPACITY("opacity"),
    OVERFLOW("overflow"),
    OVERFLOW_X("overflow-x"),
    OVERFLOW_Y("overflow-y"),

    // Positioning
    POSITION("position"),
    TOP("top"),
    RIGHT("right"),
    BOTTOM("bottom"),
    LEFT("left"),
    Z_INDEX("z-index"),

    // Sizing
    WIDTH("width"),
    HEIGHT("height"),
    MAX_WIDTH("max-width"),
    MAX_HEIGHT("max-height"),
    MIN_WIDTH("min-width"),
    MIN_HEIGHT("min-height"),

    // Flexbox
    FLEX("flex"),
    FLEX_DIRECTION("flex-direction"),
    FLEX_WRAP("flex-wrap"),
    FLEX_GROW("flex-grow"),
    FLEX_SHRINK("flex-shrink"),
    FLEX_BASIS("flex-basis"),
    JUSTIFY_CONTENT("justify-content"),
    ALIGN_ITEMS("align-items"),
    ALIGN_CONTENT("align-content"),
    GAP("gap"),

    // Grid
    GRID("grid"),
    GRID_TEMPLATE_COLUMNS("grid-template-columns"),
    GRID_TEMPLATE_ROWS("grid-template-rows"),
    GRID_COLUMN("grid-column"),
    GRID_ROW("grid-row"),
    GRID_GAP("grid-gap"),

    // Text
    TEXT_ALIGN("text-align"),
    TEXT_DECORATION("text-decoration"),
    TEXT_TRANSFORM("text-transform"),
    TEXT_SHADOW("text-shadow"),
    WHITE_SPACE("white-space"),
    WORD_WRAP("word-wrap"),

    // Background
    BACKGROUND("background"),
    BACKGROUND_IMAGE("background-image"),
    BACKGROUND_SIZE("background-size"),
    BACKGROUND_POSITION("background-position"),
    BACKGROUND_REPEAT("background-repeat"),
    BACKGROUND_ATTACHMENT("background-attachment"),

    // Effects
    BOX_SHADOW("box-shadow"),
    TRANSFORM("transform"),
    TRANSITION("transition"),
    ANIMATION("animation"),

    // Cursor
    CURSOR("cursor"),

    // Other
    FLOAT("float"),
    CLEAR("clear"),
    OBJECT_FIT("object-fit"),
    VERTICAL_ALIGN("vertical-align");

    private final String value;

    CSSPropertyName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
