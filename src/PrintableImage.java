import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

public class PrintableImage {
    public int width, height, resolution;
    public BufferedImage img;

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
    protected static boolean ifNoPic(Path file) {
        return !Files.exists(file);
    }
}
