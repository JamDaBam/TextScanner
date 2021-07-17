import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class TextRecognition {
    private static TextRecognition cvInstance;

    private Tesseract ivTesseract;

    private TextRecognition() {
        ivTesseract = initTesseract();
    }

    public static TextRecognition getInstance() {
        if (cvInstance == null) {
            cvInstance = new TextRecognition();
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

    public String read(InputStream aInputStream) {
        String result;

        try {
            BufferedImage read = ImageIO.read(aInputStream);
            result = ivTesseract.doOCR(read);

        } catch (IOException | TesseractException e) {
            e.printStackTrace();
            result = "Es ist ein Fehler aufgetreten:\n" + e.getMessage();
        }

        return result;
    }
}
