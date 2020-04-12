import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class CGImage extends Display{
    public int width,height,resolution;
    private BufferedImage img;

    public CGImage(File file) {
        if (checkIfNotPic(file)) {
            errinfo("Unable to access the image");
            return;
        }
        try {
            img = ImageIO.read(file);
            assert img != null;
        } catch (Exception ignored) {
            errinfo("Unable to read the image");
            return;
        }
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
    protected void output (boolean isOutputToConsole)
    {
        // Get customized scale (from slider)
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < height; i++) {
            str.append("\n");
            for (int j = 0; j < width; j++) {
                int scalePlace = CharGrapher.getScaleChar(getGrayValue(Integer.toHexString(img.getRGB(j, i))));
                str.append(CharGrapher.cuScaleChar, scalePlace, scalePlace + 1);
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

    /**
     * Calculate and return the most accurate grayscale (by pixel).
     * @param argb argb
     * @return Integer
     */
    protected static int getGrayValue(String argb) {
        // Convert from hexadecimal to decimal and get RGB from the String.
        int r = Integer.parseInt(argb.substring(2,4), 16);
        int g = Integer.parseInt(argb.substring(4,6), 16);
        int b = Integer.parseInt(argb.substring(6,8), 16);
        // EVEN
        String average = Integer.toHexString((r + g + b) / 3);
        if (average.length() == 1) average = "0" + average; //format to 2 units.
        // Calculate average value for ARGB
        return Integer.parseInt(average, 16);
    }

    /**
     * Check if the type of image (file) is accepted.
     * @param file the picture file
     * @return boolean
     */
    protected static boolean checkIfNotPic(File file) {
        try {
            if (ImageIO.read(file) == null)
                throw new Exception("No data!");
            return false;
        } catch(Exception e) {
            Display.errinfo("Sorry, the file you inputted is not supported");
            return true;
        }
    }
}