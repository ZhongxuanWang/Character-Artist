import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class CGImage extends PrintableImage {

    public CGImage(Path file) throws CamUnavailableException {
        if (ifNoPic(file)) {
            Display.errinfo("Unable to access the image");
            throw new CamUnavailableException();
        }
        try {
            img = ImageIO.read(new File(String.valueOf(file)));
        } catch (IOException e) {
            if (!CharGrapher.pyhasdestroid) {
                Display.errinfo("Unable to read the image");
                return;
            }
        }
        setInfo(img);
    }

    public CGImage(BufferedImage img) {
        setInfo(img);
    }

    private void setInfo(BufferedImage img) {
        this.img = img;
        width = img.getWidth();
        height = img.getHeight();
        resolution = width * height;
    }

    public BufferedImage getImage() {
        return img;
    }

    protected void compress() {
        double resBarValandResSqrt = Math.sqrt((double) CharGrapher.resolutionSlider.getValue() / resolution);
        int width = (int) (this.width * 1.1 * resBarValandResSqrt);
        int height = (int) (this.height * 0.8 * resBarValandResSqrt);

        try {
            Image trimSize = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resized.createGraphics();
            g2d.drawImage(trimSize, 0, 0, Color.WHITE, null);
            g2d.dispose();
            img = resized;
        } catch(Exception e) {
            Display.errinfo("Sorry: " + e + " was raised. Image was unable to be proceeded");
            e.printStackTrace();
        }
        this.height = height;
        this.width = width;
    }
}