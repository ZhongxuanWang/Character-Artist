//for basic needs
import java.io.*;
import sun.misc.BASE64Decoder;
import java.util.Scanner;

//for photo process
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;



class Oviax1
{

    public static String oviaxWS = System.getProperty("user.dir")+"/Oviax1WorkSpace/";
    private static Scanner scr = new Scanner(System.in);
    private static String input = "";
    private static int maxResolution = 10000;

    public static void main (String[] args) throws Exception
    {
        // Create work space
        new File(oviaxWS).mkdir();

        // Show UI
        O.info("Welcome to Oviax1.0");
        O.info("Input 'Oviax1 ?' on console to get more information");O.newline();

        // Reveive input from Arguments. If argument received, give it to argPro method to proceed.
        if(!(args.length == 0)) 
        {
            O.argPro(args);
        } else {
            O.info("No Argument received so far");
        }

        // Receive input from console
        do {
            O.info("Please input image path below");
            System.out.print(">");
            input = scr.nextLine();
        } while (!(new File(input).exists())); // This expression means if not exist, then redo.

        // Close buffer
        scr.close();

        // Treat it as image file
        BufferedImage img = ImageIO.read(new File(input));

        // Get the total width and height of the image
        final int width = img.getWidth();
        final int height = img.getHeight();
        
        // Check if resolution oversized. If it's oversized, compress before continue.
        if(width*height > maxResolution)
        {
            //picProc.imgCompress();
        }

        // Read each pixel and get each RGB value, proceed each one seperately.
        for (int i = 0; i < width; i++) 
        {
            for (int j = 0; j < height; j++) 
            {
                int rgb = img.getRGB(i, j);
                // Convert each pixel into average gray value
                img.setRGB(i, j, picProc.grayRGB(Integer.toHexString(rgb))); 
            }
        }

        // Create random number to output to prevent preoccupied.
        String opFileName2 = "grayImage_temp_" + (int) (Math.random()*2000000+1000000) + ".jpeg";
        ImageIO.write(img, "jpeg", new File(oviaxWS + opFileName2));
        // Check the type of input
        
        
        //termination denotation.
        O.info("Running finished. Thanks for using.");
        System.exit(9);
    }
    
}


/* Some of the picture processing methods are listed here. But main procedure was 
stored in main method in nominated main class. */

class picProc
{
    String scaleChar="$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. ";
    // Image compressing
    public static String imgCompress() 
    {

        return "a";
    }


    public static int grayRGB(String argb) 
    {
        // Convert from hexadecimal to decimal and get RGB from the String.
        int r = Integer.parseInt(argb.substring(2,4),16);
        int g = Integer.parseInt(argb.substring(4,6),16);
        int b = Integer.parseInt(argb.substring(6,8),16);
        /* Since red color has more wavelength of all the three colors, and green is the color that has 
        not only less wavelength then red color but also green is the color that gives more soothing effect 
        to the eyes. It means that we have to decrease the contribution of red color, and increase the 
        contribution of the green color, and put blue color contribution in between these two.
        */
        String gdGryScale = Long.toHexString(Math.round((r * 0.3 + g * 0.59 + b * 0.11) / 3));
        // Format to 2 units.
        if (gdGryScale.length() == 1)
        {
            gdGryScale = "0" + gdGryScale;
        }
        // Put them back into hexadecimal form.
        return Integer.parseInt(gdGryScale + gdGryScale + gdGryScale, 16);
    }


}


class O implements ConsoleColors
{
    public static void argPro(String[] args) 
    {
        switch(args[0])
        {
            case "?":
                System.out.println("Oviax - Photo to String. Internal version (0.0.0.1)");
            default:
                errinfo("Unlisted Arguments. Not proceeded");
                break;
        }
    }


    public static void info(String info)
    {
        System.out.println(GREEN + "+INFO+ - " + info + "." + RESET);
    }


    public static void errinfo(String info)
    {
        System.out.println(RED + "+ERROR+ - " + info + "." + RESET);
    }


    public static void newline() 
    {
        System.out.print("\n");
    }
}


interface ConsoleColors {
    public static final String RESET = "\033[0m";  // Text Reset
    // Regular Colors
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE
    // Underline
    public static final String BLACK_UNDERLINED = "\033[4;30m";  // BLACK
    public static final String RED_UNDERLINED = "\033[4;31m";    // RED
    public static final String GREEN_UNDERLINED = "\033[4;32m";  // GREEN
    public static final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
    public static final String BLUE_UNDERLINED = "\033[4;34m";   // BLUE
    public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
    public static final String CYAN_UNDERLINED = "\033[4;36m";   // CYAN
    public static final String WHITE_UNDERLINED = "\033[4;37m";  // WHITE
    // Background
    public static final String BLACK_BACKGROUND = "\033[40m";  // BLACK
    public static final String RED_BACKGROUND = "\033[41m";    // RED
    public static final String GREEN_BACKGROUND = "\033[42m";  // GREEN
    public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
    public static final String BLUE_BACKGROUND = "\033[44m";   // BLUE
    public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
    public static final String CYAN_BACKGROUND = "\033[46m";   // CYAN
    public static final String WHITE_BACKGROUND = "\033[47m";  // WHITE   
}
