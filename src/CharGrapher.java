// For basic needs
import java.io.*;
import java.awt.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Iterator;

// For photo process
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.*;
import java.awt.image.BufferedImage;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;

// For the Window and the elements
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.awt.font.*;

class CharGrapher extends JFrame implements ActionListener
{
    private static final long serialVersionUID = 001L;
    // Initialize some objects that are related to the functions.
    public static JPanel pnlObj = new JPanel();
    public static Iterator<ImageWriter> imageWriters;
    public static BufferedImage img, resizedImg, jpgImg;
    public static BufferedWriter bw;
    public static FileReader fr;
    public static Process pylaunch;

    public static String input, fileExtension, fileName, cuScaleChar, str, opFileName1 = "";
    public static String ssgWS = new File("").getAbsoluteFile().toString() + "/CharGrapherWorkSpace/";
    public static String pyfilename = ssgWS + "SSshoter.py";
    public static String[] helpTips = {
        "Start the process by clicking here.",                                                          // 0
        "It's where the output will be. It's editable",                                                 // 1
        "Input your file path here. <required>",                                                        // 2
        "Output to a txt file als You don't need to specify the txt name. <optional>",                  // 3
        "Select a converting mode to continue. <required>",                                             // 4
        "Reset all the field in this window to their default.",                                         // 5
        "The variaties of characters included in the output text."+                                     
        "'5' means keep origin. '0' means output only contains two characters."+
        " ($ and <space>). It's interactive in Camera --> CharGraph mode. <optional>",                   // 6
        "Select a font for the words that will be displayed. <required>",                               // 7
        "The resolution(dimension) of the outputted graph. By default,"+ 
        "it's 20000 pixels, which is also the maximum degree. It's interactive in Cam-->CharGraph mode.",// 8
        "Input your characters here. NOTE:']' represents output in a seperate line. "+
        "Maximum is 10 characters at once. The exceeding parts will be ignored. <required>"             // 9
    };
    public static String[] modes = {
        "Photo --> CharGraph",       // 0
        "Characters --> CharGraph",  // 1
        "Camera --> CharGraph"       // 2
    };
    public static String[] labels = {
        "Photo Path:",      // 0
        "Complexity:",      // 1
        "Characters:",      // 2
        "Resolution:",      // 3
        "Font Size:",       // 4
        "Characters Font:"  // 5
    };
    public static String[] btns = {
        "Start",              // 0
        "Reset Field",        // 1
        "Output To Txt",      // 2
        "Pause",              // 3
        "Continue",           // 4
        "Characters Reverse", // 5
        "Reverse Back"        // 6
    };
    public static String[] scales = {
        "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$                                         ",     // 0
        "WWWWWWWWWWWWWWWCCCCCCCCCCCCCCCrrrrrrrrrr{{{{{{{{{{>>>>>>>>>>``````````            ",     // 1
        "&&&&&WWWWWdddddpppppCCCCCJJJJJxxxxxrrrrr11111{{{{{<<<<<>>>>>^^^^^`````            ",     // 2  
        "%%%%8&WWWWkkkkbdppppQQQQLCJJJJuuuunxrrrr(((()1{{{{++++~<>>>>,,,,\"^`````           ",    // 3
        "BBB%8&WMMMhhhkbdpqqq000QLCJUUUvvvunxrjjj|||()1{}}}___+~<>iii:::,\"^''''`           ",    // 4
        "@@B%8&WM##aahkbdpqwwOO0QLCJUYYccvunxrjff\\\\|()1{}[[--_+~<>i!!;;:,\"^`'.             ",  // 5
        "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'..            ",   // 6
    };

    public static int imgWidth, imgHeight, imgResolution;
    public static boolean pyhasdestroid = false;

    private static boolean ifJpg = false;
    private static Scanner scr = new Scanner(System.in);


    /* This is maximum Resolution in which you want the output to hold. You can adjust but it's hoped not 
    to be too large otherwise the text will be overflowed in the output area. Of course, the bigger the
    better. But if resolution from photos that later inputted is larger than this, it will be compressed
    to this. 
    NOTICE : This must set to be a decimal, which is expected to adhere a .0 at last, but not required.*/
    public static double maxResolution = 20000.0;

    /* This is the switch to open the function that cut up the space parts in the front part of the output.
    Even though this is recommended to trun on every time and very useful, for some very very special 
    circumstances, the improvement doesn't work and would probably distort the original photo, or, user 
    doesn't want to use this function. You can change it to false if you want.    
    NOTICE : This function is only applicable in Photo --> CharGraph mode.                               */
    private static boolean isCutUpSpacePart = true;

