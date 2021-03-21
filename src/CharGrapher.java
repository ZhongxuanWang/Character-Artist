/*
 * Honestly,
 *
 * Looking back at it, and I nearly vomitted.
 */


// For basic needs
import java.io.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Scanner;

// For photo process
import java.awt.image.BufferedImage;

// For the Window and the elements
import javax.swing.*;
import java.awt.event.*;

class CharGrapher extends JFrame implements ActionListener
{
    static final String ver = "1.5.0 PyCam version Build 20210322239";

    // Initialize some objects that are related to the functions.
    static final JPanel pnlObj = new JPanel();
    static Process pyLaunch;

    static String cuScaleChar;
    // TODO Use temp folder provided by the system
    static final String ssgWS = new File("").getAbsoluteFile().toString() + "/CharGrapherWorkSpace/";
    static final String[] helpTips = {
        "Start the process by clicking here.",                                                          // 0
        "It's where the output will be. It's editable",                                                 // 1
        "Input your file path here. <required>",                                                        // 2
        "Output to a txt file als You don't need to specify the txt name. <optional>",                  // 3
        "Select a converting mode to continue. <required>",                                             // 4
        "Reset all the field in this window to their default.",                                         // 5
        "The variety of characters included in the output text."+
        "'5' means keep origin. '0' means output only contains two characters."+
        " ($ and <space>). It's interactive in Camera --> CharGraph mode. <optional>",                   // 6
        "Select a font for the words that will be displayed. <required>",                               // 7
        "The resolution(dimension) of the outputted graph. By default,"+ 
        "it's 20000 pixels, which is also the maximum degree. It's interactive in Cam-->CharGraph mode.",// 8
        "Input your characters here. NOTE:']' represents output in a separate line. "+
        "Maximum is 10 characters at once. The exceeding parts will be ignored. <required>"             // 9
    };
    static final String[] modes = {
        "Photo --> CharGraph",       // 0
        "Words --> CharGraph",       // 1
        "Camera --> CharGraph",      // 2
        "Photo --> Hexadecimal"      // 3
    };
    static final String[] labels = {
        "Photo Path:",      // 0
        "Complexity:",      // 1
        "Characters:",      // 2
        "Resolution:",      // 3
        "Font Size:",       // 4
        "Characters Font:"  // 5
    };
    static final String[] btns = {
        "Start",              // 0
        "Reset Field",        // 1
        "Output To Txt",      // 2
        "Pause",              // 3
        "Continue",           // 4
        "Color Reverse",      // 5
        "Reverse Back"        // 6
    };
    static final String[] scales = {
        "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$                                         ",     // 0
        "WWWWWWWWWWWWWWWCCCCCCCCCCCCCCCrrrrrrrrrr{{{{{{{{{{>>>>>>>>>>``````````            ",     // 1
        "&&&&&WWWWWdddddpppppCCCCCJJJJJxxxxxrrrrr11111{{{{{<<<<<>>>>>^^^^^`````            ",     // 2  
        "%%%%8&WWWWkkkkbdppppQQQQLCJJJJuuuunxrrrr(((()1{{{{++++~<>>>>,,,,\"^`````           ",    // 3
        "BBB%8&WMMMhhhkbdpqqq000QLCJUUUvvvunxrjjj|||()1{}}}___+~<>iii:::,\"^''''`           ",    // 4
        "@@B%8&WM##aahkbdpqwwOO0QLCJUYYccvunxrjff\\\\|()1{}[[--_+~<>i!!;;:,\"^`'.             ",  // 5
        "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'..            ",   // 6
    };

    static boolean pyhasdestroid = false;

    /* This is maximum Resolution in which you want the output to hold. You can adjust but it's hoped not 
    to be too large otherwise the text will be overflowed in the output area. Of course, the bigger the
    better. But if resolution from photos that later inputted is larger than this, it will be compressed
    to this. 
    NOTICE : This must set to be a decimal, which is expected to adhere a .0 at last, but not required.*/
    static double maxResolution;
    static final double sliderRes = 20000.0;

