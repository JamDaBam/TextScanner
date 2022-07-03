import org.apache.commons.lang3.tuple.Pair;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TextRecognition {
    private final Map<String, List<Image>> ivHighResImages = new TreeMap<>();
    private final Map<String, List<Image>> ivLowResImages = new TreeMap<>();

    private final Map<String, String[]> ivProcessed = new TreeMap<>();

    public void loadPdf(boolean aThumbnails, int aLowDpi, int aHighDpi) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Input");
        FileNameExtensionFilter pdfFileFilter = new FileNameExtensionFilter("PDF", "pdf");
        chooser.setFileFilter(pdfFileFilter);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int i = chooser.showOpenDialog(null);

        if (JFileChooser.APPROVE_OPTION == i) {

            try {
                File file = chooser.getSelectedFile();
                if (aThumbnails) {
                    Pair<List<Image>, List<Image>> lowHigh = TextRecognitionUtils.pdfToHighAndLowResBufferedImages(file, aLowDpi, aHighDpi);
                    ivLowResImages.put(file.getName(), lowHigh.getLeft());
                    ivHighResImages.put(file.getName(), lowHigh.getRight());
                } else {
                    List<Image> highRes = TextRecognitionUtils.pdfToBufferedImagesCompressed(file, aHighDpi);
                    ivHighResImages.put(file.getName(), highRes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadPdfBatchFolder(boolean aThumbnails, int aLowDpi, int aHighDpi) throws IOException {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Input");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int i = chooser.showOpenDialog(null);

        if (JFileChooser.APPROVE_OPTION == i) {
            File dir = chooser.getSelectedFile();
            System.out.println(dir.getAbsolutePath());

            File[] files = dir.listFiles((dir1, name) -> name.toLowerCase()
                                                             .endsWith(".pdf"));

            if (files != null) {
                for (File file : files) {
                    if (aThumbnails) {
                        Pair<List<Image>, List<Image>> lowHigh = TextRecognitionUtils.pdfToHighAndLowResBufferedImages(file, aLowDpi, aHighDpi);
                        ivLowResImages.put(file.getName(), lowHigh.getLeft());
                        ivHighResImages.put(file.getName(), lowHigh.getRight());
                    } else {
                        List<Image> highRes = TextRecognitionUtils.pdfToBufferedImages(file);
                        ivHighResImages.put(file.getName(), highRes);
                    }
                }
            }
        }
    }

    public void processLoadedFiles() {
        for (Map.Entry<String, List<Image>> entry : ivHighResImages.entrySet()) {
            String filename = entry.getKey();
            List<Image> value = entry.getValue();

            String[] result = new String[value.size()];

            for (int i = 0; i < value.size(); i++) {
                Image image = value.get(i);
                result[i] = TextRecognitionProcess.getInstance()
                                                  .read((BufferedImage) image);
            }

            ivProcessed.put(filename, result);
        }
    }

    public void write(Map<String, String[]> aMap, WriteFunction aWriteFunction) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Output");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int i = chooser.showOpenDialog(null);

        if (JFileChooser.APPROVE_OPTION == i) {
            File dir = chooser.getSelectedFile();
            String absolutePath = dir.getAbsolutePath();
            System.out.println(absolutePath);
            for (Map.Entry<String, String[]> entry : aMap.entrySet()) {
                String filename = entry.getKey();
                String[] value = entry.getValue();
                aWriteFunction.write(absolutePath, filename, value);
            }
        }
    }

    public void writeProcessedToTextFile() {
        writeTextFile(ivProcessed);
    }

    public void writeTextFile(Map<String, String[]> aMap) {
        write(aMap, (aOutputPath, aFilename, aContent) -> {
            try {
                BufferedWriter outputWriter = new BufferedWriter(new FileWriter(aOutputPath + "/" + aFilename + ".txt"));
                for (String content : aContent) {
                    outputWriter.write(content);
                    outputWriter.newLine();
                    outputWriter.newLine();
                }
                outputWriter.flush();
                outputWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void writeProcessedToDocxFile() {
        writeDocxFile(ivProcessed);
    }

    public void writeDocxFile(Map<String, String[]> aMap) {
        write(aMap, (aOutputPath, aFilename, aContent) -> {
            try {
                WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
                for (String content : aContent) {
                    wordMLPackage.getMainDocumentPart()
                                 .addParagraphOfText(content);
                }
                wordMLPackage.save(new java.io.File(aOutputPath + "/" + aFilename + ".docx"));
            } catch (Docx4JException e) {
                e.printStackTrace();
            }
        });
    }

    public Map<String, List<Image>> getLowResImages() {
        return ivLowResImages;
    }
}
