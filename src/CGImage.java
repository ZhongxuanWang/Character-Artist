import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class CGImage {
    int width,height,resolution;
    BufferedImage img;

    public CGImage(File path) {
        // Since the path here is from the trusted source(after processed and checked), exception is no need to be handled.
        try {
            img = ImageIO.read(path);
            if (img == null) {
                throw new Exception();
            }
        } catch(Exception ignored) {}
        width = img.getWidth();
        height = img.getHeight();
        resolution = width * height;
    }

    public CGImage(BufferedImage img) {
        this.img = img;
        width = img.getWidth();
        height = img.getHeight();
        resolution = width * height;
    }

    BufferedImage getImage() {
        return img;
    }

    void compress(int height, int width) {
        try {
            Image trimSize = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resized.createGraphics();
            g2d.drawImage(trimSize, 0, 0, Color.WHITE, null);
            g2d.dispose();
            img = resized;
        } catch(Exception e) {
            CharGrapher.errinfo("Sorry: " + e + " was raised. Image was unable to be proceeded");
            e.printStackTrace();
        }
        this.height = height;
        this.width = width;
    }
}