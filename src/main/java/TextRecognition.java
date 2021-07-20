import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TextRecognition {
    public static Map<String, String[]> openPdf() {
        Map<String, String[]> resultMap = null;

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Input");
        FileNameExtensionFilter pdfFileFilter = new FileNameExtensionFilter("PDF", "pdf");
        chooser.setFileFilter(pdfFileFilter);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int i = chooser.showOpenDialog(null);

        if (JFileChooser.APPROVE_OPTION == i) {
            resultMap = new TreeMap<>();

            File file = chooser.getSelectedFile();
            System.out.println(file.getAbsolutePath());
            String[] result = TextRecognitionProcess.getInstance()
                                                    .read(file);

            resultMap.put(file.getName(), result);
        }

        return resultMap == null ? Collections.emptyMap() : resultMap;
    }

    public static Map<String, String[]> openPdfBatchFolder() {
        Map<String, String[]> resultMap = null;

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

            resultMap = new TreeMap<>();

            if (files != null) {
                for (File file : files) {
                    String[] result = TextRecognitionProcess.getInstance()
                                                            .read(file);
                    resultMap.put(file.getName(), result);
                }

                Set<Map.Entry<String, String[]>> entries = resultMap.entrySet();
                for (Map.Entry<String, String[]> entry : entries) {
                    String filename = entry.getKey();
                    String[] result = entry.getValue();

                    System.out.println(filename + "\n" + Arrays.deepToString(result));
                }
            }
        }

        return resultMap == null ? Collections.emptyMap() : resultMap;
    }

    public static void write(Map<String, String[]> aMap, WriteFunction aWriteFunction) {
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

    public static void writeTextFile(Map<String, String[]> aMap) {
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

    public static void writeDocxFile(Map<String, String[]> aMap) {
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
}
