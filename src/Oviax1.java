// For basic needs
import java.io.*;
import java.awt.*;
import java.util.Scanner;
import java.util.Iterator;

// For photo process
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.*;
import java.awt.image.BufferedImage;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import jdk.jfr.events.FileWriteEvent;
import sun.misc.BASE64Decoder;

// For the Window and the elements
import javax.swing.*;
import java.awt.event.*;
import java.awt.font.*;



class Oviax1 extends JFrame implements ActionListener
{
    /**
     *
     */
    private static final long serialVersionUID = 13414319801945L;
    // Initialize some objects.
    public static JPanel pnlObj = new JPanel();
    public static BufferedImage img, resizedImg, jpgImg;
    public static BufferedWriter bw;
    public static FileReader fr;

    public static String oviaxWS = System.getProperty("user.dir") + "/Oviax1WorkSpace/";
    public static String scaleChar = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'.          ";//70+
    public static String input, fileExtension, fileName;
    public static boolean ifJpg = false;
    public static double cpPercent;

    private static Scanner scr = new Scanner(System.in);
    private static int imgWidth, imgHeight, imgResolution;

    /* This is maximum Resolution in which application can hold. You can adjust but it's hoped not to be 
    too big otherwise your computer memory might not be able to withstand that. Of course, the bigger the
    better. But if resolution from picture that later inputted is larger than this, it will be compressed
    to this.   NOTES : it must be a decimal, which is expected to adhere a .0 at last, but not required.*/
    private static double maxResolution = 10000.0;

    // Elements in the window
    public static JButton startBtn = new JButton("start");
    public static JButton startBtn2 = new JButton("start2");
    public static JTextField txt1 = new JTextField(38);
    public static JTextArea txtOutput = new JTextArea(100, 200);
    public static JLabel filePathLable = new JLabel("Photo Path");
    public static Font txtOutputFont = new Font("Courier New", Font.PLAIN, 6);

    // Constructor for the Window
    public Oviax1()
    {
        super("Oviax1");

        // Get listening event of elements
        //startBtn2.addActionListener(this);
        startBtn.addActionListener(this);
        
        // HELPs
        startBtn.setToolTipText("Start to convert picture to string");
        txtOutput.setToolTipText("It's where the output will be");
        txt1.setToolTipText("Input your file path here");

        txtOutput.setEditable(false);
        txtOutput.setFont(txtOutputFont);
        txtOutput.setLineWrap(true);
        txtOutput.setWrapStyleWord(true);
        txt1.setDragEnabled(true);
        
        // Put those parts in the Window
        pnlObj.add(filePathLable);
        pnlObj.add(txt1);
        pnlObj.add(startBtn);
        pnlObj.add(txtOutput);
        //pnlObj.add(startBtn2);

        pack();
        setSize(700, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(pnlObj);
        setVisible(true);
        setResizable(false);
    }
    /**
     * Action Performed
     * @param ActionEvent event
     */
    public void actionPerformed(ActionEvent event)
    {
        if(event.getSource() == startBtn)
        {
            try{
                process(txt1.getText());
            } catch(IOException e)
            {
                throw new RuntimeException(e);
            }

        }
    }

    public static void main (String[] args) throws IOException,Exception
    {
        // Create work space if needed
        File oviaxWSObj = new File(oviaxWS);
        if(!oviaxWSObj.exists())
        {
            oviaxWSObj.mkdir();
        }
        
        // Show UI
        O.info("Welcome to Oviax1.0");
        O.info("Input 'Oviax1 ?' on console to get more information");
        O.newline();

        // Reveive input from Arguments. If argument received, give it to argPro method to proceed.
        if(!(args.length == 0))
        {
            O.info("Argument Received. Console buffer would initiate");
            /* Since the main theme of Oviax1 is GUI based now, argument processes are 
               expected to proceed in a differerent space 
             */
            O.argPro(args);
        } else {
            O.warninfo("No Argument received so far. Window would initiate");
            // If no argument reveived, Oviax treats it as directly starting the file. Thus, create window
            Oviax1 gui = new Oviax1();
        }
        
        
        // Receive input from console
        do {
            // Print interface
            O.info("Please input image path below");
            System.out.print(">");
            input = scr.nextLine();
        } while (
            // This expression means if not exist, redo.
            !(new File(input).exists())
            ); 
        
        
        // Create elements in the window
        //buildEleOfWindow();
        // Create 


        // Close buffer
        scr.close();
        
        process(input);
        //exit(0);
    }


    public static void process(String input) throws IOException
    {
        // Check elligibility and get filename
        picProc.checkIfPic(getExtension(input));

        /* Treat it as image file and give image data to bufferedimage type img. */
        getImgBasicInfo(input);

        // Check if resolution oversized. If it's oversized, compress before continue.
        if(imgResolution > maxResolution)
        {
            /*
             Calculate the percentile in which is hoped to compress before letting imgCompress
             method to do the work. It's a logarithem.
             */
            {
                double x = maxResolution / imgResolution;
                x = Math.sqrt(x);
                imgHeight = (int) (imgHeight * x);
                imgWidth = (int) (imgWidth * x);
                // Debug print.
                System.out.println(imgHeight + " - " + imgWidth);
            }

            // Call the method
            resizedImg = picProc.imgCompress(imgHeight, imgWidth);
            // Re declare object and reget information
            getImgBasicInfo(resizedImg);
        }
        // For those who are not jpg or jpeg format.
        if(!ifJpg)
        {
            // Create a blank, RGB, same width and height, and a white background
            jpgImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
            jpgImg.createGraphics().drawImage(img, 0, 0, Color.WHITE, null);
        }
        
        // Set file writer
        String opFileName1 = fileName + "_PLAIN_STRING_CONTENT" + (int) (Math.random() * 2000000 + 1000000) + ".txt";
        try {
            bw = new BufferedWriter(new FileWriter(opFileName1));
        } catch (IOException e) {
            O.errinfo("Sorry, Oviax1 unables to create a buffer to output file");
            exit(1);
        }
        // Read each pixel and get each RGB value, proceed each one seperately.
        for (int i = 0; i < imgHeight; i++)
        {
            output("");
            for (int j = 0; j < imgWidth; j++)
            {
                int rgb = jpgImg.getRGB(j, i);
                // Convert each pixel into average gray value
                //jpgImg.setRGB(i, j, picProc.getGrayValue(Integer.toHexString(rgb))); 
                int scalePlace = picProc.getScaleChar(picProc.getGrayValue(Integer.toHexString(rgb)));
                output(scaleChar.split("")[scalePlace]);
            }
        }

        // Close buffer & end
        bw.close(); // Courier New is the most accurate display font discovered so far.
        O.newline();

        // Lauch File
        Desktop.getDesktop().open(new File(opFileName1));
    }

    /**
     * Output to file, console, textArea based on setting and conditions.
     */
    public static void output(String str) throws IOException
    {
        if(str.length() == 0)
        {
            bw.newLine();
            O.newline();

            return;
        }
        bw.write(str);
        System.out.print(str);
        txtOutput.append(str);
        return;
    }


    /**
     * It will exit by outputting a message including the exit number. 
     * This method will override existing exit method in java.lang.System.exit()
     * @param num
     */
    public static void exit(int num) 
    {
        O.info("Running finished. Thanks for using. Error:" + num);
        System.exit(0);
    }

    public static String getExtension(String path) 
    {
        /* In macOS, path contains "/" instead of "\" which is in Windows OS.
        Thus, Oviax1 is expected to only run in macOS and systems that support those 
        variations. Later compatibility in Windows may be resolved.
        */
        fileName = path.split("/")[path.split("/").length - 1]; // Get filename
        String fEx;
        try {
            fEx=fileName.split("\\.")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            O.warninfo("It is expected to input a path containing file extension. 'png' is attached by default");
            return "png";
        }
        if((fEx.toLowerCase().equals("jpg") && fEx.toLowerCase().equals("jpeg")))
            ifJpg = true;
        return fileName.split("\\.")[1];// Return file extension
    }

    /**
     * It will put image data in object img, and get the width, height and resolution 
     * of the image file.
     * @param path
     */
    private static void getImgBasicInfo(String path) throws IOException
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
        return;
    }

