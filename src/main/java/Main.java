import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        TextRecognition textRecognition = TextRecognition.getInstance();

        InputStream resourceAsStream = Main.class.getClassLoader().getResourceAsStream("pic.png");
        String text = textRecognition.read(resourceAsStream);

        System.out.print("Hier muss was stehen:\n" + text);
    }
}
