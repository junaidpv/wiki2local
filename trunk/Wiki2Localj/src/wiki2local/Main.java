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
 */
public class Main {
    public static MainFrame mainFrame;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainFrame frame = new MainFrame("Wiki2Local");
                Main.mainFrame = frame;
                frame.initialize();
            }
        });

    }
}
