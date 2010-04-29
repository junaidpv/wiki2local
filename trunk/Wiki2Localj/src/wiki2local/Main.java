/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wiki2local;

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
        MainFrame frame = new MainFrame("Wiki2Local");
        Main.mainFrame = frame;
        frame.initialize();
    }
}
