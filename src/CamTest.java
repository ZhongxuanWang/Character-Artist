import com.github.sarxos.webcam.*;

import java.awt.image.BufferedImage;

public class CamTest {
    public static void main(String[] args) {
        Webcam.setAutoOpenMode(true);
        Webcam webcam = Webcam.getDefault();
        webcam.open();


        for (int i = 0; i < 5; i++) {
            if (webcam.isImageNew()) {
                System.out.println("webcam has image");
            }
            BufferedImage bufferedImage = webcam.getImage();
            System.out.println("BufferedImage " + bufferedImage);
        }

        long t1;
        long t2;

        int p = 10;
        int r = 5;

        for (int k = 0; k < p; k++) {

            webcam.open();
            webcam.getImage();

            t1 = System.currentTimeMillis();
            for (int i = 0; ++i <= r; webcam.getImage()) {
            }
            t2 = System.currentTimeMillis();

            System.out.println("FPS " + k + ": " + (1000 * r / (t2 - t1 + 1)));

            webcam.close();
        }

    }
}