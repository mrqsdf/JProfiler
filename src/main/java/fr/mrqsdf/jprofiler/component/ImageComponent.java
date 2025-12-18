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

    private final BufferedImage image;
    private final String altText;
    private final String className;

    public ImageComponent(BufferedImage image, String altText, String className) {
        super(createImageDiv(image, altText, className));
        this.image = image;
        this.altText = altText;
        this.className = className;
    }

    private static DivTag createImageDiv(BufferedImage image, String altText, String className) {
        try {
            String base64Image = encodeImageToBase64(image);
            return div(
                img()
                    .withSrc("data:image/png;base64," + base64Image)
                    .withAlt(altText)
                    .withClass("image")
            ).withClass("image_component " + className);
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

    @Override
    public DivTag getContent() {
        return (DivTag) this.componentContent;
    }
}
