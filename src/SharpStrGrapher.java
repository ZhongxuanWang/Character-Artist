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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;
import java.awt.event.*;
import java.awt.font.*;


class SharpStrGrapher extends JFrame implements ActionListener
{
    private static final long serialVersionUID = 1L;
    private static final String Version = "0.2.1"; // Inner Version
    // Initialize some objects.
    public static JPanel pnlObj = new JPanel();
    public static Iterator<ImageWriter> imageWriters;
    public static BufferedImage img, resizedImg, jpgImg;
    public static BufferedWriter bw;
    public static FileReader fr;
    public static Process pylaunch;

    public static String scaleChar = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'.           ";
    public static String input, fileExtension, fileName;
    public static String ssgWS = new File("").getAbsoluteFile().toString() + "/SharpStrGrapherWorkSpace/";
    public static String pyfilename = ssgWS + "SSshoter.py";
    public static String cuScaleChar,opFileName1 = "";
    public static String[] helpTips = {
        "Start to convert picture to string.",
        "It's where the output will be.",
        "Input your file path here. <required>",
        "Output to a txt file als You don't need to specify the txt name. <optional>",
        "Select a converting mode to continue. <required>",
        "Reset all the field in this window to their default.",
        "The variaties of characters included in the output text. '5' means keep origin. '0' means only output two variaties. (color and no color) <optional>",
        "Capture this moment and output the text into txt file."
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

    public static int imgWidth, imgHeight, imgResolution;

    private static boolean ifJpg = false;
    private static boolean spaceTest = true;
    public static boolean pyhasdestroid = false;
    private static Scanner scr = new Scanner(System.in);


    /* This is maximum Resolution in which application can hold. You can adjust but it's hoped not to be 
    too big otherwise your computer memory might not be able to withstand that. Of course, the bigger the
    better. But if resolution from picture that later inputted is larger than this, it will be compressed
    to this.   NOTES : it must be a decimal, which is expected to adhere a .0 at last, but not required.
    <IDE_SET_Support>
    */
    public static double maxResolution = 20000.0;

    /* Even though this is recommended to trun on every time and very useful, for some very very special 
    circumstances, the improvement doesn't work and would probably distort the original photo, or, user 
    doesn't want to use this function. You can change it to false if you want. <IDE_SET_Support>     
     NOTICE : This function is not applicable in Cam2Str mode in order to improve speed                */
    private static boolean isCutUpSpacePart = true;

    /* This could be adjusted in GUI. The default action is false. <IDE_AND_GUI_SET_Support> */
    public static boolean isOutputToTxt = false;


    // Elements in the window
    public static JButton startBtn = new JButton("Start");
    public static JButton resetBtn = new JButton("Reset All");
    public static JButton optxtBtn = new JButton("Capture");
    public static JCheckBox chk1 = new JCheckBox("To TXT File");
    public static JTextField txt1 = new JTextField(38);
    public static JTextArea txtOutput = new JTextArea(120, 265);
    public static JLabel inputLable = new JLabel(labels[0]);
    public static JLabel sliderLable = new JLabel(labels[1]);
    public static JLabel sliderLable2 = new JLabel(labels[3]);
    public static Font txtOutputFont = new Font("Courier New", Font.PLAIN, 5);
    public static JComboBox<String> modeBox = new JComboBox<String>(modes);
    public static JSlider wordComplexitySlider = new JSlider(JSlider.HORIZONTAL, 0, 6, 6);
    public static JSlider resolutionSlider = new JSlider(JSlider.HORIZONTAL, 2, (int) maxResolution, (int) maxResolution);
    public static JScrollPane areaScrollPane = new JScrollPane(txtOutput);


    // Constructor for the Window
    public SharpStrGrapher()
    {
        super("Sharp String Grapher 1.0");

        // Get listening event of elements
        //startBtn2.addActionListener(this);

                // Hit Enter to process
        txt1.addKeyListener(new KeyListener(){
            public void keyPressed (KeyEvent e) {}
            public void keyTyped (KeyEvent e) {}
            public void keyReleased (KeyEvent ev)
            {
                // 10 represents Enter. Thus, if user hits enter, then process began
                if(ev.getKeyCode() == 10)
                {
                    try {
                        process(txt1.getText());
                    } catch (IOException e) {}
                }
                return;
            }
        });

        startBtn.addActionListener(new ActionListener(){
            public void actionPerformed (ActionEvent ev) 
            {
                txtOutput.setText("");
                if(modeBox.getSelectedItem().toString().equals(modes[2]))
                {
                    // Capture and Continue process
                    if(startBtn.getText().equals("Pause"))
                    {
                        pydestroy();
                        startBtn.setText("Continue");
                        return;
                    } else {
                        // If the button printed "continue"
                        if(!pyLaunch()) return;
                        startBtn.setText("Pause");
                    }
                    camModeProcess();
                    return;
                }
                try{
                    process(txt1.getText());
                } catch(IOException e){}
                return;
            }
        });

        resetBtn.addActionListener(new ActionListener(){
            public void actionPerformed (ActionEvent e)
            {
                // Reset all the fields to their default status
                txt1.setText("");
                txtOutput.setText("");
                chk1.setSelected(isOutputToTxt);
                wordComplexitySlider.setValue(6);
                resolutionSlider.setValue((int)maxResolution); 
                return;
            }
        });

        resolutionSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                // Set max resolution
                maxResolution = (double) wordComplexitySlider.getValue();
            }
        });

        wordComplexitySlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                // Inform the method to change the value
                cuScaleChar =  SharpStrGrapher.getCusScale();
            }
        });

        optxtBtn.addActionListener(new ActionListener(){
            public void actionPerformed (ActionEvent e)
            {
                String opFileName = fileName + "_PLAIN_STRING_CONTENT_FROM_CAM"
                 + (int) (Math.random() * 2000000 + 1000000) + ".txt";
                // Directly call the method to output.
                try {
                    bw = new BufferedWriter(new FileWriter(ssgWS + opFileName));
                    /* Avoid when user pressed the "capture", the software was 
                       refreshing its field  */
                    String str = txtOutput.getText();
                    while(str.length()==0)
                        str = txtOutput.getText();
                    bw.write(str);
                    bw.close();
                    Desktop.getDesktop().open(new File(ssgWS+opFileName));
                } catch (IOException er) {}
            }
        });

        chk1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) 
            {
                isOutputToTxt = !isOutputToTxt;
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
                    {   
                        // Destroy previous launched python script.
                        pydestroy();

                        resolutionSlider.setVisible(true);
                        inputLable.setVisible(true);
                        txt1.setVisible(true);
                        chk1.setVisible(true);
                        optxtBtn.setVisible(false);
                        startBtn.setText("Start");

                        inputLable.setText(labels[0]);
                    }
                    
                    if(modeBox.getSelectedItem().toString().equals(modes[1]))
                    {
                        // Destroy previous launched python script.
                        pydestroy();

                        resolutionSlider.setVisible(true);
                        inputLable.setVisible(true);
                        txt1.setVisible(true);
                        chk1.setVisible(true);
                        optxtBtn.setVisible(false);
                        startBtn.setText("Start");

                        inputLable.setText(labels[2]);
                    }
                    
                    if(modeBox.getSelectedItem().toString().equals(modes[2]))
                    {
                        // Destroy previous launched python script.
                        pydestroy();

                        resolutionSlider.setVisible(true);
                        inputLable.setVisible(false);
                        txt1.setVisible(false);
                        chk1.setVisible(false);
                        optxtBtn.setVisible(true);
                        startBtn.setText("Pause");

                        // Lauch the multithread.
                        camModeProcess();
                    }
                }
                return;
            }
        });

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                // Destroy the py thread before closing the window
                pydestroy();
                System.exit(0);
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
        optxtBtn.setToolTipText(helpTips[7]);

        txtOutput.setEditable(true);
        txtOutput.setFont(txtOutputFont);
        // Set auto wrap
        txtOutput.setLineWrap(true);
        txtOutput.setWrapStyleWord(true);
        // Set btn not visible but in the content.
        optxtBtn.setVisible(false);
        // Set effect of sliders: having tick and space & size
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
        cuScaleChar = getCusScale();

        // Put those parts in the Window / Must be sequence!

        pnlObj.add(inputLable);
        pnlObj.add(txt1);
        pnlObj.add(startBtn);
        pnlObj.add(resetBtn);

        pnlObj.add(sliderLable2);
        pnlObj.add(resolutionSlider);
        pnlObj.add(sliderLable);
        pnlObj.add(wordComplexitySlider);
        pnlObj.add(optxtBtn);
        pnlObj.add(chk1);
        pnlObj.add(modeBox);
        pnlObj.add(areaScrollPane);
        //pnlObj.add(txtOutput);

        pack();
        setSize(800, 830); // Size of Window. Adjustable
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        add(pnlObj);
        setResizable(false);
        //setAlwaysOnTop(true);
        setVisible(true);
    }

    public static void main (String[] args) throws IOException
    {
        // Create work space if needed
        File ssgWSObj = new File(ssgWS);
        if(!ssgWSObj.exists())
            ssgWSObj.mkdir();

        // Show UI
        info("Welcome to SharpStrGrapher 1.0");
        info("Input 'SharpStrGrapher ?' on console to get more information");
        newline();

        // Reveive input from Arguments. If argument received, give it to argPro method to proceed.
        if(!(args.length == 0))
        {
            info("Argument Received. Console buffer would initiate");
            /* Since the main theme of SharpStrGrapher is GUI based now, argument processes are 
               expected to proceed in a differerent space 
             */
            argPro(args);
        } else {
            warninfo("No Argument received so far. Window would initiate");
            // If no argument reveived, Oviax treats it as directly starting the file. Thus, create window
            SharpStrGrapher gui = new SharpStrGrapher();
        }

        // Receive input from console
        do {
            // Print interface
            info("Please input image path below");
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
            errinfo("Sorry, file you inputted does not exist");
            return;
        }
        // Check elligibility and get filename
        if(!checkIfPic(getExtension(input))) return;

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
                imgHeight = (int) (imgHeight * x * 0.8);
                imgWidth = (int) (imgWidth * x) ;
                // Debug print.
                System.out.println(imgHeight + " - " + imgWidth);
            }

            // Call the method
            resizedImg = imgCompress(imgHeight, imgWidth);
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
                bw = new BufferedWriter(new FileWriter(ssgWS + opFileName1));
            } catch (IOException e) {
                errinfo("Sorry, SharpStrGrapher is unable to create a buffer to output file");
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
                int scalePlace = getScaleChar(getGrayValue(Integer.toHexString(rgb)));
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
        newline();

        // Lauch File
        if(isOutputToTxt)
            Desktop.getDesktop().open(new File(ssgWS + opFileName1));
    }

