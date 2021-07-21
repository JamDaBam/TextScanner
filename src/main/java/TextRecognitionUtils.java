import java.awt.*;
import java.awt.image.BufferedImage;

public class TextRecognitionUtils {
    private TextRecognitionUtils() {
    }

    public static BufferedImage imageToBufferedImage(Image aImage) {
        if (aImage == null) {
            return null;
        }

        BufferedImage bi = new BufferedImage(aImage.getWidth(null), aImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.drawImage(aImage, 0, 0, null);
        g2d.dispose();
        return bi;
    }

    public static String getExtensionByStringHandling(String aFileName) {
        if (aFileName == null || !aFileName.contains(".")) {
            return "";
        }

        return aFileName.substring(aFileName.lastIndexOf(".") + 1);
    }
}
