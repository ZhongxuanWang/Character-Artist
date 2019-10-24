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


class SharpStrGrapher extends JFrame implements ActionListener
{
    private static final long serialVersionUID = 13414319801945L;
    private static final String Version = "0.1.2.1"; // Inner Version   Major.Minor.[Maintanence.[Build]]
    // Initialize some objects.
    public static JPanel pnlObj = new JPanel();
    public static BufferedImage img, resizedImg, jpgImg;
    public static BufferedWriter bw;
    public static FileReader fr;

    public static String oviaxWS = new File("").getAbsoluteFile().toString() + "/SharpStrGrapherWorkSpace/";
    public static String cuScaleChar,opFileName1 = "";
    public static String[] helpTips = {
        "Start to convert picture to string.",
        "It's where the output will be.",
        "Input your file path here. <required>",
        "Output to a txt file also. You don't need to specify the txt name. <optional>",
        "Select a converting mode to continue. <required>",
        "Reset all the field in this window to their default.",
        "The variaties of characters included in the output text. '5' means keep origin. '0' means only output two variaties. (color and no color) <optional>"
    };
    public static String[] modes = {
        "Photo --> StrGraph",
        "Characters --> StrGraph",
        "Camera --> StrGraph"
    };
    public static String[] labels = {
        "Photo Path:",
        "Complexity:",
        "Characters:",
        "Resolution:"
    };
    public static String scaleChar = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'.           ";
    public static String input, fileExtension, fileName;
    public static boolean ifJpg = false;
    public static boolean spaceTest = true;
    public static double cpPercent;

    private static Scanner scr = new Scanner(System.in);
    private static int imgWidth, imgHeight, imgResolution;


    /* This is maximum Resolution in which application can hold. You can adjust but it's hoped not to be 
    too big otherwise your computer memory might not be able to withstand that. Of course, the bigger the
    better. But if resolution from picture that later inputted is larger than this, it will be compressed
    to this.   NOTES : it must be a decimal, which is expected to adhere a .0 at last, but not required.
    <IDE_SET_Support>
    */
    public static double maxResolution = 20000.0;

    /* Even though this is recommended to trun on every time and very useful, for some very very special 
    circumstances, the improvement doesn't work and would probably distort the original photo, or, user 
    doesn't want to use this function. You can change it to false if you want. <IDE_SET_Support>     */
    private static boolean isCutUpSpacePart = true;

    /* This could be adjusted in GUI. The default action is false. <IDE_AND_GUI_SET_Support> */
    public static boolean isOutputToTxt = false;


    // Elements in the window
    public static JButton startBtn = new JButton("Start");
    public static JButton resetBtn = new JButton("Reset All");
    public static JCheckBox chk1 = new JCheckBox("To TXT File");
    public static JTextField txt1 = new JTextField(38);
    public static JTextArea txtOutput = new JTextArea(120, 265);
    public static JLabel inputLable = new JLabel(labels[0]);
    public static JLabel sliderLable = new JLabel(labels[1]);
    public static JLabel sliderLable2 = new JLabel(labels[3]);
    public static Font txtOutputFont = new Font("Courier New", Font.PLAIN, 5);
    public static JComboBox<String> modeBox = new JComboBox<String>(modes);
    public static JSlider wordComplexitySlider = new JSlider(JSlider.HORIZONTAL,0,5,5);
    public static JSlider resolutionSlider = new JSlider(JSlider.HORIZONTAL,2,(int)maxResolution,(int)maxResolution);
    public static JScrollPane areaScrollPane = new JScrollPane(txtOutput);



