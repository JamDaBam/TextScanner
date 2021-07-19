import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;
import org.ghost4j.document.DocumentException;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.RendererException;
import org.ghost4j.renderer.SimpleRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TextRecognitionProcess {
    private static TextRecognitionProcess cvInstance;

    private Tesseract ivTesseract;

    private TextRecognitionProcess() {
        ivTesseract = initTesseract();
    }

    public static TextRecognitionProcess getInstance() {
        if (cvInstance == null) {
            cvInstance = new TextRecognitionProcess();
        }

        return cvInstance;
    }

    private Tesseract initTesseract() {
        Tesseract tesseract = new Tesseract();
        //Ressourcen laden und in Tempdirectory extrahieren
        //Trainingsdaten von https://github.com/tesseract-ocr/tessdata laden
        File tessDataFolder = LoadLibs.extractTessResources("tessdata");
        tesseract.setDatapath(tessDataFolder.getAbsolutePath());
        //Fehler deu wird nicht geladen. Diese wurde händisch heruntergeladen
        //Als Workaround könnte man ein Download über die Trainignsdaten manuell machen und in das Tempverzeichnis legen, da dieses bekannt ist.
        tesseract.setLanguage("deu");
        tesseract.setOcrEngineMode(ITessAPI.TessOcrEngineMode.OEM_TESSERACT_LSTM_COMBINED);

        return tesseract;
    }

    public String[] read(File aFile) {
        String[] result;

        String ext = getExtensionByStringHandling(aFile.getName());

        if ("pdf".equalsIgnoreCase(ext)) {
            try {
                PDFDocument pdf = new PDFDocument();
                pdf.load(aFile);

                SimpleRenderer renderer = new SimpleRenderer();
                renderer.setResolution(300);
                List<Image> images = renderer.render(pdf);

                result = new String[images.size()];

                int site = 1;

                for (int i = 0; i < images.size(); i++) {
                    Image image = images.get(i);
                    BufferedImage bi = imageToBufferedImage(image);
                    String pdftext = read(bi);
                    bi.flush();
                    result[i] = pdftext;
                    System.out.println("Seite " + site++ + ":\n" + pdftext + "\n");
                }

            } catch (IOException | DocumentException | RendererException e) {
                e.printStackTrace();
                result = new String[]{"Es ist ein Fehler aufgetreten:\n" + e.getMessage()};
            }

            return result;
        } else {
            throw new UnsupportedOperationException("Die Extension '" + ext + "' wird nicht berücksichtigt");
        }
    }

    public String[] read(String aPdfFilePath) {
        File file = new File(aPdfFilePath);
        return read(file);
    }

    public String read(InputStream aInputStream) {
        if (aInputStream == null) {
            return "InputStream ist 'null'";
        }

        String result;

        try {
            BufferedImage read = ImageIO.read(aInputStream);
            result = read(read);
        } catch (IOException e) {
            e.printStackTrace();
            result = "Es ist ein Fehler aufgetreten:\n" + e.getMessage();
        }

        return result;
    }

    public String read(BufferedImage aBufferedImage) {
        if (aBufferedImage == null) {
            return "Image ist 'null'";
        }

        String result;

        try {
            result = ivTesseract.doOCR(aBufferedImage);
        } catch (TesseractException e) {
            e.printStackTrace();
            result = "Es ist ein Fehler aufgetreten:\n" + e.getMessage();
        }

        return result;
    }

    private BufferedImage imageToBufferedImage(Image aImage) {
        if (aImage == null) {
            return null;
        }

        BufferedImage bi = new BufferedImage(aImage.getWidth(null), aImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.drawImage(aImage, 0, 0, null);
        g2d.dispose();
        return bi;
    }

    private String getExtensionByStringHandling(String aFileName) {
        if (aFileName == null || !aFileName.contains(".")) {
            return "";
        }

        return aFileName.substring(aFileName.lastIndexOf(".") + 1);
    }
}
