/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wiki2local;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

/**
 *
 * @author Junaid
 */
public class MainFrame extends JFrame {
    JTree topicTree;
    JMenuBar menuBar;
    GridBagLayout layout;

    public MainFrame(String title) {
        super(title);
        this.initializeComponents();
    }

    private void initializeComponents() {
        this.setSize(400, 500);

        this.layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        this.menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        this.menuBar.add(fileMenu);
        this.setJMenuBar(menuBar);


        this.topicTree = new JTree();
        JScrollPane topicTreeScrollPane = new JScrollPane(this.topicTree);


        // MainFrame action listeners - BEGIN
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                frameClosing(event);
            }
        });
        // MainFrame action listeners - END
        this.setVisible(true);
    }

    public void frameClosing(WindowEvent event) {
        System.exit(0);
    }
}
