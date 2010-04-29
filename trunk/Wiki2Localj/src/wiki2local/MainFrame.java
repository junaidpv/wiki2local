/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiki2local;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Junaid
 */
public class MainFrame extends JFrame {

    JTree topicTree;
    JMenuBar menuBar;
    GridBagLayout layout;
    TocTreeModel tocTreeModel;
    JTextField baseDirTextField;
    JButton pullButton;
    JLabel statusTextLabel;

    public MainFrame(String title) {
        super(title);
    }

    public void initialize() {
        setSize(400, 600);
        initMenuBar();
        initDisplayComponents();
        setVisible(true);
    }

    public void initDisplayComponents() {
        /*
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("AnjaliOldLipi.ttf"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Exception occured: " + e.getMessage());
        }
        UIManager.put("Font", font);
        */
        this.layout = new GridBagLayout();
        this.setLayout(this.layout);
        GridBagConstraints gbc = new GridBagConstraints();

        this.topicTree = new JTree(new DefaultMutableTreeNode());
        JScrollPane topicTreeScrollPane = new JScrollPane(this.topicTree);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        this.add(topicTreeScrollPane, gbc);

        this.baseDirTextField = new JTextField();
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        this.add(this.baseDirTextField, gbc);

        JButton baseDirChooserButton = new JButton("Browse base directory");
        baseDirChooserButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                baseDirChooserButtonClicked(event);
            }
        });
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        this.add(baseDirChooserButton, gbc);

        this.pullButton = new JButton("Pull");
        gbc.weightx = 0;
        gbc.gridx = 0;
        gbc.gridy = 2;
        this.pullButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                pullButtonClicked(event);
            }
        });
        this.add(this.pullButton, gbc);

        this.statusTextLabel = new JLabel("");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        this.add(this.statusTextLabel, gbc);

        // MainFrame action listeners - BEGIN
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                frameClosing(event);
            }
        });
        // MainFrame action listeners - END
    }

    /**
     * Initialize menu of this window
     */
    public void initMenuBar() {
        this.menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        this.menuBar.add(fileMenu);
        JMenuItem openTopicMenuItem = new JMenuItem("Open Topic File...");
        openTopicMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                openTopicMenuItemClicked(event);
            }
        });
        fileMenu.add(openTopicMenuItem);
        JMenuItem quitMenuItem = new JMenuItem("Quit");
        quitMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                quitMenuItemClicked(event);
            }
        });
        fileMenu.add(quitMenuItem);

        this.setJMenuBar(menuBar);
    }

    public void frameClosing(WindowEvent event) {
        System.exit(0);
    }

    public void quitMenuItemClicked(ActionEvent event) {

        System.exit(0);
    }

    public void openTopicMenuItemClicked(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.showOpenDialog(this);
        try {
            File topicListFile = fileChooser.getSelectedFile();
            if (topicListFile != null) {
                FileInputStream fis = new FileInputStream(topicListFile);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                ArrayList<String> items = new ArrayList<String>();
                String t;
                do {
                    t = br.readLine();
                    if (t != null) {
                        items.add(t);
                    }
                } while (t != null);
                fis.close();
                File baseDir = new File(this.baseDirTextField.getText());
                String baseDirName = baseDir.getName();
                // Append backslash always
                baseDirName = baseDirName.endsWith("/") ? baseDirName : baseDirName + "/";
                this.tocTreeModel = TocTreeModel.parse(items, "example", "filetree", "closed", "folder", "file", baseDirName );
                if (this.tocTreeModel != null) {
                    this.topicTree.setModel(tocTreeModel);
                } else {
                    JOptionPane.showMessageDialog(this, "Topic file format not valid.");
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Exception occured: " + e.toString());
        }
    }

    public void baseDirChooserButtonClicked(MouseEvent event) {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.showOpenDialog(this);
        if (fileChooser.getSelectedFile() != null) {
            this.baseDirTextField.setText(fileChooser.getSelectedFile().toString());
        }
    }

    public void pullButtonClicked(MouseEvent event) {
        File baseDir = new File(this.baseDirTextField.getText());
        String baseDirName = baseDir.getName();
        // Append backslash always
        baseDirName = baseDirName.endsWith("/") ? baseDirName : baseDirName + "/";
        if (this.tocTreeModel == null) {
            JOptionPane.showMessageDialog(this, "Load topic list first.");
        } else if (this.baseDirTextField.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Specify base directory to which load contents.");
        } else if (!baseDir.exists()) {
            JOptionPane.showMessageDialog(this, "Directory to which load contents does not exist.");
        } else {
            try {
                File loggerFile = new File("log.log");
                this.statusTextLabel.setText("getting page list...");
                HashMap pageList = this.tocTreeModel.getPageList();
                this.statusTextLabel.setText("creating extractor...");
                WikiPageExtractor pageExtractor = new WikiPageExtractor(pageList, baseDirName, "ml", "wikipedia", loggerFile);
                this.statusTextLabel.setText("extracting pages...");
                pageExtractor.extractPages();
                this.statusTextLabel.setText("creating toc to write on html...");
                String htmlTree = this.tocTreeModel.getHtmlTocTree(pageExtractor.getHtmlFileList());
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(baseDirName + "toc_template.html")), "UTF-8"));
                char[] buffer = new char[1024];
                StringBuilder textBuffer = new StringBuilder();
                int numread;
                do {
                    numread = br.read(buffer);
                    if(numread > 0) textBuffer.append(buffer, 0, numread);
                } while(numread != -1);
                br.close();
                File tocFile = new File(baseDirName + "toc.html");
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tocFile), "UTF-8"));
                String newText = textBuffer.toString().replaceAll("\\{\\$TOC\\$\\}", htmlTree);
                bw.write(newText);
                bw.flush();
                bw.close();
                this.statusTextLabel.setText("Done.");
            }
            catch(Exception e ) {
                String msg = "";
                StackTraceElement stackTrace[] = e.getStackTrace();
                for(StackTraceElement item : stackTrace) {
                    msg += item.toString()+"\n";
                }
                JOptionPane.showMessageDialog(this, "Exception occured: "+msg);
            }
        }
    }
}
