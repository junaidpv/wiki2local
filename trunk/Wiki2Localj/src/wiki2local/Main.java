/* File: Main.java
 * $Author$
 * $LastChangedDate$
 * $Rev$
 * Licensed under GPL v3
 */
package wiki2local;

import javax.swing.SwingUtilities;

/**
 *
 * @author Junaid
 * @version 0.7
 * @since 0.1
 */
public class Main {
    public static MainFrame mainFrame;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        System.setProperty("program.name", "Wiki2Local");
        System.setProperty("program.version", "0.7");
        System.setProperty("program.author", "Junaid P V");
        System.setProperty("program.authorlink", "http://ml.wikipedia.org/wiki/User:Junaidpv");
        System.setProperty("program.description", "Program to capture set of wikipages to local disk.");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainFrame frame = new MainFrame("Wiki2Local v"+System.getProperty("program.version"));
                Main.mainFrame = frame;
                frame.initialize();
            }
        });

    }
}