// - - - - - - - - - - - - - - - - - - - - I M G   P R O C E S S - - - - - - - - - - - - - - - - - -
// - - - - - - - - - - - - - - - - - - - - I M G   P R O C E S S - - - - - - - - - - - - - - - - - -
// - - - - - - - - - - - - - - - - - - - - I M G   P R O C E S S - - - - - - - - - - - - - - - - - -

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
        return Math.round((float) (grayValue / 3.24686));
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
            errinfo("Sorry, the file you inputted is not supported");
            return false;
        }
        return true;
    }


// - - - - - - - - - - - - - - - - - - - - - O T H E R - - - - - - - - - - - - - - - - - - - - - - -
// - - - - - - - - - - - - - - - - - - - - - O T H E R - - - - - - - - - - - - - - - - - - - - - - -
// - - - - - - - - - - - - - - - - - - - - - O T H E R - - - - - - - - - - - - - - - - - - - - - - -


    public static void camModeProcess()
    {                        
        if(!buildPy())
            return; // If building failed, stop building.
        Thread snapshotpy = new Thread(new Snapshotpy());
        snapshotpy.start();
    }

    public static void pydestroy()
    {
        pyhasdestroid = true;
        try{
            pylaunch.destroy();
        }catch(Exception e){}
    }

    /**
     * Output to file, console, textArea based on setting and conditions.
     */
    public static void output(String str) throws IOException
    {
        if(str.length() == 0)
        {
            // Console Output - Uncontrollable
            newline();

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
        info("Running finished. Thanks for using. Error:" + num);
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
            warninfo("It is expected to input a path containing file extension. 'png' is attached by default");
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
            // Adjustable according to own preferences.
            case 0:
                return "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$                                         ";
            case 1:
                return "WWWWWWWWWWWWWWWCCCCCCCCCCCCCCCrrrrrrrrrr{{{{{{{{{{>>>>>>>>>>``````````            ";
            case 2:
                return "&&&&&WWWWWdddddpppppCCCCCJJJJJxxxxxrrrrr11111{{{{{<<<<<>>>>>^^^^^`````            ";
            case 3:
                return "%%%%8&WWWWkkkkbdppppQQQQLCJJJJuuuunxrrrr(((()1{{{{++++~<>>>>,,,,\"^`````          ";
            case 4:
                return "BBB%8&WMMMhhhkbdpqqq000QLCJUUUvvvunxrjjj|||()1{}}}___+~<>iii:::,\"^''''`          ";
            case 5:
                return "@@B%8&WM##aahkbdpqwwOO0QLCJUYYccvunxrjff\\\\|()1{}[[--_+~<>i!!;;:,\"^`'..          ";
            case 6:
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
            errinfo("Sorry, read file failed");
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

    public static boolean buildPy()
    {
        //"        imwrite(\"SSGSHOTS_\" + \"" + "" + "\" + \".jpg\",img)\n"
        try
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(new File(pyfilename)));
            String prg = "from cv2 import *\n" +
            "import time\n"+
            "os.chdir(\""+ssgWS+"\")\n"+
            "while(True):\n"+
            /* Sleep(seconds) 0.1 is recommended. If the speed of read and write of your disk
               is obnormally low, you could adjust this value higher but you will experience 
               long delays.
               It means the delay in which python takes photo. */
            "    time.sleep("+ 0.1 +")\n"+ 
            "    cam = VideoCapture(0)\n"+
            "    rep, img = cam.read()\n"+
            "    imwrite(\"SSGSHOTS_IMG.jpg\",img)\n";
            out.write(prg);
            out.close();
        } catch (Exception e) {
            errinfo("Sorry, unable to output file. " + e.toString());
            return false;
        }

        pyhasdestroid = false;
        return pyLaunch();
    }

    public static boolean pyLaunch()
    {
        try
        {
            pylaunch = Runtime.getRuntime().exec("python3 " + pyfilename);
        }
        catch (Exception e) 
        {
            errinfo("Sorry, unable to Launch python3. Please install the environment first." + e.toString());
            return false;
        }
        return true;
    }

    public static void argPro(String[] args) 
    {
        //it is expected to have the file path in the argument.
        switch(args[0])
        {
            case "?":
                System.out.println("SharpStrGrapher - Photo to String. Internal version (0.0.0.1)");
            default:
                warninfo("Unlisted Arguments. Not proceeded. Input '?' after filename to get help");
                break;
        }
        return;
    }

