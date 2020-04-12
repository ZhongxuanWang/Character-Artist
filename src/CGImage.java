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

    protected void compress(int height, int width) {
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

    /**
     * Output the content to GUI or Console.
     * @param isOutputToConsole whether to input to console
     */
    protected void output (
            boolean isOutputToConsole
    )
    {
        // Get customized scale (from slider)
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < height; i++) {
            str.append("\n");
            for (int j = 0; j < width; j++) {
                int scalePlace = CharGrapher.getScaleChar(CharGrapher.getGrayValue(Integer.toHexString(img.getRGB(j, i))));
                str.append(CharGrapher.cuScaleChar.substring(scalePlace, scalePlace + 1));
            }
        }
        // If its words -> CharGraph mode
        if (CharGrapher.modeBox.getSelectedIndex() == 1) {
            CharGrapher.txtOutput.append(str.toString());
        } else {
            CharGrapher.txtOutput.setText(str.toString());
        }
        if (isOutputToConsole) System.out.print(str);
    }
}