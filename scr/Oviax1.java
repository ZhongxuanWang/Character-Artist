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

    public static String oviaxWS = System.getProperty("user.dir")+"/Oviax1WorkSpace";
    private static Scanner scr = new Scanner(System.in);
    private static String input = "";

    public static void main (String[] args) throws Exception
    {
        // Create work space
        new File(oviaxWS).mkdir();

        // Show UI
        O.info("Welcome to Oviax1.0");
        O.info("Input 'Oviax1 ?' on console to get more information");O.newline();

        // Reveive input from Arguments. If argument received, give it to argPro method to proceed.
        if(!args[0].equals("")) 
        {
            O.argPro(args[0].toLowerCase());
        } else {
            O.info("No Argument received so far");
        }

        // Receive input from console
        do {
            O.info("Please input image path below");System.out.print(">");
            input = scr.nextLine();
        } while (!(new File(input).exists() ) );

        // Close buffer
        scr.close();

        // Treat it as image file
        BufferedImage img = ImageIO.read(new File(input));

        // Get the total width and height of the image
        final int width = img.getWidth();
        final int height = img.getHeight();
        
        // Read each pixel and get each RGB value, proceed each one seperately.
        for (int i = 0; i < width; i++) 
        {
            for (int j = 0; j < height; j++) 
            {
                int rgb = img.getRGB(i, j);
                // Convert each pixel into average gray value
                img.setRGB(i, j, grayRGB(Integer.toHexString(rgb))); 
            }
        }

        // Output image to file.
        ImageIO.write(img,"jpeg",new File("test2.jpeg"));
        // Check the type of input
        
        
        //termination denotation.
        O.info("Oviax1 terminated.");
        
    }
    
}


// Some of the picture processing methods are listed here. But main procedure was stored in main method in nominated main class.
class picProc
{
    private static int grayRGB(String argb) {
        /* Prior two digits of ARGB are transparency, RGB began after then, in 16 hex form. */
        int r = Integer.parseInt(argb.substring(2,4),16);
        int g = Integer.parseInt(argb.substring(4,6),16);
        int b = Integer.parseInt(argb.substring(6,8),16);
        // EVEN
        String average = Integer.toHexString((r + g + b) / 3);
        if (average.length() == 1) average = "0" + average; //format to 2 units.
        // Calculate average value for ARGB
        return Integer.parseInt(average + average + average, 16);
    }


}


class O implements ConsoleColors
{
    public static void argPro(Sting[] args) 
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