    /* This is the switch to open the function that cut up the space parts in the front part of the output.
    Even though this is recommended to trun on every time and very useful, for some very very special 
    circumstances, the improvement doesn't work and would probably distort the original photo, or, user 
    doesn't want to use this function. You can change it to false if you want.    
    NOTICE : This function is only applicable in Photo --> CharGraph mode.                               */
    static final boolean isCutUpSpacePart = true;

    // Elements in the window
    static final JButton startBtn = new JButton(btns[0]);
    static final JButton resetBtn = new JButton(btns[1]);
    static final JButton optxtBtn = new JButton(btns[2]);
    static final JButton reverseBtn = new JButton(btns[5]);

    static final JTextField stringInputField = new JTextField(20); // For file path
    static final JTextField charInputField = new JTextField(10); // For characters
    static final JTextArea txtOutput = new JTextArea(120, 100);

    static final JLabel inputLabel = new JLabel(labels[0]);
    static final JLabel sliderLabel = new JLabel(labels[1]);
    static final JLabel sliderLabel2 = new JLabel(labels[3]);
    static final JLabel fontLabel = new JLabel(labels[5]);

    static final Font txtOutputFont = new Font("Courier New", Font.PLAIN, 5);
    static Font charFont;

    static final JComboBox<String> modeBox = new JComboBox<>(modes);
    static final JComboBox<String> fontBox = new JComboBox<>
    (GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());

    static final JSlider wordComplexitySlider = new JSlider(JSlider.HORIZONTAL, 0, 6, 6);
    static final JSlider resolutionSlider = new JSlider(JSlider.HORIZONTAL, 2,
     (int) sliderRes, (int) sliderRes);

    static final JScrollPane areaScrollPane = new JScrollPane(txtOutput);


