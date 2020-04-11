import javax.swing.*;
class Display {
    static void info(String info) {
        System.out.println("\033[0;32m" + "+INFO+ - " + info + "." + "\033[0m");
    }

    static void errinfo(String info) {
        System.out.println("\033[0;31m" + "+ERROR+ - " + info + "." + "\033[0m");
        JOptionPane.showMessageDialog(null, info + ".", "An Error Occurs", JOptionPane.ERROR_MESSAGE);
    }

    static void warninfo(String info) {
        System.out.println("\033[0;33m" + "+WARNING+ - " + info + "." + "\033[0m");
    }
}
