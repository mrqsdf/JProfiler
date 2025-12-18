package fr.mrqsdf.jprofiler.component;

import static j2html.TagCreator.div;
import static j2html.TagCreator.img;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import j2html.tags.specialized.DivTag;

public class ImageComponent extends Component {

    private BufferedImage image;
    private final String altText;
    private final String className;
    private final String id;

    public ImageComponent(BufferedImage image, String altText, String className) {
        this(image, altText, className, null);
    }

    public ImageComponent(BufferedImage image, String altText, String className, String id) {
        super(createImageDiv(image, altText, className, id));
        this.image = image;
        this.altText = altText;
        this.className = className;
        this.id = id;
        if (id != null) {
            try {
                String base64Image = encodeImageToBase64(image);
                addDataAttribute("image-base64", base64Image);
            } catch (IOException e) {
                // Silently ignore
            }
        }
    }

    private static DivTag createImageDiv(BufferedImage image, String altText, String className, String id) {
        try {
            String base64Image = encodeImageToBase64(image);
            DivTag div = div(
                img()
                    .withSrc("data:image/png;base64," + base64Image)
                    .withAlt(altText)
                    .withClass("image")
            ).withClass("image_component " + className);
            if (id != null) {
                div = div.withId(id).attr("data-image-base64", base64Image);
            }
            return div;
        } catch (IOException e) {
            return div("Error loading image: " + e.getMessage())
                .withClass("image_component error " + className);
        }
    }

    private static String encodeImageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getAltText() {
        return altText;
    }

    public String getClassName() {
        return className;
    }

    public String getId() {
        return id;
    }

    public void updateImage(BufferedImage newImage) {
        this.image = newImage;
        this.componentContent = createImageDiv(newImage, altText, className, id);
        try {
            String base64Image = encodeImageToBase64(newImage);
            addDataAttribute("image-base64", base64Image);
        } catch (IOException e) {
            // Silently ignore
        }
    }

    @Override
    public DivTag getContent() {
        return (DivTag) this.componentContent;
    }
}
