import org.apache.commons.lang3.tuple.Pair;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.imgscalr.Scalr;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextRecognitionUtils {
    private TextRecognitionUtils() {
    }

    public static BufferedImage imageToBufferedImage(Image aImage) {
        if (aImage == null) {
            return null;
        }
        return (BufferedImage) aImage;
    }

    public static String getExtensionByStringHandling(String aFileName) {
        if (aFileName == null || !aFileName.contains(".")) {
            return "";
        }

        return aFileName.substring(aFileName.lastIndexOf(".") + 1);
    }

    public static BufferedImage resizeImageAndCompress(Image aOriginalImage, int aTargetWidth, int aTargetHeight) throws IOException {
        return resizeImageAndCompress(imageToBufferedImage(aOriginalImage), aTargetWidth, aTargetHeight);
    }


    public static BufferedImage resizeImageAndCompress(BufferedImage aOriginalImage, int aTargetWidth, int aTargetHeight) {
        Image resultingImage = aOriginalImage.getScaledInstance(aTargetWidth, aTargetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(aTargetWidth, aTargetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics()
                   .drawImage(resultingImage, 0, 0, null);

        outputImage = Scalr.resize(outputImage, Scalr.Method.SPEED, aTargetWidth, aTargetHeight, Scalr.OP_ANTIALIAS);
        return outputImage;
    }

    public static Image compressImage(Image aImage, int aWidth, int aHeight) {
        return (Image) Scalr.resize((BufferedImage) aImage, Scalr.Method.SPEED, aWidth, aHeight, Scalr.OP_ANTIALIAS);
    }

    public static Pair<List<Image>, List<Image>> pdfToHighAndLowResBufferedImages(File aPdfFile, int aLowDpi, int aHighDpi) throws IOException {
        List<Image> highRes = pdfToBufferedImagesCompressed(aPdfFile, aHighDpi);
        List<Image> lowRes = pdfToBufferedImagesCompressed(aPdfFile, aLowDpi);
        return Pair.of(lowRes, highRes);
    }

    public static List<Image> pdfToBufferedImages(File aPdfFile) throws IOException {
        return pdfToBufferedImagesCompressed(aPdfFile, 300);
    }

    public static List<Image> pdfToBufferedImagesCompressed(File aPdfFile, int aDpi) throws IOException {
        PDDocument document = Loader.loadPDF(aPdfFile);
        PDFRenderer renderer = new PDFRenderer(document);
        List<Image> images = new ArrayList<>();

        for (int i = 0; i < document.getNumberOfPages(); i++) {
            BufferedImage bufferedImage = renderer.renderImageWithDPI(i, aDpi);
            images.add(bufferedImage);
        }


        return images;
    }
}