    // Elements in the window
    public static JButton startBtn = new JButton(btns[0]);
    public static JButton resetBtn = new JButton(btns[1]);
    public static JButton optxtBtn = new JButton(btns[2]);
    public static JButton reverseBtn = new JButton(btns[5]);
    public static JTextField stringInputField = new JTextField(20); // For file path
    public static JTextField charInputField = new JTextField(10); // For characters
    public static JTextArea txtOutput = new JTextArea(120, 270);
    public static JLabel inputLable = new JLabel(labels[0]);
    public static JLabel sliderLable = new JLabel(labels[1]);
    public static JLabel sliderLable2 = new JLabel(labels[3]);
    public static JLabel fontLable = new JLabel(labels[5]);
    public static Font txtOutputFont = new Font("Courier New", Font.PLAIN, 5), charFont;
    public static JComboBox<String> modeBox = new JComboBox<String>(modes);
    public static JComboBox<String> fontBox = new JComboBox<String>
    (GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
    public static JSlider wordComplexitySlider = new JSlider(JSlider.HORIZONTAL, 0, 6, 6);
    public static JSlider resolutionSlider = new JSlider(JSlider.HORIZONTAL, 2,
     (int) maxResolution, (int) maxResolution);
    public static JScrollPane areaScrollPane = new JScrollPane(txtOutput);


    // Constructor for the Window
    public CharGrapher()
    {
        super("Sharp String Grapher 1.0");

        // Hit Enter to process. This area is only for Photo --> CharGraph mode
        stringInputField.addKeyListener(new KeyListener(){
            public void keyPressed (KeyEvent e) {}
            public void keyTyped (KeyEvent e) {}
            public void keyReleased (KeyEvent ev)
            {
                // 10 represents Enter. Thus, if user hits enter, then process began
                if(ev.getKeyCode() == 10)
                {
                    try {
                        photoToGraph(stringInputField.getText());
                    } catch (IOException e) {}
                }
                return;
            }
        });

        startBtn.addActionListener(new ActionListener(){
            public void actionPerformed (ActionEvent ev) 
            {
                txtOutput.setText("");
                // If it's Characters --> CharGraph mode
                if(modeBox.getSelectedItem().toString().equals(modes[1]))
                {
                    // Direct to Character --> CharGraph method.
                    charToGraph();
                    return;
                }

                // If it's Camera --> CharGraph mode
                if(modeBox.getSelectedItem().toString().equals(modes[2]))
                {
                    // If the status is 'paused'
                    if(startBtn.getText().equals(btns[3]))
                    {
                        pydestroy();
                        startBtn.setText(btns[4]);
                    } else { // If the status is 'running'
                        // Kill the previous-launcged Thread
                        pydestroy();
                        // If the button printed "continue"
                        startBtn.setText(btns[3]);
                        camModeProcess();
                    }
                    return;
                }
                // If it's Photo --> CharGraph mode
                try{
                    photoToGraph(stringInputField.getText());
                } catch(IOException e){}
                return;
            }
        });

        resetBtn.addActionListener(new ActionListener(){
            public void actionPerformed (ActionEvent e)
            {
                // Reset all the fields to their default status
                charInputField.setText("");
                stringInputField.setText("");
                txtOutput.setText("");
                wordComplexitySlider.setValue(6);
                resolutionSlider.setValue((int)maxResolution); 
                return;
            }
        });

        resolutionSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                // If it's Characters --> CharGraph mode, exit the method
                if(modeBox.getSelectedItem().toString().equals(modes[1])) return;
                // Set max resolution
                maxResolution = (double) resolutionSlider.getValue();
            }
        });

        wordComplexitySlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                // Inform the method to change the value
                cuScaleChar =  CharGrapher.getCusScale();
            }
        });

        reverseBtn.addActionListener(new ActionListener(){
            public void actionPerformed (ActionEvent e)
            {
                // Set the text of Button and change
                if(reverseBtn.getText() == btns[5])
                {
                    reverseBtn.setText(btns[6]);
                } else {
                    reverseBtn.setText(btns[5]);
                }
                // Read each characters from back and put on the top front.
                for(int i = 0; i < scales.length; i++)
                {
                    String scales_temp = "";
                    for(int j=1; j <= scales[i].length(); j++)
                    {
                        scales_temp += scales[i].substring(scales[i].length()-j,scales[i].length()-j+1);
                    }
                    scales[i] = scales_temp;
                }
                String scales_temp = "";
                for(int i=1; i <= cuScaleChar.length(); i++)
                {
                    scales_temp += cuScaleChar.substring(cuScaleChar.length()-i, cuScaleChar.length()-i+1);
                }
                cuScaleChar = scales_temp;
            }
        });

        optxtBtn.addActionListener(new ActionListener(){
            public void actionPerformed (ActionEvent e)
            {
                if(!outputToTxt())
                    errinfo("Software is unable to output to txt file. Either because" +
                    "the output field is blank or the it doesn't have right to write");
            }
        });

        // Select Mode
        modeBox.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e)
            {
                // If change is detected
                if(e.getStateChange() == ItemEvent.SELECTED)
                {
                    // Photo --> CharGraph mode
                    if(modeBox.getSelectedItem().toString().equals(modes[0]))
                    {
                        // Remove previously written data
                        txtOutput.setText("");
                        // Destroy previous launched python script.
                        pydestroy();
                        // Reconstructing GUI
                        inputLable.setVisible(true);
                        stringInputField.setVisible(true);
                        charInputField.setVisible(false);
                        sliderLable.setVisible(true);
                        wordComplexitySlider.setVisible(true);
                        fontLable.setVisible(false);
                        fontBox.setVisible(false);
                        startBtn.setText(btns[0]);
                        sliderLable2.setText(labels[3]);
                        resolutionSlider.setMinimum(0);
                        resolutionSlider.setMaximum((int) maxResolution);
                        resolutionSlider.setMajorTickSpacing(1);
                        resolutionSlider.setSnapToTicks(false);
                        resolutionSlider.setPaintTicks(false);

                        inputLable.setText(labels[0]);
                    }
                    
                    // Char --> CharGraph mode
                    if(modeBox.getSelectedItem().toString().equals(modes[1]))
                    {
                        // Remove previously written data
                        txtOutput.setText("");
                        // Destroy previous launched python script.
                        pydestroy();
                        // Reconstructing GUI
                        inputLable.setVisible(true);
                        stringInputField.setVisible(true);
                        stringInputField.setVisible(false);
                        charInputField.setVisible(true);
                        sliderLable.setVisible(false);
                        wordComplexitySlider.setVisible(false);
                        fontLable.setVisible(true);
                        fontBox.setVisible(true);
                        startBtn.setText(btns[0]);
                        sliderLable2.setText(labels[4]);
                        resolutionSlider.setMinimum(5);
                        resolutionSlider.setMaximum(70);
                        resolutionSlider.setMajorTickSpacing(4);
                        resolutionSlider.setSnapToTicks(true);
                        resolutionSlider.setPaintTicks(true);

                        inputLable.setText(labels[2]);
                    }
                    
                    // Cam --> CharGraph mode
                    if(modeBox.getSelectedItem().toString().equals(modes[2]))
                    {   
                        // Remove previously written data
                        txtOutput.setText("");
                        // Destroy previous launched python script.
                        pydestroy();
                        // Reconstructing GUI
                        inputLable.setVisible(false);
                        stringInputField.setVisible(false);
                        charInputField.setVisible(false);
                        sliderLable.setVisible(true);
                        wordComplexitySlider.setVisible(true);
                        fontLable.setVisible(false);
                        fontBox.setVisible(false);
                        sliderLable2.setText(labels[3]);
                        resolutionSlider.setMinimum(0);
                        resolutionSlider.setMaximum((int)maxResolution);
                        resolutionSlider.setMajorTickSpacing(1);
                        resolutionSlider.setSnapToTicks(false);
                        resolutionSlider.setPaintTicks(false);

                        startBtn.setText(btns[3]);
                        // Lauch the multithread.
                        if(!buildPy())
                        {
                            errinfo("Sorry, python script building failed."+ 
                            "Please see 'readme.md' for further instruction");
                            return; // If building failed, stop building.
                        }
                        camModeProcess();
                    }
                }
                return;
            }
        });

        addWindowListener(new WindowAdapter()
        {
            /* The manipulations after user hitting 'close' and before 
            closing process starts.*/
            public void windowClosing(WindowEvent e)
            {
                // Destroy the py thread before closing the window
                pydestroy();
                // Delete the photo created from camera
                try{
                    new File(ssgWS + "SSGSHOTS_IMG.jpg").delete();
                    new File(ssgWS + "SSGSHOTS_IMG.py").delete();
                }catch(Exception er){}
                System.exit(0);
            }
        });

        // HELP INFORMATION - - - - - - - - - - - - - - - - - - -
        startBtn.setToolTipText(helpTips[0]);
        txtOutput.setToolTipText(helpTips[1]);
        optxtBtn.setToolTipText(helpTips[3]);
        stringInputField.setToolTipText(helpTips[2]);
        modeBox.setToolTipText(helpTips[4]);
        resetBtn.setToolTipText(helpTips[5]);
        wordComplexitySlider.setToolTipText(helpTips[6]);
        sliderLable.setToolTipText(helpTips[6]);
        resolutionSlider.setToolTipText(helpTips[8]);
        sliderLable2.setToolTipText(helpTips[8]);
        fontBox.setToolTipText(helpTips[7]);
        charInputField.setToolTipText(helpTips[9]);

        // OTHER SETTINGS - - - - - - - - - - - - - - - - - - - -
        txtOutput.setEditable(true);
        txtOutput.setFont(txtOutputFont);
        // Set auto wrap
        txtOutput.setLineWrap(true);
        txtOutput.setWrapStyleWord(true);
        // Set btn and char input not visible but in the content.
        charInputField.setVisible(false);
        fontLable.setVisible(false);
        
        fontBox.setVisible(false);
        // Set effect of sliders: having tick and space & size
        wordComplexitySlider.setMajorTickSpacing(1);
        wordComplexitySlider.setSnapToTicks(true);;
        wordComplexitySlider.setPaintTicks(true);
        wordComplexitySlider.setPreferredSize(new Dimension(130,30));
        resolutionSlider.setPreferredSize(new Dimension(130,30));
        areaScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(800, 800));
        areaScrollPane.setAutoscrolls(true);
        cuScaleChar = getCusScale();

        // ADD COMPONENTS - - - - - - - - - - - - - - - - - - - -
        pnlObj.add(inputLable);
        pnlObj.add(stringInputField);
        pnlObj.add(charInputField);
        pnlObj.add(fontLable);
        pnlObj.add(fontBox);
        pnlObj.add(startBtn);
        pnlObj.add(resetBtn);

        pnlObj.add(sliderLable2);
        pnlObj.add(resolutionSlider);
        pnlObj.add(sliderLable);
        pnlObj.add(wordComplexitySlider);
        pnlObj.add(reverseBtn);
        pnlObj.add(optxtBtn);
        pnlObj.add(modeBox);
        pnlObj.add(areaScrollPane);

        // SET WINDOW PROPERTY - - - - - - - - - - - - - - - - - - 
        pack();
        setSize(800, 835);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        add(pnlObj);
        setResizable(false);
        //setAlwaysOnTop(true);
        setVisible(true);
    }

    // To override the abstract method in ActionEvent
    public void actionPerformed(ActionEvent e){}
    public static void main (String[] args) throws IOException
    {
        // Create work space if needed
        File ssgWSObj = new File(ssgWS);
        if(!ssgWSObj.exists())
            ssgWSObj.mkdir();

        // Show UI
        info("Welcome to CharGrapher 1.0");
        info("Console will also get output. Warnings and Infos will be displayed in this area.");
        newline();

        CharGrapher gui = new CharGrapher();
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
        
        photoToGraph(input);
    }


    public static void photoToGraph(String input) throws IOException
    {
        if(!new File(input).exists()) 
        {
            errinfo("Sorry, file you inputted does not exist");
            return;
        }
        // Check elligibility and get filename
        if(!checkIfPic(getExtension(input))) return;

        // Treat it as image file and give image data to bufferedimage type img.
        getImgBasicInfo(input);
        // Check if resolution oversized. If it's oversized, compress before continue.
        if(imgResolution > maxResolution)
        {
            /* Calculate the percentile in which is hoped to compress before letting imgCompress
            method to do the work. It's a logarithem. */
            {
                double x = maxResolution / imgResolution;
                x = Math.sqrt(x);
                imgHeight = (int) (imgHeight * x * 0.8);
                imgWidth = (int) (imgWidth * x);
            }

            // Call the method
            resizedImg = imgCompress(img, imgHeight, imgWidth);
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

        /* This is a logarithm that output the specific charactor(char) from the scale. The 
        position of char is determined from the complexity slider or internally built variable. 
        Meanwhile, here implements a log that remove extra whitespace from the output.   */

        // Initialize some variables will be used in later
        boolean spaceTest = true;
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
                    // Align the text
                    output(" ".repeat(j+1));
                }
                output(speChar);
            }
        }
        spaceTest = true;

        newline();
    }


    public static void charToGraph() 
    {
        // Seperate each line by \n and proceed them individually.
        if(charInputField.getText().length() == 0) 
        {
            errinfo("It is expected to input some characters in the input field");
            return;
        }
        for(int i = 0; i < charInputField.getText().split("]").length; i++)
        {
            // Create a blank canvas
            Graphics2D graphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).createGraphics();


            // Get the size of the font, help to create a new String
            charFont = new Font(fontBox.getSelectedItem().toString(), Font.PLAIN, resolutionSlider.getValue());
            FontMetrics metrics = graphics.getFontMetrics(charFont);
            int hgt = metrics.getHeight();
            int adv = metrics.stringWidth(charInputField.getText().split("]")[i]);

            // Create the canves with expected dimension
            BufferedImage canvas = new BufferedImage(adv+4, hgt, BufferedImage.TYPE_INT_RGB);
            graphics = canvas.createGraphics();

            // Fill with all white
            graphics.fillRect(0, 0, adv+4, hgt);
            graphics.setColor(Color.BLACK);

            // Set the font and output
            graphics.setFont(charFont);
            graphics.drawString(charInputField.getText().split("]")[i], 0, hgt-5); // Draw String on canvas

            // Get basic information of the BufferedImage
            getImgBasicInfo(canvas);
            output(canvas, imgHeight, imgWidth, true);
        }
    }