    // Constructor for the Window
    public SharpStrGrapher()
    {
        super("AccurateStrGrapher 1.0");

        // Get listening event of elements
        //startBtn2.addActionListener(this);
        startBtn.addActionListener(new ActionListener(){
            public void actionPerformed (ActionEvent ev) 
            {
                txtOutput.setText("");
                try{
                    process(txt1.getText());
                } catch(IOException e){}
                return;
            }
        });

        resetBtn.addActionListener(new ActionListener(){
            public void actionPerformed (ActionEvent e)
            {
                txt1.setText("");
                txtOutput.setText("");
                chk1.setSelected(isOutputToTxt);
                modeBox.setSelectedItem(modes[0]);
                wordComplexitySlider.setValue(10);
                return;
            }
        });

        chk1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                isOutputToTxt = !isOutputToTxt;
                return;
            }
        });
        
        // Hit Enter to process
        txt1.addKeyListener(new KeyListener(){
            public void keyPressed (KeyEvent e) {}
            public void keyTyped (KeyEvent e) {}
            public void keyReleased (KeyEvent ev)
            {
                if(ev.getKeyCode() == 10)
                {
                    try {
                        process(txt1.getText());
                    } catch (IOException e) {}
                }
                return;
            }
        });

        // Select Mode
        modeBox.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e)
            {
                if(e.getStateChange() == ItemEvent.SELECTED)
                {
                    if(modeBox.getSelectedItem().toString().equals(modes[0]))
                        inputLable.setText(labels[0]);

                    if(modeBox.getSelectedItem().toString().equals(modes[1]))
                        inputLable.setText(labels[2]);
                }
                return;
            }
        });

        // HELPs
        startBtn.setToolTipText(helpTips[0]);
        txtOutput.setToolTipText(helpTips[1]);
        txt1.setToolTipText(helpTips[2]);
        chk1.setToolTipText(helpTips[3]);
        modeBox.setToolTipText(helpTips[4]);
        resetBtn.setToolTipText(helpTips[5]);
        wordComplexitySlider.setToolTipText(helpTips[6]);
        sliderLable.setToolTipText(helpTips[6]);

        txtOutput.setEditable(true);
        txtOutput.setFont(txtOutputFont);
        txtOutput.setLineWrap(true);
        txtOutput.setWrapStyleWord(true);
        txt1.setDragEnabled(true);
        wordComplexitySlider.setMajorTickSpacing(1);
        wordComplexitySlider.setSnapToTicks(true);;
        wordComplexitySlider.setPaintTicks(true);
        wordComplexitySlider.setPreferredSize(new Dimension(130,30));
        resolutionSlider.setPreferredSize(new Dimension(130,30));
        chk1.setSelected(isOutputToTxt);
        areaScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(800, 800));
        areaScrollPane.setAutoscrolls(true);

        // Put those parts in the Window / Must be sequence!

        pnlObj.add(inputLable);
        pnlObj.add(txt1);
        pnlObj.add(startBtn);
        pnlObj.add(resetBtn);

        pnlObj.add(sliderLable2);
        pnlObj.add(resolutionSlider);
        pnlObj.add(sliderLable);
        pnlObj.add(wordComplexitySlider);
        pnlObj.add(chk1);
        pnlObj.add(modeBox);
        pnlObj.add(areaScrollPane);
        //pnlObj.add(txtOutput);

        pack();
        setSize(800, 830); // Size of Window. Adjustable
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(pnlObj);
        setResizable(false);
        //setAlwaysOnTop(true);
        setVisible(true);
    }

    public static void main (String[] args) throws IOException
    {
        // Create work space if needed
        File oviaxWSObj = new File(oviaxWS);
        if(!oviaxWSObj.exists())
            oviaxWSObj.mkdir();

        // Show UI
        O.info("Welcome to SharpStrGrapher.0");
        O.info("Input 'SharpStrGrapher ?' on console to get more information");
        O.newline();

        // Reveive input from Arguments. If argument received, give it to argPro method to proceed.
        if(!(args.length == 0))
        {
            O.info("Argument Received. Console buffer would initiate");
            /* Since the main theme of SharpStrGrapher is GUI based now, argument processes are 
               expected to proceed in a differerent space 
             */
            O.argPro(args);
        } else {
            O.warninfo("No Argument received so far. Window would initiate");
            // If no argument reveived, Oviax treats it as directly starting the file. Thus, create window
            SharpStrGrapher gui = new SharpStrGrapher();
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
        // Close buffer
        scr.close();
        
        process(input);
    }


    public static void process(String input) throws IOException
    {
        if(!new File(input).exists()) 
        {
            O.errinfo("Sorry, file you inputted is not exist");
            return;
        }
        // Check elligibility and get filename
        if(!picProc.checkIfPic(getExtension(input))) return;

        /* Treat it as image file and give image data to bufferedimage type img. */
        getImgBasicInfo(input);
        // Check if resolution oversized. If it's oversized, compress before continue.
        maxResolution = resolutionSlider.getValue();
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
        
        if(isOutputToTxt)
        {
            opFileName1 = fileName + "_PLAIN_STRING_CONTENT" + (int) (Math.random() * 2000000 + 1000000) + ".txt";
            try {
                bw = new BufferedWriter(new FileWriter(oviaxWS + opFileName1));
            } catch (IOException e) {
                O.errinfo("Sorry, SharpStrGrapher unables to create a buffer to output file");
                return;
            }
        }

        cuScaleChar = getCusScale();

        String speChar = "";

        // Read each pixel and get each RGB value, proceed each one seperately.
        for (int i = 0; i < imgHeight; i++)
        {
            // Ignore the rule if cutup is turned down
            if(!isCutUpSpacePart) output("");
            if(!spaceTest) output("");
            for (int j = 0; j < imgWidth; j++)
            {
                int rgb = jpgImg.getRGB(j, i);
                // Convert each pixel into average gray value
                int scalePlace = picProc.getScaleChar(picProc.getGrayValue(Integer.toHexString(rgb)));
                speChar = cuScaleChar.split("")[scalePlace];
                // If it's not a space, then stop not inputting
                if(isCutUpSpacePart && spaceTest && !(speChar.equals(" "))) {
                    spaceTest = false;
                    output("");
                }
                output(speChar);
            }
        }

        // Close buffer & end
        if(isOutputToTxt)
            bw.close(); // Courier New is the most accurate display font discovered so far.
        O.newline();

        // Lauch File
        if(isOutputToTxt)
            Desktop.getDesktop().open(new File(oviaxWS+opFileName1));
    }

    /**
     * Output to file, console, textArea based on setting and conditions.
     */
    public static void output(String str) throws IOException
    {
        if(str.length() == 0)
        {
            // Console Output - Uncontrollable
            O.newline();

            if(isOutputToTxt)
                // File Output - Controllable
                bw.newLine();

            // GUI output - Controllable (set it in the field part)
            txtOutput.append("\n");
            return;
        }
        if(isOutputToTxt)
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
        Thus, SharpStrGrapher is expected to only run in macOS and systems that support those 
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

    public static String getCusScale()
    {
        switch(wordComplexitySlider.getValue())
        {
            case 0:
                return "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$                                         ";
            case 1:
                return "&&&&&WWWWWdddddpppppCCCCCJJJJJxxxxxrrrrr11111{{{{{<<<<<>>>>>^^^^^`````            ";
            case 2:
                return "%%%%8&WWWWkkkkbdppppQQQQLCJJJJuuuunxrrrr(((()1{{{{++++~<>>>>,,,,\"^````           ";
            case 3:
                return "BBB%8&WMMMhhhkbdpqqq000QLCJUUUvvvunxrjjj|||()1{}}}___+~<>iii:::,\"^`'''           ";
            case 4:
                return "@@B%8&WM##aahkbdpqwwOO0QLCJUYYccvunxrjff\\\\|()1{}[[--_+~<>i!!;;:,\"^`'..          ";
            case 5:
                return scaleChar;
        }
        return "ERRORERRORERRORERRORERRORERRORERRORERRORERRORERRORERRORERRORERRORERRORERRORERROR";
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
            return;
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
        SharpStrGrapher.ifJpg = false;
        Image trimSize = SharpStrGrapher.img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
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
        int placement = Math.round((float) (grayValue / 3.24686));
        return placement;
    }

    /**
     * Check if the type of image (file) is accepted.
     * @param fileExt
     * @return
     */
    public static boolean checkIfPic(String fileExt) throws IOException
    {
        imageWriters = ImageIO.getImageWritersByFormatName(fileExt);
        // Check if it has a image writer
        if (!imageWriters.hasNext()) 
        {
            O.errinfo("Sorry, the file you inputted is not supported");
            return false;
        }
        return true;
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
                warninfo("Unlisted Arguments. Not proceeded. Input '?' after filename to get help");
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
        JOptionPane.showMessageDialog(null, info + ".", "An Error Occurs", JOptionPane.ERROR_MESSAGE);
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
 * Some colors for displaying in console
 */
interface ConsoleColors
{
    public static final String RESET = "\033[0m";  // Text Reset
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
}
