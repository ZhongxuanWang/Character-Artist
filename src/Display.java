import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Display {
    public static Logger logger = Logger.getLogger(Display.class.getName());
    static void info(String info) {
        // System.out.println("\033[0;32m" + "+INFO+ - " + info + "." + "\033[0m");
        logger.log(Level.INFO, info);
    }
    
    static void warninfo(String info) {
        System.out.println("\033[0;33m" + "+WARNING+ - " + info + "." + "\033[0m");
        logger.log(Level.WARNING, info);
    }

    static void errinfo(String info) {
        // System.out.println("\033[0;31m" + "+ERROR+ - " + info + "." + "\033[0m");
        JOptionPane.showMessageDialog(null, info + ".", "An Error Occurs", JOptionPane.ERROR_MESSAGE);
        logger.log(Level.SEVERE, info);
    }


    static void output(String str)
    {
        if(str.length() == 0)
        {
            // Console Output - Uncontrollable
            System.out.println();
            // GUI output - Controllable (set it in the field part)
            CharGrapher.txtOutput.append("\n");
            return;
        }
        System.out.print(str);
        CharGrapher.txtOutput.append(str);
    }

}