// - - - - - - - - - - - - - - - - - - - - F O R   D I S P L A Y - - - - - - - - - - - - - - - - - -
// - - - - - - - - - - - - - - - - - - - - F O R   D I S P L A Y - - - - - - - - - - - - - - - - - -
// - - - - - - - - - - - - - - - - - - - - F O R   D I S P L A Y - - - - - - - - - - - - - - - - - -

    public static void info(String info)
    {
        System.out.println("\033[0;32m" + "+INFO+ - " + info + "." + "\033[0m");
        return;
    }

    public static void errinfo(String info)
    {
        System.out.println("\033[0;31m" + "+ERROR+ - " + info + "." + "\033[0m");
        JOptionPane.showMessageDialog(null, info + ".", "An Error Occurs", JOptionPane.ERROR_MESSAGE);
        return;
    }

    public static void warninfo(String info)
    {
        System.out.println("\033[0;33m" + "+WARNING+ - " + info + "." + "\033[0m");
        return;
    }

    public static void newline() 
    {
        System.out.print("\n");
        return;
    }
}


/* Some of the picture processing methods are listed here. But main procedure was 
stored in main method in nominated main class. */

class Snapshotpy implements Runnable 
{
    // TO FIND REFERENCES THAT HELP YOU TO READ THE CODE, GO TO PROCESS METHOD IN MAIN(SIGNIFICANT) CLASS.
    // YOU WILL FIND EXACTLY THE SAME CODE THEIR.
    public static BufferedImage bufferedImg;

