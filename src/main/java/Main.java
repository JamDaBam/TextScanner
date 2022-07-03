import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        TextRecognition tr = new TextRecognition();

        tr.loadPdf(true, 35, 300);

        JFrame frame = new JFrame("TextRecognition");
        frame.setBounds(500, 500, 800, 800);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container pane = frame.getContentPane();
        BorderLayout mainLayout = new BorderLayout(5, 5);
        pane.setLayout(mainLayout);


        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.X_AXIS));
//        JLabel label = new JLabel("labelText");
//        listPane.add(jScrollPane);
        //listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Map<String, List<JLabel>> pdfPages = new HashMap<>();

        for (Map.Entry<String, List<Image>> stringListEntry : tr.getLowResImages()
                                                                .entrySet()) {
            String key = stringListEntry.getKey();
            List<Image> value = stringListEntry.getValue();

            JLabel jLabel = new JLabel(key);
            listPane.add(jLabel, BorderLayout.CENTER);

            List<JLabel> jLabels = pdfPages.get(key);
            if (jLabels == null) {
                jLabels = new ArrayList<>();
                pdfPages.put(key, jLabels);
            }

            jLabels.add(jLabel);

            for (int i = 0; i < value.size(); i++) {
                Image image = value.get(i);


                jLabel = new JLabel(new ImageIcon(image));
                JLabel finalJLabel = jLabel;
                jLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        if (MouseEvent.BUTTON1 == e.getButton()) {
                            System.out.println("Linksklick");
                            if ("clicked".equalsIgnoreCase(finalJLabel.getText())) {
                                finalJLabel.setText("");
                                System.out.println("deselect");
                            } else {
                                finalJLabel.setText("Clicked");
                                System.out.println("select");
                            }
                        }
                    }
                });
                listPane.add(jLabel, BorderLayout.CENTER);
                listPane.add(Box.createRigidArea(new Dimension(0, 5)));
                listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            }
        }

        JScrollPane scroller = new JScrollPane(listPane);
        pane.add(scroller, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);


//        tr.loadPdfBatchFolder(true, 25,300);
//        tr.processLoadedFiles();
//        tr.writeProcessedToDocxFile();
    }
}

/*
    Ideen:
    + Pdfs laden und einzelne Seiten anzeigen lassen.
    - Als default sind alle Seiten ausgewählt, welche jedoch auch einzeln ausgewählt werden können.
    - Dafür müsste eine GUI erstellt werden.
    - Als kleine Vorschaubilder PDF-Einmal durchscannen und niedrig auflösende Bilder zwischenspeichern.
    - Kauderwelsch herausfiltern duden.
    + Als Worddatei ausgeben.
 */