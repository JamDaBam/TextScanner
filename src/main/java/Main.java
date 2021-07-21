public class Main {
    public static void main(String[] args) {
        TextRecognition.writeDocxFile(TextRecognition.openPdf());
//        TextRecognition.writeDocxFile(TextRecognition.openPdfBatchFolder());
    }
}

/*
    Ideen:
    - Pdfs laden und einzelne Seiten anzeigen lassen.
    - Als default sind alle Seiten ausgewählt, welche jedoch auch einzeln ausgewählt werden können.
    - Dafür müsste eine GUI erstellt werden.
    - Als kleine Vorschaubilder PDF-Einmal durchscannen und niedrigauflösende Bilder zwischenspeichern.
    - Kauderwelsch herausfiltern duden.
    + Als Worddatei ausgeben.
 */