    // Constructor for the Window
    public CharGrapher()
    {
        super("Sharp String Grapher "+ver);

        // Hit Enter to process. This area is only for Photo --> CharGraph mode
        stringInputField.addKeyListener(new KeyListener(){
            public void keyPressed (KeyEvent e) {}
            public void keyTyped (KeyEvent e) {}
            public void keyReleased (KeyEvent ev)
            {
                // 10 represents Enter. Thus, if user hits enter, then process began
                if(ev.getKeyCode() == 10) {
                    Display.warninfo(stringInputField.getText());
                    photoToGraph(stringInputField.getText());
                }
            }
        });

        startBtn.addActionListener(ev -> {
            txtOutput.setText("");
            // If it's Photo --> CharGraph mode
            if (Objects.requireNonNull(modeBox.getSelectedItem()).toString().equals(modes[0])) {
                photoToGraph(stringInputField.getText().trim());
                return;
            }

            // If it's Characters --> CharGraph mode
            if (modeBox.getSelectedItem().toString().equals(modes[1])) {
                // Direct to Character --> CharGraph method.
                charToGraph();
                return;
            }

            // If it's Camera --> CharGraph mode
            if(modeBox.getSelectedItem().toString().equals(modes[2])) {
                // If the status is 'paused'
                if(startBtn.getText().equals(btns[3])) {
                    pydestroy();
                    startBtn.setText(btns[4]);
                } else { // If the status is 'running
                    // Kill the previous-launched Thread
                    pydestroy();
                    // If the button printed "continue"
                    startBtn.setText(btns[3]);
                    camModeProcess();
                }
                return;
            }

            // If it's Photo --> CharGraph mode
            if(modeBox.getSelectedItem().toString().equals(modes[3])) {
                photoToHex(stringInputField.getText().trim());
            }
        });

        resetBtn.addActionListener(e -> {
            // Reset all the fields to their default status
            charInputField.setText("");
            stringInputField.setText("");
            txtOutput.setText("");
            wordComplexitySlider.setValue(6);
            resolutionSlider.setValue((int)maxResolution);
            if (reverseBtn.getText().equals(btns[6])) {
                // Simulate a clicking.
                reverseBtn.doClick();
            }
            resolutionSlider.setValue(resolutionSlider.getMaximum());
        });

        resolutionSlider.addChangeListener(e -> {
            // If it's Characters --> CharGraph mode, exit the method
            if (Objects.requireNonNull(modeBox.getSelectedItem()).toString().equals(modes[1])) return;
            // Set max resolution
            maxResolution = resolutionSlider.getValue();
        });

        wordComplexitySlider.addChangeListener(e -> {
            // Inform the method to change the value
            cuScaleChar =  CharGrapher.getCusScale();
        });

        reverseBtn.addActionListener(e -> {
            // Set the text of Button and change
            if(reverseBtn.getText().equals(btns[5])) {
                reverseBtn.setText(btns[6]);
            } else {
                reverseBtn.setText(btns[5]);
            }
            // Read each characters from back and put on the top front.
            for(int i = 0; i < scales.length; i++) {
                StringBuilder scales_temp = new StringBuilder();
                for(int j=1; j <= scales[i].length(); j++) {
                    scales_temp.append(scales[i], scales[i].length() - j, scales[i].length() - j + 1);
                }
                scales[i] = scales_temp.toString();
            }
            StringBuilder scales_temp = new StringBuilder();
            for(int i=1; i <= cuScaleChar.length(); i++) {
                scales_temp.append(cuScaleChar, cuScaleChar.length() - i, cuScaleChar.length() - i + 1);
            }
            cuScaleChar = scales_temp.toString();
        });

        optxtBtn.addActionListener(e -> outputToTxt());

        // Select Mode
        modeBox.addItemListener(e -> {
            // If change is detected
            if(e.getStateChange() == ItemEvent.SELECTED) {
                // Photo --> CharGraph mode
                if (Objects.requireNonNull(modeBox.getSelectedItem()).toString().equals(modes[0])) {
                    // Remove previously written data
                    txtOutput.setText("");
                    // Destroy previous launched python script.
                    pydestroy();
                    // Reconstructing GUI
                    inputLabel.setVisible(true);
                    stringInputField.setVisible(true);
                    charInputField.setVisible(false);
                    sliderLabel.setVisible(true);
                    wordComplexitySlider.setVisible(true);
                    reverseBtn.setVisible(true);
                    fontLabel.setVisible(false);
                    fontBox.setVisible(false);
                    startBtn.setText(btns[0]);
                    sliderLabel2.setText(labels[3]);
                    resolutionSlider.setMinimum(0);
                    resolutionSlider.setMaximum((int) sliderRes);
                    resolutionSlider.setValue(resolutionSlider.getMaximum());
                    resolutionSlider.setMajorTickSpacing(1);
                    resolutionSlider.setSnapToTicks(false);
                    resolutionSlider.setPaintTicks(false);

                    inputLabel.setText(labels[0]);
                    txtOutput.setFont(txtOutputFont);
                }

                // Char --> CharGraph mode
                if(modeBox.getSelectedItem().toString().equals(modes[1])) {
                    // Remove previously written data
                    txtOutput.setText("");
                    // Destroy previous launched python script.
                    pydestroy();
                    // Reconstructing GUI
                    inputLabel.setVisible(true);
                    stringInputField.setVisible(true);
                    stringInputField.setVisible(false);
                    charInputField.setVisible(true);
                    sliderLabel.setVisible(false);
                    wordComplexitySlider.setVisible(false);
                    reverseBtn.setVisible(true);
                    fontLabel.setVisible(true);
                    fontBox.setVisible(true);
                    startBtn.setText(btns[0]);
                    sliderLabel2.setText(labels[4]);
                    resolutionSlider.setMinimum(5);
                    resolutionSlider.setMaximum(70);
                    resolutionSlider.setValue(resolutionSlider.getMaximum());
                    resolutionSlider.setMajorTickSpacing(4);
                    resolutionSlider.setSnapToTicks(true);
                    resolutionSlider.setPaintTicks(true);

                    inputLabel.setText(labels[2]);
                    txtOutput.setFont(txtOutputFont);
                }

                // Cam --> CharGraph mode
                if(modeBox.getSelectedItem().toString().equals(modes[2])) {
                    // Remove previously written data
                    txtOutput.setText("");
                    // Destroy previous launched python script.
                    pydestroy();
                    // Reconstructing GUI
                    inputLabel.setVisible(false);
                    stringInputField.setVisible(false);
                    charInputField.setVisible(false);
                    sliderLabel.setVisible(true);
                    wordComplexitySlider.setVisible(true);
                    reverseBtn.setVisible(true);
                    fontLabel.setVisible(false);
                    fontBox.setVisible(false);
                    sliderLabel2.setText(labels[3]);
                    resolutionSlider.setMinimum(20);
                    resolutionSlider.setMaximum((int)sliderRes);
                    resolutionSlider.setValue(resolutionSlider.getMaximum());
                    resolutionSlider.setMajorTickSpacing(1);
                    resolutionSlider.setSnapToTicks(false);
                    resolutionSlider.setPaintTicks(false);

                    startBtn.setText(btns[3]);

                    txtOutput.setFont(txtOutputFont);
                    // Multithreading.
                    if(!buildPy()) {
                        Display.errinfo("Sorry, python script building failed."+
                        "Please see 'readme.md' for further instruction");
                        return; // If building failed, stop building.
                    }
                    camModeProcess();
                }

                if(modeBox.getSelectedItem().toString().equals(modes[3])) {
                    // Remove previously written data
                    txtOutput.setText("");
                    // Destroy previous launched python script.
                    pydestroy();
                    // Reconstructing GUI
                    inputLabel.setVisible(true);
                    stringInputField.setVisible(true);
                    charInputField.setVisible(false);
                    sliderLabel.setVisible(false);
                    wordComplexitySlider.setVisible(false);
                    reverseBtn.setVisible(false);
                    fontLabel.setVisible(false);
                    fontBox.setVisible(false);
                    sliderLabel2.setText(labels[3]);

                    resolutionSlider.setMinimum(0);
                    resolutionSlider.setMaximum((int)sliderRes);
                    resolutionSlider.setValue(resolutionSlider.getMaximum());
                    resolutionSlider.setMajorTickSpacing(1);
                    resolutionSlider.setSnapToTicks(false);
                    resolutionSlider.setPaintTicks(false);

                    startBtn.setText(btns[0]);

                    txtOutput.setFont(new Font("Courier New",Font.BOLD,12));
                }
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
                try {
                    // Don't care about it. it has cases when those files were not generated but expected to remove them.
                    if (new File(ssgWS + "SSGSHOTS_IMG.jpg").delete()||
                    new File(ssgWS + "SSGSHOTS_IMG.py").delete()) {
                        Display.info("Some files are unable to delete.");
                    }
                } catch(Exception er) {
                    Display.errinfo("Unable to remove caches, but the software will still quit. " + er);
                }
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
        sliderLabel.setToolTipText(helpTips[6]);
        resolutionSlider.setToolTipText(helpTips[8]);
        sliderLabel2.setToolTipText(helpTips[8]);
        fontBox.setToolTipText(helpTips[7]);
        charInputField.setToolTipText(helpTips[9]);

        stringInputField.setDragEnabled(true);
        // OTHER SETTINGS - - - - - - - - - - - - - - - - - - - -
        txtOutput.setEditable(true);
        txtOutput.setFont(txtOutputFont);
        // Set auto wrap
        txtOutput.setLineWrap(true);
        txtOutput.setWrapStyleWord(true);
        // Set btn and char input not visible but in the content.
        charInputField.setVisible(false);
        fontLabel.setVisible(false);
        
        fontBox.setVisible(false);
        // Set effect of sliders: having tick and space & size
        wordComplexitySlider.setMajorTickSpacing(1);
        wordComplexitySlider.setSnapToTicks(true);
        wordComplexitySlider.setPaintTicks(true);
        wordComplexitySlider.setPreferredSize(new Dimension(130,30));
        resolutionSlider.setPreferredSize(new Dimension(130,30));
        areaScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(800, 739));
        areaScrollPane.setAutoscrolls(true);
        cuScaleChar = getCusScale();

        // ADD COMPONENTS - - - - - - - - - - - - - - - - - - - -
        pnlObj.add(inputLabel);
        pnlObj.add(stringInputField);
        pnlObj.add(charInputField);
        pnlObj.add(fontLabel);
        pnlObj.add(fontBox);
        pnlObj.add(startBtn);
        pnlObj.add(resetBtn);

        pnlObj.add(sliderLabel2);
        pnlObj.add(resolutionSlider);
        pnlObj.add(sliderLabel);
        pnlObj.add(wordComplexitySlider);
        pnlObj.add(reverseBtn);
        pnlObj.add(optxtBtn);
        pnlObj.add(modeBox);
        pnlObj.add(areaScrollPane);

        pack();
        setSize(800, 835);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Since we have already set it up
        add(pnlObj);
        setResizable(false);
        //setAlwaysOnTop(true);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
    }

    public static void main(String[] args) {
        if (args.length != 0) {
            Display.info("Console mode is not enabled");
        }

        // Create work space if needed
        File ssgWSObj = new File(ssgWS);
        if (!ssgWSObj.exists() && !ssgWSObj.mkdir()) {
            Display.errinfo("Error while trying to create a folder!");
        }

        // Show UI
        Display.info("Welcome to CharGrapher " + ver +
                "\nConsole will also get updates and warnings");
        System.out.println();

        new CharGrapher();

        // Receive input from console
        String input;
        // The variable may not have been initialized popped up when you initialize only in if statement without else.

        Scanner scr = new Scanner(System.in);
        do {
            // Print interface
            Display.info("Please input image path below");
            input = scr.nextLine();
        } while (
            // This expression means if not exist, redo.
                !(new File(input).exists())
        );
        // Close buffer
        scr.close();

        photoToGraph(input.trim());
    }


    private static void photoToGraph(String input) {
        // Check eligibility
        if (CGImage.ifNoPic(Path.of(input))) return;

        // Treat it as image file and give image data to BufferedImage type img.
        CGImage cgimage = new CGImage(Path.of(input));

        if (cgimage.resolution > resolutionSlider.getValue()) {
            cgimage.compress();
        }

        /* This is a algorithm that output the specific character(char) from the scale. The
        position of char is determined from the complexity slider or internally built variable. 
        Meanwhile, here implements a log that remove extra whitespace from the output.   */

        // Initialize some variables will be used in later
        boolean spaceTest = true;
        String speChar;
        // Read each pixel and get each RGB value, proceed each one separately.
        for (int i = 0; i < cgimage.height; i++) {
            // output a new line
            if(!isCutUpSpacePart || !spaceTest) Display.output("");

            for (int j = 0; j < cgimage.width; j++) {
                int rgb = cgimage.getImage().getRGB(j, i);
                System.out.println(rgb);
                // Convert each pixel into average gray value
                int scalePlace = getScaleChar(CGImage.getGrayValue(Integer.toHexString(rgb)));
                speChar = cuScaleChar.split("")[scalePlace];
                // If it's not a space, then stop not inputting
                if (isCutUpSpacePart && spaceTest && !(speChar.equals(" "))) {
                    spaceTest = false;
                    txtOutput.setText("");
                    // Align the text
                    Display.output(" ".repeat(j+1));
                }
                Display.output(speChar);
            }
        }
        // Resume for the next time
        System.out.println();
    }

    private static void charToGraph()
    {
        // Separate each line by \n and proceed them individually.
        if (charInputField.getText().length() == 0) {
            Display.errinfo("The Word Field couldn't be empty");
            return;
        }
        for (int i = 0; i < charInputField.getText().split("]").length; i++) {
            // Create a blank canvas
            Graphics2D graphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).createGraphics();

            // Get the size of the font, help to create a new String
            try {
                charFont = new Font(Objects.requireNonNull(fontBox.getSelectedItem()).toString(), Font.PLAIN,
                        resolutionSlider.getValue());
            } catch (NullPointerException e) {
                Display.errinfo("Font is not selected since it's not in the system anymore.");
                return;
            }
            FontMetrics metrics = graphics.getFontMetrics(charFont);
            int hgt = metrics.getHeight();
            int adv = metrics.stringWidth(charInputField.getText().split("]")[i]);

            // Create the canvas with expected dimension
            CGImage canvas = new CGImage(new BufferedImage(adv+4, hgt, BufferedImage.TYPE_INT_RGB)); 
            graphics = canvas.getImage().createGraphics();

            // Fill with all white
            graphics.fillRect(0, 0, adv+4, hgt);
            graphics.setColor(Color.BLACK);

            // Set the font and output
            graphics.setFont(charFont);
            graphics.drawString(charInputField.getText().split("]")[i], 0, hgt-5); // Draw String on canvas

            // Get basic information of the BufferedImage
            canvas.output(true);
        }
    }

    private static void photoToHex(String path) {
        File file = new File(path);
        if(!file.exists()) {
            Display.errinfo("Sorry, file you inputted does not exist");
            return;
        }
        if (CGImage.ifNoPic(Path.of(path))) return;
        CGImage cgimage = new CGImage(Path.of(path));

        if (cgimage.resolution > resolutionSlider.getValue()) {
            cgimage.compress();
        }
        
        for (int i = 0; i < cgimage.width; i++) {
            for (int j = 0; j < cgimage.height; j++) {
                String grid = Integer.toHexString(CGImage.getGrayValue(Integer.toHexString(
                        cgimage.getImage().getRGB(i, j)
                )));
                if (grid.length() == 1) grid = "0" + grid;
                txtOutput.append(grid + " ");
            }
        }
        
    }

    /**
     * Method will return the position+1 of array in integer 
     */
    protected static int getScaleChar(int grayValue) {
        // grayValue will vary from 0 to 255, which is from pure black to pure white.
        return Math.round((float)(grayValue / 3.24686));
    }

// - - - - - - - - - - - - - - - - - - - - - O T H E R - - - - - - - - - - - - - - - - - - - - - -
// - - - - - - - - - - - - - - - - - - - - - O T H E R - - - - - - - - - - - - - - - - - - - - - -
// - - - - - - - - - - - - - - - - - - - - - O T H E R - - - - - - - - - - - - - - - - - - - - - -
    private static String getCusScale()
    {
        try {
            return scales[wordComplexitySlider.getValue() - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            // If there's an error in getting the value, then use last position in array to replace
            return scales[scales.length-1];
        }
    }

    private static void outputToTxt()
    {
        String str = txtOutput.getText();
        // If it's camera mode and if it's not pausing
        if(str.length()==0)
        {
            if(Objects.requireNonNull(modeBox.getSelectedItem()).toString().equals(modes[2]) &&
            !startBtn.getText().equals(btns[4]))
            {
                while(str.length()==0)
                    str = txtOutput.getText();
            } else {
                Display.errinfo("Software is unable to output to txt file. Either because" +
                        "the output field is blank or the it doesn't have right to write");
            }
        }
        String opFileName = "PLAIN_STRING_CONTENT_FROM_SSG" + (int) (Math.random() * 2000000 + 1000000) + ".txt";
        // Directly call the method to output.
        try {
            var file = new File(ssgWS + opFileName);
            BufferedWriter bw = new BufferedWriter(new FileWriter(ssgWS + opFileName));
            bw.write(str);
            bw.close();
            Desktop.getDesktop().open(file);
            if (!file.exists()) {
                throw new IOException();
            }
        } catch (IOException er) {
            Display.errinfo("Software is unable to output to txt file. Either because" +
                "the output field is blank or the it doesn't have right to write");
        }
    }

    static void camModeProcess()
    {                        
        if(!pyLaunch()) return;
        Thread snapshotpy = new Thread(new Snapshotpy());
        snapshotpy.start();
    }

    static void pydestroy()
    {
        if (pyhasdestroid) return;
        pyhasdestroid = true;
        try{
            pyLaunch.destroyForcibly();
        }catch(Exception e){
            pyhasdestroid = false;
        }
    }

    static boolean buildPy()
    {
        try {
            var out = new BufferedWriter(new FileWriter(new File(ssgWS + "SSshoter.py")));
            String prg = "from cv2 import *\n" +
            "import time\n"+
            "os.chdir(\"" + ssgWS + "\")\n"+
            "while True:\n"+
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
        } catch (IOException e) {
            Display.errinfo("Sorry, unable to output python file. " + e.toString());
            return false;
        }
        return true;
    }

    static boolean pyLaunch()
    {
        try {
            pyLaunch = Runtime.getRuntime().exec("python3 " + ssgWS + "SSshoter.py");
        }
        catch (Exception e) {
            Display.errinfo("Sorry, unable to Launch python3 . Please install the environment or check" +
            "if the python script is exist." + e.toString());
            return false;
        }
        pyhasdestroid = false;
        return true;
    }
}
