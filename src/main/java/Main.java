public class Main {
    public static void main(String[] args) {
        TextRecognition textRecognition = TextRecognition.getInstance();

//        InputStream resourceAsStream = Main.class.getClassLoader().getResourceAsStream("pic.png");
//        String text = textRecognition.read(resourceAsStream);
//        resourceAsStream.close();
//
//        System.out.print("Hier muss was stehen:\n" + text);

        String[] read = textRecognition.read("C:\\Users\\Emulator\\IdeaProjects\\TextScanner\\src\\main\\resources\\test.pdf");
        for (int i = 0; i < read.length; i++) {
            String s = read[i];
            System.out.println("Seite " + (i + 1) + "\n" + s);
        }
    }
}