    public static String str = ""; // The string of graph
    public static int imgWidth, imgHeight, imgResolution;
    public static double x = 0;
    public static String imgPath = SharpStrGrapher.ssgWS + "SSGSHOTS_IMG.jpg";
    public static File imgfile = new File(imgPath);
    
    public void run()
    {
        // If python script task is alive, then continue to output. 
        while(!SharpStrGrapher.pyhasdestroid)
        {
            try{
               /* Thread.leep(MS) 100ms = 0. This means the delay in which the
                software read image from the disk. If the value is lower, it may
                result in exceeding disk consumption. Vice versa. */
                Thread.sleep(100);
                SharpStrGrapher.txtOutput.setText("");
                picproc();
            }catch(Exception e){}
        }
        return;
    }

    public void picproc() throws IOException
    {
        bufferedImg = ImageIO.read(imgfile);
        imgHeight = bufferedImg.getHeight();
        imgWidth = bufferedImg.getWidth();
        imgResolution = bufferedImg.getWidth() * bufferedImg.getHeight();
        if(imgResolution > SharpStrGrapher.maxResolution)
        {
            if(x==0)
            {
                // Get the ratio to compress
                x = SharpStrGrapher.maxResolution / imgResolution;
                x = Math.sqrt(x);
            }
            // The 0.8 and 1.1 is ratio that adjust the output to suit the font.
            imgHeight = (int) (imgHeight * x * 0.8);
            imgWidth = (int) (imgWidth * x * 1.1);
            bufferedImg = imgCompress(imgHeight, imgWidth);
            imgResolution = imgWidth * imgHeight;
        }
        // Clear memory of str to avoid repeatation.
        str = "";
        for (int i = 0; i < imgHeight; i++)
        {
            str += "\n";
            for (int j = 0; j < imgWidth; j++)
            {
                int scalePlace = SharpStrGrapher.getScaleChar(SharpStrGrapher.getGrayValue(Integer.toHexString(bufferedImg.getRGB(j, i))));
                str += (SharpStrGrapher.cuScaleChar.substring(scalePlace,scalePlace+1));
            }
        }
        // Do this is to avoid distortation of image.
        SharpStrGrapher.txtOutput.append(str);
    }

    public static BufferedImage imgCompress(int height, int width) throws IOException
    {
        // Get the trimmed image
        Image trimSize = bufferedImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        // Create a blank board
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // Create g2d object using the blank board
        Graphics2D g2d = resized.createGraphics();
        // Put trimmed image in the board
        g2d.drawImage(trimSize, 0, 0, null);
        g2d.dispose();
        return resized;
    }
}
