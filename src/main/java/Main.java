import org.ghost4j.document.DocumentException;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.RendererException;
import org.ghost4j.renderer.SimpleRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, RendererException, DocumentException {
        TextRecognition textRecognition = TextRecognition.getInstance();

//        InputStream resourceAsStream = Main.class.getClassLoader().getResourceAsStream("pic.png");
//        String text = textRecognition.read(resourceAsStream);
//        resourceAsStream.close();
//
//        System.out.print("Hier muss was stehen:\n" + text);

        PDFDocument pdf = new PDFDocument();
        pdf.load(Main.class.getClassLoader().getResourceAsStream("test.pdf"));

        SimpleRenderer renderer = new SimpleRenderer();
        renderer.setResolution(300);
        List<Image> images = renderer.render(pdf);

        int site = 1;

        for (Image image : images) {
            BufferedImage bi = imageToBufferedImage(image);
            String pdftext = textRecognition.read(bi);
            bi.flush();
            System.out.println("Seite " + site++ + ":\n" + pdftext + "\n");
        }
    }

    public static BufferedImage imageToBufferedImage(Image aImage) {
        BufferedImage bi = new BufferedImage(aImage.getWidth(null), aImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.drawImage(aImage, 0, 0, null);
        g2d.dispose();
        return bi;
    }
}
