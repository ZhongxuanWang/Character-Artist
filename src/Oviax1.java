//for basic needs
import java.io.*;
import sun.misc.BASE64Decoder;
import java.util.Scanner;
import java.util.Iterator;

//for photo process
import javax.imageio.IIOParam;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.*;
import java.awt.image.BufferedImage;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;



class Oviax1
{
    // Initialize some objects.
    public static BufferedImage img;
    public static String oviaxWS = System.getProperty("user.dir")+"/Oviax1WorkSpace/";
    // scaleChar: 70 characters (except escapes)
    public static String scaleChar = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. ";
    public static String input,fileExtension;
    public static double cpPercent;

    private static Scanner scr = new Scanner(System.in);
    private static int imgWidth, imgHeight, imgResolution;

    /* This is maximum Resolution in which application can hold. You can adjust but it's hoped not to be 
    too big otherwise your computer memory might not be able to withstand that. Of course, the bigger the
    better. But if resolution from picture that later inputted is larger than this, it will be compressed
    to this.*/
    private static int maxResolution = 10000;

    public static void main (String[] args) throws IOException
    {
        // Create work space if needed
        File oviaxWSObj = new File(oviaxWS);
        if(!oviaxWSObj.exists()) 
        {
            oviaxWSObj.mkdir();
        }

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
            // Print interface
            O.info("Please input image path below");
            System.out.print(">");

            input = scr.nextLine();

            // Get extension for further use
            fileExtension = getExtension(input);

        } while (
            // This expression means if not exist or if image is not in a supported format, redo.
            !(new File(input).exists() && picProc.checkIfPic(fileExtension))
            ); 

        // Close buffer
        scr.close();

        // Get extension for further use
        fileExtension = getExtension(input);

        /* Treat it as image file. Please see the detail in getImgBasicInfo method for better description */
        getImgBasicInfo(input);

        // Check if resolution oversized. If it's oversized, compress before continue.
        if(imgResolution > maxResolution)
        {
            /*
             Calculate the percentile in which is hoped to compress before letting imgCompress
             method to do the work
             */
            double cpPercentBfFormat = 10000 / imgResolution;
            // Format to 2-digit decimal
            String cpPercentBfFormatTemp = String.format("%.2f", cpPercentBfFormat); 
            cpPercent = Double.parseDouble(cpPercentBfFormatTemp);

            // Call the method
            picProc.imgCompress();

            // Redo the process by re-read the image file and re-get the width and height of the image.
            getImgBasicInfo(Oviax1.oviaxWS+picProc.opFileName1);
        }

        // Read each pixel and get each RGB value, proceed each one seperately.
        for (int i = 0; i < imgWidth; i++)
        {
            for (int j = 0; j < imgHeight; j++)
            {
                int rgb = img.getRGB(i, j);
                // Convert each pixel into average gray value
                img.setRGB(i, j, picProc.grayRGB(Integer.toHexString(rgb))); 
            }
        }

        // Create random number to output to prevent preoccupied.
        String opFileName2 = "grayImage_temp_" + (int) (Math.random()*2000000+1000000) + fileExtension;
        ImageIO.write(img, fileExtension, new File(oviaxWS + opFileName2));
        // Check the type of input
        
        exit(0);
    }


    /**
     * It will exit by outputting a message including the exit number. This method will override existing exit
     * method in java.lang.System.exit()
     * @param num
     */
    public static void exit(int num) 
    {
        O.info("Running finished. Thanks for using. Errorlevel:" + num);
        System.exit(0);
    }

    public static String getExtension(String path) 
    {
        /* In macOS, path contains "/" instead of "\" which is in Windows OS.
        Thus, Oviax1 is expected to only run in macOS and systems that support those variations. 
        Later compatibility in Windows may be resolved.
        */
        String fileNametp = path.split("/")[path.split("/").length-1]; // Get filename
        return fileNametp.split("\\.")[1];// Return file extension
    }

    /**
     * It will put image data in object img, and get the width, height and resolution of the image file.
     * @param path
     */
    private static void getImgBasicInfo(String path) 
    {
        try 
        {
            img = ImageIO.read(new File(path));
        } catch (IOException e) {
            O.errinfo("Sorry, read file failed");
            exit(0);
        }
        
        imgWidth = img.getWidth();
        imgHeight = img.getHeight();
        imgResolution = imgWidth * imgHeight;
    }
}



/* Some of the picture processing methods are listed here. But main procedure was 
stored in main method in nominated main class. */

class picProc
{
    public static Iterator<ImageWriter> imageWriters;
    public static String opFileName1;

    // Image compressing
    public static void imgCompress() throws FileNotFoundException,IOException
    {
        // Set output file stream with specified file name
        opFileName1 = "compressedImage_temp_" + (int) (Math.random()*2000000+1000000) + "." + Oviax1.fileExtension;
        File compressedImageFile = new File(Oviax1.oviaxWS + opFileName1);
        OutputStream opStream = new FileOutputStream(compressedImageFile);

        // Set image quality after compressing
        float imageQuality = (float)Oviax1.cpPercent;

        // Create output stream
        ImageWriter imageWriter = (ImageWriter) imageWriters.next();
        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(opStream);
        imageWriter.setOutput(imageOutputStream);

        // Set the compress quality metrics
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(imageQuality);

        // Create image
        imageWriter.write(null, new IIOImage(Oviax1.img, null, null), imageWriteParam);

        // Close all streams
        opStream.close();
        imageOutputStream.close();
        imageWriter.dispose();
    }

    /**
     * Calculate and return the most accurate grayscale (by pixel).
     * @param argb
     * @return Integer
     */
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

    /**
     * Check if the type of image (file) is accepted.
     * @param fileExt
     * @return
     */
    public static boolean checkIfPic(String fileExt) {
        imageWriters = ImageIO.getImageWritersByFormatName(fileExt);
        // Check if it has a image writer
        if (!imageWriters.hasNext()) 
        {
            O.errinfo("Sorry, the file you inputted is not supported");
            return false;
        } else {
            return true;
        }
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


/**
 * No actual use... Just like a database containing useful information...
 */
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