// - - - - - - - - - - - - - - - - - - - - I M G   P R O C E S S - - - - - - - - - - - - - - - - -
// - - - - - - - - - - - - - - - - - - - - I M G   P R O C E S S - - - - - - - - - - - - - - - - -
// - - - - - - - - - - - - - - - - - - - - I M G   P R O C E S S - - - - - - - - - - - - - - - - -

    // Image to smaller size
    public static BufferedImage imgCompress(BufferedImage img, int height, int width)
    {
        // Put it to origin
        CharGrapher.ifJpg = false;
        try{
            Image trimSize = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resized.createGraphics();
            g2d.drawImage(trimSize, 0, 0, null);
            g2d.dispose();
            return resized;
        } catch(Exception e){
            errinfo("Error met while processing image. Image was unable to be proceeded. Sorry");
            // Return a 10x10 sized blank image if errors were caught
            return new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        }
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


// - - - - - - - - - - - - - - - - - - - - - O T H E R - - - - - - - - - - - - - - - - - - - - - -
// - - - - - - - - - - - - - - - - - - - - - O T H E R - - - - - - - - - - - - - - - - - - - - - -
// - - - - - - - - - - - - - - - - - - - - - O T H E R - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Output the content to GUI or Console.
     * @param bufferedImg
     * @param imgHeight
     * @param imgWidth
     * @param isOutputToConsole
     */
    public static void output(
        BufferedImage bufferedImg, 
        int imgHeight, 
        int imgWidth, 
        boolean isOutputToConsole
        )
    {
        // Get customized scale (from slider)

        String str = "";
        for (int i = 0; i < imgHeight; i++)
        {
            str += "\n";
            for (int j = 0; j < imgWidth; j++)
            {
                int scalePlace = CharGrapher.getScaleChar(CharGrapher.getGrayValue(Integer.toHexString(bufferedImg.getRGB(j, i))));
                str += (CharGrapher.cuScaleChar.substring(scalePlace,scalePlace+1));                    
            }
        }
        // Do this is to avoid bad effect of image.
        CharGrapher.txtOutput.append(str);
        if(isOutputToConsole) System.out.print(str);
    }

    public static void output(String str)
    {
        if(str.length() == 0)
        {
            // Console Output - Uncontrollable
            newline();
            // GUI output - Controllable (set it in the field part)
            txtOutput.append("\n");
            return;
        }
        System.out.print(str);
        txtOutput.append(str);
        return;
    }

    public static String getExtension(String path) 
    {
        /* In macOS, path contains "/" instead of "\" which is in Windows OS.
        Thus, CharGrapher is expected to only run in macOS and systems that support those 
        variations. Later compatibility in Windows may be resolved. */
        fileName = path.split("/")[path.split("/").length - 1]; // Get filename
        String fEx;
        try {
            fEx=fileName.split("\\.")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            warninfo("It is expected to input a path containing a file extension."+
            "'png' is selected by default");
            return "png";
        }
        if((fEx.toLowerCase().equals("jpg") && fEx.toLowerCase().equals("jpeg")))
            ifJpg = true;
        return fileName.split("\\.")[1];// Return file extension
    }

    public static String getCusScale()
    {
        try{
            return scales[wordComplexitySlider.getValue()-1];
        } catch(ArrayIndexOutOfBoundsException e)
        {
            // If there's an error in getting the value, then use 0 position in array to replace
            return scales[0];
        }
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

    private static void getImgBasicInfo(BufferedImage bufferedImg)
    {
        img = resizedImg;
        try{
            imgWidth = bufferedImg.getWidth();
            imgHeight = bufferedImg.getHeight();
        } catch(Exception e){
            errinfo("Software is unable to get the image info by width or height");
            return;
        }
        imgResolution = imgWidth * imgHeight;
        return;
    }

    public static boolean outputToTxt()
    {
        String str = txtOutput.getText();
        // If it's camera mode and if it's not pausing
        if(str.length()==0)
        {
            if(modeBox.getSelectedItem().toString().equals(modes[2]) && 
            !startBtn.getText().equals(btns[4]))
            {
                while(str.length()==0)
                    str = txtOutput.getText();
            } else {
                return false;
            }
        }

        String opFileName = fileName + "_PLAIN_STRING_CONTENT_FROM_SSG"
        + (int) (Math.random() * 2000000 + 1000000) + ".txt";
        // Directly call the method to output.
        try {
            bw = new BufferedWriter(new FileWriter(ssgWS + opFileName));
            /* Avoid when user pressed the "capture", the software was 
            refreshing its field */
            bw.write(str);
            bw.close();
            Desktop.getDesktop().open(new File(ssgWS + opFileName));

            return true;
        } catch (IOException er) {return false;}
    }

    public static void camModeProcess()
    {                        
        if(!pyLaunch()) return;
        Thread snapshotpy = new Thread(new Snapshotpy());
        snapshotpy.start();
    }

    public static void pydestroy()
    {
        pyhasdestroid = true;
        try{
            pylaunch.destroyForcibly();
        }catch(Exception e){}
    }

    public static boolean buildPy()
    {
        try
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(new File(pyfilename)));
            String prg = "from cv2 import *\n" +
            "import time\n"+
            "os.chdir(\""+ssgWS+"\")\n"+
            "while True:\n"+
            /* Sleep(seconds) 0.1 is recommended. If the speed of read and write of your disk
               is obnormally low, you could adjust this value higher but you will experience 
               long delays.
               It means the delay in which python takes photo. */
            "    time.sleep("+ 0.18 +")\n"+ 
            "    cam = VideoCapture(0)\n"+
            "    rep, img = cam.read()\n"+
            "    imwrite(\"SSGSHOTS_IMG.jpg\",img)\n";
            out.write(prg);
            out.close();
        } catch (Exception e) {
            errinfo("Sorry, unable to output file. " + e.toString());
            return false;
        }
        return true;
    }

    public static boolean pyLaunch()
    {
        try
        {
            pylaunch = Runtime.getRuntime().exec("python3 " + pyfilename);
        }
        catch (Exception e) 
        {
            errinfo("Sorry, unable to Launch python3. Please install the environment or check" +
            "if the python script is exist." + e.toString());
            return false;
        }
        pyhasdestroid = false;
        return true;
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
    public static BufferedImage bufferedImg;

    // To receive content that will be outputted
    public static String str = "";
    public static int imgWidth, imgHeight, imgResolution;
    public static double x = 0;
    public static String imgPath = CharGrapher.ssgWS + "SSGSHOTS_IMG.jpg";
    public static File imgfile = new File(imgPath);
    
    public void run()
    {
        // If python script task is alive, then continue to output. 
        while(!CharGrapher.pyhasdestroid)
        {
            try{
                /* Thread.leep(MS) 100ms = .1s This means the delay in which the
                software read image from the disk. If the value is lower, it may
                result in exceeding disk consumption. Vice versa. */
                Thread.sleep(100);
                CharGrapher.txtOutput.setText("");
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
        if(imgResolution > CharGrapher.maxResolution)
        {
            // Get the ratio to compress
            x = CharGrapher.maxResolution / imgResolution;
            x = Math.sqrt(x);
            // The 0.8 and 1.1 is ratio that adjust the output to suit the font.
            imgHeight = (int) (imgHeight * x * 0.8);
            imgWidth = (int) (imgWidth * x * 1.1);
            bufferedImg = imgCompress(imgHeight, imgWidth);
            imgResolution = imgWidth * imgHeight;
        }
        // Output the image. 
        CharGrapher.output(bufferedImg, imgHeight, imgWidth, false);
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