    private static void getImgBasicInfo(BufferedImage bufferedImg) throws IOException
    {
        img = resizedImg;
        imgWidth = bufferedImg.getWidth();
        imgHeight = bufferedImg.getHeight();
        imgResolution = imgWidth * imgHeight;
        return;
    }

}



/* Some of the picture processing methods are listed here. But main procedure was 
stored in main method in nominated main class. */

class picProc
{
    public static Iterator<ImageWriter> imageWriters;

    // Image to smaller size
    public static BufferedImage imgCompress(int height, int width) throws IOException
    {
        // Put it to origin
        Oviax1.ifJpg = false;
        Image trimSize = Oviax1.img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(trimSize, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    /**
     * Calculate and return the most accurate grayscale (by pixel).
     * @param argb
     * @return Integer
     */
    public static int getGrayValue(String argb) 
    {
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
     * Method will return the position+1 of array in integer 
     */
    public static int getScaleChar(int grayValue) 
    {
        // grayValue will vary from 0 to 255, which is from pure black to pure white.
        int placement = (int) (grayValue / 3.24686);
        return placement;
    }

    /**
     * Check if the type of image (file) is accepted.
     * @param fileExt
     * @return
     */
    public static void checkIfPic(String fileExt) throws IOException
    {
        imageWriters = ImageIO.getImageWritersByFormatName(fileExt);
        // Check if it has a image writer
        if (!imageWriters.hasNext()) 
        {
            O.errinfo("Sorry, the file you inputted is not supported");
            Oviax1.exit(1);
        }
        return;
    }
}



class O implements ConsoleColors
{
    public static void argPro(String[] args) 
    {
        //it is expected to have the file path in the argument.
        switch(args[0])
        {
            case "?":
                System.out.println("Oviax - Photo to String. Internal version (0.0.0.1)");
            default:
                errinfo("Unlisted Arguments. Not proceeded");
                break;
        }
        return;
    }

    public static void info(String info)
    {
        System.out.println(GREEN + "+INFO+ - " + info + "." + RESET);
        return;
    }


    public static void errinfo(String info)
    {
        System.out.println(RED + "+ERROR+ - " + info + "." + RESET);
        return;
    }


    public static void warninfo(String info)
    {
        System.out.println(YELLOW + "+WARNING+ - " + info + "." + RESET);
        return;
    }


    public static void newline() 
    {
        System.out.print("\n");
        return;
    }
}


/**
 * No actual use... Just like a database containing useful information...
 */
interface ConsoleColors 
{
    public static final String RESET = "\033[0m";  // Text Reset
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
}
